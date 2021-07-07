package io.com.didingapp.history;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

import io.com.didingapp.R;
import io.com.didingapp.Utility;
import io.com.didingapp.Volley.Singleton;
import io.com.didingapp.createbid.createBiding;
import io.com.didingapp.main.view.biding_details;

public class historyAdabter extends RecyclerView.Adapter<historyAdabter.MyViewHolder> implements Filterable {
    private ArrayList<historyModel> paths;
    private ArrayList<historyModel> pathsitem;
    private Context mContext;
    private LayoutInflater inflater;

    private Activity context;
    String price;
    int pathh;


    int flag;


    public historyAdabter(Activity context, ArrayList<historyModel> paths, int flag) {
        this.paths = paths;
        this.pathsitem = paths;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.flag = flag;


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {

        final historyModel foodModel = paths.get(i);

        //Toast.makeText(context, "hello", Toast.LENGTH_SHORT).show();

        if (flag==4){
             price = "Price : " + foodModel.getMybids();

             myViewHolder.status.setVisibility(View.GONE);

        }else {
             price = "Price : " + foodModel.getMin_bids();
            switch (Integer.parseInt(foodModel.getStatus())) {

                case 1:
                    myViewHolder.status.setText("Draft");
                    myViewHolder.status.setBackgroundResource(R.color.colorAccent);

                    break;
                case 2:
                    myViewHolder.status.setText("Publish");
                    myViewHolder.status.setBackgroundResource(R.color.green);

                    break;
                case 3:
                    myViewHolder.status.setText("stop");
                    myViewHolder.status.setBackgroundResource(R.color.red);

                    break;


            }
        }


        String startdate = "", Enddate = "";
        if (foodModel.getStart_bid_time().length() == 19) {
            startdate = "Start date : " + foodModel.getStart_bid_time().subSequence(0, 11);

        } else {
            startdate = "Start date : " + foodModel.getStart_bid_time();

        }
        if (foodModel.getEnd_bid_time().length() == 19) {
            Enddate = "End date : " + foodModel.getEnd_bid_time().subSequence(0, 11);

        } else {
            Enddate = "End date : " + foodModel.getEnd_bid_time();

        }
        //System.out.println("diaiiaiaiaiia :" + foodModel.getEnd_bid_time().subSequence(0, 11));

        if(flag==4){
            myViewHolder.price.setText(foodModel.getMybids());
        }
        myViewHolder.title.setText(foodModel.getTitle());
        myViewHolder.price.setText(price);
        myViewHolder.startdate.setText(startdate);
        myViewHolder.enddate.setText(Enddate);


        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Singleton.historyModel = null;

                Singleton.historyModel = foodModel;
                if (flag == 1) {
                    view.getContext().startActivity(new Intent(view.getContext(), biding_details.class));

                }else if(flag==2){
                    view.getContext().startActivity(new Intent(view.getContext(), createBiding.class).putExtra("isEdit", "200"));

//                    view.getContext().startActivity(new Intent(view.getContext(), biding_details.class).putExtra("history", "2"));

                }else if(flag==3){
                    view.getContext().startActivity(new Intent(view.getContext(), createBiding.class).putExtra("isEdit", "100"));

//                    view.getContext().startActivity(new Intent(view.getContext(), biding_details.class).putExtra("history", "3"));

                }else if(flag==5){
//                    view.getContext().startActivity(new Intent(view.getContext(), biding_details.class).putExtra("history", "5"));
                    view.getContext().startActivity(new Intent(view.getContext(), biding_details.class).putExtra("history", "4"));

                }else if (flag==4){

                }

            }
        });




        switch (Integer.parseInt(foodModel.getStatus())) {

            case 1:
                myViewHolder.status.setText("Draft");
                myViewHolder.status.setBackgroundResource(R.color.colorAccent);

                break;
            case 2:
                myViewHolder.status.setText("Publish");
                myViewHolder.status.setBackgroundResource(R.color.green);

                break;
            case 3:
                myViewHolder.status.setText("stop");
                myViewHolder.status.setBackgroundResource(R.color.red);

                break;


        }


    }


    @Override
    public int getItemCount() {

        return paths.size();

    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString();

                if (query.isEmpty()) {
                    paths = pathsitem;
                } else {

                    ArrayList<historyModel> filteredList = new ArrayList<>();

                    for (historyModel upcomingGuestData : pathsitem ) {

                        System.out.println("ajhfjkhajhkjh :"+upcomingGuestData.getMin_bids()+upcomingGuestData.getTitle()+upcomingGuestData.getStart_bid_time());

                        if (upcomingGuestData.getTitle().toLowerCase().contains(query) || upcomingGuestData.getMin_bids().toLowerCase().contains(query) || Utility.formatDateForDisplay(upcomingGuestData.getStart_bid_time(),"yyyy-MM-dd",""+"MMM dd, yyyy" ).replace(" ", "").toLowerCase().contains(query)) {
                            filteredList.add(upcomingGuestData);
                        }
                    }
                    paths = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = paths;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                paths = (ArrayList<historyModel>) filterResults.values;
                notifyDataSetChanged();

            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title, price, startdate, enddate, status;


        public MyViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title_his);
            price = (TextView) itemView.findViewById(R.id.price);
            startdate = (TextView) itemView.findViewById(R.id.startdate);
            enddate = (TextView) itemView.findViewById(R.id.enddate);
            status = (TextView) itemView.findViewById(R.id.status);


        }
    }
}
