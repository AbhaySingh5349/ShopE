package com.example.eshopping;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.eshopping.firebasetree.Constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductDetailActivity extends AppCompatActivity {

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

    String productId, publisherId, title, price, description, quantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        ButterKnife.bind(this);

        productId = getIntent().getStringExtra("productId");
        publisherId = getIntent().getStringExtra("publisherId");
        title = getIntent().getStringExtra("title");
        price = getIntent().getStringExtra("price");
        description = getIntent().getStringExtra("description");
        quantity = getIntent().getStringExtra("quantity");

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
    }
}