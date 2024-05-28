package com.roaaserver.placementstudent.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.roaaserver.placementstudent.Models.RequestInfoClass;
import com.roaaserver.placementstudent.R;
import com.roaaserver.placementstudent.RequestDetailActivity;

import java.util.ArrayList;

public class OfflineRequestAdapter extends RecyclerView.Adapter<OfflineRequestAdapter.ViewHolder> {
    ArrayList<RequestInfoClass> requestList;
    Context context;

    public OfflineRequestAdapter(Context context, ArrayList<RequestInfoClass> requestList) {
        this.context = context;
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_layout, parent, false);
        return new OfflineRequestAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //holder.receiverId.setText(requestList.get(position).getId());
        holder.name.setText(requestList.get(position).getName());
        holder.branch.setText(requestList.get(position).getDepartment());
        holder.modifyCount.setText(String.valueOf(requestList.get(position).getModifyCount()));
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, branch, modifyCount;
        public LinearLayout requestLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            branch = itemView.findViewById(R.id.branch);
            modifyCount = itemView.findViewById(R.id.modify_count);
            requestLayout = itemView.findViewById(R.id.requests_cardview);
        }
    }
}
