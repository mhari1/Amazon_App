package com.example.amazonapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.amazonapp.Controllers.CartFragment;
import com.example.amazonapp.Controllers.LoginActivity;
import com.example.amazonapp.Controllers.ProdFragment;
import com.example.amazonapp.Helper.FireBaseHelper;
import com.example.amazonapp.Models.CartFireBase;
import com.example.amazonapp.Models.CartModel;
import com.example.amazonapp.Models.PurchaseCartModel;
import com.example.amazonapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter {
/*
*To display all the cart items that will bind multiple times with cart layout and will be displyed through Adapter
*
 */
    private List<CartModel> cartItemModelList;
    PurchaseCartModel purchaseCartModel;
    Context context;

    //constructor
    public CartAdapter(List<CartModel> cartItemModelList , Context context) {
        this.cartItemModelList = cartItemModelList;
        this.context = context;
        this.purchaseCartModel=purchaseCartModel;
    }

    // three methods

    @Override
    public int getItemViewType(int position) {


        return CartModel.CART_ITEM;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View cartItemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_item_layout, viewGroup, false);
        return  new CartItemViewHolder(cartItemView);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        String resource = cartItemModelList.get(position).getProductImage();
        String title = cartItemModelList.get(position).getProduct_name();
        String price = cartItemModelList.get(position).getProductPrice();
        String quantity = cartItemModelList.get(position).getProductQuantity();

        //
        String snapId = cartItemModelList.get(position).getSnapId();
        ((CartItemViewHolder) viewHolder).setItemDetails(resource, title, price, quantity, snapId);



    }
/*
    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        Message message = dataSnapshot.getValue(Message.class);

        for (int i = 0; i < messagesList.size(); i++) {
            if (messagesList.get(i).getMessageID().equals(message.getMessageID())) {
                messagesList.set(i, message);
                adapter.notifyItemChanged(i);
                break;
            }
        }
    }*/
    @Override
    public int getItemCount() {
        return cartItemModelList.size();
    }

    class CartItemViewHolder extends RecyclerView.ViewHolder{

        Button btnRemove;
        private ImageView productImage;
        private TextView productTitle;
        private TextView productPrice;
        private ElegantNumberButton productQuantity;
        private TextView hiddenSnapId;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.product_title);
            productPrice = itemView.findViewById(R.id.product_price);
            productQuantity = itemView.findViewById(R.id.product_quantity);
            hiddenSnapId=itemView.findViewById(R.id.snapShotId);
            btnRemove =itemView.findViewById(R.id.btnCartRemove);
            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // Toast.makeText(v.getContext(),hiddenSnapId.getText().toString(),Toast.LENGTH_SHORT).show();
                    Log.v("Show id",hiddenSnapId.getText().toString());
                    FireBaseHelper helper=new FireBaseHelper();
                    helper.removeItem(hiddenSnapId.getText().toString());
                    Toast.makeText(v.getContext(),"Item removed successfully..!!",Toast.LENGTH_SHORT).show();
                    /*FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(this).attach(this).commit();*/
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    CartFragment cartFragment = new CartFragment();
                    FragmentTransaction ft= activity.getSupportFragmentManager().beginTransaction();
                    ft.detach(cartFragment).attach(cartFragment).commit();
                    /*
                    *there is one bug here on click of remove its remove from the database but
                    * its not directly reflected to this page and same for the purchase total part
                     */
                }
            });

            productQuantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener(){

                @Override
                public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                    String qty=productQuantity.getNumber();
                    FireBaseHelper helper=new FireBaseHelper();
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    CartFragment cartFragment = new CartFragment();
                    FragmentTransaction ft= activity.getSupportFragmentManager().beginTransaction();
                    ft.detach(cartFragment).attach(cartFragment).commit();
                   /* Toast.makeText(getContext(),"Quantity updated successfully..!!",Toast.LENGTH_SHORT).show();*/
                    /*On update of any item it will update to the fire database and also reflected here

                     */
                }
            });

        }
        private void setItemDetails(String resources, String title, String price,String quantity,String snapId){
            /*productImage.setImageResource(Integer.parseInt(resources));*/
            Picasso.with(context).load(resources).resize(150, 150).centerCrop().into(productImage);
            productTitle.setText(title);
            productQuantity.setNumber(quantity);
            productPrice.setText(price);
            hiddenSnapId.setText(snapId);


        }
    }

}