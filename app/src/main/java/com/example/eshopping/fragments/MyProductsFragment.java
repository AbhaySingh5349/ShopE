package com.example.eshopping.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.eshopping.AddProductActivity;
import com.example.eshopping.R;
import com.example.eshopping.adapter.ProductDisplayAdapter;
import com.example.eshopping.firebasetree.NodeNames;
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

public class MyProductsFragment extends Fragment {

    private ImageView addImageView;
    private RecyclerView myProductsRecyclerView;

    private List<ProductInfoModelClass> productInfoModelClassList;
    private ProductDisplayAdapter productDisplayAdapter;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    String currentUserId;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public MyProductsFragment() {
        // Required empty public constructor
    }

    public static MyProductsFragment newInstance(String param1, String param2) {
        MyProductsFragment fragment = new MyProductsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_products, container, false);

        // retrieving current user

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        currentUserId = Objects.requireNonNull(firebaseUser).getUid();

        addImageView = view.findViewById(R.id.addImageView);
        myProductsRecyclerView = view.findViewById(R.id.myProductsRecyclerView);

        LinearLayoutManager productLinearLayoutManager = new GridLayoutManager(getContext(),2);
        myProductsRecyclerView.setLayoutManager(productLinearLayoutManager);

        productInfoModelClassList = new ArrayList<>();
        productDisplayAdapter = new ProductDisplayAdapter(getContext(),productInfoModelClassList);
        myProductsRecyclerView.setAdapter(productDisplayAdapter);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(NodeNames.PRODUCTS).whereEqualTo(NodeNames.PRODUCTPUBLISHERID,currentUserId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                productInfoModelClassList.clear();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                    ProductInfoModelClass productInfoModelClass = documentSnapshot.toObject(ProductInfoModelClass.class);
                    productInfoModelClassList.add(productInfoModelClass);
                    productDisplayAdapter.notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        addImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AddProductActivity.class));
            }
        });

        return view;
    }
}