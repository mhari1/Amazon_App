package com.example.amazonapp.Controllers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.amazonapp.AsyncTasks.AsyncResponse;
import com.example.amazonapp.AsyncTasks.WebServiceCallGet;
import com.example.amazonapp.AsyncTasks.WebserviceCall;
import com.example.amazonapp.Helper.Config;
import com.example.amazonapp.Helper.Utils;
import com.example.amazonapp.Models.CountryModel;
import com.example.amazonapp.Models.CountryResponseModel;
import com.example.amazonapp.Models.ProfilePageModel;
import com.example.amazonapp.Models.ProfileResponseModel;
import com.example.amazonapp.Models.ProvinceModel;
import com.example.amazonapp.Models.ProvinceResponseModel;
import com.example.amazonapp.Models.ResponseSignupModel;
import com.example.amazonapp.R;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;
import static com.example.amazonapp.Controllers.SignUpActivity.countryId;
import static com.example.amazonapp.Controllers.SignUpActivity.provinceId;


public class ProfilePage extends Fragment implements Spinner.OnItemSelectedListener , View.OnClickListener {


    View view;
    Button btn_edit, btn_save;
    EditText edit_fname, edit_lname, edit_email, edit_cnumber, edit_pcode, sp_city, edit_address;
    Spinner sp_ctry, sp_province;
    Context context;
    ProfileResponseModel profileModelmodel;
    public static String countryId, provinceId;

    public ProfilePage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        return view;
    }


    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        btn_edit = view.findViewById(R.id.btn_update);
        btn_save = view.findViewById(R.id.btn_savechanges);
        edit_fname = view.findViewById(R.id.firstName_pr);

        edit_lname = view.findViewById(R.id.lastName_pr);
        edit_email = view.findViewById(R.id.emailaddress_pr);
        edit_cnumber = view.findViewById(R.id.contactnumber_pr);
        edit_pcode = view.findViewById(R.id.postalcode_pr);
        edit_address = view.findViewById(R.id.address_pr);
        sp_city = view.findViewById(R.id.city_pr);

        //Spinner
        sp_ctry = view.findViewById(R.id.country_pr);
        sp_ctry.setOnItemSelectedListener(this);
        sp_province = view.findViewById(R.id.province_pr);

        edit_fname.setEnabled(false);
        btn_edit.setText("Edit");
        btn_edit.setOnClickListener(this);
        btn_save.setOnClickListener(this);

        //calling API for country Spinner(Drop Down List)
        final String countery = Config.GET_COUNTERY;

        new WebServiceCallGet(getContext(), countery, null, "Getting Countery..", true, new AsyncResponse() {
            @Override
            public void onCallback(String response) {
                Log.d("response", response);
                CountryResponseModel model = new Gson().fromJson(response, CountryResponseModel.class);
                ArrayList<CountryModel> countryModels = model.getData();
                // HashMap<Integer, String> P_Hash=new HashMap<Integer, String>();


                if (model.getSuccess().equals("1")) {
                    // Toast.makeText(SignUpActivity.this, "" + response, Toast.LENGTH_SHORT).show();
                    ArrayList<String> counteryString = new ArrayList<>();
                    for (CountryModel cm : countryModels) {
                        counteryString.add(cm.getName());
                        /*counterInt.add(cm.getCountryId());
                        P_Hash.put(Integer.parseInt(cm.getCountryId()),cm.getName());*/
                    }
                    //ArrayAdapter<HashMap<Integer, String>> bindCategory = new ArrayAdapter<HashMap<Integer,String>>(SignUpActivity.this, android.R.layout.simple_spinner_item);
                    ArrayAdapter<String> bindCategory = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, counteryString);
                    //bindCategory.add(P_Hash);
                    bindCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    sp_ctry.setAdapter(bindCategory);
                } else if (model.getSuccess().equals("0")) {
                    Toast.makeText(getContext(), "" + model.getSuccess(), Toast.LENGTH_SHORT).show();

                }
            }
        }).execute();


        SharedPreferences preferences = this.getActivity().getSharedPreferences("AmazonApp", Context.MODE_PRIVATE);
        final String userId = preferences.getString("CustomerId", null);
        final String token = preferences.getString("Token", null);
        if (userId != null) {
            String[] keys = new String[]{"userId"};
            String[] values = new String[]{userId};
            final String JSONREQUEST = Utils.createJsonRequest(keys, values);
            final String PROFILEURL = Config.GETPROFILE + userId;
            String header = "Bearer " + token;

            new WebServiceCallGet(getContext(), PROFILEURL, JSONREQUEST, header, "Getting Profile..", true, new AsyncResponse() {
                @Override
                public void onCallback(String response) {
                    Log.d("response", response);
                    ProfileResponseModel model = new Gson().fromJson(response, ProfileResponseModel.class);
                    ArrayList<ProfilePageModel> profilePageModels = model.getData();

                    if (model.getSuccess().equals("1")) {
                        // Toast.makeText(getContext(), "" + response, Toast.LENGTH_SHORT).show();
                        edit_fname.setText(profilePageModels.get(0).getFname());
                        edit_lname.setText(profilePageModels.get(0).getLname());
                        edit_email.setText(profilePageModels.get(0).getEmail());
                        edit_cnumber.setText(profilePageModels.get(0).getPhoneNumber());
                        sp_city.setText(profilePageModels.get(0).getCity());
                        edit_pcode.setText(profilePageModels.get(0).getPostalCode());
                        edit_address.setText(profilePageModels.get(0).getAddress());


                    } else if (model.getSuccess().equals("0")) {
                        Toast.makeText(view.getContext(), "" + model.getSuccess(), Toast.LENGTH_SHORT).show();
                    }
                }
            }).execute();
            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    registerUsers();
                }


            });
        }
    }


    @Override
    public void onItemSelected (AdapterView < ? > parent, View view,int position, long id){

        int counId = sp_ctry.getSelectedItemPosition() + 1;
        countryId = String.valueOf(counId);
        String[] keys = new String[]{"country_id"};
        String[] values = new String[]{countryId};
        final String JSONREQUEST = Utils.createJsonRequest(keys, values);
        String URL = Config.PROVINCE;
        new WebserviceCall(getContext(), URL, JSONREQUEST, "Getting catgeories..", true, new AsyncResponse() {
            @Override
            public void onCallback(String response) {
                Log.d("response", response);
                ProvinceResponseModel model = new Gson().fromJson(response, ProvinceResponseModel.class);
                ArrayList<ProvinceModel> provinceModels = model.getData();

                if (model.getSuccess().equals("1")) {
                    //Toast.makeText(getContext(), "" + response, Toast.LENGTH_SHORT).show();

                    /*  ArrayList<String> categoryName=new ArrayList<>();*/

                    ArrayList<String> provinceString = new ArrayList<>();


                    for (ProvinceModel pm : provinceModels) {
                        provinceString.add(pm.getName());

                    }

                    ArrayAdapter<String> bindCategory = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, provinceString);
                    bindCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    sp_province.setAdapter(bindCategory);
                    //binding cardview with api data

                } else if (model.getSuccess().equals("0")) {
                    Toast.makeText(getContext(), "" + model.getSuccess(), Toast.LENGTH_SHORT).show();

                }
            }
        }).execute();

        int provId = sp_province.getSelectedItemPosition() + 1;
        provinceId = String.valueOf(provId);
    }

    @Override
    public void onNothingSelected (AdapterView < ? > parent){

    }




    @Override
    public void onClick (View view){

        switch (view.getId()) {
            case R.id.btn_update:  //Your edit button
                enableDisableEditText();
                break;

            default:
                break;
        }
    }



    //method for enable or disable edittext


    private void enableDisableEditText () {

        edit_fname.setFocusableInTouchMode(true);
        edit_lname.setFocusableInTouchMode(true);
        edit_email.setFocusableInTouchMode(true);
        edit_cnumber.setFocusableInTouchMode(true);
        sp_ctry.setFocusableInTouchMode(true);
        sp_city.setFocusableInTouchMode(true);
        sp_province.setFocusableInTouchMode(true);
        edit_pcode.setFocusableInTouchMode(true);
        edit_address.setFocusableInTouchMode(true);
        if (edit_fname.isEnabled()) {
            edit_fname.setEnabled(false);
            ;
            // btn_edit.setText("Edit");
        } else {
            edit_fname.setEnabled(true);

            // btn_edit.setText("Done");
        }
    }


    public void registerUsers (){

        final String get_firstname = edit_fname.getText().toString().trim();
        final String get_lastname = edit_lname.getText().toString().trim();
        final String get_email = edit_email.getText().toString().trim();
        final String get_contactno = edit_cnumber.getText().toString().trim();
        final String get_city = sp_city.getText().toString().trim();
        final String get_postalcode = edit_pcode.getText().toString().trim();
        final String get_address = edit_address.getText().toString().trim();


        //first we will do the validations

        if (TextUtils.isEmpty(get_firstname)) {
            edit_fname.setError("Please enter your first name");
            edit_fname.requestFocus();
            return;
        }

        else if (TextUtils.isEmpty(get_lastname)) {
            edit_lname.setError("Please enter your last name");
            edit_lname.requestFocus();
            return;
        }

        else if (!Patterns.EMAIL_ADDRESS.matcher(get_email).matches()) {
            edit_email.setError("Enter a valid email");
            edit_email.requestFocus();
            return;
        }

        else if(TextUtils.isEmpty(get_city)){
            sp_city.setError("Enter city plz");
            sp_city.requestFocus();
            return;
        }


        else if (TextUtils.isEmpty(get_contactno)) {
            edit_cnumber.setError("Enter a contact number");
            edit_cnumber.requestFocus();
            return;
        }

        else if (TextUtils.isEmpty(get_postalcode)) {
            edit_pcode.setError("Enter a contact number");
            edit_pcode.requestFocus();
            return;
        }

        else if (TextUtils.isEmpty(get_postalcode)) {
            edit_pcode.setError("Enter a contact number");
            edit_pcode.requestFocus();
            return;
        }

        else if (TextUtils.isEmpty(get_address)) {
            edit_address.setError("Enter a address");
            edit_address.requestFocus();
            return;
        }

        else {
            String[] keys1=new String[]{"Fname","Lname","email","phonenumber","countryid","provinceid","city","postal","address","CompanyId"};
            String[] values1=new String[]{get_firstname,get_lastname,get_email,get_contactno, countryId, provinceId,get_city,get_postalcode,get_address, "1"};
            String jsonReq= Utils.createJsonRequest(keys1,values1);

            //variable for calling api
            String URL_CREATE_USER= Config.POSTPROFILE;

            new WebserviceCall(getContext(), URL_CREATE_USER, jsonReq, "Save Changes..!!", true, new AsyncResponse() {
                @Override
                public void onCallback(String response) {
                    profileModelmodel  = new Gson().fromJson(response, ProfileResponseModel.class);
                    if (profileModelmodel.getSuccess().equals("1")) {
                        Toast.makeText(getActivity(), ""+profileModelmodel.getMessage(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                    }
                    else if (profileModelmodel.getSuccess().equals("0") ) {
                        Toast.makeText(getActivity(), ""+profileModelmodel.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }).execute();
        }
    }
}