package com.example.eshopping;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.eshopping.firebasetree.Constants;
import com.example.eshopping.firebasetree.NodeNames;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddProductActivity extends AppCompatActivity {

    @BindView(R.id.cancelImageView)
    ImageView cancelImageView;
    @BindView(R.id.uploadImageView)
    ImageView uploadImageView;
    @BindView(R.id.addProductImageView)
    ImageView addProductImageView;
    @BindView(R.id.productNameTextInputLayout)
    TextInputLayout productNameTextInputLayout;
    @BindView(R.id.productNameTextInputEditText)
    TextInputEditText productNameTextInputEditText;
    @BindView(R.id.priceTextInputLayout)
    TextInputLayout priceTextInputLayout;
    @BindView(R.id.priceTextInputEditText)
    TextInputEditText priceTextInputEditText;
    @BindView(R.id.descriptionTextInputLayout)
    TextInputLayout descriptionTextInputLayout;
    @BindView(R.id.descriptionTextInputEditText)
    TextInputEditText descriptionTextInputEditText;
    @BindView(R.id.quantityTextInputLayout)
    TextInputLayout quantityTextInputLayout;
    @BindView(R.id.quantityTextInputEditText)
    TextInputEditText quantityTextInputEditText;

    FirebaseAuth firebaseAuth; // to create object of Firebase Auth class to fetch currently loged in user
    FirebaseUser firebaseUser; // to create object of Firebase User class to get current user to store currently loged in user
    StorageReference storageReference, productStorageReference; // to upload profile image to firebase
    Uri selectedImageUri;

    int profileImageRequestCode=101 , readExternalStorageRequestCode=102;
    Intent profileImageIntent;

    String productName, price, description, quantity, currentUserId;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        currentUserId = Objects.requireNonNull(firebaseUser).getUid();

        progressDialog = new ProgressDialog(this); // instantiating Spinner Progress Dialog

        storageReference = FirebaseStorage.getInstance().getReference(); // give reference to root folder of file storage

        cancelImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddProductActivity.this,MainActivity.class));
                finish();
            }
        });

        addProductImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // checking permission to access photo and video gallery of device
                if(ContextCompat.checkSelfPermission(AddProductActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                    profileImageIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(profileImageIntent,profileImageRequestCode);
                }else {
                    ActivityCompat.requestPermissions(AddProductActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},readExternalStorageRequestCode);
                }
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0); // hiding keyboard
            }
        });

        uploadImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkProductCredentials();
            }
        });
    }

    // validating required fields

    private boolean validateProductName(){
        productName = Objects.requireNonNull(productNameTextInputEditText.getText()).toString().trim();
        if(productName.isEmpty()){
            productNameTextInputLayout.setError("Enter Product Name");
            productNameTextInputLayout.setErrorEnabled(true);
            return false;
        }else {
            productNameTextInputLayout.setError(null);
            productNameTextInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePrice(){
        price = Objects.requireNonNull(priceTextInputEditText.getText()).toString().trim();
        if(price.isEmpty()){
            priceTextInputLayout.setError("Enter Product Price");
            priceTextInputLayout.setErrorEnabled(true);
            return false;
        }else {
            priceTextInputLayout.setError(null);
            priceTextInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateDescription(){
        description = Objects.requireNonNull(descriptionTextInputEditText.getText()).toString().trim();
        if(description.isEmpty()){
            descriptionTextInputLayout.setError("Enter Product Description");
            descriptionTextInputLayout.setErrorEnabled(true);
            return false;
        }else {
            descriptionTextInputLayout.setError(null);
            descriptionTextInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateQuantity(){
        quantity = Objects.requireNonNull(quantityTextInputEditText.getText()).toString().trim();
        if(quantity.isEmpty()){
            quantityTextInputLayout.setError("Enter Product Quantity");
            quantityTextInputLayout.setErrorEnabled(true);
            return false;
        }else {
            quantityTextInputLayout.setError(null);
            quantityTextInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (resultCode == RESULT_OK) {
                    if (requestCode == profileImageRequestCode) {
                        // Get the url from data
                        selectedImageUri = Objects.requireNonNull(data).getData();

                        if (null != selectedImageUri) {
                            // Get the path from the Uri
                            String path = getPathFromURI(selectedImageUri);
                            // Set the image in ImageView
                            addProductImageView.post(new Runnable() {
                                @Override
                                public void run() {
                                    addProductImageView.setImageURI(selectedImageUri);
                                }
                            });
                        }
                    }
                }
            }
        }).start();
    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (Objects.requireNonNull(cursor).moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==readExternalStorageRequestCode){
            if(ContextCompat.checkSelfPermission(AddProductActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Intent profileImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(profileImageIntent,profileImageRequestCode);
                }else {
                    Toast.makeText(AddProductActivity.this,"Access Gallery Permission Required",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean validateProductImage(){
        if(selectedImageUri==null){
            Toast.makeText(AddProductActivity.this,"Add Product Image",Toast.LENGTH_SHORT).show();
            return false;
        }else {
            return true;
        }
    }

    private void checkProductCredentials() {
        if(!validateProductName() | !validatePrice() | !validateDescription() | !validateQuantity() | !validateProductImage()){
            validateProductName();
            validatePrice();
            validateDescription();
            validateQuantity();
            validateProductImage();
        }else {
            // updating post on database

            progressDialog.setTitle("Uploading Product");
            progressDialog.setMessage("Please wait while product is being uploaded");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            DocumentReference documentReference = firebaseFirestore.collection(NodeNames.PRODUCTS).document();
            String productId  = documentReference.getId();

            HashMap<String,Object> productHashMap = new HashMap<>();

            productStorageReference = storageReference.child(Constants.PRODUCTIMAGES).child(currentUserId).child(productId);
            productStorageReference.putFile(selectedImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        productStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                productHashMap.put(NodeNames.PRODUCTURL,uri.getPath());
                            }
                        });
                        productHashMap.put(NodeNames.PRODUCTNAME,productName);
                        productHashMap.put(NodeNames.PRODUCTPRICE,price);
                        productHashMap.put(NodeNames.PRODUCTDESCRIPTION,description);
                        productHashMap.put(NodeNames.PRODUCTQUANTITY,quantity);

                        // Calender is an abstract class that provides methods for converting between a specific instant in time and a set of calendar fields such as YEAR, MONTH, DAY_OF_MONTH, HOUR

                        Calendar date = Calendar.getInstance();
                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd,yyyy"); // Nov 26,2020
                        String currentDate = currentDateFormat.format(date.getTime());
                        productHashMap.put(NodeNames.PRODUCTDATE,currentDate);

                        productHashMap.put(NodeNames.PRODUCTID,productId);
                        productHashMap.put(NodeNames.PRODUCTPUBLISHERID,currentUserId);

                        firebaseFirestore.collection(NodeNames.PRODUCTS).document(productId).set(productHashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                Toast.makeText(AddProductActivity.this,"Your product uploaded successfully",Toast.LENGTH_SHORT).show();
                                addProductImageView.setImageURI(null);
                                addProductImageView.setImageResource(R.drawable.add_image_icon);
                                productNameTextInputEditText.setText(null);
                                priceTextInputEditText.setText(null);
                                descriptionTextInputEditText.setText(null);
                                quantityTextInputEditText.setText(null);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddProductActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });
        }
    }
}