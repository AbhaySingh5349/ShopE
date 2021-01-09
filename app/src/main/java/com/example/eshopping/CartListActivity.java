package com.example.eshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
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

    @BindView(R.id.cartListRecyclerView)
    RecyclerView cartListRecyclerView;
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

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(NodeNames.CARTITEMS).whereEqualTo(NodeNames.BUYERID,currentUserId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                cartProductModelClassList.clear();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                    CartProductModelClass cartProductModelClass = documentSnapshot.toObject(CartProductModelClass.class);
                    cartProductModelClassList.add(cartProductModelClass);
                    cartProductAdapter.notifyDataSetChanged();
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