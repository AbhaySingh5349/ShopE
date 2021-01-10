package com.example.eshopping;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eshopping.firebasetree.Constants;
import com.example.eshopping.firebasetree.NodeNames;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.circularProgressBar)
    ProgressBar circularProgressBar;
    @BindView(R.id.userProfileImage)
    CircleImageView userProfileImage;
    @BindView(R.id.nameTextInputLayout)
    TextInputLayout nameTextInputLayout;
    @BindView(R.id.nameTextInputEditText)
    TextInputEditText nameTextInputEditText;
    @BindView(R.id.emailTextInputLayout)
    TextInputLayout emailTextInputLayout;
    @BindView(R.id.emailTextInputEditText)
    TextInputEditText emailTextInputEditText;
    @BindView(R.id.changePasswordTextView)
    TextView changePasswordTextView;
    @BindView(R.id.confirmBtn)
    Button confirmBtn;
    @BindView(R.id.addressConstraintLayout)
    ConstraintLayout addressConstraintLayout;
    @BindView(R.id.logoutBtn)
    Button logoutBtn;

    FirebaseAuth firebaseAuth; // to create object of Firebase Auth class
    FirebaseUser firebaseUser; // to create object of Firebase User class to get current user
    StorageReference storageReference, profileImageStorageReference; // to upload profile image to firebase
    Uri serverImageUri, selectedImageUri;

    HashMap<String,Object> usersNodeHashMap;

    String name, currentUserId;
    Intent profileImageIntent;
    int profileImageRequestCode=101 , readExternalStorageRequestCode=102;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        storageReference = FirebaseStorage.getInstance().getReference(); // give reference to root folder of file storage

        // getting current user
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        currentUserId = Objects.requireNonNull(firebaseUser).getUid();

        storageReference = FirebaseStorage.getInstance().getReference(); // give reference to root folder of file storage

        progressDialog = new ProgressDialog(this); // instantiating Spinner Progress Dialog

        if(firebaseUser!=null){
            circularProgressBar.setProgress(20);
            retrieveUserProfile();

            userProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // checking permission to access photo and video gallery of device
                    if(ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                        profileImageIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // intent to open Photo Gallery
                        startActivityForResult(profileImageIntent,profileImageRequestCode);
                    }else {
                        ActivityCompat.requestPermissions(ProfileActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},readExternalStorageRequestCode);
                    }
                }
            });

            changePasswordTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(ProfileActivity.this,ChangePasswordActivity.class));
                }
            });

            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateProfileInfo();
                }
            });

            addressConstraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(ProfileActivity.this,AddressListActivity.class));
                }
            });

            logoutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    firebaseAuth.signOut();
                    Intent intent = new Intent(ProfileActivity.this,LoginActivity.class);
                    // Closing all the Activities
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    // Add new Flag to start new Activity
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    private void retrieveUserProfile() {
        circularProgressBar.setProgress(40);
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection(NodeNames.USERS).document(currentUserId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                circularProgressBar.setProgress(60);
                if(task.isSuccessful()){
                    circularProgressBar.setProgress(80);
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(Objects.requireNonNull(documentSnapshot).exists()){
                        String userNameDB = Objects.requireNonNull(documentSnapshot.get(NodeNames.USERNAME)).toString();
                        nameTextInputEditText.setText(userNameDB);

                        String emailDB = Objects.requireNonNull(documentSnapshot.get(NodeNames.EMAIL)).toString();
                        emailTextInputEditText.setText(emailDB);
                        emailTextInputEditText.setEnabled(false);

                        StorageReference profileImageDB = storageReference.child(Constants.PROFILEIMAGES).child(currentUserId);
                        profileImageDB.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                serverImageUri = uri;
                                Glide.with(ProfileActivity.this).load(serverImageUri).placeholder(R.drawable.profile).into(userProfileImage); // loading image to user profile image
                                circularProgressBar.setProgress(100);
                            }
                        });
                    }
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (resultCode == RESULT_OK ) {
                    if (requestCode == profileImageRequestCode) {
                        // Get the url from data
                        selectedImageUri = Objects.requireNonNull(data).getData();

                        if (null != selectedImageUri) {
                            // Get the path from the Uri
                            String path = getPathFromURI(selectedImageUri);
                            // Set the image in ImageView
                            userProfileImage.post(new Runnable() {
                                @Override
                                public void run() {
                                    userProfileImage.setImageURI(selectedImageUri);
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
            if(ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Intent profileImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(profileImageIntent,profileImageRequestCode);
                }else {
                    Toast.makeText(ProfileActivity.this,"Access Gallery Permission Required",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // updating user profile info

    private void updateProfileInfo() {
        progressDialog.setTitle("Updating Profile");
        progressDialog.setMessage("Please wait while we are updating your profile");
        progressDialog.show();

        // retrieving info from edit texts

        name = Objects.requireNonNull(nameTextInputEditText.getText()).toString().trim();

        usersNodeHashMap = new HashMap<>();

        if(!name.isEmpty()){
            usersNodeHashMap.put(NodeNames.USERNAME,name);
        }

        usersNodeHashMap.put(NodeNames.USERID,currentUserId);

        if(selectedImageUri!=null){
            profileImageStorageReference = storageReference.child(Constants.PROFILEIMAGES).child(currentUserId);

            profileImageStorageReference.putFile(selectedImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        profileImageStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                selectedImageUri = uri;
                                usersNodeHashMap.put(NodeNames.PHOTOURL,selectedImageUri.getPath());
                            }
                        });
                    }
                }
            });
        }

        // updating nodes
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection(NodeNames.USERS).document(currentUserId).update(usersNodeHashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ProfileActivity.this,MainActivity.class));
        finish();
    }
}