package com.example.eshopping.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.eshopping.R;
import com.example.eshopping.adapter.ProductDisplayAdapter;
import com.example.eshopping.firebasetree.NodeNames;
import com.example.eshopping.model.ProductInfoModelClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShopFragment extends Fragment {

    private ImageView cartImageView;
    private RecyclerView productsRecyclerView;

    private List<ProductInfoModelClass> productInfoModelClassList;
    private ProductDisplayAdapter productDisplayAdapter;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ShopFragment() {
        // Required empty public constructor
    }

    public static ShopFragment newInstance(String param1, String param2) {
        ShopFragment fragment = new ShopFragment();
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
        View view = inflater.inflate(R.layout.fragment_shop, container, false);

        cartImageView = view.findViewById(R.id.cartImageView);
        productsRecyclerView = view.findViewById(R.id.productsRecyclerView);

        LinearLayoutManager productLinearLayoutManager = new GridLayoutManager(getContext(),2);
        productsRecyclerView.setLayoutManager(productLinearLayoutManager);

        productInfoModelClassList = new ArrayList<>();
        productDisplayAdapter = new ProductDisplayAdapter(getContext(),productInfoModelClassList);
        productsRecyclerView.setAdapter(productDisplayAdapter);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(NodeNames.PRODUCTS).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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

        return view;
    }
}