package com.example.user.foodie;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Add_Item_Activity extends AppCompatActivity
{
    private ImageView item_image;
    private EditText item_name;
    private EditText item_description;
    private EditText item_price;
    private Button save_btn;
    Uri image_uri;
    private final static int CAPTURE_IMAGE_FROM_CAMERA = 1;
    private final static int CHOOSE_IMAGE_FROM_GALLERY = 2;
    private FirebaseUser current_user;
    private DatabaseReference item_database;
    private StorageReference item_image_ref;
    private String item_image_url;
    private String user_id;
    private int random_key;
    private String random_key_value;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        item_image = findViewById(R.id.add_item_image);
        item_name = findViewById(R.id.add_item_name_input);
        item_description = findViewById(R.id.add_item_description_input);
        item_price = findViewById(R.id.add_item_price_input);
        save_btn = findViewById(R.id.add_item_save_button);
        progress = new ProgressDialog(Add_Item_Activity.this);
        progress.setMessage("Loading, Please Wait...");
        progress.setCanceledOnTouchOutside(false);

        current_user = FirebaseAuth.getInstance().getCurrentUser();
        user_id = current_user.getUid();
        // Toast.makeText(Add_Item_Activity.this,"user_id:"+user_id,Toast.LENGTH_LONG).show();
        item_database = FirebaseDatabase.getInstance().getReference().child("Items").child(user_id);
        item_image_ref = FirebaseStorage.getInstance().getReference().child("images");
  //-------------------------------------------------------------\\
        item_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
                AlertDialog.Builder builder = new AlertDialog.Builder(Add_Item_Activity.this);
                builder.setTitle("Add Photo!");
                builder.setItems(options, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int item)
                    {
                        if (options[item].equals("Take Photo"))
                        {
                           Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                           startActivityForResult(intent,CAPTURE_IMAGE_FROM_CAMERA);
                        }
                        else if (options[item].equals("Choose from Gallery"))
                        {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent,"select image"),CHOOSE_IMAGE_FROM_GALLERY);
                        }
                        else if (options[item].equals("Cancel"))
                        {
                            dialog.dismiss();
                            Toast.makeText(Add_Item_Activity.this,"item image loading cancelled",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.show();
            }
        });
   //----------------------------item_image.setOnClickListener end--------------------------------------------------\\
    save_btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            String name = item_name.getText().toString();
            String price = item_price.getText().toString();
            String description = item_description.getText().toString();
            item_name.setText("");
            item_price.setText("");
            item_description.setText("");
            item_image.setImageResource(R.drawable.item_avatar3);

            Map<String,Object> item_map = new HashMap<>();
            item_map.put("name",name);
            item_map.put("price",price);
            item_map.put("descriptions",description);
            item_map.put("favourite","default");
            item_map.put("item_key",random_key_value);

            if(item_image_url!=null )
            {
                item_map.put("image",item_image_url);
            }
            else
            {
                item_map.put("image","default");
            }

          item_database.child(random_key_value).setValue(item_map).addOnCompleteListener(new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task)
              {
                 if(task.isSuccessful())
                 {
                     Toast.makeText(Add_Item_Activity.this,"item has been added to Restaurent menu,add more",Toast.LENGTH_SHORT).show();
                   //  Intent intent = new Intent(Add_Item_Activity.this,MainActivity.class);
                  //   startActivity(intent);

                 }
                 else
                 {
                     Toast.makeText(Add_Item_Activity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                 }
              }
          });

        }
    });//save_btn end


    }//onCreate() end

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        progress.show();
        if(requestCode == CAPTURE_IMAGE_FROM_CAMERA && resultCode == RESULT_OK)
        {
            image_uri = data.getData();
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            bitmap = getResizedBitmap(bitmap, 200);
            item_image.setImageBitmap(bitmap);
        }
        if(requestCode == CHOOSE_IMAGE_FROM_GALLERY && resultCode == RESULT_OK)
        {
            image_uri = data.getData();
            try
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),image_uri);
                bitmap = getResizedBitmap(bitmap, 200);
                item_image.setImageBitmap(bitmap);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        Random random = new Random();
        random_key = random.nextInt(100);
        random_key_value = String.valueOf(random_key);
        StorageReference image_filepath = item_image_ref.child("item_image").child(user_id).child(random_key_value);
        image_filepath.putFile(image_uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
            {
                if(task.isSuccessful())
                {
                   final String item_image_downloaded_url = task.getResult().getDownloadUrl().toString();
                    item_image_url = item_image_downloaded_url;
                    HashMap<String,String> image_map = new HashMap<>();
                    image_map.put("image",item_image_downloaded_url);
                    item_database.child(random_key_value).setValue(image_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                progress.dismiss();
                               // Toast.makeText(Add_Item_Activity.this,"image uri updated in item database",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                progress.dismiss();
                                Toast.makeText(Add_Item_Activity.this,"image uri could not updated in item database",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }
                else
                {
                    progress.dismiss();
                    Toast.makeText(Add_Item_Activity.this,"large item Image URL could not be updated in image storage",Toast.LENGTH_SHORT).show();
                }
            }
        });
                  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    }//OnActivityResult() end

    public Bitmap getResizedBitmap(Bitmap image, int maxSize)
    {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1)
        {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        }
        else
            {
            height = maxSize;
            width = (int) (height * bitmapRatio);
            }
          return Bitmap.createScaledBitmap(image, width, height, true);
    }//getResizeBitmap() end

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Add_Item_Activity.this,MainActivity.class);
        startActivity(intent);
    }

}//Activity end
