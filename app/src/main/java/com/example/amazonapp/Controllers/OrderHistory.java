package com.example.amazonapp.Controllers;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.amazonapp.Adapters.OrderHistoryAdapter;
import com.example.amazonapp.AsyncTasks.AsyncResponse;
import com.example.amazonapp.AsyncTasks.WebServiceCallGet;
import com.example.amazonapp.AsyncTasks.WebserviceCall;
import com.example.amazonapp.Helper.Config;
import com.example.amazonapp.Helper.Utils;
import com.example.amazonapp.Models.OrderHistoryModel;
import com.example.amazonapp.Models.OrderHistoryResponse;
import com.example.amazonapp.R;
import com.google.gson.Gson;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrderHistory extends Fragment {

    RecyclerView recyclerView;



    public OrderHistory() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_order_history, container, false);
        recyclerView = (RecyclerView)v.findViewById(R.id.recylerView);

/*

        s1 = getResources().getStringArray(R.array.clothes_names);
        s2 = getResources().getStringArray(R.array.clothes_description);
*/
        final SharedPreferences preferences = this.getActivity().getSharedPreferences("AmazonApp", Context.MODE_PRIVATE);
        String customer_id=preferences.getString("CustomerId",null);
        final String token=preferences.getString("Token",null);
        String[] keys=new String[]{"CustomerId"};
        String[] values=new String[]{customer_id};
        String jsonReq= Utils.createJsonRequest(keys,values);
        String header="Bearer "+token;
        final ArrayList<OrderHistory> orderHistories;
        new WebserviceCall(getContext(), Config.ORDERHISTORY, jsonReq,header, "Getting Order history Details..", true, new AsyncResponse() {
            @Override
            public void onCallback(String response) {
                Log.d("response", response);
                OrderHistoryResponse model = new Gson().fromJson(response, OrderHistoryResponse.class);
                ArrayList<OrderHistoryModel> orders =model.getData();

                if (model.getSuccess().equals("1")) {
                    if(orders.size()==0){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("No Orders");
                        builder.setMessage("No Orders till date Found");
                        // add a button
                        builder.setPositiveButton("OK", null);
                        // create and show the alert dialog
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    }
                    ArrayList<String> imgUrl=new ArrayList<>();
                    ArrayList<String> qty=new ArrayList<>();
                    ArrayList<String> title=new ArrayList<>();
                    Toast.makeText(getContext(), "" + response, Toast.LENGTH_SHORT).show();
                    for(OrderHistoryModel om:orders)
                    {
                            imgUrl.add(om.getImage());
                            qty.add(om.getTotal());
                            title.add(om.getProduct_name());


                    }
                    OrderHistoryAdapter myAdapter =  new OrderHistoryAdapter(getContext(), imgUrl,qty,title);
                    recyclerView.setAdapter(myAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                } else if (model.getSuccess().equals("0")) {
                    Toast.makeText(getContext(), "" + model.getSuccess(), Toast.LENGTH_SHORT).show();

                }
            }
        }).execute();


        // Inflate the layout for this fragment
        return v;
    }

}
