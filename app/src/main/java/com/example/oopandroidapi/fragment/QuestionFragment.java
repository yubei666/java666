package com.example.oopandroidapi.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.oopandroidapi.R;

public class QuestionFragment extends Fragment {
    private String question;
    private Boolean answer;
    private TextView questionText;
    private RadioButton answerTrue;
    private RadioButton answerFalse;

    public QuestionFragment(String question, Boolean answer) {
        this.question = question;
        this.answer = answer;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.getActivity().findViewById(R.id.next).setEnabled(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question, container, false);
        questionText = view.findViewById(R.id.question_text_view);
        answerTrue = view.findViewById(R.id.answer_true);
        answerFalse = view.findViewById(R.id.answer_false);
        questionText.setText(question);

        answerTrue.setOnClickListener(v -> {
            this.getActivity().findViewById(R.id.next).setEnabled(true);
        });
        answerFalse.setOnClickListener(v -> {
            this.getActivity().findViewById(R.id.next).setEnabled(true);
        });

        return view;
    }
}