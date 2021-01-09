package com.example.eshopping.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eshopping.ProductDetailActivity;
import com.example.eshopping.R;
import com.example.eshopping.firebasetree.Constants;
import com.example.eshopping.model.ProductInfoModelClass;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Objects;

public class ProductDisplayAdapter extends RecyclerView.Adapter<ProductDisplayAdapter.ProductDisplayViewHolder> {

    private Context context;
    private List<ProductInfoModelClass> productInfoModelClassList;

    public ProductDisplayAdapter(Context context, List<ProductInfoModelClass> productInfoModelClassList) {
        this.context = context;
        this.productInfoModelClassList = productInfoModelClassList;
    }

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    String currentUserId;

    SharedPreferences sharedPreferences; // it allows to save and retrieve data in the form of key,value pair

    private ProgressDialog progressDialog;

    @NonNull
    @Override
    public ProductDisplayAdapter.ProductDisplayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.products_layout,parent,false); // attaching layout to Recycler View to display posts uploaded
        return new ProductDisplayViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProductDisplayAdapter.ProductDisplayViewHolder holder, int position) {
        ProductInfoModelClass productInfoModelClass = productInfoModelClassList.get(position);


        // retrieving current user

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        currentUserId = Objects.requireNonNull(firebaseUser).getUid();

        // getSharedPreferences() returns an instance pointing to the file that contains the values of preferences. By setting this mode, the file can only be accessed using calling application

        sharedPreferences = context.getSharedPreferences("com.example.eshopping.adapter", Context.MODE_PRIVATE);

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Loading Products");
        progressDialog.setMessage("Please wait while we are loading your products");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String productId = productInfoModelClass.getProductId();
        String publisherId = productInfoModelClass.getProductPublisherId();
        String title = productInfoModelClass.getProductName();
        String price = productInfoModelClass.getProductPrice();
        String description = productInfoModelClass.getProductDescription();
        String quantity = productInfoModelClass.getProductQuantity();

        StorageReference productImage = FirebaseStorage.getInstance().getReference().child(Constants.PRODUCTIMAGES).child(publisherId).child(productId);
        productImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).placeholder(R.drawable.add_image_icon).into(holder.productImageView);
            }
        });

        holder.priceTextView.setText("$" + price);
        holder.titleTextView.setText(title);
        progressDialog.dismiss();

        if(Integer.parseInt(quantity)>0 || currentUserId.equals(publisherId)){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    intent.putExtra("title",title);
                    intent.putExtra("price",price);
                    intent.putExtra("description",description);
                    intent.putExtra("quantity",quantity);
                    intent.putExtra("productId",productId);
                    intent.putExtra("publisherId",publisherId);
                    context.startActivity(intent);
                }
            });
        }else {
            Toast.makeText(context,"Product Out Of Stock",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return productInfoModelClassList.size();
    }

    public static class ProductDisplayViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView, priceTextView;
        ImageView productImageView;

        public ProductDisplayViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.titleTextView);
            productImageView = itemView.findViewById(R.id.productImageView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
        }
    }
}
