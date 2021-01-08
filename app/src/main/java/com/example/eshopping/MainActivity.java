package com.example.eshopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.eshopping.fragments.MyProductsFragment;
import com.example.eshopping.fragments.OrdersFragment;
import com.example.eshopping.fragments.ShopFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.frameLayout)
    FrameLayout frameLayout;

    public static BottomNavigationView bottomNavigation; // It is an implementation of material design bottom navigation used to make easy switch between top-level views in single tap


    Fragment fragment; // a fragment is a kind of sub-activity having its own layout and its own behaviour with its own life cycle callbacks
    FragmentManager fragmentManager; // A FragmentManager manages Fragments in Android, specifically it handles transactions between fragments

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        currentUserId = Objects.requireNonNull(firebaseUser).getUid(); // storing currently active user Id

        bottomNavigation = findViewById(R.id.bottomNavigation);

        bottomNavigation.setOnNavigationItemSelectedListener(this);

        fragmentManager = this.getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameLayout, new ShopFragment()).commit(); // setting Home Fragment as default fragment when application starts
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navShop:
                fragment = new ShopFragment();
                Toast.makeText(this, "Shop items", Toast.LENGTH_SHORT).show();
                fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit(); // switching to Home Fragment
                return true;

            case R.id.navMyProducts:
                fragment = new MyProductsFragment();
                Toast.makeText(this, "My Products", Toast.LENGTH_SHORT).show();
                fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit(); // switching to User Search Fragment
                return true;

            case R.id.navOrders:
                fragment = new OrdersFragment();
                Toast.makeText(this, "Orders", Toast.LENGTH_SHORT).show();
                fragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit(); // switching to Orders Fragment
                return true;

            case R.id.navProfile:
                startActivity(new Intent(this, ProfileActivity.class));
                Toast.makeText(this,"Profile",Toast.LENGTH_SHORT).show(); // switching to Add Post Fragment
                return true;

            default:
                return false;
        }
    }
}