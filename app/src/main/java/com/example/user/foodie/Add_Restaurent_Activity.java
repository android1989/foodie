package com.example.user.foodie;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class Add_Restaurent_Activity extends AppCompatActivity
{
   private CircleImageView add_restaurent_image;
   private Button    add_restaurent_image_btn;
   private EditText  add_name_edt_text;
   private EditText  add_location_edt_text;
   private EditText  add_restaurent_phone;
   private EditText  add_restaurent_email;
   private Button    add_restaurent_btn;
   private Toolbar   add_res_toolbar;
   private final static int GALLERY_PICK = 1;
   private FirebaseUser current_user;
   private String user_id;
   private DatabaseReference restaurent_database;
   private StorageReference image_storage_ref;
   private String mdownloaded_large_image_url;
   private String mdownloaded_thumb_image_url;
    private Bitmap thumb_bitmap;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurent);
         add_res_toolbar = findViewById(R.id.add_restaurent_toolbar);
         setSupportActionBar(add_res_toolbar);
         getSupportActionBar().setTitle("Add Restaurents");
         progress = new ProgressDialog(Add_Restaurent_Activity.this);
         progress.setMessage("Loading, Please Wait...");
         progress.setCanceledOnTouchOutside(false);

         add_restaurent_image = findViewById(R.id.add_restaurent_image);
         add_restaurent_image_btn = findViewById(R.id.add_restaurent_image_button);
         add_name_edt_text = findViewById(R.id.add_restaurent_name_input);
         add_location_edt_text = findViewById(R.id.add_restaurent_location_input);
         add_restaurent_email = findViewById(R.id.add_restaurent_email_input);
         add_restaurent_phone = findViewById(R.id.add_restaurent_phone_input);
         add_restaurent_btn = findViewById(R.id.add_restaurent_button);

         current_user = FirebaseAuth.getInstance().getCurrentUser();
         user_id = current_user.getUid();
         restaurent_database = FirebaseDatabase.getInstance().getReference().child("Restaurents").child(user_id);

    //---------------------------------------------------------------------------------\\
        add_restaurent_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                final String name = add_name_edt_text.getText().toString();
                String location = add_location_edt_text.getText().toString();
                String phone = add_restaurent_phone.getText().toString();
                String email = add_restaurent_email.getText().toString();

                if(!name.isEmpty() || !location.isEmpty() || !phone.isEmpty() || !email.isEmpty() ) {
                    add_restaurent_image.setImageResource(R.drawable.profile_avatar);
                    add_name_edt_text.setText(" ");
                    add_location_edt_text.setText(" ");
                    add_restaurent_email.setText(" ");
                    add_restaurent_phone.setText(" ");
                    HashMap<String, String> restaurent_map = new HashMap<>();
                    restaurent_map.put("name", name);
                    restaurent_map.put("location", location);
                    restaurent_map.put("phone", phone);
                    restaurent_map.put("email", email);
                    restaurent_map.put("rating", "default");
                    restaurent_map.put("likes", "112");
                    restaurent_map.put("status", "open");
                    restaurent_map.put("favourite", "0");
                    restaurent_map.put("reviews", "0");

                    if (mdownloaded_large_image_url != null) {
                        restaurent_map.put("image", mdownloaded_large_image_url);
                    } else {
                        restaurent_map.put("image", "default");
                    }

                    if (mdownloaded_thumb_image_url != null) {
                        restaurent_map.put("thumb_image", mdownloaded_thumb_image_url);
                    } else {
                        restaurent_map.put("thumb_image", "default");
                    }

                    restaurent_database.setValue(restaurent_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent restaurent_list_intent = new Intent(Add_Restaurent_Activity.this, MainActivity.class);
                                restaurent_list_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(restaurent_list_intent);
                                finish();

                                Toast.makeText(Add_Restaurent_Activity.this, name + "has been added to restaurent list", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Add_Restaurent_Activity.this, name + "could not added to restaurent list ", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                }
                else
                {
                    Toast.makeText(Add_Restaurent_Activity.this,"Please Fill all the field", Toast.LENGTH_SHORT).show();
                }

            }
        });
        //---------------------------------------------------------------------------------\\

        image_storage_ref = FirebaseStorage.getInstance().getReference().child("images");

        add_restaurent_image_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });

    }//onCreate Close

    private void captureImage()
    {
        //creating the intent for picking the image from gallery
        Intent galleryintent = new Intent();
        galleryintent.setType("image/*");
        galleryintent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(galleryintent,"SELECT IMAGE"),GALLERY_PICK);
    }

//------------------------------------------------------------------------------\\
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        progress.show();
        //if this request was for checking that request was for gallery pick
        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK)
        {
            Uri imageUri = data.getData();

            // start cropping activity for pre-acquired image saved on the device
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .setMinCropWindowSize(300,300)
                    .start(this);
        }


        //if request was for cropping then below code runs
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK)

            {
                Uri resultUri = result.getUri();
                final File thumb_filepath = new File(resultUri.getPath());
                try {
                    thumb_bitmap = new Compressor(this)
                            .setMaxWidth(150)
                            .setMaxHeight(150)
                            .setQuality(60)
                            .compressToBitmap(thumb_filepath);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                final  byte[] thumb_data = baos.toByteArray();


                StorageReference restaurent_large_image_filepath = image_storage_ref.child("restaurent_images").child("large_images").child(user_id + ".jpg");
                final StorageReference restaurent_thumb_image_filepath = image_storage_ref.child("restaurent_images").child("thumb_images").child(user_id + ".jpg");
                restaurent_large_image_filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                          final String downloaded_large_image_url =  task.getResult().getDownloadUrl().toString();
                          mdownloaded_large_image_url = downloaded_large_image_url;
                          UploadTask uploadTask = restaurent_thumb_image_filepath.putBytes(thumb_data);
                          uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                              @Override
                              public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task)
                              {
                                  if(thumb_task.isSuccessful())
                                  {
                                     String downloaded_thumb_image_url = thumb_task.getResult().getDownloadUrl().toString();
                                      mdownloaded_thumb_image_url = downloaded_thumb_image_url;
                                      Map image_map = new HashMap<>();
                                      image_map.put("image",downloaded_large_image_url);
                                      image_map.put("thumb_image",downloaded_thumb_image_url);

                                      restaurent_database.updateChildren(image_map).addOnCompleteListener(new OnCompleteListener() {
                                          @Override
                                          public void onComplete(@NonNull Task update_task)
                                          {
                                              if(update_task.isSuccessful())
                                              {
                                                  Picasso.with(Add_Restaurent_Activity.this).load(downloaded_large_image_url).fit().centerCrop()
                                                          .placeholder(R.drawable.profile_avatar)
                                                          .error(R.drawable.profile_avatar)
                                                          .into(add_restaurent_image);
                                                  Toast.makeText(Add_Restaurent_Activity.this,"Restaurent image uploaded",Toast.LENGTH_SHORT).show();
                                                  progress.dismiss();

                                              }
                                              else
                                              {
                                                  progress.dismiss();
                                                  Toast.makeText(Add_Restaurent_Activity.this,"Error in image uploading",Toast.LENGTH_SHORT).show();
                                              }

                                          }
                                      });
                                  }
                                  else
                                  {
                                      progress.dismiss();
                                   Toast.makeText(Add_Restaurent_Activity.this,"thumb image uploaded in storage database",Toast.LENGTH_SHORT).show();
                                  }

                              }
                          });


                        }

                        else
                        {
                            progress.dismiss();
                            Toast.makeText(Add_Restaurent_Activity.this,"large image uploaded in storage database",Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            } //this is nested if(resultCode == RESULT_OK)

            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
                error.printStackTrace();
                progress.dismiss();
            }
        }

    }
    //onActivityResult

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       Intent intent = new Intent(Add_Restaurent_Activity.this,MainActivity.class);
       startActivity(intent);
    }
} //Activity end











