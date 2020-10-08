package com.example.user.foodie;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import java.util.HashMap;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class RegisterActivity extends AppCompatActivity {
    private EditText mname;
    private EditText musername;
    private EditText mpassword;
    private EditText mmobile;
    private Button   mregister;
    private FirebaseAuth mauth;
    private String user_id;
    private DatabaseReference user_database;
    ProgressBar reg_progress;
    RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mname = findViewById(R.id.name);
        musername = findViewById(R.id.email1);
        mpassword = findViewById(R.id.password1);
        mmobile = findViewById(R.id.mobile);
        mregister = findViewById(R.id.register_btn);
        layout = new RelativeLayout(RegisterActivity.this);
        mauth = FirebaseAuth.getInstance();

  //--------------------------------------------------------------------\\
        mregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String name = mname.getText().toString();
                String username = musername.getText().toString();
                String password = mpassword.getText().toString();
                String mobile = mmobile.getText().toString();

                if(!TextUtils.isEmpty(name) || !TextUtils.isEmpty(username) || !TextUtils.isEmpty(password) || !TextUtils.isEmpty(mobile))
                {
                    reg_progress = new ProgressBar(RegisterActivity.this,null,android.R.attr.progressBarStyleLarge);
                    reg_progress.setIndeterminate(true);
                    reg_progress.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                    layout.addView(reg_progress,params);
                    setContentView(layout);
                    ValidateRegister(name,username,password);
                }
            }
        });
     //----------------------------------------------------------------------------------\\
    }//onCreate ends


    private void ValidateRegister(final String name, final String username, final String password)
    {
            mauth.createUserWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                final String device_token = FirebaseInstanceId.getInstance().getToken();
                                user_database = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

                                HashMap<String, String> data = new HashMap<>();
                                data.put("name", name);
                                data.put("username", username);
                                data.put("password", password);
                                data.put("email", "abc@gmail.com");
                                data.put("location", "default");
                                data.put("image", "default_image");
                                data.put("thumb_image", "default");
                                data.put("phone", "91-**********");
                                data.put("device_token", device_token);

                                user_database.setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            reg_progress.setVisibility(View.GONE);
                                            Intent location_intent = new Intent(RegisterActivity.this, MainActivity.class);
                                            location_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(location_intent);
                                            finish();
                                        } else {
                                            reg_progress.setVisibility(View.GONE);
                                            Toast.makeText(RegisterActivity.this, task.getResult().toString(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                                //-------------------------------\\

                            } else {
                                Toast.makeText(RegisterActivity.this, task.getResult().toString(), Toast.LENGTH_LONG).show();
                            }

                        }
                    });

    }//ValidateRegister


}
