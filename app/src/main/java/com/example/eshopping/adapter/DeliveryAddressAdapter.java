package com.example.eshopping.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eshopping.R;
import com.example.eshopping.model.DeliveryAddressModelClass;

import java.util.List;

public class DeliveryAddressAdapter extends RecyclerView.Adapter<DeliveryAddressAdapter.DeliveryAddressViewHolder> {

    private Context context;
    private List<DeliveryAddressModelClass> deliveryAddressModelClassList;

    public DeliveryAddressAdapter(Context context, List<DeliveryAddressModelClass> deliveryAddressModelClassList) {
        this.context = context;
        this.deliveryAddressModelClassList = deliveryAddressModelClassList;
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


        Double latitude = deliveryAddressModelClass.getLatitude();
        Double longitude = deliveryAddressModelClass.getLongitude();
    }

    @Override
    public int getItemCount() {
        return deliveryAddressModelClassList.size();
    }

    public static class DeliveryAddressViewHolder extends RecyclerView.ViewHolder {

        TextView placeTextView, addressTextView, placeContactTextView;
        ImageView locationImageView;

        public DeliveryAddressViewHolder(@NonNull View itemView) {
            super(itemView);

            placeTextView = itemView.findViewById(R.id.placeTextView);
            addressTextView = itemView.findViewById(R.id.addresTextView);
            locationImageView = itemView.findViewById(R.id.locationImageView);
            placeContactTextView = itemView.findViewById(R.id.placeContactTextView);
        }
    }
}
