package com.example.user.foodie;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.foodie.Model.GetTimeAgo;
import com.example.user.foodie.Model.Items;
import com.example.user.foodie.Model.Restaurent_Reviews;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class RestaurentsActivity extends AppCompatActivity
{
    private TextView   res_name;
    private TextView   res_address;
    private TextView res_likes;
    private RatingBar  res_rating;
    private TextView   res_reviews;
    private Spinner    res_status;
    private ImageView  fav_btn;
    private ImageView  loc_btn;
    private Button   res_contact;
    private Button   add_review_btn;
    private ImageView rating_btn;
    private RecyclerView reviews_list;
    private String  restaurent_email;
    private String  restaurent_phone;
    private RecyclerView items_list;
    private FirebaseRecyclerAdapter item_Adapter;
    private DatabaseReference item_database;
    private DatabaseReference restaurent_database;
    private DatabaseReference favourites_database;
    private DatabaseReference users_database;
    private FirebaseUser current_user;
    private String user_id;
    private String fav_name;
    private String fav_location;
    private String fav_rating;
    private String fav_thumb_image;
    private String i_Latitude;
    private String i_Longitude;
    private String fav_key;
    private int restaurent_position;
    private String restaurent_id;
    private String user_name;
    private String user_thumb_image;
    private DatabaseReference review_database;
    private FirebaseRecyclerAdapter review_adapter;
    private HashMap<Integer,String> restaurent_value = new HashMap<Integer,String>();
    private int counter ;
    private ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurents);
        res_name = findViewById(R.id.Restaurent_name);
        res_address = findViewById(R.id.Restaurent_address);
        res_rating = findViewById(R.id.restaurent_rating);
        res_reviews = findViewById(R.id.restaurent_reviews);
        res_status = findViewById(R.id.restaurent_status);
        fav_btn = findViewById(R.id.fav_button);
        loc_btn = findViewById(R.id.location_button);
        add_review_btn = findViewById(R.id.reviews_add_review_btn);
        rating_btn = findViewById(R.id.rating_button);
        res_contact = findViewById(R.id.restaurent_contact);
        res_likes = findViewById(R.id.Restaurent_likes);

        progress = new ProgressDialog(RestaurentsActivity.this);
        progress.setMessage("Loading, Please Wait...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        current_user = FirebaseAuth.getInstance().getCurrentUser();
        user_id = current_user.getUid();
        Intent main_intent = getIntent();
        restaurent_id =  main_intent.getStringExtra("user_id");
        restaurent_position =  main_intent.getIntExtra("position",0);
        //*********************************************************************************||

        users_database =FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
        users_database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                user_name = dataSnapshot.child("name").getValue().toString();
                user_thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Toast.makeText(RestaurentsActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        //------------------------------------------------------------------------------------\\
        restaurent_database = FirebaseDatabase.getInstance().getReference().child("Restaurents").child(restaurent_id);
        restaurent_database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String name = dataSnapshot.child("name").getValue().toString();
                String location = dataSnapshot.child("location").getValue().toString();
                String reviews = dataSnapshot.child("reviews").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String rating = dataSnapshot.child("rating").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                String phone = dataSnapshot.child("phone").getValue().toString();
                String likes = dataSnapshot.child("likes").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                String key = dataSnapshot.getKey();
                //-----------------------------------------------------------------\\
                //data taken for favourite database
                fav_name = name;
                fav_location = location;
                fav_thumb_image = thumb_image;
                fav_key = key;
                fav_rating = rating;
                //-----------------------------------------------------------------\\
                res_name.setText(name);
                res_address.setText(location);
                res_reviews.setText(reviews);
                res_likes.setText(likes);
                if(rating.equals("default"))
                {
                    res_rating.setRating(0);
                }
                else
                {
                    res_rating.setRating(Float.parseFloat(rating));
                }
                if(status.equals("open"))
                {
                    res_status.setSelection(0);
                }
                else if(status.equals("closed"))
                {
                    res_status.setSelection(1);
                }
                restaurent_email = email;
                restaurent_phone = phone;
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Toast.makeText(RestaurentsActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

     //-----------------------------------------------------------------------------------\\
        fav_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                favourites_database = FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child("Favourite_restaurents").child(fav_key);
               // favourites_database = FirebaseDatabase.getInstance().getReference().child("Favourites").child(user_id);
                HashMap<String,String> fav_map = new HashMap<>();
                fav_map.put("name",fav_name);
                fav_map.put("location",fav_location);
                fav_map.put("rating",fav_rating);
                fav_map.put("thumb_image",fav_thumb_image);
                favourites_database.setValue(fav_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                      if(task.isSuccessful())
                      {
                          Toast.makeText(RestaurentsActivity.this,"this restaurent has added as favourites for user",Toast.LENGTH_SHORT).show();
                      }
                      else
                      {
                          Toast.makeText(RestaurentsActivity.this,"this restaurent could not added as favourites for user",Toast.LENGTH_SHORT).show();
                      }
                    }
                });
                }
        });
        //-------------------------------------------------------------------------------------------\\
        loc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                MyTask task = new MyTask(RestaurentsActivity.this);
                task.execute(fav_location);
            }
        });
        //-------------------------------------------------------------------------------------------\\
        res_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.single_contact_layout,null,false);
                TextView phone_text = view.findViewById(R.id.contact_phone);
                TextView email_text = view.findViewById(R.id.contact_email);
                phone_text.setText(restaurent_phone);
                email_text.setText(restaurent_email);
                new AlertDialog.Builder(RestaurentsActivity.this)
                        .setTitle("Restaurent Contact")
                        .setView(view)
                        .show();
            }
        });
    //--------------------------------------------------------------------------------------------------------\\
        rating_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.single_rating_layout,null,false);
                final RatingBar rating = view.findViewById(R.id.restaurent_rating_row);
                SharedPreferences sp = getSharedPreferences("rating_counter", Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = sp.edit();
                new AlertDialog.Builder(RestaurentsActivity.this)
                        .setTitle("Rating")
                        .setView(view)
                        .setCancelable(false)
                        .setPositiveButton("Rate us", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                if(rating.getRating() != 0)
                                {
                                    if(fav_rating.equals("default"))
                                    {
                                        counter++;
                                        float rating_value = rating.getRating();
                                        editor.putInt("counter", counter);
                                        editor.putFloat("rating", rating_value);
                                        editor.commit();
                                        float avg_rating_actual = (float) (rating_value / counter);
                                        float avg_rating_reminder = (float) (rating_value % counter);
                                        float avg_rating = avg_rating_actual + avg_rating_reminder;
                                        String rating = String.valueOf(avg_rating);
                                        final String counter_value = String.valueOf(counter);

                                        HashMap<String,String> rating_map = new HashMap<>();
                                        rating_map.put("rating",rating);
                                        rating_map.put("counter", counter_value);
                                        //-----------------------------------------------------------\\
                                        restaurent_database.child("rating").setValue(rating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                restaurent_database.child("counter").setValue(counter_value).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task)
                                                    {
                                                        if(task.isSuccessful())
                                                        {
                                                            Toast.makeText(RestaurentsActivity.this,"rating added uccesfully",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                        //-----------------------------------------------------------\\
                                    }//Inner if end
                                    else
                                    {
                                        SharedPreferences sp = getSharedPreferences("rating_counter", Context.MODE_PRIVATE);
                                        int saved_counter = sp.getInt("counter", 0);
                                        float saved_rating = sp.getFloat("rating", 0);
                                        saved_counter++;
                                        float rating_value = rating.getRating();
                                        saved_rating = saved_rating + rating_value;
                                        float avg_rating_actual = (float) (saved_rating / saved_counter);
                                        float avg_rating_reminder = (float) (saved_rating % saved_counter);
                                        float avg_rating = avg_rating_actual + avg_rating_reminder;
                                        String rating = String.valueOf(avg_rating);
                                        editor.putInt("counter", saved_counter);
                                        editor.putFloat("rating", saved_rating);
                                        editor.commit();
                                        final String counter_value = String.valueOf(saved_counter);
                                        HashMap<String,String> rating_map = new HashMap<>();
                                        rating_map.put("rating",rating);
                                        rating_map.put("counter", counter_value);
                                        restaurent_database.child("rating").setValue(rating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                restaurent_database.child("counter").setValue(counter_value).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task)
                                                    {
                                                        if(task.isSuccessful())
                                                        {
                                                            Toast.makeText(RestaurentsActivity.this,"review added succesfully",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                        }//Inner else end
                                }//Outer if end
                                else
                                {
                                  Toast.makeText(RestaurentsActivity.this,"user didnt give rating",Toast.LENGTH_SHORT).show();
                                }//Outer else end

                            }//onClick end
                        })
                        .setNegativeButton("Later" ,new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Toast.makeText(RestaurentsActivity.this,"user cancelled rating invitation",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
            }
        });
        //--------------------------------------------------------------------------------------------------------\\
        add_review_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.add_review_layout,null,false);
                final EditText input = view.findViewById(R.id.review_input);
                //-------------------------\\
                new AlertDialog.Builder(RestaurentsActivity.this)
                        .setTitle("Add Review")
                        .setView(view)
                        .setCancelable(false)
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                    String review = input.getText().toString();
                                    long time = 100201;
                                    final DatabaseReference review_ref =  restaurent_database.child("Restaurent_Reviews").child(user_id);
                                    HashMap<String,Object> review_map = new HashMap<>();
                                    review_map.put("review_text",review);
                                    review_map.put("name",user_name);
                                    review_map.put("thumb_image",user_thumb_image);
                                    review_map.put("time_ago", time);
                                    review_map.put("rating",fav_rating);
                                    review_ref.setValue(review_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if(task.isSuccessful())
                                        {
                                            review_ref.child("time_ago").setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    if(task.isSuccessful())
                                                    {
                                                        int counter=0;
                                                        counter++;
                                                        String count = String.valueOf(counter);
                                                        restaurent_database.child("reviews").setValue(count);
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(RestaurentsActivity.this,"Nreview timestamp value could not saved to database",Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                        else
                                        {
                                            Toast.makeText(RestaurentsActivity.this,"review could not saved to database",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Toast.makeText(RestaurentsActivity.this,"Entering review cancelled",Toast.LENGTH_LONG).show();
                            }
                        })
                        .show();
                //-----------------------------------------\\
            }
        });
        //--------------------------------------------------------------------------------------------------------\\
        items_list = findViewById(R.id.restaurent_items_list);
        items_list.setHasFixedSize(true);
        items_list.setLayoutManager(new LinearLayoutManager(RestaurentsActivity.this,LinearLayoutManager.HORIZONTAL,false));
        item_database = FirebaseDatabase.getInstance().getReference().child("Items").child(restaurent_id);
       // progress.show();
        Query query = item_database;
        FirebaseRecyclerOptions items_Options = new FirebaseRecyclerOptions.Builder<Items>().setQuery(query, Items.class).build();
        item_Adapter = new FirebaseRecyclerAdapter<Items, ItemsViewHolder>(items_Options) {
            @NonNull
            @Override
            public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_item_layout, parent, false);
                return new ItemsViewHolder(view);
            }
            @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
            @Override
            protected void onBindViewHolder(@NonNull final ItemsViewHolder viewHolder, int position, @NonNull Items model)
            {
                viewHolder.setName(model.getName());
                viewHolder.setImage(model.getImage(),getApplicationContext());
                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        String item_key = getRef(viewHolder.getAdapterPosition()).getKey();
                        Intent items_intent = new Intent(RestaurentsActivity.this,ItemsActivity.class);
                        items_intent.putExtra("item_key",item_key);
                        items_intent.putExtra("item_user_id",restaurent_id);
                        items_intent.putExtra("position", viewHolder.getAdapterPosition());
                        startActivity(items_intent);
                    }
                }); //viewholder onclick end
            }

            @Override
            public void onDataChanged()
            {
                if(progress != null && progress.isShowing())
                {
                    progress.dismiss();
                }
            }
        };
             items_list.setAdapter(item_Adapter);
             item_Adapter.startListening();
        //-------------------------------------------------------------------------------------------------\\
        reviews_list = findViewById(R.id.restaurent_reviews_list);
        reviews_list.setHasFixedSize(true);
        reviews_list.setLayoutManager(new LinearLayoutManager(RestaurentsActivity.this));
        review_database = restaurent_database.child("Restaurent_Reviews");
        Query review_query = review_database.orderByChild(FirebaseAuth.getInstance().getCurrentUser().getUid());
        FirebaseRecyclerOptions usersOptions = new FirebaseRecyclerOptions.Builder<Restaurent_Reviews>().setQuery(review_query, Restaurent_Reviews.class).build();
        FirebaseRecyclerOptions restaurents_Options = new FirebaseRecyclerOptions.Builder<Restaurent_Reviews>()
                .setQuery(review_query, Restaurent_Reviews.class).build();
        review_adapter = new FirebaseRecyclerAdapter<Restaurent_Reviews, ReviewsViewHolder>(restaurents_Options) {
            @Override
            protected void onBindViewHolder(@NonNull ReviewsViewHolder viewHolder, final int position, @NonNull Restaurent_Reviews model)
            {
                viewHolder.setName(model.getName());
                viewHolder.setThumb_Image(model.getThumb_image(),getApplicationContext());
                viewHolder.setReview_text(model.getReview_text());
                viewHolder.setTimeAgo(model.getTime_ago());
                viewHolder.setRating(model.getRating());

                final String review_id = getRef(position).getKey();
                restaurent_value.put(position,review_id);
            }
            @NonNull
            @Override
            public ReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_reviews_layout, parent, false);
                return new ReviewsViewHolder(view);
            }

            @Override
            public void onDataChanged()
            {
            }
        };
        reviews_list.setAdapter(review_adapter);
        review_adapter.startListening();
    }//onCreate end
    //-----------------------------------------------------------------------------------\\

    public static class ItemsViewHolder extends RecyclerView.ViewHolder
    {
        String thumbnail;
        Context ctx;
        ImageView items_image;
        View mview;

        public ItemsViewHolder(View itemView)
        {
            super(itemView);
            mview = itemView;
        }

        public void setName(String name)
        {
            TextView name_text = mview.findViewById(R.id.item_name);
            name_text.setText(name);
        }

        public void setImage(String image, Context context)
        {
            thumbnail = image;
            ctx = context;
            items_image = mview.findViewById(R.id.item_image);

            Picasso.with(ctx).load(thumbnail).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.res_icon).into(items_image, new Callback() {
                @Override
                public void onSuccess()
                {

                }
                @Override
                public void onError()
                {
                    Picasso.with(ctx).load(thumbnail).placeholder(R.drawable.res_icon).into(items_image);
                }
            });
        }
    }//viewholder class
    //-----------------------------------------------------------------------------------\\
    public static class ReviewsViewHolder extends RecyclerView.ViewHolder
    {
        String thumbnail;
        Context ctx;
        CircleImageView review_image;
        View mview;

        public ReviewsViewHolder(View itemView)
        {
            super(itemView);
            mview = itemView;
        }

        public void setName(String name)
        {
            TextView name_text = mview.findViewById(R.id.reviews_username);
            name_text.setText(name);
        }

        public void setReview_text(String review)
        {
            TextView review_text = mview.findViewById(R.id.restaurent_reviews_text);
            review_text.setText(review);
        }

        public void setTimeAgo(Long time_ago)
        {
            TextView review_timeago = mview.findViewById(R.id.reviews_time_ago_text);
            String last_seen = GetTimeAgo.getTimeAgo(time_ago,ctx);
            review_timeago.setText(last_seen);
        }

        public void setRating(String rating)
        {
            RatingBar review_rating = mview.findViewById(R.id.reviews_restaurent_rating);
            if(rating.equals("default"))
            {
                review_rating.setRating(0);
            }
            else
            {
                review_rating.setRating(Float.parseFloat(rating));
            }

        }

        public void setThumb_Image(String image, Context context)
        {
            thumbnail = image;
            ctx = context;
            review_image = mview.findViewById(R.id.reviews_user_image);

            Picasso.with(ctx).load(thumbnail).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.profile_avatar_small).into(review_image, new Callback() {
                @Override
                public void onSuccess()
                {

                }

                @Override
                public void onError()
                {
                    Picasso.with(ctx).load(thumbnail).placeholder(R.drawable.profile_avatar_small).into(review_image);
                }
            });
        }

    }//viewholder class

    private  class MyTask extends AsyncTask<String,Void,String>
    {
        private Context context;
        public MyTask(Context context)
        {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params)
        {
            String location = params[0];
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());

            try
            {
                List addressList = geocoder.getFromLocationName(location, 5);
                if (addressList != null && addressList.size() > 0) {
                    Address address = (Address) addressList.get(0);
                    i_Latitude = String.valueOf(address.getLatitude());
                    i_Longitude = String.valueOf(address.getLongitude());
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return "executed sucesffully...";
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            Toast.makeText(context, result ,Toast.LENGTH_SHORT).show();
             Toast.makeText(context,"latitude : " + i_Latitude + "longitude : " + i_Longitude ,Toast.LENGTH_SHORT).show();

            Intent i = new Intent(context, MapActivity.class);
            i.putExtra("latitude",i_Latitude);
            i.putExtra("longitude",i_Longitude);
            i.putExtra("title",fav_location);
            i.putExtra("user_id",restaurent_id);
            i.putExtra("position",restaurent_position);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    } //asynctask class end
    //-----------------------------------------------------------------------------------\\

    public void goBack(View view)
    {
       Intent intent = new Intent(RestaurentsActivity.this,MainActivity.class);
       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
       startActivity(intent);
    }

}

