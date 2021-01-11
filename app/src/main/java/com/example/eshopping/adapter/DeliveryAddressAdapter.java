package com.example.eshopping.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eshopping.AddressListActivity;
import com.example.eshopping.CartListActivity;
import com.example.eshopping.CheckOutActivity;
import com.example.eshopping.R;
import com.example.eshopping.firebasetree.NodeNames;
import com.example.eshopping.model.DeliveryAddressModelClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DeliveryAddressAdapter extends RecyclerView.Adapter<DeliveryAddressAdapter.DeliveryAddressViewHolder> {

    private Context context;
    private List<DeliveryAddressModelClass> deliveryAddressModelClassList;
    private boolean addressSelected = false;
    private String payableAmount;
    private ArrayList<String> cartItemIdArrayList;

    public DeliveryAddressAdapter(Context context, List<DeliveryAddressModelClass> deliveryAddressModelClassList, boolean addressSelected, String payableAmount, ArrayList<String> cartItemIdArrayList) {
        this.context = context;
        this.deliveryAddressModelClassList = deliveryAddressModelClassList;
        this.addressSelected = addressSelected;
        this.payableAmount = payableAmount;
        this.cartItemIdArrayList = cartItemIdArrayList;
    }

    @NonNull
    @Override
    public DeliveryAddressAdapter.DeliveryAddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.address_list_layout,parent,false);
        return new DeliveryAddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryAddressAdapter.DeliveryAddressViewHolder holder, int position) {
        DeliveryAddressModelClass deliveryAddressModelClass = deliveryAddressModelClassList.get(position);

        String addressId = deliveryAddressModelClass.getAddressId();

        holder.placeTextView.setText(deliveryAddressModelClass.getPlaceName());
        holder.addressTextView.setText(deliveryAddressModelClass.getAddress());
        holder.placeContactTextView.setText(deliveryAddressModelClass.getContact());

        if(!addressSelected){
            holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(context).setTitle("Remove Address").setMessage("Are you sure you want to remove:" + deliveryAddressModelClass.getPlaceName())
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    FirebaseFirestore.getInstance().collection(NodeNames.DELIVERYADDRESSES).document(addressId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(context,"Address Deleted Successfully",Toast.LENGTH_SHORT).show();
                                            if (context instanceof AddressListActivity) {
                                                ((AddressListActivity)context).getAddressList();
                                            }
                                        }
                                    });
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).setCancelable(false).show();
                }
            });
        }else {
            holder.deleteImageView.setVisibility(View.INVISIBLE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context,deliveryAddressModelClass.getPlaceName(),Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, CheckOutActivity.class);
                    intent.putExtra(NodeNames.ADDRESS,deliveryAddressModelClass.getAddress());
                    intent.putExtra(NodeNames.PLACENAME,deliveryAddressModelClass.getPlaceName());
                    intent.putExtra(NodeNames.CONTACT,deliveryAddressModelClass.getContact());
                    intent.putExtra(NodeNames.PAYABLEAMOUNT,payableAmount);
                    intent.putExtra(NodeNames.CARTITEMIDS,cartItemIdArrayList);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return deliveryAddressModelClassList.size();
    }

    public static class DeliveryAddressViewHolder extends RecyclerView.ViewHolder {

        TextView placeTextView, addressTextView, placeContactTextView;
        ImageView deleteImageView;

        public DeliveryAddressViewHolder(@NonNull View itemView) {
            super(itemView);

            placeTextView = itemView.findViewById(R.id.placeTextView);
            addressTextView = itemView.findViewById(R.id.addresTextView);
            deleteImageView = itemView.findViewById(R.id.deleteImageView);
            placeContactTextView = itemView.findViewById(R.id.placeContactTextView);
        }
    }
}
