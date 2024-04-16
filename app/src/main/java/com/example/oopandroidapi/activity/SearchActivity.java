package com.example.oopandroidapi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oopandroidapi.R;
import com.example.oopandroidapi.data.HistoryData;
import com.example.oopandroidapi.data.UserData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

public class SearchActivity extends AppCompatActivity {
    private HistoryData history = new HistoryData();
    private EditText editMunicipalityName;
    private Button searchButton;
    private ImageButton userButton;
    private RecyclerView historyRecyclerView;
    private HistoryRecyclerViewAdapter historyAdapter;
    private UserData user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        history.readFromFile(this);

        bindViews();
    }

    private void bindViews() {
        userButton = findViewById(R.id.userButton);
        editMunicipalityName = findViewById(R.id.editMunicipalityName);
        searchButton = findViewById(R.id.searchButton);
        historyRecyclerView = findViewById(R.id.historyRecyclerView);

        userButton.setOnClickListener(v -> {
            startActivity(new Intent(this, UserActivity.class));
        });
        searchButton.setOnClickListener(v -> {
            String municipalityName = editMunicipalityName.getText().toString();
            if (municipalityName.isEmpty()) {
                return;
            }
            if (!history.history.contains(municipalityName)) {
                history.add(municipalityName);
                history.saveToFile(this);
                updateViews();
            }
            startActivity(new Intent(this, InformantionActivity.class).putExtra("municipalityName", municipalityName));
        });
        updateViews();
    }

    private void updateViews() {
        historyAdapter = new HistoryRecyclerViewAdapter(this, history.history);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        historyRecyclerView.setLayoutManager(manager);
        historyRecyclerView.setAdapter(historyAdapter);
    }

    public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.ViewHolder> {
        private List<String> history_list;
        private Context context;
        private View inflater;

        public HistoryRecyclerViewAdapter(Context context, List<String> history_list) {
            this.context = context;
            this.history_list = history_list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            inflater = LayoutInflater.from(context).inflate(R.layout.history_item, parent, false);
            ViewHolder holder = new ViewHolder(inflater);
            holder.history_item.setOnClickListener(v -> {
                editMunicipalityName.setText(holder.history_item.getText());
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.history_item.setText(history_list.get(position));
        }

        @Override
        public int getItemCount() {
            return history_list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView history_item;

            public ViewHolder(View itemView) {
                super(itemView);
                history_item = itemView.findViewById(R.id.historyItem);
            }
        }
    }
}