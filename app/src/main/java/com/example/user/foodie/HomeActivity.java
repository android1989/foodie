package com.example.user.foodie;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.ValueEventListener;
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

public class HomeActivity extends AppCompatActivity {

    private CircleImageView user_image;
    private TextView user_name;
    private TextView user_location;
    private TextView user_phone;
    private TextView user_email;
    private String user_password;
    private Button change_password_btn;
    private Button change_image_btn;

    private TextView edit_location;
    private TextView edit_phone;
    private TextView edit_email;

    private String edit_location_text;
    private String edit_phone_text;
    private String edit_email_text;
    private String edit_username_text;

    private final static int GALLERY_PICK = 1;
    private DatabaseReference user_database;
    private StorageReference image_storage_ref;
    private FirebaseAuth mauth;
    private FirebaseUser current_user;
    private String user_id ;
    private ProgressDialog progress;
    private String muser_large_image_download_url;
    private String muser_thumb_image_download_url;

    private Bitmap thumb_bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        user_image = findViewById(R.id.user_profile_image);
        change_image_btn = findViewById(R.id.update_image_btn);
        user_name = findViewById(R.id.user_name);
        user_location = findViewById(R.id.user_location);
        user_phone = findViewById(R.id.user_contact);
        user_email = findViewById(R.id.user_gmail);
        change_password_btn = findViewById(R.id.change_password_btn);

        progress = new ProgressDialog(HomeActivity.this);
        progress.setMessage("Loading, Please Wait...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        edit_email = findViewById(R.id.edit_email);
        edit_location = findViewById(R.id.edit_location);
        edit_phone = findViewById(R.id.edit_phone);

        mauth = FirebaseAuth.getInstance();
        current_user = mauth.getCurrentUser();
        user_id = current_user.getUid();
        user_database = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
      //---------------------------------------------------------------------------\\
        //Fetching values From database
        user_database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String username = dataSnapshot.child("name").getValue().toString();
                String password  = dataSnapshot.child("password").getValue().toString();
                String location = dataSnapshot.child("location").getValue().toString();
                String phone = dataSnapshot.child("phone").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();

                edit_email_text = email;
                edit_location_text = location;
                edit_phone_text = phone;
                edit_username_text = username;

                user_name.setText(username);
                user_location.setText(location);
                user_phone.setText(phone);
                user_email.setText(email);

                if(!image.equals("default"))
                {
                    Picasso.with(HomeActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.profile_avatar).into(user_image, new Callback() {
                        @Override
                        public void onSuccess()
                        {
                            //do nothing
                            progress.dismiss();
                        }

                        @Override
                        public void onError()
                        {
                            Picasso.with(HomeActivity.this).load(image).placeholder(R.drawable.profile_avatar).into(user_image);
                            progress.dismiss();
                        }
                    });
                } //if end

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Toast.makeText(HomeActivity.this,databaseError.getMessage().toString(),Toast.LENGTH_LONG).show();
            }
        });
        //---------------------------------------------------------------------------\\

   //-----------------------------------------------------------------------------\\
        //Change_password_button click event
        change_password_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                final EditText password_text = new EditText(HomeActivity.this);

                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Change Password")
                        .setMessage("Do you want to update password")
                        .setView(password_text)
                        .setIcon(R.mipmap.ic_launcher_round)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                final String password = password_text.getText().toString();
                                password_text.setText(" ");

                                user_database.child("password").setValue(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(HomeActivity.this,"password updated in database:"+ " " + password,Toast.LENGTH_LONG).show();
                                        }

                                    }
                                });

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Toast.makeText(HomeActivity.this,"Password updation cancelled",Toast.LENGTH_LONG).show();
                            }
                        })
                        .show();

            }
        });
       //-----------------------------------------------------------------------------------------\\

        //Change_image_Button click event
        image_storage_ref = FirebaseStorage.getInstance().getReference().child("images");

        change_image_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });


    } //onCreate End

    private void captureImage()
    {
        //creating the intent for picking the image from gallery
        Intent galleryintent = new Intent();
        galleryintent.setType("image/*");
        galleryintent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(galleryintent,"SELECT IMAGE"),GALLERY_PICK);
    }

//---------------------------------------------------------------------------------------\\
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    progress.show();
    //if this request was for checking that request was for gallery pick
    if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
        Uri imageUri = data.getData();

        // start cropping activity for pre-acquired image saved on the device
        CropImage.activity(imageUri)
                .setAspectRatio(1, 1)
                .setMinCropWindowSize(500, 500)
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
            // String current_user_id = current_user.getUid();
            try {
                thumb_bitmap = new Compressor(this)
                        .setMaxWidth(200)
                        .setMaxHeight(200)
                        .setQuality(60)
                        .compressToBitmap(thumb_filepath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            final byte[] thumb_data = baos.toByteArray();

            StorageReference user_image_filepath = image_storage_ref.child("profile_images").child("large_images").child(user_id + ".jpg");
            final StorageReference user_thumb_image_filepath = image_storage_ref.child("profile_images").child("thumb_images").child(user_id + ".jpg");

            //putting the cropped image to image filepath to upload to storage
            user_image_filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                {
                    if (task.isSuccessful())
                    {
                        final String user_large_image_download_url = task.getResult().getDownloadUrl().toString();
                        UploadTask uploadTask = user_thumb_image_filepath.putBytes(thumb_data);
                        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task)
                            {
                                String user_thumb_image_download_url = thumb_task.getResult().getDownloadUrl().toString();
                                if(thumb_task.isSuccessful())
                                {
                                    Map user_image_map = new HashMap();
                                    user_image_map.put("image",user_large_image_download_url);
                                    user_image_map.put("thumb_image",user_thumb_image_download_url);

                                    user_database.updateChildren(user_image_map).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                progress.dismiss();
                                                Toast.makeText(HomeActivity.this, "large and thumb image url updated in users databsse", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                                }
                                else
                                {
                                    progress.dismiss();
                                    Toast.makeText(HomeActivity.this, "user thumb image could not be stored in users databsse", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                    }
                    else
                        {
                            progress.dismiss();
                          Toast.makeText(HomeActivity.this, "user large image could not be stored in users databsse", Toast.LENGTH_SHORT).show();
                        }

                } //oncomplete method closes

            }); //putFile onComplete listener closes


        } //this is nested if(resultCode == RESULT_OK)

        else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            progress.dismiss();
            Exception error = result.getError();
            error.printStackTrace();
        }
    }
}



    //-------------------------------------------------------------------------------------------------\\


    //Edit functionalities

    public void edit_location(View view)
    {
        if(view.getId() == R.id.edit_location)
        {

            final EditText location_text = new EditText(HomeActivity.this);
            location_text.setText(edit_location_text);
            new AlertDialog.Builder(HomeActivity.this)
                    .setTitle("Change Location")
                    .setView(location_text)
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            final String location = location_text.getText().toString();
                            location_text.setText(" ");

                            user_database.child("location").setValue(location).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        user_location.setText(location);
                                        Toast.makeText(HomeActivity.this,"Location updated in database:"+ " " + location,Toast.LENGTH_LONG).show();
                                    }

                                }
                            });

                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Toast.makeText(HomeActivity.this,"Location updation cancelled",Toast.LENGTH_LONG).show();
                        }
                    })
                    .show();
           }
        else
        {
            Toast.makeText(HomeActivity.this,"location OnClick could not performed",Toast.LENGTH_SHORT).show();
        }
    }

    public void edit_phone(View view)
    {
        if(view.getId() == R.id.edit_phone)
        {

            final EditText phone_text = new EditText(HomeActivity.this);
            phone_text.setText(edit_phone_text);
            new AlertDialog.Builder(HomeActivity.this)
                    .setTitle("Change Phone Number")
                    .setView(phone_text)
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            final String phone = phone_text.getText().toString();
                            phone_text.setText(" ");

                            user_database.child("phone").setValue(phone).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        user_phone.setText(phone);
                                        Toast.makeText(HomeActivity.this,"phone updated in database:"+ " " + phone,Toast.LENGTH_LONG).show();
                                    }

                                }
                            });

                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Toast.makeText(HomeActivity.this,"Phone updation cancelled",Toast.LENGTH_LONG).show();
                        }
                    })
                    .show();
           }

           else
        {
            Toast.makeText(HomeActivity.this,"phone OnClick could not performed",Toast.LENGTH_SHORT).show();
        }
    }

    public void edit_email(View view)
    {
        if(view.getId() == R.id.edit_email)
        {

            final EditText email_text = new EditText(HomeActivity.this);
            email_text.setText(edit_email_text);
            new AlertDialog.Builder(HomeActivity.this)
                    .setTitle("Change Email")
                    .setView(email_text)
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            final String email = email_text.getText().toString();
                            email_text.setText(" ");

                            user_database.child("email").setValue(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        user_email.setText(email);
                                        Toast.makeText(HomeActivity.this,"email updated in database:"+ " " + email,Toast.LENGTH_LONG).show();
                                    }

                                }
                            });

                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Toast.makeText(HomeActivity.this,"email updation cancelled",Toast.LENGTH_LONG).show();
                        }
                    })
                    .show();
              }
        else
        {
            Toast.makeText(HomeActivity.this,"email OnClick could not performed",Toast.LENGTH_SHORT).show();
        }
    }

    public void edit_username(View view)
    {
        if(view.getId() == R.id.edit_username)
        {

            final EditText username_text = new EditText(HomeActivity.this);
            username_text.setText(edit_username_text);
            new AlertDialog.Builder(HomeActivity.this)
                    .setTitle("Change Name")
                    .setView(username_text)
                    .setIcon(R.mipmap.ic_launcher_round)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            final String name = username_text.getText().toString();
                            username_text.setText(" ");

                            user_database.child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        user_name.setText(name);
                                        Toast.makeText(HomeActivity.this,"name updated in database:"+ " " + name,Toast.LENGTH_LONG).show();
                                    }

                                }
                            });

                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Toast.makeText(HomeActivity.this,"name updation cancelled",Toast.LENGTH_LONG).show();
                        }
                    })
                    .show();
        }
        else
        {
            Toast.makeText(HomeActivity.this,"name edit could not performed",Toast.LENGTH_SHORT).show();
        }


    }

    //----------------------------------------------------------------------------------------------\\


} //Activity end
