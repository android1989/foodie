package com.example.user.foodie;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity
{
    private static final int SPLASH_DURATION = 2000;
    FirebaseUser current_user;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        current_user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
  //-------------------------------------------------------------------\\
        Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (current_user == null)
                {
                    Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                    {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                    }
            }

        }, SPLASH_DURATION);
   //--------------------------------------------------------------------\\

    }

}
