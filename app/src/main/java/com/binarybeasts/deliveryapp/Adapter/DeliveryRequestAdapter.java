package com.binarybeasts.deliveryapp.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.binarybeasts.deliveryapp.MainActivity;
import com.binarybeasts.deliveryapp.Model.DeliveryRequests;
import com.binarybeasts.deliveryapp.R;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class DeliveryRequestAdapter extends RecyclerView.Adapter<DeliveryRequestAdapter.MyViewHolder>{
    ArrayList<DeliveryRequests> list;
    ClickHandler clickHandler;
    public static interface ClickHandler{
        public void accept(int position);
        public void decline(int position);
    }

    public DeliveryRequestAdapter(ArrayList<DeliveryRequests> list, MainActivity mainActivity) {
        clickHandler=mainActivity;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_order_request,parent,false);
        MyViewHolder myViewHolder=new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.to.setText(list.get(position).getCustomerAddress());
        holder.from.setText(list.get(position).getFarmersAddress());
        holder.amount.setText(list.get(position).getAmount());

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickHandler.accept(position);
            }
        });
        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickHandler.decline(position);
            }
        });

    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView from,to,amount;
        Button accept,decline;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            from=itemView.findViewById(R.id.from);
            to=itemView.findViewById(R.id.to);
            amount=itemView.findViewById(R.id.amount);

            accept=itemView.findViewById(R.id.accept);
            decline=itemView.findViewById(R.id.decline);
        }
    }
}
