package com.ebookfrenzy.quizappeksamen;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ebookfrenzy.quizappeksamen.Database.QuizItem;
import com.ebookfrenzy.quizappeksamen.ViewModel.QuizItemViewModel;
import com.ebookfrenzy.quizappeksamen.databinding.FragmentQuizBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuizFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuizFragment extends Fragment {
    private FragmentQuizBinding binding;
    private QuizItemViewModel viewModel;
    private List<QuizItem> unusedQuestions = new ArrayList<>();
    private List<QuizItem> usedQuestions   = new ArrayList<>();
    private QuizItem currentQuestion;
    private Random random = new Random();
    private static final int COLOR_CORRECT = Color.parseColor("#4CAF50");
    private static final int COLOR_INCORRECT = Color.parseColor("#F44336");
    private static final int COLOR_NEUTRAL = Color.LTGRAY;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public QuizFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QuizFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QuizFragment newInstance(String param1, String param2) {
        QuizFragment fragment = new QuizFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        viewModel = new ViewModelProvider(this).get(QuizItemViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentQuizBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set up UI components, listeners, and observers here
        // Initialize quiz logic
        // Connect to ViewModel data
        viewModel.getAllQuizItems().observe(getViewLifecycleOwner(), allItems -> {
            if (unusedQuestions.isEmpty() && usedQuestions.isEmpty()) {
                unusedQuestions.addAll(Objects.requireNonNull(viewModel.getAllQuizItems().getValue()));
                newQuestion();
            }
        });
        viewModel.getTotalGuesses().observe(getViewLifecycleOwner(), total -> updateScore());
        viewModel.getCorrectGuesses().observe(getViewLifecycleOwner(), correct -> updateScore());

        View.OnClickListener answerClickListener = v -> checkIfCorrectGuess(v);
        binding.buttonA.setOnClickListener(answerClickListener);
        binding.buttonB.setOnClickListener(answerClickListener);
        binding.buttonC.setOnClickListener(answerClickListener);


    }

    private void checkIfCorrectGuess(View v) {
        viewModel.incrementTotalGuesses();

        String valgtSvar = "";
        if (v.getId() == binding.buttonA.getId()) {
            valgtSvar = binding.buttonA.getText().toString();
        } else if (v.getId() == binding.buttonB.getId()) {
            valgtSvar = binding.buttonB.getText().toString();
        } else if (v.getId() == binding.buttonC.getId()) {
            valgtSvar = binding.buttonC.getText().toString();
        }

        String riktigSvar = currentQuestion.getImageName();
        if (valgtSvar.equals(riktigSvar)) {
            viewModel.incrementCorrectGuesses();
        }

        if (binding.buttonA.getText().toString().equals(riktigSvar)) {
            binding.buttonA.setBackgroundColor(COLOR_CORRECT);
            binding.buttonB.setBackgroundColor(COLOR_INCORRECT);
            binding.buttonC.setBackgroundColor(COLOR_INCORRECT);
        } else if (binding.buttonB.getText().toString().equals(riktigSvar)) {
            binding.buttonB.setBackgroundColor(COLOR_CORRECT);
            binding.buttonA.setBackgroundColor(COLOR_INCORRECT);
            binding.buttonC.setBackgroundColor(COLOR_INCORRECT);
        } else if (binding.buttonC.getText().toString().equals(riktigSvar)) {
            binding.buttonC.setBackgroundColor(COLOR_CORRECT);
            binding.buttonB.setBackgroundColor(COLOR_INCORRECT);
            binding.buttonA.setBackgroundColor(COLOR_INCORRECT);
        }

        v.postDelayed(this::newQuestion, 1500);
    }

    private void newQuestion() {
        if (unusedQuestions.isEmpty()) {
            Toast.makeText(this.getContext(), "You got " + viewModel.getCorrectGuesses().getValue() + "/" +
                    viewModel.getTotalGuesses().getValue() + " correct", Toast.LENGTH_SHORT).show();
            resetQuiz();
            return;
        }

        binding.buttonA.setBackgroundColor(COLOR_NEUTRAL);
        binding.buttonB.setBackgroundColor(COLOR_NEUTRAL);
        binding.buttonC.setBackgroundColor(COLOR_NEUTRAL);

        int index = random.nextInt(unusedQuestions.size());
        currentQuestion = unusedQuestions.remove(index);
        usedQuestions.add(currentQuestion);

        try {
            Glide.with(requireContext())
                    .load(Uri.parse(currentQuestion.getImageUri()))
                    .into(binding.quizBilde);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error loading image", Toast.LENGTH_SHORT).show();
        }

        setUpButtons();
    }

    private void setUpButtons() {
        String correctAnswer = currentQuestion.getImageName();

        List<QuizItem> allQuizItems = viewModel.getAllQuizItems().getValue();
        if (allQuizItems == null || allQuizItems.size() < 3) {
            // Handle case when there aren't enough quiz items
            Toast.makeText(getContext(), "Not enough quiz items for multiple choice", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> answerOptions = new ArrayList<>();
        answerOptions.add(correctAnswer);

        int attemptsLimit = 20;
        while (answerOptions.size() < 3 && attemptsLimit > 0) {
            int randomIndex = random.nextInt(allQuizItems.size());
            String randomAnswer = allQuizItems.get(randomIndex).getImageName();

            if (!answerOptions.contains(randomAnswer)) {
                answerOptions.add(randomAnswer);
            }

            attemptsLimit--;
        }

        Collections.shuffle(answerOptions);

        binding.buttonA.setText(answerOptions.get(0));
        binding.buttonB.setText(answerOptions.get(1));
        binding.buttonC.setText(answerOptions.get(2));
    }

    private void resetQuiz() {
        unusedQuestions.addAll(usedQuestions);
        usedQuestions.clear();

        viewModel.getCorrectGuesses().setValue(0);
        viewModel.getTotalGuesses().setValue(0);

        newQuestion();
    }

    private void updateScore() {
        Integer correct = viewModel.getCorrectGuesses().getValue();
        Integer total = viewModel.getTotalGuesses().getValue();

        if (correct != null && total != null) {
            String tekst = correct + "/" + total;
            binding.score.setText(tekst);
        }
    }
}