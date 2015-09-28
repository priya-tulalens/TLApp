/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.HashMap;
import java.util.List;

public class SurveyMenuActivity extends ActionBarActivity implements View.OnClickListener {
    EditText respondentName;
    HashMap<String, String> surveyIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_menu);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        this.surveyIDs = new HashMap<String, String>();

        try {
            this.populateAvailableSurveys();
        } catch (ParseException e) {
            Log.d("error", e.getMessage());
        }
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
        Intent intent = new Intent(this, SurveyActivity.class);
        String surveyName = ((Button) v).getText().toString();
        intent.putExtra("surveyID", this.surveyIDs.get(surveyName));
        startActivity(intent);
        finish();
    }

    public void populateAvailableSurveys() throws ParseException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Survey.TABLE_NAME);
        query.fromLocalDatastore();
        List<ParseObject> surveys = query.find();

        LinearLayout ll = (LinearLayout)findViewById(R.id.main_layout);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        if (surveys.size() > 0) {
            for (ParseObject s : surveys) {
                String surveyName = s.getString("name");
                this.surveyIDs.put(surveyName, s.getObjectId());
                Button b = new Button(this);
                b.setText(surveyName);
                b.setWidth(260); // 260 pixels
                b.setHeight(85); // 85 pixels
                b.setTextSize(20);

                b.setOnClickListener(this);
                ll.addView(b, lp);
            }
        } else {
            // Alert that there are no surveys, suggest that we need to sync the surveys.
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setMessage("There are not currently any surveys.  Please go to options and sync surveys.");
            alertBuilder.setCancelable(true);
            alertBuilder.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(SurveyMenuActivity.this, OptionsActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        }
    }
}