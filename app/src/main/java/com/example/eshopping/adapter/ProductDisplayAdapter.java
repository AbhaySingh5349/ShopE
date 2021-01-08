package com.example.eshopping.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eshopping.R;
import com.example.eshopping.firebasetree.Constants;
import com.example.eshopping.model.ProductInfoModelClass;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ProductDisplayAdapter extends RecyclerView.Adapter<ProductDisplayAdapter.ProductDisplayViewHolder> {

    private Context context;
    private List<ProductInfoModelClass> productInfoModelClassList;

    public ProductDisplayAdapter(Context context, List<ProductInfoModelClass> productInfoModelClassList) {
        this.context = context;
        this.productInfoModelClassList = productInfoModelClassList;
    }

    SharedPreferences sharedPreferences; // it allows to save and retrieve data in the form of key,value pair

    private ProgressDialog progressDialog;

    @NonNull
    @Override
    public ProductDisplayAdapter.ProductDisplayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.products_layout,parent,false); // attaching layout to Recycler View to display posts uploaded
        return new ProductDisplayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductDisplayAdapter.ProductDisplayViewHolder holder, int position) {
        ProductInfoModelClass productInfoModelClass = productInfoModelClassList.get(position);

        // getSharedPreferences() returns an instance pointing to the file that contains the values of preferences. By setting this mode, the file can only be accessed using calling application

        sharedPreferences = context.getSharedPreferences("com.example.eshopping.adapter", Context.MODE_PRIVATE);

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Loading Products");
        progressDialog.setMessage("Please wait while we are loading your products");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String productId = productInfoModelClass.getProductId();
        String title = productInfoModelClass.getProductName();
        String date = productInfoModelClass.getProductDate();
        String publisherId = productInfoModelClass.getProductPublisherId();

        StorageReference postImage = FirebaseStorage.getInstance().getReference().child(Constants.PRODUCTIMAGES).child(publisherId).child(productId);
        postImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).placeholder(R.drawable.add_image_icon).into(holder.productImageView);
            }
        });

        holder.dateTextView.setText(date);
        holder.titleTextView.setText(title);
        progressDialog.dismiss();
    }

    @Override
    public int getItemCount() {
        return productInfoModelClassList.size();
    }

    public class ProductDisplayViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView, dateTextView;
        ImageView productImageView;

        public ProductDisplayViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.titleTextView);
            productImageView = itemView.findViewById(R.id.productImageView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
}
