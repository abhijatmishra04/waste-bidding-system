package io.com.didingapp.history;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.com.didingapp.R;
import io.com.didingapp.Volley.Singleton;
import io.com.didingapp.Volley.VolleyApi;


public class historyActivity extends AppCompatActivity implements VolleyApi.ResponseListener {
    View convertView;
    ArrayList<historyModel> foodModels = new ArrayList<>();
    RecyclerView recyclerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        getSupportActionBar().setTitle("History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);

        getData();

    }


    public  void getData(){
        foodModels.clear();
//        VolleyApi.getInstance().gethistory(this,this, Singleton.id);

        System.out.println("et dataag");
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);

        return true;
    }
    @Override
    public void _onResponseError(Throwable e) {

    }

    @Override
    public void _onNext(String obj) {
        try {

            JSONObject obj1 = new JSONObject(obj);
            JSONArray jArray = obj1.getJSONArray("data");
            //int len = jArray.length();
            for (int i = 0; i < jArray.length(); i++) {

                JSONObject json_data = jArray.getJSONObject(i);
                foodModels.add(new historyModel(
                        ""+json_data.getString("title"),
                        ""+json_data.getString("min_bids"),
                        ""+json_data.getString("start_bid_time"),
                        ""+json_data.getString("end_bid_time"),
                        ""+json_data.getString("status"),
                        ""+json_data.getString("cat_id"),
                        ""+json_data.getString("description"),
                        ""+json_data.getString("id")



                ));




            }

            historyAdabter foodAdapter = new historyAdabter(this,foodModels,2);
            recyclerView.setAdapter(foodAdapter);


        } catch (
                JSONException e) {
            e.printStackTrace();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);

    }
}

