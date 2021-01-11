package com.example.eshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eshopping.adapter.ProductDisplayAdapter;
import com.example.eshopping.firebasetree.NodeNames;
import com.example.eshopping.model.ProductInfoModelClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckOutActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.payableAmountTextView)
    TextView payableAmountTextView;
    @BindView(R.id.addressTextView)
    TextView addressTextView;
    @BindView(R.id.placeNameTextView)
    TextView placeNameTextView;
    @BindView(R.id.contactTextView)
    TextView contactTextView;
    @BindView(R.id.placeOrderBtn)
    Button placeOrderBtn;
    @BindView(R.id.checkOutRecyclerView)
    RecyclerView checkOutRecyclerView;

    private List<ProductInfoModelClass> productInfoModelClassList;
    private ProductDisplayAdapter productDisplayAdapter;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    String currentUserId;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        ButterKnife.bind(this);

        // add back arrow on CheckOut toolbar
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        // retrieving current user

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        currentUserId = Objects.requireNonNull(firebaseUser).getUid();

        // receiving intent from DeliveryAddressAdapter
        String address = getIntent().getStringExtra(NodeNames.ADDRESS);
        addressTextView.setText(address);

        String placeName = getIntent().getStringExtra(NodeNames.PLACENAME);
        placeNameTextView.setText("(" + placeName + ")");

        String contact = getIntent().getStringExtra(NodeNames.CONTACT);
        contactTextView.setText(contact);

        String payableAmount = getIntent().getStringExtra(NodeNames.PAYABLEAMOUNT);
        payableAmountTextView.setText(payableAmount);

        ArrayList<String> cartItemIdArrayList = new ArrayList<>();
        cartItemIdArrayList = getIntent().getStringArrayListExtra(NodeNames.CARTITEMIDS);

        LinearLayoutManager productLinearLayoutManager = new GridLayoutManager(this,2);
        checkOutRecyclerView.setLayoutManager(productLinearLayoutManager);

        productInfoModelClassList = new ArrayList<>();
        productDisplayAdapter = new ProductDisplayAdapter(this,productInfoModelClassList);
        checkOutRecyclerView.setAdapter(productDisplayAdapter);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        ArrayList<String> finalCartItemIdArrayList = cartItemIdArrayList;
        firebaseFirestore.collection(NodeNames.CARTITEMS).whereEqualTo(NodeNames.BUYERID,currentUserId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                productInfoModelClassList.clear();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                    ProductInfoModelClass productInfoModelClass = documentSnapshot.toObject(ProductInfoModelClass.class);
                    productInfoModelClassList.add(productInfoModelClass);
                    productDisplayAdapter.notifyDataSetChanged();
                }
                placeOrderBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DocumentReference documentReference = firebaseFirestore.collection(NodeNames.PRODUCTS).document();
                        String orderId  = documentReference.getId();
                        HashMap<String,Object> orderHashMap = new HashMap<>();
                        orderHashMap.put(NodeNames.PAYABLEAMOUNT,payableAmount);
                        orderHashMap.put(NodeNames.BUYERID,currentUserId);
                        orderHashMap.put(NodeNames.ORDERID,orderId);
                        orderHashMap.put(NodeNames.ADDRESS,address);
                        orderHashMap.put(NodeNames.PLACENAME,placeName);
                        orderHashMap.put(NodeNames.CONTACT,contact);
                        orderHashMap.put(NodeNames.CARTITEMIDS, finalCartItemIdArrayList);
                        firebaseFirestore.collection(NodeNames.ORDERDETAILS).document(orderId).set(orderHashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(CheckOutActivity.this,"Order Placed Successfully!!",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(CheckOutActivity.this,MainActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CheckOutActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CheckOutActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}