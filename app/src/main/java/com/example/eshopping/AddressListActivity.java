package com.example.eshopping;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eshopping.adapter.DeliveryAddressAdapter;
import com.example.eshopping.firebasetree.Constants;
import com.example.eshopping.firebasetree.NodeNames;
import com.example.eshopping.model.CartProductModelClass;
import com.example.eshopping.model.DeliveryAddressModelClass;
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

public class AddressListActivity extends AppCompatActivity {

    @BindView(R.id.toolbarTitleTextView)
    TextView toolbarTitleTextView;
    @BindView(R.id.addAddressBtn)
    Button addAddressBtn;
    @BindView(R.id.addressRecyclerView)
    RecyclerView addressRecyclerView;

    FirebaseAuth firebaseAuth; // to create object of Firebase Auth class to fetch currently loged in user
    FirebaseUser firebaseUser; // to create object of Firebase User class to get current user to store currently loged in user

    String currentUserId, payableAmount = "";

    ArrayList<String> cartItemIdArrayList = new ArrayList<>();

    List<DeliveryAddressModelClass> deliveryAddressModelClassList;
    DeliveryAddressAdapter deliveryAddressAdapter;

    LocationManager locationManager;
    boolean gpsProviderEnabled;
    AlertDialog gpsAlertDialog;
    int gpsEnableRequestCode = 101;

    boolean addressSelected = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);
        ButterKnife.bind(this);

        // get current user

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        currentUserId = Objects.requireNonNull(firebaseUser).getUid();

        if(isGPSEnabled()){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }

        // receiving intent from CartListActivity
        if(getIntent().hasExtra(Constants.ADDRESSSELECTED)){
            addressSelected = getIntent().getBooleanExtra(Constants.ADDRESSSELECTED,false);
        }
        if(getIntent().hasExtra(NodeNames.PAYABLEAMOUNT)){
            payableAmount = getIntent().getStringExtra(NodeNames.PAYABLEAMOUNT);
        }
        if(getIntent().hasExtra(NodeNames.CARTITEMIDS)){
            cartItemIdArrayList = getIntent().getStringArrayListExtra(NodeNames.CARTITEMIDS);
        }

        if(addressSelected){
            toolbarTitleTextView.setText("Select Address for Delivery");
            addAddressBtn.setVisibility(View.GONE);
        }

        addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddressListActivity.this,AddAddressMapsActivity.class));
            }
        });

        deliveryAddressModelClassList  = new ArrayList<>();
        addressRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        deliveryAddressAdapter = new DeliveryAddressAdapter(this,deliveryAddressModelClassList,addressSelected,payableAmount,cartItemIdArrayList);
        addressRecyclerView.setAdapter(deliveryAddressAdapter);

        getAddressList();
    }

    public void getAddressList() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(NodeNames.DELIVERYADDRESSES).whereEqualTo(NodeNames.USERID,currentUserId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                deliveryAddressModelClassList.clear();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                    DeliveryAddressModelClass deliveryAddressModelClass = documentSnapshot.toObject(DeliveryAddressModelClass.class);
                    deliveryAddressModelClassList.add(deliveryAddressModelClass);
                    deliveryAddressAdapter.notifyDataSetChanged();
                }
                if(deliveryAddressModelClassList.size()==0){
                    addressRecyclerView.setVisibility(View.INVISIBLE);
                    Toast.makeText(AddressListActivity.this,"No Address Added for Delivery",Toast.LENGTH_LONG).show();
                }else {
                    addressRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddressListActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    // checking if GPS is enabled

    private boolean isGPSEnabled(){
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        gpsProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(gpsProviderEnabled){
            return true;
        }else {
            gpsAlertDialog = new AlertDialog.Builder(this)
                    .setTitle("GPS Enabling Permission").setMessage("GPS is required for tracking location,Please enable Location Services")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent gpsSettingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(gpsSettingsIntent,gpsEnableRequestCode);
                        }
                    }).setCancelable(false).show();
        }
        return false;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==gpsEnableRequestCode){
            gpsProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(gpsProviderEnabled){
                Toast.makeText(AddressListActivity.this,"Location Services Enabled",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(AddressListActivity.this,"GPS not enabled,Unable to track user location",Toast.LENGTH_SHORT).show();
            }
        }
    }
}