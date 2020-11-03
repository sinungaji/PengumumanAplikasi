package com.example.pengumumanaplikasi.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.pengumumanaplikasi.MainActivity;
import com.example.pengumumanaplikasi.R;

public class ProfileUser extends AppCompatActivity {
    private ImageView imgBack, imgSetting;
    private LinearLayout informasiAkun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        imgBack = findViewById(R.id.imgBack);
        imgSetting = findViewById(R.id.imgSetting);
        informasiAkun = findViewById(R.id.informasiAkun);

        actionButton();
    }

    private void actionButton() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        imgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ResetPassword.class);
                startActivity(intent);
            }
        });

        informasiAkun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), InformasiAkunActivity.class);
                startActivity(intent);
            }
        });
    }
}