package com.parse.starter;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by peter on 8/28/15.
 */
public class Survey {
    public static final String TABLE_NAME = "tlSurvey";
    private static final String STRING_KEY_NAME = "name";

    public String id;
    public String name;
    public QuestionCollection questions;
    public Integer questionCount;

    public Survey() {}

    public Survey(String id) {
        this.id = id;
        try {
            this.loadQuestions();
        } catch (ParseException e) {
            Log.d("error", e.getMessage());
        }
    }

    public void loadQuestions() throws ParseException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Question.TABLE_NAME);
        query.fromLocalDatastore();
        query.whereEqualTo("surveyID", this.id);
        this.questions = new QuestionCollection(query.find());
        this.questionCount = this.questions.size();
    }
}
