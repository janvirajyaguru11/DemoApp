package com.example.demoapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText etName, etEmail, etMobile;
    private Button btnSave;
    private RecyclerView recyclerView;
    private CustomerAdapter adapter;
    private List<Customer> customerList;
    private int editIndex = -1;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    ImageView imgSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etMobile = findViewById(R.id.etMobile);
        btnSave = findViewById(R.id.btnSave);
        recyclerView = findViewById(R.id.recyclerView);
        imgSelect = findViewById(R.id.imgSelect);

        customerList = new ArrayList<>();
        adapter = new CustomerAdapter(customerList, this, new CustomerAdapter.OnCustomerClickListener() {
            @Override
            public void onEdit(int position) {
                // Edit customer
                Customer c = customerList.get(position);
                etName.setText(c.getName());
                etEmail.setText(c.getEmail());
                etMobile.setText(c.getMobile());
                selectedImageUri = Uri.parse(c.getImageUri());  // Set the image URI
                editIndex = position;
            }

            @Override
            public void onDelete(int position) {
                // Delete customer
                customerList.remove(position);
                adapter.notifyDataSetChanged();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        imgSelect.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String email = etEmail.getText().toString();
            String mobile = etMobile.getText().toString();
            String imgUri = selectedImageUri != null ? selectedImageUri.toString() : null;

            if (editIndex == -1) {
                customerList.add(new Customer(name, email, mobile, imgUri));
            } else {
                Customer c = customerList.get(editIndex);
                c.setName(name);
                c.setEmail(email);
                c.setMobile(mobile);
                c.setImageUri(imgUri);
                editIndex = -1;
            }

            adapter.notifyDataSetChanged();
            clearInputs();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imgSelect.setImageURI(selectedImageUri);
        }
    }

    private void clearInputs() {
        etName.setText("");
        etEmail.setText("");
        etMobile.setText("");
        imgSelect.setImageResource(R.mipmap.ic_launcher);
        selectedImageUri = null;
    }


    public static class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

        private List<Customer> customerList;
        private Context context;
        private OnCustomerClickListener listener;

        public interface OnCustomerClickListener {
            void onEdit(int position);
            void onDelete(int position);
        }

        public CustomerAdapter(List<Customer> customerList, Context context, OnCustomerClickListener listener) {
            this.customerList = customerList;
            this.context = context;
            this.listener = listener;
        }

        @NonNull
        @Override
        public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_customer, parent, false);
            return new CustomerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
            Customer customer = customerList.get(position);
            holder.tvName.setText(customer.getName());
            holder.tvEmail.setText(customer.getEmail());
            holder.tvMobile.setText(customer.getMobile());
            if (customer.getImageUri() != null) {
                holder.imgProfile.setImageURI(Uri.parse(customer.getImageUri()));
            }

            // Handle Edit button click
            holder.btnEdit.setOnClickListener(v -> listener.onEdit(position));

            // Handle Delete button click
            holder.btnDelete.setOnClickListener(v -> listener.onDelete(position));
        }

        @Override
        public int getItemCount() {
            return customerList.size();
        }

        public static class CustomerViewHolder extends RecyclerView.ViewHolder {
            ImageView imgProfile;
            TextView tvName, tvEmail, tvMobile;
            Button btnEdit, btnDelete;

            public CustomerViewHolder(@NonNull View itemView) {
                super(itemView);
                imgProfile = itemView.findViewById(R.id.imgProfile);
                tvName = itemView.findViewById(R.id.tvName);
                tvEmail = itemView.findViewById(R.id.tvEmail);
                tvMobile = itemView.findViewById(R.id.tvMobile);
                btnEdit = itemView.findViewById(R.id.btnEdit);
                btnDelete = itemView.findViewById(R.id.btnDelete);
            }
        }
    }

}