package com.example.user.foodie;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ItemsActivity extends AppCompatActivity {

    private ImageView mitem_image;
    private TextView  mitem_name_text;
    private TextView  mitem_description_text;
    private TextView  mitem_price;
    private ImageView mitem_like_btn;
    private ImageView mitem_rating_btn;
    private ImageView mitem_review_btn;

    private int item_position;
    private DatabaseReference item_ref;
    private String item_key;
    private String item_desc;
    private String item_name;
    private String item_image_url;
    private String item_price;
    private FirebaseUser current_user;
    private String current_user_id;
    private String item_user_id;
    private Button mitem_order_button;

    private DatabaseReference user_ref;
    private DatabaseReference restaurent_ref;
    private String user_image_url;
    private String user_name;
    private String res_name;
    private String item_rating;
    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        mitem_image = findViewById(R.id.item_image);
        mitem_name_text = findViewById(R.id.item_name_text);
        mitem_description_text = findViewById(R.id.item_description_text);
        mitem_price = findViewById(R.id.item_price);

        mitem_like_btn = findViewById(R.id.item_like_btn);
        mitem_rating_btn = findViewById(R.id.item_rating_btn);
        mitem_review_btn = findViewById(R.id.item_review_btn);
        mitem_order_button = findViewById(R.id.item_order_button);

        progress = new ProgressDialog(ItemsActivity.this);
        progress.setMessage("Loading...");
        progress.show();

        Intent intent = getIntent();
        item_key = intent.getStringExtra("item_key");
        item_user_id = intent.getStringExtra("item_user_id");
        item_position = intent.getIntExtra("position", 0);

        current_user = FirebaseAuth.getInstance().getCurrentUser();
        current_user_id = current_user.getUid();

//---------------------------------------------------------------------------------------------------------------------------\\
    restaurent_ref = FirebaseDatabase.getInstance().getReference().child("Restaurents").child(item_user_id);
    restaurent_ref.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot)
        {
         res_name =  dataSnapshot.child("name").getValue().toString();
        }
        @Override
        public void onCancelled(DatabaseError databaseError)
        {

        }
    });


//---------------------------------------------------------------------------------------------------------------------------\\
        item_ref = FirebaseDatabase.getInstance().getReference().child("Items").child(item_user_id).child(item_key);
        item_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    item_desc = dataSnapshot.child("descriptions").getValue().toString();
                    item_name = dataSnapshot.child("name").getValue().toString();
                    item_image_url = dataSnapshot.child("image").getValue().toString();
                    item_price = dataSnapshot.child("price").getValue().toString();
                } else {
                    Toast.makeText(ItemsActivity.this, "no data fetched from item database", Toast.LENGTH_SHORT).show();
                }

                mitem_name_text.setText(item_name);
                mitem_description_text.setText(item_desc);
                mitem_price.setText(item_price+" "+"Rs");

                Picasso.with(ItemsActivity.this).load(item_image_url).fit().centerCrop()
                        .placeholder(R.drawable.item_avatar3)
                        .error(R.drawable.item_avatar3)
                        .into(mitem_image, new Callback() {
                            @Override
                            public void onSuccess() {
                                if (progress != null && progress.isShowing()) {
                                    progress.dismiss();
                                }
                            }

                            @Override
                            public void onError()
                            {
                                if (progress != null && progress.isShowing()) {
                                    progress.dismiss();
                                }
                            }
                        });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ItemsActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

//-------------------------------------------------------------------------------------------------------------------------------\\

        user_ref = FirebaseDatabase.getInstance().getReference().child("users").child(current_user_id);
        user_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user_name = dataSnapshot.child("name").getValue().toString();
                user_image_url = dataSnapshot.child("thumb_image").getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });


        //---------------------------------------------------------------------------------------------------------------------------------\\
        mitem_like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference like_ref = FirebaseDatabase.getInstance().getReference().child("users").child(current_user_id)
                        .child("Favourite_items").child(item_user_id);

                Map<String, Object> like_map = new HashMap<>();
                like_map.put("name", item_name);
                like_map.put("restaurent_name", res_name);
                like_map.put("image", item_image_url);
                like_map.put("item_key",item_key);

                like_ref.setValue(like_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ItemsActivity.this, "item has added as Favourite for the current user", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ItemsActivity.this, "item could not added as Favourite for the current user", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }//onClick end
        });

        //----------------------------------------------------------------------------------------------------------------------------------\\

        mitem_rating_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.single_rating_layout, null, false);
                final RatingBar rating = view.findViewById(R.id.restaurent_rating_row);

                new AlertDialog.Builder(ItemsActivity.this)
                        .setTitle("Rating")
                        .setView(view)
                        .setCancelable(false)
                        .setPositiveButton("Rate us", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                float rating_value = rating.getRating();
                                item_rating = String.valueOf(rating_value);
                                DatabaseReference item_rating_ref = FirebaseDatabase.getInstance().getReference().child("Items").
                                        child(item_user_id).child(item_key).child("item_rating").child(current_user_id);
                                Map<String, Object> user_map = new HashMap<>();
                                user_map.put("name", user_name);
                                user_map.put("thumb_image", user_image_url);
                                user_map.put("rating", item_rating);

                                item_rating_ref.setValue(user_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            Toast.makeText(ItemsActivity.this, "rating has been added to item", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                            {
                                            Toast.makeText(ItemsActivity.this, "rating could not be added to item", Toast.LENGTH_SHORT).show();
                                            }
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(ItemsActivity.this, "user cancelled rating invitation", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
            }
        });

        //----------------------------------------------------------------------------------------------------------------------------------\\
        mitem_review_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.add_review_layout, null, false);
                final EditText input = view.findViewById(R.id.review_input);

                new AlertDialog.Builder(ItemsActivity.this)
                        .setTitle("Add Review")
                        .setView(view)
                        .setCancelable(false)
                        .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                String review = input.getText().toString();
                                long time = 100201;
                                final DatabaseReference item_review_ref = FirebaseDatabase.getInstance().getReference().child("Items").
                                        child(item_user_id).child(item_key).child("Item_Reviews").child(current_user_id);

                                Map<String, Object> user_map = new HashMap<>();
                                user_map.put("review_text",review);
                                user_map.put("name", user_name);
                                user_map.put("thumb_image", user_image_url);
                                user_map.put("time_ago", time);
                              
                              item_review_ref.setValue(user_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                  @Override
                                  public void onComplete(@NonNull Task<Void> task)
                                  {
                                   if(task.isSuccessful())
                                   {
                                       item_review_ref.child("time_ago").setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task)
                                           {
                                               if(task.isSuccessful())
                                               {
                                                   Toast.makeText(ItemsActivity.this,"item review added with timestamp",Toast.LENGTH_SHORT).show();
                                               }
                                               else
                                               {
                                                   Toast.makeText(ItemsActivity.this,"item review could not added with timestamp",Toast.LENGTH_SHORT).show();
                                               }
                                           }
                                       });
                                   }
                                   else
                                   {
                                       Toast.makeText(ItemsActivity.this,"item review could not added..",Toast.LENGTH_SHORT).show();
                                   }
                                  }
                              });

                            }
                        })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ItemsActivity.this, "Entering review cancelled", Toast.LENGTH_LONG).show();
                    }
                })
                .show();
            }
        });

    //------------------------------------------------------------------------------------------------------------------------------\\
        mitem_order_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent order_intent = new Intent(ItemsActivity.this,OrderActivity.class);
                order_intent.putExtra("item_key",item_key);
                order_intent.putExtra("item_user_id",item_user_id);
                startActivity(order_intent);
            }

        });
    //-----------------------------------------------------------------------------------------------------------------------------------\\




    }//onCreate end


}
