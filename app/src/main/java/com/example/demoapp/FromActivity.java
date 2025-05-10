package com.example.demoapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FromActivity extends AppCompatActivity {

    EditText etEmail, etPassword, etPhoneNumber;
    ImageView imge;
    Button btnSubmit;
    RecyclerView recyclerView;
    UserAdapter adapter;
    List<User> userList = new ArrayList<>();
    Uri imageUri;
    boolean isEditMode = false;
    int editPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_from);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        imge = findViewById(R.id.imge);
        btnSubmit = findViewById(R.id.btnSubmit);
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new UserAdapter(userList, this::editUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        if (intent.hasExtra("user")) {
            isEditMode = true;
            User user = (User) intent.getSerializableExtra("user");
            etEmail.setText(user.getEmail());
            etPassword.setText(user.getPassword());
            etPhoneNumber.setText(user.getPhone());
            imageUri = Uri.parse(user.getImageUri());
            imge.setImageURI(imageUri);
            editPosition = intent.getIntExtra("position", -1);
        }

        imge.setOnClickListener(v -> {
            Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickImage, 100);
        });

        btnSubmit.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String phone = etPhoneNumber.getText().toString().trim();

            if (imageUri == null) {
                Toast.makeText(this, "Select an image", Toast.LENGTH_SHORT).show();
                return;
            }
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Enter valid email");
                return;
            }
            if (password.length() < 4) {
                etPassword.setError("Password must be at least 4 digits");
                return;
            }
            if (phone.length() != 10) {
                etPhoneNumber.setError("Enter valid 10 digit number");
                return;
            }

            User user = new User(email, password, phone, imageUri.toString());
            if (isEditMode && editPosition != -1 && editPosition < userList.size()) {
                userList.set(editPosition, user);
                Toast.makeText(this, "User Updated", Toast.LENGTH_SHORT).show();
            } else {
                userList.add(user);
                Toast.makeText(this, "User Added", Toast.LENGTH_SHORT).show();
            }


            adapter.notifyDataSetChanged();

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(Objects.requireNonNull(selectedImageUri));
                File file = new File(getFilesDir(), "temp.jpg");
                FileOutputStream outputStream = new FileOutputStream(file);

                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, len);
                }

                outputStream.close();
                inputStream.close();

                imageUri = Uri.fromFile(file);
                imge.setImageURI(imageUri);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Image error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void editUser(int position) {
        User user = userList.get(position);
        Intent intent = new Intent(this, FromActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    public static class User implements Serializable {
        private String email;
        private String password;
        private String phone;
        private String imageUri;

        public User(String email, String password, String phone, String imageUri) {
            this.email = email;
            this.password = password;
            this.phone = phone;
            this.imageUri = imageUri;
        }

        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public String getPhone() { return phone; }
        public String getImageUri() { return imageUri; }
    }

    public static class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
        List<User> userList;
        OnEditClickListener listener;

        public interface OnEditClickListener {
            void onEdit(int position);
        }

        public UserAdapter(List<User> userList, OnEditClickListener listener) {
            this.userList = userList;
            this.listener = listener;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView txtEmail, txtPhone;
            ImageView imgUser;
            Button btnEdit;

            ViewHolder(View itemView) {
                super(itemView);
                txtEmail = itemView.findViewById(R.id.tvEmail);
                txtPhone = itemView.findViewById(R.id.tvMobile);
                imgUser = itemView.findViewById(R.id.imgProfile);
                btnEdit = itemView.findViewById(R.id.btnEdit);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer, parent, false);
            return new ViewHolder(v);
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            User u = userList.get(position);
            holder.txtEmail.setText(u.getEmail());
            holder.txtPhone.setText(u.getPhone());
            holder.imgUser.setImageURI(Uri.parse(u.getImageUri()));

            holder.btnEdit.setOnClickListener(v -> listener.onEdit(position));
        }

        @Override
        public int getItemCount() {
            return userList.size();
        }
    }
}
