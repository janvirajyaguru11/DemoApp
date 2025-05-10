package com.example.demoapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DisplayActivity extends AppCompatActivity {
    ImageView imgDisplay;
    TextView tvEmail, tvPassword, tvPhone;
    Button btnEdit, btnDelete;
    String email, password, phone, imageUriStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        imgDisplay = findViewById(R.id.imgDisplay);
        tvEmail = findViewById(R.id.Email);
        tvPassword = findViewById(R.id.Password);
        tvPhone = findViewById(R.id.Phone);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");
        phone = intent.getStringExtra("phone");
        imageUriStr = intent.getStringExtra("imageUri");

        tvEmail.setText("Email: " + email);
        tvPassword.setText("Password: " + password);
        tvPhone.setText("Phone: " + phone);
        imgDisplay.setImageURI(Uri.parse(imageUriStr));

        btnEdit.setOnClickListener(v -> {
            Intent i = new Intent(this, FromActivity.class);
            i.putExtra("email", email);
            i.putExtra("password", password);
            i.putExtra("phone", phone);
            i.putExtra("imageUri", imageUriStr);
            startActivity(i);
        });

        btnDelete.setOnClickListener(v -> {
            imgDisplay.setImageDrawable(null);
            tvEmail.setText("");
            tvPassword.setText("");
            tvPhone.setText("");
            Toast.makeText(this, "Data deleted", Toast.LENGTH_SHORT).show();
        });
    }
}
