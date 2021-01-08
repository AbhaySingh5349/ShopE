package com.example.eshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.appLogoImage)
    ImageView appLogoImage;
    @BindView(R.id.emailTextInputLayout)
    TextInputLayout emailTextInputLayout;
    @BindView(R.id.emailTextInputEditText)
    TextInputEditText emailTextInputEditText;
    @BindView(R.id.passwordTextInputLayout)
    TextInputLayout passwordTextInputLayout;
    @BindView(R.id.passwordTextInputEditText)
    TextInputEditText passwordTextInputEditText;
    @BindView(R.id.loginBtn)
    Button loginBtn;
    @BindView(R.id.forgotPasswordTextView)
    TextView forgotPasswordTextView;
    @BindView(R.id.signUpTextView)
    TextView signUpTextView;

    String email, password;

    FirebaseAuth firebaseAuth; // to create object of Firebase Auth class to fetch currently logged in user
    FirebaseUser firebaseUser; // to create object of Firebase User class to get current user to store currently logged in user

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            }
        });

        progressDialog = new ProgressDialog(this); // instantiating Progress Dialog

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLogInCredentials();
            }
        });

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
    }

    // validating required fields

    private boolean validateEmail() {
        email = Objects.requireNonNull(emailTextInputEditText.getText()).toString().trim();
        if (email.isEmpty()) {
            emailTextInputLayout.setError("Enter Email Address");
            return false;
        } else {
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailTextInputLayout.setError(null);
                emailTextInputLayout.setErrorEnabled(false);
                return true;
            } else {
                emailTextInputLayout.setError("Invalid Email Format");
                return false;
            }
        }
    }

    private boolean validatePassword() {
        password = Objects.requireNonNull(passwordTextInputEditText.getText()).toString().trim();
        if (password.isEmpty()) {
            passwordTextInputLayout.setError("Enter Password");
            return false;
        } else if (password.length() < 6) {
            passwordTextInputLayout.setError("Weak Password");
            return false;
        } else {
            passwordTextInputLayout.setError(null);
            passwordTextInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    private void checkLogInCredentials() {
        if(!validateEmail() | !validatePassword()){
            validateEmail();
            validatePassword();
        }else {
            progressDialog.setTitle("Logging In User");
            progressDialog.setMessage("Please wait while we are updating your details");
            progressDialog.show();

            // logging in user using Email and Password

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // user is authenticated
                        progressDialog.dismiss();
                        startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Login Failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(firebaseUser!=null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
}