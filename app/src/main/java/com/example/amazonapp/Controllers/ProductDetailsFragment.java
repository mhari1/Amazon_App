package com.example.amazonapp.Controllers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.amazonapp.AsyncTasks.AsyncResponse;
import com.example.amazonapp.AsyncTasks.WebServiceCallGet;
import com.example.amazonapp.Helper.Config;
import com.example.amazonapp.Helper.FireBaseHelper;
import com.example.amazonapp.Models.CartFireBase;
import com.example.amazonapp.Models.ProductDetail;
import com.example.amazonapp.Models.ResponseProductDetail;
import com.example.amazonapp.R;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ProductDetailsFragment extends Fragment {


    static int productId;
    static  int qty;
    static String imgURL;
    View view;
    String prodId;
    Context context;
    ResponseProductDetail responseProductDetail;
    ImageView prodImage;
    TextView prodTitle,prodDesc,itemInStock,itemPrice,itemSku;
    EditText txtQty;
    Button addtoCart;
    ElegantNumberButton numberButton;

    public ProductDetailsFragment(Context context,String prodId) {
        this.context=context;
        // Required empty public constructor
        this.prodId=prodId;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_product_details, container, false);
        prodImage=view.findViewById(R.id.productImage);
        prodTitle=view.findViewById(R.id.productTitle);
        prodDesc=view.findViewById(R.id.productDesc);
        itemInStock=view.findViewById(R.id.itemInSTock);
        itemPrice=view.findViewById(R.id.itemPrice);
        itemSku=view.findViewById(R.id.itemSku);
        numberButton=view.findViewById(R.id.number_button);
        numberButton.setOnClickListener(new ElegantNumberButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                qty =Integer.parseInt(numberButton.getNumber());
            }
        });
        /*txtQty=view.findViewById(R.id.txtQty);*/
        addtoCart=view.findViewById(R.id.addTocart);


        final SharedPreferences preferences = this.getActivity().getSharedPreferences("AmazonApp", Context.MODE_PRIVATE);
        addtoCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                   String email=preferences.getString("Email",null);
                   if(!preferences.contains("Email")){
                       Toast.makeText(getContext()," Please login ",Toast.LENGTH_SHORT);
                       AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                       builder.setTitle("Please login");
                       builder.setMessage("please login to add the products");
                       // add a button
                       builder.setPositiveButton("OK", null);
                       // create and show the alert dialog
                       AlertDialog dialog = builder.create();
                       dialog.show();
                       Intent intent = new Intent(getActivity(), MainActivity.class);
                       startActivity(intent);

                   }
                   else {
                       String customerName=preferences.getString("customerName",null);
                       String customer_id=preferences.getString("CustomerId",null);
                       FireBaseHelper fireBaseHelper=new FireBaseHelper();

                       fireBaseHelper.InsertCart(customer_id,prodId,String.valueOf(qty),prodDesc.getText().toString(),itemPrice.getText().toString(),imgURL);
                       AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                       builder.setTitle("Added ");
                       builder.setMessage("Data Added to Cart..!");
                       // add a button
                       builder.setPositiveButton("OK", null);
                       // create and show the alert dialog
                       AlertDialog dialog = builder.create();
                       dialog.show();
                      /* ArrayList<CartFireBase> cartFireBases=fireBaseHelper.ViewCart(customer_id);*/
                   }


            }
        });

        init();
        return view ;    }
    private  void init(){

        String URL= Config.PRODUCTDETAILS+prodId+"&CompanyId=1";

        new WebServiceCallGet(context, URL, null, "Getting products Details..", true, new AsyncResponse() {
            @Override
            public void onCallback(String response) {
                Log.d("response", response);
                ResponseProductDetail model = new Gson().fromJson(response, ResponseProductDetail.class);
                ArrayList<ProductDetail> productDetails=model.getData();

                if (model.getSuccess().equals("1")) {
                    //Toast.makeText(view.getContext(), "" + response, Toast.LENGTH_SHORT).show();
                    prodId=productDetails.get(0).getProductId();
                    imgURL=productDetails.get(0).getImage();
                    Picasso.with(context).load(productDetails.get(0).getImage()).resize(150, 150).centerCrop().into(prodImage);
                    prodDesc.setText(productDetails.get(0).getDescription());
                    prodTitle.setText(productDetails.get(0).getDescription());
                    itemInStock.setText(productDetails.get(0).getAvailableQty());
                    itemPrice.setText(productDetails.get(0).getRetailPrice());
                    itemSku.setText(productDetails.get(0).getSKU());
                } else if (model.getSuccess().equals("0")) {
                    Toast.makeText(view.getContext(), "" + model.getSuccess(), Toast.LENGTH_SHORT).show();

                }
            }
        }).execute();


    }


}
