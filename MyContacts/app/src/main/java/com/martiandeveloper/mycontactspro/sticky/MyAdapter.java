package com.martiandeveloper.mycontactspro.sticky;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.martiandeveloper.mycontactspro.R;
import com.martiandeveloper.mycontactspro.auth.MainActivity;
import com.martiandeveloper.mycontactspro.feed.InfoActivity;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    Context mContext;
    ArrayList<String> mList;
    ArrayList<String> mList2;
    String type;

    public MyAdapter(Context mContext, ArrayList<String> mList, ArrayList<String> mList2, String type) {
        this.mContext = mContext;
        this.mList = mList;
        this.mList2 = mList2;
        this.type = type;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater
                .from(mContext).inflate(R.layout.recycler_row, viewGroup, false);

        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, final int position) {
        try {
            viewHolder.rowTV.setText(mList.get(position));
            viewHolder.rowTV2.setText(mList2.get(position));
            viewHolder.recycler_row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, InfoActivity.class);
                    intent.putExtra("title",mList.get(position));
                    intent.putExtra("type",type);
                    mContext.startActivity(intent);
                    ((Activity)mContext).finish();
                }
            });
            ((Activity) mContext).registerForContextMenu(viewHolder.recycler_row);
        }
        catch (Exception e){
            Log.d("Error: ",e.getLocalizedMessage());
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
