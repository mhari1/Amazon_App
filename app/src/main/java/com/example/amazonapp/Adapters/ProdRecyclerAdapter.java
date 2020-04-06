package com.example.amazonapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amazonapp.Controllers.ProdFragment;
import com.example.amazonapp.Controllers.ProductDetailsFragment;
import com.example.amazonapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProdRecyclerAdapter  extends RecyclerView.Adapter<ProdRecyclerAdapter.MyViewHolders> {


/*
*Products Adapter
 */
    List<String> prodTitle;
    List<String> prodImgUrl;
    List<String> prodId;
    Context context;


    public ProdRecyclerAdapter( Context mContext,List<String> prodTitle,List<String> prodImgUrl,List<String> prodId) {

        this.prodTitle=prodTitle;
        this.prodImgUrl=prodImgUrl;
        this.prodId=prodId;
        this.context = mContext;
    }

    @NonNull
    @Override
    public MyViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        LayoutInflater mInflater = LayoutInflater.from(context);
        view = mInflater.inflate(R.layout.prodcardview,parent,false);
        return new MyViewHolders(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolders holder, int position) {

        // Prod prod = array.get(position);
        holder.tv_title.setText(prodTitle.get(position));
        Picasso.with(context).load(prodImgUrl.get(position)).into(holder.img);


    }

    @Override
    public int getItemCount() {
        return prodTitle.size();
    }

    public  class MyViewHolders extends RecyclerView.ViewHolder{

        TextView tv_title;
        ImageView img;
        //CardView cardView ;


        public MyViewHolders(@NonNull View itemView) {
            super(itemView);


            tv_title = (TextView) itemView.findViewById(R.id.productTitle);
            img = (ImageView) itemView.findViewById(R.id.productImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Toast.makeText(v.getContext(), "Clicked -> " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                    //calling
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    ProductDetailsFragment productFragment = new ProductDetailsFragment(v.getContext(),prodId.get(getAdapterPosition()));

                        /*category.setAlpha(0);
                        product.setAlpha(0);*/

                    activity.getSupportFragmentManager().beginTransaction();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, productFragment).addToBackStack(null).commit();




                }
            });
        }
    }
}