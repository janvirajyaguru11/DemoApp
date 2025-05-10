package com.example.demoapp;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
public class DetailActivity extends AppCompatActivity {
    private TextView tvName, tvEmail, tvMobile;
    private ImageView imgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvMobile = findViewById(R.id.tvMobile);
        imgProfile = findViewById(R.id.imgProfile);

        Customer customer = (Customer) getIntent().getSerializableExtra("customer");

        if (customer != null) {
            tvName.setText(customer.getName());
            tvEmail.setText(customer.getEmail());
            tvMobile.setText(customer.getMobile());

            if (customer.getImageUri() != null) {
                imgProfile.setImageURI(Uri.parse(customer.getImageUri()));
            }
        }
    }
}