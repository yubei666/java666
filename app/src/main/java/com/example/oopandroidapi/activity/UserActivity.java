package com.example.oopandroidapi.activity;

import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.oopandroidapi.data.UserData;
import com.example.oopandroidapi.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.logging.Logger;

public class UserActivity extends AppCompatActivity {
    private UserData user = new UserData();
    private TextView userTitle;
    private EditText userName;
    private EditText userEmail;
    private EditText userAge;
    private EditText userGender;
    private Button button;
    private ImageButton userBackButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user);

        try {
            File file = new File(this.getFilesDir(), "user.text");
            if (file.exists()) {
                Logger.getLogger("UserActivity").info("User data found");
                InputStream input = this.openFileInput("user.text");
                byte[] filecontent = new byte[(int) file.length()];
                input.read(filecontent);
                input.close();

                String[] data = new String(filecontent).split("\n");
                user.name = data[0];
                user.email = data[1];
                user.age = Integer.parseInt(data[2]);
                user.gender = data[3];
            } else {
                Logger.getLogger("UserActivity").info("User data not found, creating new user");
                user.name = "Lucien";
                user.email = "yangxin092333@gmail.com";
                user.age = 22;
                user.gender = "male";
                String filecontent = user.name + "\n" + user.email + "\n" + String.valueOf(user.age) + "\n" + user.gender;
                FileOutputStream output = this.openFileOutput("user.text", Context.MODE_PRIVATE);
                output.write(filecontent.getBytes());
                output.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        bindViews();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void bindViews() {
        userTitle = findViewById(R.id.userTitle);
        userName = findViewById(R.id.username);
        userEmail = findViewById(R.id.userEmail);
        userAge = findViewById(R.id.userAge);
        userGender = findViewById(R.id.userGender);
        button = findViewById(R.id.userSaveButton);
        userBackButton = findViewById(R.id.userBackButton);
        updateViews();

        userBackButton.setOnClickListener(v -> {
            finish();
        });

        button.setOnClickListener(v -> {
            user.name = userName.getText().toString().isEmpty() ? user.name : userName.getText().toString();
            user.email = userEmail.getText().toString().isEmpty() ? user.email : userEmail.getText().toString();
            user.age = userAge.getText().toString().isEmpty() ? user.age : Integer.parseInt(userAge.getText().toString());
            user.gender = userGender.getText().toString().isEmpty() ? user.gender : userGender.getText().toString();

            try {
                String filecontent = user.name + "\n" + user.email + "\n" + String.valueOf(user.age) + "\n" + user.gender;
                FileOutputStream output = this.openFileOutput("user.text", Context.MODE_PRIVATE);
                output.write(filecontent.getBytes());
                output.close();
                Logger.getLogger("UserActivity").info("User data saved");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            try {
                InputStream input = this.openFileInput("user.text");
                byte[] filecontent = new byte[2048];
                input.read(filecontent);
                input.close();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            updateViews();
        });
    }

    private void updateViews() {
        userTitle.setText("Hi, " + user.name + "!");
        userName.setText(user.name);
        userEmail.setText(user.email);
        userAge.setText(String.valueOf(user.age));
        userGender.setText(user.gender);
    }
}