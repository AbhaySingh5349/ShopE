package com.example.eshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eshopping.firebasetree.Constants;
import com.example.eshopping.firebasetree.NodeNames;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductDetailActivity extends AppCompatActivity {

    @BindView(R.id.addToCartImageView)
    ImageView addToCartImageView;
    @BindView(R.id.addProductImageView)
    ImageView productImageView;
    @BindView(R.id.productNameTextInputEditText)
    TextInputEditText productNameTextInputEditText;
    @BindView(R.id.priceTextInputEditText)
    TextInputEditText priceTextInputEditText;
    @BindView(R.id.descriptionTextInputEditText)
    TextInputEditText descriptionTextInputEditText;
    @BindView(R.id.quantityTextInputEditText)
    TextInputEditText quantityTextInputEditText;

    FirebaseAuth firebaseAuth; // to create object of Firebase Auth class to fetch currently loged in user
    FirebaseUser firebaseUser; // to create object of Firebase User class to get current user to store currently loged in user

    String productId, publisherId, title, price, description, quantity, currentUserId;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(this); // instantiating Spinner Progress Dialog

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        currentUserId = Objects.requireNonNull(firebaseUser).getUid();

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        productId = getIntent().getStringExtra("productId");
        publisherId = getIntent().getStringExtra("publisherId");
        title = getIntent().getStringExtra("title");
        price = getIntent().getStringExtra("price");
        description = getIntent().getStringExtra("description");
        quantity = getIntent().getStringExtra("quantity");


        firebaseFirestore.collection(NodeNames.CARTITEMS).whereEqualTo(NodeNames.BUYERID,currentUserId).whereEqualTo(NodeNames.PRODUCTID,productId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            //    addToCartImageView.setVisibility(View.INVISIBLE);
                Log.e("Product purchased", queryDocumentSnapshots.getDocuments().toString());
                if(queryDocumentSnapshots.getDocuments().size()>0){
                    addToCartImageView.setVisibility(View.INVISIBLE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProductDetailActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        if(publisherId.equals(currentUserId)){
            addToCartImageView.setVisibility(View.INVISIBLE);
        }else {
            addToCartImageView.setVisibility(View.VISIBLE);
        }

        StorageReference productImage = FirebaseStorage.getInstance().getReference().child(Constants.PRODUCTIMAGES).child(publisherId).child(productId);
        productImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(ProductDetailActivity.this).load(uri).placeholder(R.drawable.add_image_icon).into(productImageView);
            }
        });

        productNameTextInputEditText.setText(title);
        productNameTextInputEditText.setEnabled(false);

        priceTextInputEditText.setText(price);
        priceTextInputEditText.setEnabled(false);

        descriptionTextInputEditText.setText(description);
        descriptionTextInputEditText.setEnabled(false);

        quantityTextInputEditText.setText(quantity);
        quantityTextInputEditText.setEnabled(false);

        DocumentReference documentReference = firebaseFirestore.collection(NodeNames.CARTITEMS).document();
        String cartItemId  = documentReference.getId();

        HashMap<String,Object> cartHashMap = new HashMap<>();
        cartHashMap.put(NodeNames.PRODUCTID,productId);
        cartHashMap.put(NodeNames.PRODUCTPUBLISHERID,publisherId);
        cartHashMap.put(NodeNames.PRODUCTNAME,title);
        cartHashMap.put(NodeNames.PRODUCTPRICE,price);
        cartHashMap.put(NodeNames.PRODUCTDESCRIPTION,description);
        cartHashMap.put(NodeNames.STOCKQUANTITY,quantity);
        cartHashMap.put(NodeNames.CARTITEMID,cartItemId);
        cartHashMap.put(NodeNames.DEFAULTCARTQUANTITY,"1");
        cartHashMap.put(NodeNames.BUYERID,currentUserId);

        // Calender is an abstract class that provides methods for converting between a specific instant in time and a set of calendar fields such as YEAR, MONTH, DAY_OF_MONTH, HOUR

        Calendar date = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd,yyyy"); // Nov 26,2020
        String currentDate = currentDateFormat.format(date.getTime());
        cartHashMap.put(NodeNames.PRODUCTDATE,currentDate);

        addToCartImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setTitle("Adding Product");
                progressDialog.setMessage("Please wait while product is being added to Cart");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                firebaseFirestore.collection(NodeNames.CARTITEMS).document(cartItemId).set(cartHashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(ProductDetailActivity.this,"Product added to cart",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProductDetailActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}