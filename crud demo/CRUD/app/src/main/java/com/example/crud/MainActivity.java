package com.example.crud;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    EditText edtName, edtEmail;
    Button btnAdd;
    RecyclerView recyclerView;
    UserAdapter adapter;
    List<User> userList = new ArrayList<>();
    ApiService apiService;
    int editingUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        btnAdd = findViewById(R.id.btnAdd);
        recyclerView = findViewById(R.id.recyclerView);

        apiService = ApiClient.getClient().create(ApiService.class);

        adapter = new UserAdapter(this, userList, new UserAdapter.OnItemClickListener() {
            @Override
            public void onEdit(User user) {
                edtName.setText(user.getName());
                edtEmail.setText(user.getEmail());
                editingUserId = user.getId();
                btnAdd.setText("Update User");
            }

            @Override
            public void onDelete(User user) {
                deleteUser(user.getId());
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> {
            String name = edtName.getText().toString();
            String email = edtEmail.getText().toString();
            if (editingUserId == -1) {
                createUser(new User(name, email));
            } else {
                updateUser(editingUserId, new User(name, email));
            }
        });

        getUsers();
    }

    private void getUsers() {
        apiService.getUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    userList.clear();
                    userList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
            }
        });
    }

    private void createUser(User user) {
        apiService.createUser(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Toast.makeText(MainActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                User createdUser = response.body();
                if (createdUser != null) {
                    userList.add(createdUser);
                    adapter.notifyItemInserted(userList.size() - 1);
                } else {
                    userList.add(user);
                    adapter.notifyItemInserted(userList.size() - 1);
                }
                clearFields();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed to create user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUser(int id, User updatedUser) {
        apiService.updateUser(id, updatedUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Toast.makeText(MainActivity.this, "User Updated", Toast.LENGTH_SHORT).show();
                for (int i = 0; i < userList.size(); i++) {
                    if (userList.get(i).getId() == id) {
                        userList.get(i).setName(updatedUser.getName());
                        userList.get(i).setEmail(updatedUser.getEmail());
                        adapter.notifyItemChanged(i);
                        break;
                    }
                }
                clearFields();
                editingUserId = -1;
                btnAdd.setText("Add User");
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
            }
        });
    }

    private void deleteUser(int id) {
        apiService.deleteUser(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(MainActivity.this, "User Deleted", Toast.LENGTH_SHORT).show();
                for (int i = 0; i < userList.size(); i++) {
                    if (userList.get(i).getId() == id) {
                        userList.remove(i);
                        adapter.notifyItemRemoved(i);
                        break;
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }

    private void clearFields() {
        edtName.setText("");
        edtEmail.setText("");
    }

    public static class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
        private List<User> userList;
        private Context context;
        private OnItemClickListener listener;

        public interface OnItemClickListener {
            void onEdit(User user);

            void onDelete(User user);
        }

        public UserAdapter(Context context, List<User> userList, OnItemClickListener listener) {
            this.context = context;
            this.userList = userList;
            this.listener = listener;
        }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
            return new UserViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
            User user = userList.get(position);
            holder.txtName.setText(user.getName());
            holder.txtEmail.setText(user.getEmail());

            holder.btnEdit.setOnClickListener(v -> listener.onEdit(user));
            holder.btnDelete.setOnClickListener(v -> listener.onDelete(user));
        }

        @Override
        public int getItemCount() {
            return userList.size();
        }

        static class UserViewHolder extends RecyclerView.ViewHolder {
            TextView txtName, txtEmail;
            Button btnEdit, btnDelete;

            public UserViewHolder(@NonNull View itemView) {
                super(itemView);
                txtName = itemView.findViewById(R.id.txtName);
                txtEmail = itemView.findViewById(R.id.txtEmail);
                btnEdit = itemView.findViewById(R.id.btnEdit);
                btnDelete = itemView.findViewById(R.id.btnDelete);
            }
        }
    }
}