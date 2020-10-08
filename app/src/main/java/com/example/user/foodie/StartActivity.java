package com.example.user.foodie;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {

    private  FirebaseAuth mauth;
    private  FirebaseUser current_user;
    private EditText mstart_username;
    private EditText mstart_password;
    private Button mstart_login;
    private Button mstart_register;
    ProgressBar reg_progress;
    RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mauth = FirebaseAuth.getInstance();
        mstart_username = findViewById(R.id.start_username);
        mstart_password = findViewById(R.id.start_password);
        mstart_login = findViewById(R.id.start_login);
        mstart_register = findViewById(R.id.start_register);
        layout = new RelativeLayout(StartActivity.this);

        mstart_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String username = mstart_username.getText().toString();
                String password = mstart_password.getText().toString();
                if(!TextUtils.isEmpty(username) || !TextUtils.isEmpty(password))
                {
                    reg_progress = new ProgressBar(StartActivity.this,null,android.R.attr.progressBarStyleLarge);
                    reg_progress.setIndeterminate(true);
                    reg_progress.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                    layout.addView(reg_progress,params);
                    setContentView(layout);
                    ValidateLogin(username,password);
                }
            }
        });

        mstart_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent main_intent = new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(main_intent);
                finish();
            }
        });

    }

    private void ValidateLogin(String username, String password)
    {
        mauth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            reg_progress.setVisibility(View.GONE);
                         Intent location_intent = new Intent(StartActivity.this,MainActivity.class);
                         startActivity(location_intent);
                         finish();
                        }
                        else
                            {
                                reg_progress.setVisibility(View.GONE);
                                Toast.makeText(StartActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            }

                    }
                });
    }

}
