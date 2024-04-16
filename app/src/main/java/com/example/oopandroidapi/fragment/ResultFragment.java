package com.example.oopandroidapi.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.oopandroidapi.R;
import com.example.oopandroidapi.activity.QuizActivity;
import com.example.oopandroidapi.activity.SearchActivity;

import java.util.List;

public class ResultFragment extends Fragment {

    private List<String> questions;
    private List<Boolean> answers;
    private int score;
    private TextView scoreText;
    private RecyclerView recyclerView;

    public ResultFragment(List<String> questions, List<Boolean> answers) {
        this.questions = questions;
        this.answers = answers;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        score = ((QuizActivity) this.getActivity()).getScore();
        scoreText.setText("Score: " + score + "/" + questions.size());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        scoreText = view.findViewById(R.id.score);
        recyclerView = view.findViewById(R.id.answer_recyclerView);
        AnswerRecyclerViewAdapter adapter = new AnswerRecyclerViewAdapter(getContext(), questions, answers);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    public class AnswerRecyclerViewAdapter extends RecyclerView.Adapter<AnswerRecyclerViewAdapter.ViewHolder> {
        private List<String> questions;
        private List<Boolean> answers;
        private Context context;
        private View inflater;

        public AnswerRecyclerViewAdapter(Context context, List<String> questions, List<Boolean> answers) {
            this.context = context;
            this.questions = questions;
            this.answers = answers;
        }

        @Override
        public AnswerRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            inflater = LayoutInflater.from(context).inflate(R.layout.answer_item, parent, false);
            AnswerRecyclerViewAdapter.ViewHolder holder = new AnswerRecyclerViewAdapter.ViewHolder(inflater);
            return holder;
        }

        @Override
        public void onBindViewHolder(AnswerRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.question.setText("Question " + (position + 1) + ": " + questions.get(position));
            holder.answer.setText("Answer: " + (answers.get(position) ? "True" : "False"));
        }

        @Override
        public int getItemCount() {
            return questions.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView question;
            public TextView answer;

            public ViewHolder(View itemView) {
                super(itemView);
                question = itemView.findViewById(R.id.question);
                answer = itemView.findViewById(R.id.answer);
            }
        }
    }
}