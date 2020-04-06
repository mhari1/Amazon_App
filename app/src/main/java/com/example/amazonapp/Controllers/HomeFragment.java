package com.example.amazonapp.Controllers;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.amazonapp.Adapters.CategoryAdapter;
import com.example.amazonapp.Adapters.PopularProductAdapter;
import com.example.amazonapp.AsyncTasks.AsyncResponse;
import com.example.amazonapp.AsyncTasks.WebServiceCallGet;
import com.example.amazonapp.AsyncTasks.WebserviceCall;
import com.example.amazonapp.Helper.Config;
import com.example.amazonapp.Helper.Utils;
import com.example.amazonapp.Models.CategoryModel;
import com.example.amazonapp.Models.PopularProductModel;
import com.example.amazonapp.Models.PopularProductResponseModel;
import com.example.amazonapp.Models.ResponseModel;
import com.example.amazonapp.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */

/**
 *
 */
public class HomeFragment extends Fragment {
    RecyclerView categoryList, productList;
    List<String> titles;
    CategoryAdapter adapter;
    PopularProductAdapter popularProductAdapter;
    List<String> productName;
    List<String> imgUrl;
   Context context;
    public HomeFragment(Context context) {
        this.context=context;
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        categoryList=view.findViewById(R.id.categoryList);
        productList=view.findViewById(R.id.popularproduct);
        productName=new ArrayList<>();
        imgUrl=new ArrayList<>();String PRODUCTURL= Config.POPULAR_PRODUCT;
        new WebServiceCallGet(context, PRODUCTURL,null, "Getting Popular Product..", true, new AsyncResponse() {
            @Override
            public void onCallback(String response) {
                Log.d("response", response);
                PopularProductResponseModel model = new Gson().fromJson(response, PopularProductResponseModel.class);
                ArrayList<PopularProductModel> popularProductModels=model.getData();


                if (model.getSuccess().equals("1") ) {

                    //Toast.makeText(context, "" + response, Toast.LENGTH_SHORT).show();
                    for(PopularProductModel pm:popularProductModels)
                    {
                        productName.add(pm.getProductname());
                        imgUrl.add(pm.getImage());


                    }
                    popularProductAdapter=new PopularProductAdapter(context,productName,imgUrl);


                    GridLayoutManager gridLayoutManagerProduct = new GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false);


                    productList.setAdapter(popularProductAdapter);
                    productList.setLayoutManager(gridLayoutManagerProduct);


                } else if (model.getSuccess().equals("0")) {
                    Toast.makeText(context, "" + model.getSuccess(), Toast.LENGTH_SHORT).show();

                }
            }
        }).execute();
        String[] keys=new String[]{"CompanyId"};
        String[] values=new String[]{"1"};
        final String JSONREQUEST= Utils.createJsonRequest(keys,values);
        String URL= Config.GET_CATEGORIES;

        new WebserviceCall(getContext(), URL, JSONREQUEST, "Getting catgeories..", true, new AsyncResponse() {
            @Override
            public void onCallback(String response) {
                Log.d("response", response);
                ResponseModel model = new Gson().fromJson(response, ResponseModel.class);
                ArrayList<CategoryModel> categoryModel=model.getData();

                if (model.getSuccess().equals("1") ) {

                    //Toast.makeText(getContext(), "" + response, Toast.LENGTH_SHORT).show();

                    //Toast.makeText(getContext(), "" + response, Toast.LENGTH_SHORT).show();


                      ArrayList<String> categoryName=new ArrayList<>();

                    ArrayList<String> categoryString=new ArrayList<>();


                    for(CategoryModel cm:categoryModel)
                    {
                        categoryString.add(cm.getCategoryname());

                    }


                    //binding cardview with api data
                    adapter = new CategoryAdapter(getContext(), categoryString);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
                    categoryList.setLayoutManager(gridLayoutManager);
                    categoryList.setAdapter(adapter);

                    //will call api of popular product






                } else if (model.getSuccess().equals("0")) {
                    Toast.makeText(getContext(), "" + model.getSuccess(), Toast.LENGTH_SHORT).show();

                }
            }
        }).execute();
        return view;
        //return inflater.inflate(R.layout.fragment_home, container, false);
    }

}
