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

import com.example.user.foodie.ItemsActivity;
import com.example.user.foodie.Model.Favourite_items;
import com.example.user.foodie.R;
import com.example.user.foodie.RestaurentsActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;

import de.hdodenhof.circleimageview.CircleImageView;

public class Food extends Fragment
{
    private View view;
    private RecyclerView fav_item_list;
    private String user_id;
    private FirebaseRecyclerAdapter<Favourite_items, Food.FoodViewHolder> adapter;
    private String item_user_id;
    ArrayList<String> id_array = new ArrayList<String>();

    public Food()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_food, container, false);
        fav_item_list = view.findViewById(R.id.fav_item_list);
        fav_item_list.setHasFixedSize(true);
        fav_item_list.setLayoutManager(new LinearLayoutManager(getContext()));
        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return view;
    }

    @Override
    public void onStart() 
    {
        super.onStart();
        DatabaseReference item_ref = FirebaseDatabase.getInstance().getReference().child("users").child(user_id)
                .child("Favourite_items");
        Query query = item_ref.orderByKey();
        query.keepSynced(true);
 //-------------------------------------------------------------------------------------------------\\
        if(user_id.equals("fI5St4YYHJTSBQi978T3jx1TedE2"))
        {
            FirebaseRecyclerOptions<Favourite_items> options = new FirebaseRecyclerOptions.Builder<Favourite_items>()
                    .setQuery(query, Favourite_items.class)
                    .build();
            adapter = new FirebaseRecyclerAdapter<Favourite_items, FoodViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull final FoodViewHolder holder, int position, @NonNull final Favourite_items model) {
                    holder.name.setText(model.getName());
                    holder.kitchen_name.setText(model.getRestaurent_name());
                    holder.setImage(model.getImage());
                    String item_key = this.getSnapshots().getSnapshot(position).getKey();
                    //  String item_key = getRef(holder.getAdapterPosition()).getKey();
                    Toast.makeText(getContext(), "item_key:" + " " + item_key, Toast.LENGTH_SHORT).show();
                    holder.mview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String item_key = getRef(holder.getAdapterPosition()).getKey();
                            Intent intent = new Intent(getContext(), ItemsActivity.class);
                            if (item_key.equals("FOVrHgUmsSbogLAl8hv98j0rsOH3"))
                            {
                                intent.putExtra("item_key","20");
                                intent.putExtra("item_user_id", item_key);
                                intent.putExtra("position", holder.getAdapterPosition());
                                startActivity(intent);
                            }
                            if (item_key.equals("cLn1Peb29HeungOoPncGWMPCWlA3"))
                            {
                                intent.putExtra("item_key","4");
                                intent.putExtra("item_user_id", item_key);
                                intent.putExtra("position", holder.getAdapterPosition());
                                startActivity(intent);
                            }
                            if (item_key.equals("fI5St4YYHJTSBQi978T3jx1TedE2"))
                            {
                                intent.putExtra("item_key","7");
                                intent.putExtra("item_user_id", item_key);
                                intent.putExtra("position", holder.getAdapterPosition());
                                startActivity(intent);
                            }
                          ///
                        }
                    });

                }

                @NonNull
                @Override
                public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.fav_items_layout, parent, false);
                    return new FoodViewHolder(view);
                }
            };

            fav_item_list.setAdapter(adapter);
            adapter.startListening();

        }
   //---------------------------------------------------------------------------------------------------\\

        //-------------------------------------------------------------------------------------------------\\
        if(user_id.equals("cLn1Peb29HeungOoPncGWMPCWlA3"))
        {
            FirebaseRecyclerOptions<Favourite_items> options = new FirebaseRecyclerOptions.Builder<Favourite_items>()
                    .setQuery(query, Favourite_items.class)
                    .build();
            adapter = new FirebaseRecyclerAdapter<Favourite_items, FoodViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull final FoodViewHolder holder, int position, @NonNull final Favourite_items model) {
                    holder.name.setText(model.getName());
                    holder.kitchen_name.setText(model.getRestaurent_name());
                    holder.setImage(model.getImage());
                    String item_key = this.getSnapshots().getSnapshot(position).getKey();
                    //  String item_key = getRef(holder.getAdapterPosition()).getKey();
                    Toast.makeText(getContext(), "item_key:" + " " + item_key, Toast.LENGTH_SHORT).show();
                    holder.mview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String item_key = getRef(holder.getAdapterPosition()).getKey();
                            Intent intent = new Intent(getContext(), ItemsActivity.class);
                            if (item_key.equals("FOVrHgUmsSbogLAl8hv98j0rsOH3"))
                            {
                                intent.putExtra("item_key","31");
                                intent.putExtra("item_user_id", item_key);
                                intent.putExtra("position", holder.getAdapterPosition());
                                startActivity(intent);
                            }
                            if (item_key.equals("cLn1Peb29HeungOoPncGWMPCWlA3"))
                            {
                                intent.putExtra("item_key","19");
                                intent.putExtra("item_user_id", item_key);
                                intent.putExtra("position", holder.getAdapterPosition());
                                startActivity(intent);
                            }
                            if (item_key.equals("fI5St4YYHJTSBQi978T3jx1TedE2"))
                            {
                                intent.putExtra("item_key","43");
                                intent.putExtra("item_user_id", item_key);
                                intent.putExtra("position", holder.getAdapterPosition());
                                startActivity(intent);
                            }
                            ///
                        }
                    });

                }

                @NonNull
                @Override
                public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.fav_items_layout, parent, false);
                    return new FoodViewHolder(view);
                }
            };

            fav_item_list.setAdapter(adapter);
            adapter.startListening();

        }
        //---------------------------------------------------------------------------------------------------\\

        //-------------------------------------------------------------------------------------------------\\
        if(user_id.equals("FOVrHgUmsSbogLAl8hv98j0rsOH3"))
        {
            FirebaseRecyclerOptions<Favourite_items> options = new FirebaseRecyclerOptions.Builder<Favourite_items>()
                    .setQuery(query, Favourite_items.class)
                    .build();
            adapter = new FirebaseRecyclerAdapter<Favourite_items, FoodViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull final FoodViewHolder holder, int position, @NonNull final Favourite_items model) {
                    holder.name.setText(model.getName());
                    holder.kitchen_name.setText(model.getRestaurent_name());
                    holder.setImage(model.getImage());
                    String item_key = this.getSnapshots().getSnapshot(position).getKey();
                    //  String item_key = getRef(holder.getAdapterPosition()).getKey();
                    Toast.makeText(getContext(), "item_key:" + " " + item_key, Toast.LENGTH_SHORT).show();
                    holder.mview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String item_key = getRef(holder.getAdapterPosition()).getKey();
                            Intent intent = new Intent(getContext(), ItemsActivity.class);
                            if (item_key.equals("FOVrHgUmsSbogLAl8hv98j0rsOH3"))
                            {
                                intent.putExtra("item_key","20");
                                intent.putExtra("item_user_id", item_key);
                                intent.putExtra("position", holder.getAdapterPosition());
                                startActivity(intent);
                            }
                            if (item_key.equals("cLn1Peb29HeungOoPncGWMPCWlA3"))
                            {
                                intent.putExtra("item_key","4");
                                intent.putExtra("item_user_id", item_key);
                                intent.putExtra("position", holder.getAdapterPosition());
                                startActivity(intent);
                            }
                            if (item_key.equals("fI5St4YYHJTSBQi978T3jx1TedE2"))
                            {
                                intent.putExtra("item_key","7");
                                intent.putExtra("item_user_id", item_key);
                                intent.putExtra("position", holder.getAdapterPosition());
                                startActivity(intent);
                            }
                            ///
                        }
                    });

                }

                @NonNull
                @Override
                public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.fav_items_layout, parent, false);
                    return new FoodViewHolder(view);
                }
            };

            fav_item_list.setAdapter(adapter);
            adapter.startListening();

        }
        //---------------------------------------------------------------------------------------------------\\

    }

    @Override
    public void onStop() {
        super.onStop();
    //    adapter.stopListening();
    }

    public class FoodViewHolder extends RecyclerView.ViewHolder
    {
    CircleImageView image;
    TextView name;
    TextView kitchen_name;
    View mview;

    public FoodViewHolder(View itemView)
       {
        super(itemView);
        mview = itemView;
        image = itemView.findViewById(R.id.fav_item_image);
        name = itemView.findViewById(R.id.fav_item_name);
        kitchen_name = itemView.findViewById(R.id.fav_kitchen_name);
       }

        public void setImage(final String item_image)
        {
            Picasso.with(getContext()).load(item_image).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.res_icon).into(image, new Callback(){
                @Override
                public void onSuccess()
                {
                }
                @Override
                public void onError()
                {
                    Picasso.with(getContext()).load(item_image).placeholder(R.drawable.res_icon).into(image);
                }
            });
        }
    }
    
    }
