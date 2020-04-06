package com.example.amazonapp.Controllers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.amazonapp.Adapters.CartAdapter;
import com.example.amazonapp.Adapters.OrderHistoryAdapter;
import com.example.amazonapp.AsyncTasks.AsyncResponse;
import com.example.amazonapp.AsyncTasks.WebserviceCall;
import com.example.amazonapp.Helper.Config;
import com.example.amazonapp.Helper.FireBaseHelper;
import com.example.amazonapp.Models.CartFireBase;
import com.example.amazonapp.Models.CartModel;
import com.example.amazonapp.Models.LoginModel;
import com.example.amazonapp.Models.LoginResponseModel;
import com.example.amazonapp.Models.PurchaseCartModel;
import com.example.amazonapp.Models.PurchaseItem;
import com.example.amazonapp.Models.ResponseProductDetail;
import com.example.amazonapp.Models.ResponsePurchaseItems;
import com.example.amazonapp.Models.SuccessModel;
import com.example.amazonapp.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */

public class CartFragment extends Fragment {

    public RecyclerView recyclerView_cart;
    private TextView totalItems;
    private TextView totalShippingCharges;
    private TextView totalItemsAmount;
    Button btnpurchase;
    static ArrayList<CartFireBase> responseCartFireBase;
    //Objects for api call
    private RequestQueue mQueue;

    public CartFragment() {
        // Required empty public constructor
    }
/*
* Binded Cart data from the firebase database
* To show all cart data it will call Cart Adapter
 */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        SharedPreferences preferences = this.getActivity().getSharedPreferences("AmazonApp", Context.MODE_PRIVATE);
        final String customerId=preferences.getString("CustomerId",null);
        final String token=preferences.getString("Token",null);
        if(customerId!=null) {
            mQueue= Volley.newRequestQueue(getContext());
            View v = inflater.inflate(R.layout.fragment_cart, container, false);


            recyclerView_cart = v.findViewById(R.id.recylerViewCart);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView_cart.setLayoutManager(layoutManager);
            totalItems = v.findViewById(R.id.total_items);
            totalShippingCharges = v.findViewById(R.id.total_shipping_charges);
            totalItemsAmount = v.findViewById(R.id.total_price);
            btnpurchase=v.findViewById(R.id.btn_purchase);
            btnpurchase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //binding with API and conforming the purchase order

                    ArrayList<PurchaseItem> itemsPurchase=new ArrayList<>();
                    PurchaseItem item=null;
                    ResponsePurchaseItems responsePurchaseItems=new ResponsePurchaseItems();

                    double mainTotal=0;
                    for(int i=0;i<responseCartFireBase.size();i++) {
                        item = new PurchaseItem();
                        item.setProductid(responseCartFireBase.get(i).getProduct_id());
                        item.setQty(responseCartFireBase.get(i).getProductQuantity());
                        item.setPrice(responseCartFireBase.get(i).getProductPrice());
                        double qty = Double.parseDouble(responseCartFireBase.get(i).getProductQuantity());
                        double price = Double.parseDouble(responseCartFireBase.get(i).getProductPrice());
                        double totalPrice = qty * price;
                        item.setSubtotal(Double.toString(totalPrice));
                        itemsPurchase.add(item);
                        mainTotal += totalPrice;
                    }
                    responsePurchaseItems.setCustomerid(customerId);
                    responsePurchaseItems.setTotal(Double.toString(mainTotal));
                    responsePurchaseItems.setProduct(itemsPurchase);
                    String jsonInString = new Gson().toJson(responsePurchaseItems);

                    String header="Bearer "+token;
                    new WebserviceCall(getContext(), Config.PURCHASEPRODUCT, jsonInString, header,"sending purchase request...", true, new AsyncResponse() {
                        @Override
                        public void onCallback(String response) {
                            Log.d("myapp", response);
                            SuccessModel model=new Gson().fromJson(response,SuccessModel.class);
                            //  Toast.makeText(LoginActivity.this,model.getMessage() , Toast.LENGTH_SHORT).show();

                            if (model.getSuccess().equals("1")) {
                                FireBaseHelper fireBaseHelper=new FireBaseHelper();
                                ArrayList<String> array_snapId=new ArrayList<>();
                                for(int i=0;i<responseCartFireBase.size();i++){
                                    array_snapId.add(responseCartFireBase.get(i).getSnapId());
                                }
                                fireBaseHelper.updatePurchaseOrder(array_snapId);
                                Toast.makeText(getContext(), ""+model.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                            else if (model.getSuccess().equals("0") ) {
                                Toast.makeText(getContext(), ""+model.getMessage(), Toast.LENGTH_SHORT).show();
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Error");
                                builder.setMessage("Opps Error "+model.getMessage());
                                // add a button
                                builder.setPositiveButton("OK", null);
                                // create and show the alert dialog
                                AlertDialog dialog = builder.create();
                                dialog.show();

                            }
                        }
                    }).execute();




                }
            });

            FireBaseHelper fireBaseHelper = new FireBaseHelper();


            fireBaseHelper.ViewCart(customerId, recyclerView_cart,totalItems,totalShippingCharges,totalItemsAmount);

            return v;

        }
        else {
            Toast.makeText(getContext()," Please login ",Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getActivity(), LoginActivity.class);
            startActivity(i);
            ((Activity) getActivity()).overridePendingTransition(0, 0);
            View main = inflater.inflate(R.layout.activity_login, container, false);
            return main;
        }
    }
    public  void callCartAdapter(ArrayList<CartFireBase>  _adapterCart,RecyclerView _recyclerView_cart,TextView _totalItemsn,TextView _totalShippingCharges,TextView _totalItemsAmount){

//Call back method for displaying cart again
        ArrayList<CartModel> cartModel=new ArrayList<>();
        int count=0;
        responseCartFireBase=_adapterCart;
        CartModel c=null;
        double totalPrice=0;
        int totalItem=0;
        String shippingCharges="10";
        PurchaseCartModel purchaseCartModel=new PurchaseCartModel();
        if(responseCartFireBase.size()==0){
           /* AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("No Products ");
            builder.setMessage("No Products Found in cart");
            // add a button
            builder.setPositiveButton("OK", null);
            // create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.show();*/
           /* Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);*/
        }
        else {


            for (CartFireBase cm : _adapterCart) {
                c = new CartModel();
                c.setProductQuantity(cm.getProductQuantity());
                c.setProductPrice(cm.getProductPrice());
                c.setProductImage(cm.getProductImage());
                c.setProduct_name(cm.getProduct_name());
                c.setSnapId(cm.getSnapId());
                totalPrice += Double.parseDouble(cm.getProductPrice());
                totalItem += Integer.parseInt(cm.getProductQuantity());
                cartModel.add(c);
                c.setType(0);
            }

            purchaseCartModel.setShippingCharges(shippingCharges);
            purchaseCartModel.setTotalItem(Integer.toString(totalItem));
            purchaseCartModel.setTotalPrice(Double.toString(totalPrice));


            String totalItemText = purchaseCartModel.getTotalItem();
            String totalShippingChargesText = purchaseCartModel.getShippingCharges();
            String totalAmountText = purchaseCartModel.getTotalPrice();

            _totalItemsn.setText(totalItemText);
            _totalShippingCharges.setText(totalShippingChargesText);
            _totalItemsAmount.setText(totalAmountText);
            CartAdapter cartAdapter = new CartAdapter(cartModel, getContext());
            _recyclerView_cart.setAdapter(cartAdapter);
            cartAdapter.notifyDataSetChanged();
            _recyclerView_cart.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }

}