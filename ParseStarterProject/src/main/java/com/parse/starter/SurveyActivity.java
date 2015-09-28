/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class SurveyActivity extends ActionBarActivity implements View.OnClickListener {
    Button submitButton;
    Button homeButton;
    Button editLastQuestion;
    TextView questionPrompt;
    TextView surveyProgress;
    SurveyResponse surveyResponse;
    Question q;
    LinearLayout responseLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        this.submitButton = (Button) findViewById(R.id.submit_button);
        this.submitButton.setOnClickListener(this);

        this.homeButton = (Button) findViewById(R.id.return_button);
        this.homeButton.setOnClickListener(this);

        this.editLastQuestion = (Button) findViewById(R.id.edit_last_question);
        this.editLastQuestion.setOnClickListener(this);

        this.questionPrompt = (TextView) findViewById(R.id.question_prompt);
        this.responseLayout = (LinearLayout) findViewById(R.id.response_layout);

        this.surveyProgress = (TextView) findViewById(R.id.survey_progress);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.surveyResponse = new SurveyResponse(extras.getString("surveyID"), this);
            if (this.surveyResponse.survey.questionCount > 0) {
                this.updateCurrentQuestion();
                this.updatePrompt();
            } else {
                // Alert them if there are no questions
                AlertDialog.Builder questionCountBuilder = new AlertDialog.Builder(this);
                questionCountBuilder.setMessage("This survey has no questions.  Returning home.");
                questionCountBuilder.setCancelable(true);
                questionCountBuilder.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent intent = new Intent(SurveyActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                AlertDialog questionCountAlert = questionCountBuilder.create();
                questionCountAlert.show();
            }
        }

        /* Populate questions
        Data d = new Data();
        d.create();
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_button:
                if (this.q.setAnswer(this)) {
                    this.writeResponseToDB();
                    this.updateQuestion();
                }
                break;
            case R.id.return_button:
                this.goHome();
                break;
            case R.id.edit_last_question:
                this.showLastQuestion();
                break;
        }
    }

    private void writeResponseToDB() {
        if (this.q.responses != null) {
            this.q.writeResponse(); // this.surveyResponse.uuid);
        }
    }

    private void updateQuestion() {
        int index = this.q.index;
        if (this.surveyResponse.survey.questions.getNextQuestion()) {
            this.updateCurrentQuestion();
            this.updatePrompt();
        } else {
            this.displayAlert("No more questions remaining!");
            this.finishSurvey();
        }
    }

    private void showLastQuestion() {
        if (this.surveyResponse.survey.questions.getLastQuestion()) {
            this.q = this.surveyResponse.survey.questions.currentQuestion;
            this.updatePrompt();
        } else {
            this.displayAlert("No previous question to display.");
        }
    }

    private void updateCurrentQuestion() {
        if (this.q != null) {
            int index = this.q.index; // This is the last question
            this.q = null;
            this.q = this.surveyResponse.survey.questions.currentQuestion;
            this.q.setLastAnsweredQuestionIndex(index);
        } else {
            this.q = this.surveyResponse.survey.questions.currentQuestion;
        }
    }

    private void updatePrompt() {
        this.questionPrompt.setText(this.q.prompt);

        this.responseLayout.removeAllViewsInLayout();
        this.updateResponseOptions();
        this.updateSurveyProgressText();
    }

    private void updateResponseOptions() {
        // I bet I just violated about thirty coding practices, whatever
        this.responseLayout.addView(this.q.getResponseLayout(this));
    }

    private void updateSurveyProgressText() {
        String text = "Question " + this.q.index.toString() + " out of " + this.surveyResponse.survey.questionCount.toString();
        this.surveyProgress.setText(text);
    }

    public void displayAlert(String message) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage(message);
        b.setCancelable(true);
        b.setPositiveButton("Ok",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
        b.create().show();
    }

    private void goHome() {
        // this.saveSurvey();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void finishSurvey() {
        //this.saveSurvey();
        Intent intent = new Intent(this, FinishedSurveyActivity.class);
        startActivity(intent);
        finish();
    }

    /*
    private void saveSurvey() {
        Response.saveToDB(this.surveyResponse.uuid);
    } */
}