package com.example.amazonapp.Controllers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amazonapp.AsyncTasks.AsyncResponse;
import com.example.amazonapp.AsyncTasks.WebServiceCallGet;
import com.example.amazonapp.Helper.Config;
import com.example.amazonapp.Models.ContactModel;
import com.example.amazonapp.Models.ContactResponseModel;
import com.example.amazonapp.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class ContactFragment extends Fragment {
/*
*Contact Us page binded with getCompaydetails by comapny id API
 */
    Context context;

    TextView placename, companywebsite,placeAddress1,placeAddress2,city,postalcode;
    ImageView logoimg;
    Button mapBtn;

    // Required empty public constructor
    public ContactFragment(Context context)
    {
        this.context=context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        placename = view.findViewById(R.id.placeName);
        companywebsite=view.findViewById(R.id.companywebsite);
        logoimg=view.findViewById(R.id.placeImage);
        placeAddress1=view.findViewById(R.id.placeAddress1);
        placeAddress2=view.findViewById(R.id.placeAddress2);
        city=view.findViewById(R.id.city);
        postalcode=view.findViewById(R.id.postalcode);
        mapBtn=view.findViewById(R.id.mapButton);



        new WebServiceCallGet(context, Config.COMPANYDETAIL,null, "Getting Comapany address..", true, new AsyncResponse() {
            @Override
            public void onCallback(String response) {
                Log.d("response", response);

                ContactResponseModel model = new Gson().fromJson(response, ContactResponseModel.class);
                ContactModel contactModel=model.getData();

                if (model.getSuccess().equals("1") ) {
                    Toast.makeText(context, "" + response, Toast.LENGTH_SHORT).show();

                    placename.setText(contactModel.getCompany_name());
                    companywebsite.setText(contactModel.getWebsite());
                    Picasso.with(context).load(contactModel.getLogo()).into(logoimg);
                    placeAddress1.setText(contactModel.getAddress1());
                    placeAddress2.setText(contactModel.getAddress2());
                    city.setText(contactModel.getCity());
                    postalcode.setText(contactModel.getPostalCode());


                } else if (model.getSuccess().equals("0")) {
                    Toast.makeText(context, "" + model.getSuccess(), Toast.LENGTH_SHORT).show();

                }
            }
        }).execute();

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?q=loc:%f,%f", 45.5256395, -73.5512917);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);

            }
        });
        return view;


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
