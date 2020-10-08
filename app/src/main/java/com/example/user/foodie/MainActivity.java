package com.example.user.foodie;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.foodie.Model.Restaurents;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

     private FirebaseAuth mauth;
     private FirebaseUser current_user;
     private DatabaseReference restaurent_database;
     private String user_id;
     private FirebaseRecyclerAdapter restaurent_adapter;
     private HashMap<Integer,String> restaurent_value = new HashMap<Integer,String>();
     private DrawerLayout drawer;
     private NavigationView navigationView;
     private RecyclerView restaurent_list;
     private AutoCompleteTextView search_input;
     private ImageView search_btn;
     private ImageView mnavigation_btn;
     private ProgressDialog progress;
    private ViewGroup parent;
    private int index;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation);
        search_btn = findViewById(R.id.main_search_button);
        mnavigation_btn = findViewById(R.id.navigation_btn);
        search_input = findViewById(R.id.main_search_input);
        restaurent_list = findViewById(R.id.restaurent_list);
        restaurent_list.setHasFixedSize(true);
        restaurent_list.setLayoutManager(new LinearLayoutManager(this));
        mauth = FirebaseAuth.getInstance();
        current_user = mauth.getCurrentUser();
        user_id = current_user.getUid();
        parent = (ViewGroup) restaurent_list.getParent();
        index = parent.indexOfChild(restaurent_list);
        progress = new ProgressDialog(MainActivity.this);
        progress.setMessage("Loading...");
        progress.show();

        mnavigation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawer.isDrawerOpen(Gravity.START))
                {
                    drawer.closeDrawers();
                }
                else
                {
                    drawer.openDrawer(Gravity.START);
                }

            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                if(item.getItemId()== R.id.dashboard)
                {
                    item.setChecked(true);
                    drawer.closeDrawers();
                    Intent home_intent = new Intent(MainActivity.this,HomeActivity.class);
                    startActivity(home_intent);
                    return true;
                }
                if(item.getItemId() == R.id.add_restaurent)
                {
                    item.setChecked(true);
                    drawer.closeDrawers();
                    Intent add_restaurent_intent = new Intent(MainActivity.this,Add_Restaurent_Activity.class);
                    startActivity(add_restaurent_intent);
                    return true;
                }
                if(item.getItemId() == R.id.add_items)
                {
                    item.setChecked(true);
                    drawer.closeDrawers();
                    Intent add_restaurent_intent = new Intent(MainActivity.this, Add_Item_Activity.class);
                    startActivity(add_restaurent_intent);
                    return true;
                }

                if(item.getItemId() == R.id.add_fav)
                {
                    item.setChecked(true);
                    drawer.closeDrawers();
                    Intent add_restaurent_intent = new Intent(MainActivity.this, Favourite_Activity.class);
                    startActivity(add_restaurent_intent);
                    return true;
                }


                if(item.getItemId() == R.id.logout)
                {
                    item.setChecked(true);
                    drawer.closeDrawers();


                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(getString(R.string.menu_logout))
                            .setCancelable(false)
                            .setMessage("Do you want to LogOut ?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseAuth.getInstance().signOut();
                                    Intent logout_intent = new Intent(MainActivity.this,StartActivity.class);
                                    logout_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(logout_intent);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    // do nothing
                                }
                            })
                            //  .setIcon(R.drawable.ic_logout)
                            .show();
                    return true;
                }

                return false;
            }
        });

        restaurent_database = FirebaseDatabase.getInstance().getReference().child("Restaurents");
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                getAdapter();

            }//onclick end
        });

    } //OnCreate End

    @Override
    protected void onStart() {
        super.onStart();
        current_user = mauth.getCurrentUser();
        if(current_user == null)
        {
            sendToStart();
        }
        else
        {
           getAdapter();

        }
    }

    public void getAdapter()
    {
        String search_text = search_input.getText().toString();
        if(search_text !=null)
        {
            //-----------------------------------------------------------------------------------------------------\\
            Query query = restaurent_database.orderByChild("name").startAt(search_text).endAt(search_text + "\uf8ff");
            FirebaseRecyclerOptions restaurents_Options = new FirebaseRecyclerOptions.Builder<Restaurents>()
                    .setQuery(query, Restaurents.class).build();
            restaurent_adapter
                    = new FirebaseRecyclerAdapter<Restaurents, RestaurentViewHolder>(restaurents_Options) {
                @Override
                protected void onBindViewHolder(@NonNull RestaurentViewHolder viewHolder, @SuppressLint("RecyclerView") final int position, @NonNull Restaurents model)
                {
                    viewHolder.setName(model.getName());
                    viewHolder.setLocation(model.getLocation());
                    viewHolder.setRating(model.getRating());
                    viewHolder.setThumb_Image(model.getThumb_image(),getApplicationContext());
                    final String user_id = getRef(position).getKey();
                    restaurent_value.put(position,user_id);
                    viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent profile_intent = new Intent(MainActivity.this,RestaurentsActivity.class);
                            profile_intent.putExtra("user_id",user_id);
                            profile_intent.putExtra("position",position);
                            startActivity(profile_intent);
                        }
                    }); //view
                }

                @NonNull
                @Override
                public RestaurentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.single_restaurent_layout, parent, false);
                    return new RestaurentViewHolder(view);
                }

                @Override
                public void onDataChanged()
                {
                    if (progress != null && progress.isShowing()) {
                        progress.dismiss();
                    }
                }
            };
            restaurent_list.setAdapter(restaurent_adapter);
            restaurent_adapter.startListening();

        /*    if(restaurent_adapter.getItemCount() == 0)
            {
                parent.removeView(restaurent_list);
                View view = getLayoutInflater().inflate(R.layout.empty_layout, parent, false);
                parent.addView(view, index);
                progress.dismiss();
            }*/

            //----------------------------------------------------------------\\
        }
        else
        {
            //----------------------------------------------------------------------------------------\\
            Query query = restaurent_database.orderByChild(FirebaseAuth.getInstance().getCurrentUser().getUid());
            FirebaseRecyclerOptions usersOptions = new FirebaseRecyclerOptions.Builder<Restaurents>().setQuery(query, Restaurents.class).build();

            FirebaseRecyclerOptions restaurents_Options = new FirebaseRecyclerOptions.Builder<Restaurents>()
                    .setQuery(query, Restaurents.class).build();

            restaurent_adapter = new FirebaseRecyclerAdapter<Restaurents, RestaurentViewHolder>(restaurents_Options) {
                @Override
                protected void onBindViewHolder(@NonNull RestaurentViewHolder viewHolder, @SuppressLint("RecyclerView") final int position, @NonNull Restaurents model)
                {
                    viewHolder.setName(model.getName());
                    viewHolder.setLocation(model.getLocation());
                    viewHolder.setRating(model.getRating());
                    viewHolder.setThumb_Image(model.getThumb_image(),getApplicationContext());

                    final String user_id = getRef(position).getKey();
                    restaurent_value.put(position,user_id);

                    viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent profile_intent = new Intent(MainActivity.this,RestaurentsActivity.class);
                            profile_intent.putExtra("user_id",restaurent_value);
                            profile_intent.putExtra("position",position);
                            startActivity(profile_intent);
                        }
                    }); //viewholder onclick end
                }
                @NonNull
                @Override
                public RestaurentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.single_restaurent_layout, parent, false);
                    return new RestaurentViewHolder(view);
                }

                @Override
                public void onDataChanged()
                {
                    if (progress != null && progress.isShowing())
                    {
                        progress.dismiss();
                    }
                }

            };
            restaurent_list.setAdapter(restaurent_adapter);
            restaurent_adapter.startListening();
         /*  if(restaurent_adapter.getItemCount() == 0)
           {
               parent.removeView(restaurent_list);
               View view = getLayoutInflater().inflate(R.layout.empty_layout, parent, false);
               parent.addView(view, index);
               progress.dismiss();
           }*/

            //--------------------------------------------------------------------------------------------\\

        }//else end

    } //getAdapter ends

    //-------------------------------------------------------------------\\
    public static class RestaurentViewHolder extends RecyclerView.ViewHolder
    {
        String thumbnail;
        Context ctx;
        CircleImageView restaurent_image;
         View mview;

        public RestaurentViewHolder(View itemView)
        {
            super(itemView);
            mview = itemView;
        }

        public void setName(String name)
        {
             TextView name_text = mview.findViewById(R.id.restaurent_name);
             name_text.setText(name);
        }

        public void setLocation(String location)
        {
            TextView location_text = mview.findViewById(R.id.restaurent_location);
            location_text.setText(location);
        }

        public void setRating(String rating)
        {
            TextView rating_text = mview.findViewById(R.id.rating_text);
            if(rating.equals("default"))
            {
                rating_text.setText("0");
            }
            else
            {
                rating_text.setText(rating);
            }

        }

        public void setThumb_Image(String thumb_image, Context context)
        {
            thumbnail = thumb_image;
            ctx = context;
            restaurent_image = mview.findViewById(R.id.restaurent_image);

            Picasso.with(ctx).load(thumbnail).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.res_icon).into(restaurent_image, new Callback() {
                @Override
                public void onSuccess()
                {

                }

                @Override
                public void onError()
                {
                    Picasso.with(ctx).load(thumbnail).placeholder(R.drawable.res_icon).into(restaurent_image);
                }
            });

        }

    }//viewholder class
    //---------------------------------------------------------------------------------------------\\

    private void sendToStart()
    {
        Intent start_intent = new Intent(MainActivity.this,StartActivity.class);
        startActivity(start_intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        ExitApp();
        //super.onBackPressed();
    }

    private void ExitApp() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.app_name))
                .setMessage("Do you want to Exit App ?")
                .setIcon(R.drawable.foodie_icon)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

}
