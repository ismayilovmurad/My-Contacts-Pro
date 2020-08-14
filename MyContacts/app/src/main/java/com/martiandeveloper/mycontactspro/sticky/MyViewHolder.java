package com.martiandeveloper.mycontactspro.sticky;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.martiandeveloper.mycontactspro.R;

class MyViewHolder extends RecyclerView.ViewHolder {

    LinearLayout recycler_row;
    TextView rowTV;
    TextView rowTV2;

    public MyViewHolder(View itemView) {
        super(itemView);
        recycler_row = itemView.findViewById(R.id.recycler_row);
        rowTV = itemView.findViewById(R.id.rowTV);
        rowTV2 = itemView.findViewById(R.id.rowTV2);
    }
}
