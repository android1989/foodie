package com.example.user.foodie.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.foodie.Model.Favourite_restaurents;
import com.example.user.foodie.R;
import com.example.user.foodie.RestaurentsActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Kitchen extends Fragment
{
   private View main_view;
   private RecyclerView kitchen_list;
   private FirebaseRecyclerAdapter<Favourite_restaurents, Kitchen.KitchenViewHolder> adapter;
   private String user_id;


    public Kitchen()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        main_view =  inflater.inflate(R.layout.fragment_kitchen, container, false);

        kitchen_list = main_view.findViewById(R.id.fav_restaurent_list);
        kitchen_list.setHasFixedSize(true);
        kitchen_list.setLayoutManager(new LinearLayoutManager(getContext()));

        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        return main_view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        //-----------------------------------------------------------------------\\
        DatabaseReference res_ref = FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child("Favourite_restaurents");
        Query query = res_ref.orderByKey();
        query.keepSynced(true);
        FirebaseRecyclerOptions<Favourite_restaurents> options = new FirebaseRecyclerOptions.Builder<Favourite_restaurents>()
                .setQuery(query, Favourite_restaurents.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Favourite_restaurents, KitchenViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final KitchenViewHolder holder, int position, @NonNull final Favourite_restaurents model)
            {
                holder.name.setText(model.getName());
                holder.location.setText(model.getLocation());
                holder.rating.setText(model.getRating());
                Picasso.with(getContext()).load(model.getThumb_image()).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.res_icon).into(holder.image, new Callback() {
                    @Override
                    public void onSuccess()
                    {

                    }
                    @Override
                    public void onError()
                    {
                        Picasso.with(getContext()).load(model.getThumb_image()).placeholder(R.drawable.res_icon).into(holder.image);
                    }
                });
                final String user_id = getRef(position).getKey();
               holder.mview.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v)
                   {
                        Intent intent = new Intent(getContext(),RestaurentsActivity.class);
                       intent.putExtra("user_id",user_id);
                       intent.putExtra("position",holder.getAdapterPosition());
                        startActivity(intent);
                   }
               });

            }

            @NonNull
            @Override
            public KitchenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_restaurent_layout, parent, false);
                return new KitchenViewHolder(view);
            }
        };

        kitchen_list.setAdapter(adapter);
        adapter.startListening();
  //-----------------------------------------------------------------------------------------\\
    }
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public class KitchenViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView image;
        TextView name;
        TextView location;
        TextView rating;
        View mview;

        public KitchenViewHolder(View itemView)
        {
            super(itemView);
            mview = itemView;
             image = itemView.findViewById(R.id.restaurent_image);
             name = itemView.findViewById(R.id.restaurent_name);
             location = itemView.findViewById(R.id.restaurent_location);
             rating = itemView.findViewById(R.id.rating_text);
        }
    }


}
