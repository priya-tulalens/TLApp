package com.parse.starter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.parse.ParseAnalytics;

/**
 * Created by peter on 9/10/15.
 */
public class FinishedSurveyActivity extends ActionBarActivity implements View.OnClickListener {
    Button returnHomeButton;
    Button surveyMenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_survey);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        this.returnHomeButton = (Button) findViewById(R.id.return_button);
        this.surveyMenuButton = (Button) findViewById(R.id.go_to_surveys_menu_button);
        this.returnHomeButton.setOnClickListener(this);
        this.surveyMenuButton.setOnClickListener(this);
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
        Intent intent;
        switch (v.getId()) {
            case R.id.return_button:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.go_to_surveys_menu_button:
                intent = new Intent(this, SurveyMenuActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }
}