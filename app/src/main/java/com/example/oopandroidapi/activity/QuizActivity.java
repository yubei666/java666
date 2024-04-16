package com.example.oopandroidapi.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.oopandroidapi.R;
import com.example.oopandroidapi.data.MunicipalityData;
import com.example.oopandroidapi.fragment.QuestionFragment;
import com.example.oopandroidapi.fragment.ResultFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class QuizActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private ImageButton backButton;
    private Button nextButton;
    private List<String> questions = new ArrayList<>();
    private List<Boolean> answers = Arrays.asList(true, false, true, false, true, false, true, false, true, false);
    private int score = 0;

    public int getScore() {
        return score;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ArrayList<MunicipalityData> data = (ArrayList) (this.getIntent().getSerializableExtra("data"));
        String municipality = this.getIntent().getStringExtra("municipalityName");
        for (int i = 0; i < answers.size(); i++) {
            if (answers.get(i))
                questions.add("Does " + municipality + " have a population of " + data.get(i).getPopulation() + " in " + data.get(i).getYear() + "?");
            else
                questions.add("Does " + municipality + " have a population of " + (data.get(i).getPopulation() + 200) + " in " + data.get(i).getYear() + "?");
        }
        bindViews();
    }

    private void bindViews() {
        viewPager = findViewById(R.id.view_pager);
        backButton = findViewById(R.id.quizBackButton);
        nextButton = findViewById(R.id.next);

        backButton.setOnClickListener(v -> finish());
        nextButton.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() < 10) {
                int idx = viewPager.getCurrentItem();
                Fragment fragment = getSupportFragmentManager().findFragmentByTag("f" + idx);
                if (((RadioButton) fragment.getActivity().findViewById(R.id.answer_true)).isChecked() == answers.get(idx)) {
                    score++;
                }
                viewPager.setCurrentItem(idx + 1);
            } else {
                finish();
            }
        });
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                if (position == 10) {
                    return new ResultFragment(questions, answers);
                } else {
                    return new QuestionFragment(questions.get(position), answers.get(position));
                }
            }

            @Override
            public int getItemCount() {
                return 11;
            }
        });
    }
}