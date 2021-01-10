package com.example.eshopping.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.example.eshopping.CartListActivity;
import com.example.eshopping.R;
import com.example.eshopping.firebasetree.Constants;
import com.example.eshopping.firebasetree.NodeNames;
import com.example.eshopping.model.CartProductModelClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;

public class CartProductAdapter extends RecyclerView.Adapter<CartProductAdapter.CartViewHolder> {

    private Context context;
    private List<CartProductModelClass> cartProductModelClassList;

    public CartProductAdapter(Context context, List<CartProductModelClass> cartProductModelClassList) {
        this.context = context;
        this.cartProductModelClassList = cartProductModelClassList;
    }

    @NonNull
    @Override
    public CartProductAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_list_item,parent,false) ;
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartProductAdapter.CartViewHolder holder, int position) {
        CartProductModelClass cartProductModelClass = cartProductModelClassList.get(position);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        String title = cartProductModelClass.getProductName();
        holder.cartProductTitleTextView.setText(title);

        String cartItemId = cartProductModelClass.getCartItemId();
        String productId = cartProductModelClass.getProductId();
        String publisherId = cartProductModelClass.getProductPublisherId();
        StorageReference productImage = FirebaseStorage.getInstance().getReference().child(Constants.PRODUCTIMAGES).child(publisherId).child(productId);
        productImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).placeholder(R.drawable.add_image_icon).into(holder.cartProductImageView);
            }
        });

        String stockQuantity = cartProductModelClass.getStockQuantity();

        String basePrice = cartProductModelClass.getProductPrice();
        int cartQuantity = Integer.parseInt(cartProductModelClass.getDefaultCartQuantity());
        double price = Integer.parseInt(basePrice)*cartQuantity;
        holder.itemAmountTextView.setText(String.valueOf(price));

        holder.deleteItemImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseFirestore.collection(NodeNames.CARTITEMS).document(cartItemId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context,"Item Deleted Successfully!!",Toast.LENGTH_LONG).show();
                        if (context instanceof CartListActivity) {
                            ((CartListActivity)context).getCartItems();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        holder.addItemImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cartQuantity = Integer.parseInt(cartProductModelClass.getDefaultCartQuantity());
                if(cartQuantity <= Integer.parseInt(stockQuantity)){
                    cartQuantity = cartQuantity+1;
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put(NodeNames.DEFAULTCARTQUANTITY,String.valueOf(cartQuantity));
                    String cartItemId = cartProductModelClass.getCartItemId();
                    int finalCartQuantity = cartQuantity;
                    firebaseFirestore.collection(NodeNames.CARTITEMS).document(cartItemId).update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (context instanceof CartListActivity) {
                                ((CartListActivity)context).getCartItems();
                            }
                            double amount = Double.parseDouble(basePrice)*(finalCartQuantity);
                            holder.itemAmountTextView.setText(String.valueOf(amount));
                            holder.itemQuantityTextView.setText(String.valueOf(finalCartQuantity));
                        }
                    });
                }else {
                    Toast.makeText(context,"Limited Stock Available",Toast.LENGTH_LONG).show();
                }
            }
        });

        holder.removeItemImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Integer.parseInt(cartProductModelClass.getDefaultCartQuantity()) > 1){
                    HashMap<String,Object> hashMap = new HashMap<>();
                    int cartQuantity = Integer.parseInt(cartProductModelClass.getDefaultCartQuantity());
                    cartQuantity = cartQuantity-1;
                    hashMap.put(NodeNames.DEFAULTCARTQUANTITY,String.valueOf(cartQuantity));
                    String cartItemId = cartProductModelClass.getCartItemId();
                    int finalCartQuantity = cartQuantity;
                    firebaseFirestore.collection(NodeNames.CARTITEMS).document(cartItemId).update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (context instanceof CartListActivity) {
                                ((CartListActivity)context).getCartItems();
                            }
                            double amount = Double.parseDouble(basePrice)*(finalCartQuantity);
                            holder.itemAmountTextView.setText(String.valueOf(amount));
                            holder.itemQuantityTextView.setText(String.valueOf(finalCartQuantity));
                            Toast.makeText(context,String.valueOf(finalCartQuantity),Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartProductModelClassList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {

        TextView cartProductTitleTextView, itemAmountTextView, itemQuantityTextView;
        ImageView cartProductImageView, removeItemImageView, addItemImageView, deleteItemImageView;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            cartProductTitleTextView = itemView.findViewById(R.id.cartProductTitleTextView);
            cartProductImageView = itemView.findViewById(R.id.cartProductImageView);
            itemAmountTextView = itemView.findViewById(R.id.itemAmountTextView);
            removeItemImageView = itemView.findViewById(R.id.removeItemImageView);
            itemQuantityTextView = itemView.findViewById(R.id.itemQuantityTextView);
            addItemImageView = itemView.findViewById(R.id.addItemImageView);
            deleteItemImageView = itemView.findViewById(R.id.deleteItemImageView);
        }
    }
}
