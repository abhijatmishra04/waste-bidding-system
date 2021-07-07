package io.com.didingapp.category;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.com.didingapp.R;
import io.com.didingapp.Volley.VolleyApi;

public class Selectcategory extends AppCompatActivity implements VolleyApi.ResponseListener {
    View convertView;
    ArrayList<catModel> foodModels = new ArrayList<>();
    RecyclerView recyclerView;
    public static Selectcategory food;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        food=this;
        setContentView(R.layout.select_category);
        recyclerView = (RecyclerView)findViewById(R.id.rv_services);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);

        getData();

    }

   /*public void finish(){
        Intent intent = getIntent();
       setResult(Activity.RESULT_OK, intent);
       finish();
   }*/

    public  void getData(){
        foodModels.clear();
        VolleyApi.getInstance().category(this,this);

        System.out.println("et dataag");
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
                foodModels.add(new catModel(
                        ""+json_data.getString("name"),
                        ""+json_data.getString("id")
                ));


            }

            categoryAdabter foodAdapter = new categoryAdabter(this,foodModels);
            recyclerView.setAdapter(foodAdapter);


        } catch (
                JSONException e) {
            e.printStackTrace();

        }
    }
}
