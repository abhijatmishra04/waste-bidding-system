package io.com.didingapp.category;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;

import io.com.didingapp.R;
import io.com.didingapp.Volley.Singleton;

import static android.app.Activity.RESULT_OK;


public class categoryAdabter extends RecyclerView.Adapter<categoryAdabter.MyViewHolder>{
    private ArrayList<catModel> paths;
    private Activity context;


    public categoryAdabter(Activity context, ArrayList<catModel> paths) {
        this.paths = paths;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cat, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {
        final catModel foodModel = paths.get(i);
        myViewHolder.title.setText(foodModel.getName());
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Singleton.category= myViewHolder.title.getText().toString();
                Singleton.cat_id = foodModel.getId();
                context.setResult(RESULT_OK, intent);
                context.finish();
                context.overridePendingTransition(R.anim.right_in, R.anim.right_out);


            }
        });
    }



    @Override
    public int getItemCount() {
        return paths.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title,id;


        public MyViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.tv_name);


        }
    }
}
