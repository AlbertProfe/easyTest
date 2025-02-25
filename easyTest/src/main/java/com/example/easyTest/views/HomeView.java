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

    @Autowired
    public HomeView(QuestionTestRepository repository) {
        this.repository = repository;

        add(new H1("EasyTest Quiz"));

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
                    group.setItems("a", "b", "c", "d");
                    group.setLabel("Choose your answer:");
                    questionLayout.add(group);

                    add(questionLayout);
                    return group;
                })
                .toList();
    }

    private void addSubmitButton() {
        Button submitButton = new Button("Submit Answers", event -> checkAnswers());
        add(submitButton);
    }

    private void checkAnswers() {
        int correctAnswers = 0;
        for (int i = 0; i < questions.size(); i++) {
            QuestionTest question = questions.get(i);
            RadioButtonGroup<String> group = answerGroups.get(i);
            String selectedAnswer = group.getValue();
            if (selectedAnswer != null && selectedAnswer.equals(String.valueOf((char)('a' + question.getSolution())))) {
                correctAnswers++;
            }
        }

        Paragraph result = new Paragraph("You got " + correctAnswers + " out of " + questions.size() + " correct!");
        add(result);
    }
}
