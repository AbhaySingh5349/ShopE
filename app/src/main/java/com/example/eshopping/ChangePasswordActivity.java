package com.example.eshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.eshopping.firebasetree.NodeNames;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangePasswordActivity extends AppCompatActivity {

    @BindView(R.id.appLogoImage)
    ImageView appLogoImage;
    @BindView(R.id.passwordTextInputLayout)
    TextInputLayout passwordTextInputLayout;
    @BindView(R.id.passwordTextInputEditText)
    TextInputEditText passwordTextInputEditText;
    @BindView(R.id.confirmPasswordTextInputLayout)
    TextInputLayout confirmPasswordTextInputLayout;
    @BindView(R.id.confirmPasswordTextInputEditText)
    TextInputEditText confirmPasswordTextInputEditText;
    @BindView(R.id.changePasswordBtn)
    Button changePasswordBtn;

    FirebaseAuth firebaseAuth; // to create object of Firebase Auth class
    FirebaseUser firebaseUser; // to create object of Firebase User class to get current user

    String password, confirmPassword, currentUserId;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        currentUserId = firebaseUser.getUid();

        progressDialog = new ProgressDialog(this); // instantiating Progress Dialog

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPasswordCredentials();
            }
        });
    }

    // validating required fields

    private boolean validatePassword(){
        password = Objects.requireNonNull(passwordTextInputEditText.getText()).toString().trim();
        if(password.isEmpty()){
            passwordTextInputLayout.setError("Enter Password");
            return false;
        }else if(password.length()<6){
            passwordTextInputLayout.setError("Weak Password");
            return false;
        }else {
            passwordTextInputLayout.setError(null);
            passwordTextInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateConfirmPassword(){
        confirmPassword = Objects.requireNonNull(confirmPasswordTextInputEditText.getText()).toString().trim();
        if(confirmPassword.isEmpty()){
            confirmPasswordTextInputLayout.setError("Re-Enter Password");
            return false;
        }else {
            if(confirmPassword.equals(password)){
                confirmPasswordTextInputLayout.setError(null);
                confirmPasswordTextInputLayout.setErrorEnabled(false);
                return true;
            }else {
                confirmPasswordTextInputLayout.setError("Password Mismatch");
                return false;
            }

        }
    }

    public void checkPasswordCredentials(){
        if(!validatePassword() | !validateConfirmPassword()){
            validatePassword();
            validateConfirmPassword();
        }else {
            if(firebaseUser!=null){
                progressDialog.setTitle("Updating Password");
                progressDialog.setMessage("Please wait while we are updating your details");
                progressDialog.show();

                // updating password

                firebaseUser.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                            HashMap<String, Object> userHashMap = new HashMap<>();
                            userHashMap.put(NodeNames.PASSWORD, password);
                            firebaseFirestore.collection(NodeNames.USERS).document(currentUserId).update(userHashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    startActivity(new Intent(ChangePasswordActivity.this, ProfileActivity.class));
                                    finish();
                                }
                            });
                        }else {
                            Toast.makeText(ChangePasswordActivity.this,"Password change Failed: " + task.getException(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
}