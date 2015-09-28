package com.parse.starter;

import com.parse.ParseObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by peter on 8/28/15.
 */
public class QuestionCollection extends HashMap<Integer, Question> {
    public static final int FIRST_QUESTION_INDEX = 1;
    public Question currentQuestion;

    public QuestionCollection(List<ParseObject> qList) {

        for (int i = 0; i < qList.size(); i++) {
            Question q = new Question(qList.get(i));

            this.put(q.index, q);
        }

        this.currentQuestion = this.get(this.FIRST_QUESTION_INDEX);
    }

    public boolean getNextQuestion() {
        Question q = this.currentQuestion;
        int nextIndex;
        boolean success = false;

        if (q.isConditional && q.responses.get(0).content.equalsIgnoreCase("Yes") && q.conditionalNextIndex != null) {
            nextIndex = q.conditionalNextIndex;
        } else {
            nextIndex = q.nextIndex;
        }

        if (this.containsKey(nextIndex)) {
            this.currentQuestion = this.get(nextIndex);
            success = true;
        }

        return success;
    }

    public boolean getLastQuestion() {
        boolean success = false;

        if (this.currentQuestion.lastAnsweredQuestionIndex != null) {
            this.currentQuestion = this.get(this.currentQuestion.lastAnsweredQuestionIndex);
            success = true;
        }

        return success;
    }

    public boolean hasNextQuestion() {
        return this.containsKey(this.currentQuestion.index + 1);
    }
}
