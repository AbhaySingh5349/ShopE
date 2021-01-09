package com.example.eshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eshopping.adapter.CartProductAdapter;
import com.example.eshopping.adapter.ProductDisplayAdapter;
import com.example.eshopping.firebasetree.NodeNames;
import com.example.eshopping.model.CartProductModelClass;
import com.example.eshopping.model.ProductInfoModelClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartListActivity extends AppCompatActivity {

    @BindView(R.id.cartImageView)
    ImageView cartImageView;
    @BindView(R.id.cartListRecyclerView)
    RecyclerView cartListRecyclerView;
    @BindView(R.id.checkoutCardView)
    CardView checkoutCardView;
    @BindView(R.id.subtotalTextView)
    TextView subtotalTextView;
    @BindView(R.id.shippingChargesTextView)
    TextView shippingChargesTextView;
    @BindView(R.id.amountTextView)
    TextView amountTextView;
    @BindView(R.id.checkOutBtn)
    Button checkOutBtn;

    private List<CartProductModelClass> cartProductModelClassList;
    private CartProductAdapter cartProductAdapter;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    String currentUserId;

    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_list);
        ButterKnife.bind(this);

        // retrieving current user

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        currentUserId = Objects.requireNonNull(firebaseUser).getUid();

        cartListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        cartProductModelClassList = new ArrayList<>();
        cartProductAdapter = new CartProductAdapter(this,cartProductModelClassList);
        cartListRecyclerView.setAdapter(cartProductAdapter);

        checkoutCardView.setVisibility(View.INVISIBLE);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(NodeNames.CARTITEMS).whereEqualTo(NodeNames.BUYERID,currentUserId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                cartProductModelClassList.clear();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                    CartProductModelClass cartProductModelClass = documentSnapshot.toObject(CartProductModelClass.class);
                    cartProductModelClassList.add(cartProductModelClass);
                    cartProductAdapter.notifyDataSetChanged();
                }
                if(cartProductModelClassList.size()==0){
                    cartListRecyclerView.setVisibility(View.INVISIBLE);
                    checkoutCardView.setVisibility(View.INVISIBLE);
                    Toast.makeText(CartListActivity.this,"No Products Available in Cart",Toast.LENGTH_LONG);
                }else {
                    cartListRecyclerView.setVisibility(View.VISIBLE);

                    cartImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            checkoutCardView.setVisibility(View.VISIBLE);

                            double subTotal = 0.0, shippingCharges = 0.0, total = 0.0;
                            for (CartProductModelClass cartProductModelClass : cartProductModelClassList){
                                double price = Double.parseDouble(cartProductModelClass.getProductPrice());
                                int quantity = Integer.parseInt(cartProductModelClass.getDefaultCartQuantity());
                                subTotal += price*quantity;
                                shippingCharges += 0.1*subTotal;
                            }
                            total = subTotal + shippingCharges;
                            subtotalTextView.setText("$" + subTotal);
                            shippingChargesTextView.setText("$" + shippingCharges);
                            amountTextView.setText("$" + total);
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CartListActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}