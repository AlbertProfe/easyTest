package com.example.easyTest.views;

import com.example.easyTest.model.QuestionTest;
import com.example.easyTest.repository.QuestionTestRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.IntStream;

@Route("")
public class HomeView extends VerticalLayout {

    private final QuestionTestRepository repository;
    private List<QuestionTest> questions;
    private List<RadioButtonGroup<String>> answerGroups;
    private VerticalLayout quizLayout;
    private VerticalLayout resultLayout;

    @Autowired
    public HomeView(QuestionTestRepository repository) {
        this.repository = repository;

        quizLayout = new VerticalLayout();
        resultLayout = new VerticalLayout();

        add(new H1("EasyTest Quiz"));
        add(quizLayout);
        add(resultLayout);

        startQuiz();
    }

    private void startQuiz() {
        quizLayout.removeAll();
        resultLayout.removeAll();

        loadQuestions();
        createQuestionViews();
        addSubmitButton();
    }

    private void loadQuestions() {
        questions = repository.findAll().stream().limit(10).toList();
    }

    private void createQuestionViews() {
        answerGroups = IntStream.range(0, questions.size())
                .mapToObj(i -> {
                    QuestionTest q = questions.get(i);
                    VerticalLayout questionLayout = new VerticalLayout();
                    questionLayout.add(new H2("Question " + (i + 1)));
                    questionLayout.add(new Paragraph(q.getQuestion()));

                    RadioButtonGroup<String> group = new RadioButtonGroup<>();
                    group.setItems(q.getAnswer());
                    group.setItemLabelGenerator(answer -> {
                        int index = q.getAnswer().indexOf(answer);
                        return (char)('a' + index) + ". " + answer;
                    });
                    group.setLabel("Choose your answer:");
                    questionLayout.add(group);

                    quizLayout.add(questionLayout);
                    return group;
                })
                .toList();
    }



    private void addSubmitButton() {
        Button submitButton = new Button("Submit Answers", event -> checkAnswers());
        quizLayout.add(submitButton);
    }

    private void checkAnswers() {
        quizLayout.removeAll();
        resultLayout.removeAll();

        resultLayout.add(new H2("Correction"));

        int correctAnswers = 0;
        VerticalLayout summaryLayout = new VerticalLayout();

        for (int i = 0; i < questions.size(); i++) {
            QuestionTest question = questions.get(i);
            RadioButtonGroup<String> group = answerGroups.get(i);
            String selectedAnswer = group.getValue();
            String correctAnswer = question.getAnswer().get(question.getSolution());

            boolean isCorrect = selectedAnswer != null && selectedAnswer.equals(correctAnswer);
            if (isCorrect) {
                correctAnswers++;
            }

            Paragraph questionResult = new Paragraph(String.format(
                    "Question %d: %s - Your answer: %s, Correct answer: %s - %s",
                    i + 1,
                    question.getQuestion(),
                    selectedAnswer != null ? selectedAnswer : "Not answered",
                    correctAnswer,
                    isCorrect ? "Correct" : "Incorrect"
            ));
            summaryLayout.add(questionResult);
        }

        Paragraph result = new Paragraph(String.format("You got %d out of %d correct!", correctAnswers, questions.size()));
        resultLayout.add(result);
        resultLayout.add(summaryLayout);

        Button restartButton = new Button("Start Another Quiz", event -> startQuiz());
        resultLayout.add(restartButton);
    }

}
