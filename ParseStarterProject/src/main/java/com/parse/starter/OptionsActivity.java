package com.parse.starter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by peter on 9/7/15.
 */
public class OptionsActivity extends ActionBarActivity implements View.OnClickListener {
    Button returnHomeButton;
    Button syncSurveysButton;
    Button refreshUnsyncedResponseCountButton;
    TextView unsyncedResponseCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        this.syncSurveysButton = (Button) findViewById(R.id.sync_surveys_button);
        this.syncSurveysButton.setOnClickListener(this);

        this.returnHomeButton = (Button) findViewById(R.id.return_home_button);
        this.returnHomeButton.setOnClickListener(this);

        this.refreshUnsyncedResponseCountButton = (Button) findViewById(R.id.refresh_unsynced_responses);
        this.refreshUnsyncedResponseCountButton.setOnClickListener(this);

        this.unsyncedResponseCount = (TextView) findViewById(R.id.unsynced_responses);
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
        int v_id = v.getId();

        switch (v_id) {
            case R.id.return_home_button:
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("surveyID", ((Button) v).getText().toString());
                startActivity(intent);
                finish();
                break;
            case R.id.sync_surveys_button:
                this.syncData();
                break;
            case R.id.refresh_unsynced_responses:
                try {
                    this.updateLocalResponseCount();
                } catch (ParseException e) {
                    Log.d("error", e.getMessage());
                }
                break;
            default:
                break;
        }
    }

    public void syncData() {
        if (this.canSyncWithParseCloud()) {
            this.syncDataToLocalStore(Survey.TABLE_NAME);
            this.syncDataToLocalStore(Question.TABLE_NAME);
            this.alert("Surveys have synced.");
        } else {
            this.alert("You are not connected to WiFi.  Please connect to WiFi and try again.");
        }
    }

    public void syncDataToLocalStore(String dataName) {
        try {
            // Out with the old...
            ParseQuery<ParseObject> unpinQuery = ParseQuery.getQuery(dataName);
            unpinQuery.fromLocalDatastore();
            List<ParseObject> results = unpinQuery.find();
            ParseObject.unpinAllInBackground(results);

            // ...in with the new
            ParseQuery<ParseObject> pinQuery = ParseQuery.getQuery(dataName);
            List<ParseObject> objectList = pinQuery.find();
            ParseObject.pinAllInBackground(objectList);
        } catch (ParseException e) {
            Log.d("error", e.getMessage());
        }
    }

    private void saveResponses(String objectName) throws ParseException {
        ParseQuery<ParseObject> q = ParseQuery.getQuery(objectName);
        q.whereEqualTo(Response.STRING_KEY_SAVED_TO_CLOUD, false);
        q.fromLocalDatastore();
        Integer objectCount = q.count();

        final List<ParseObject> objects = q.find();

        if (this.canSyncWithParseCloud()) {
            ParseObject.saveAllInBackground(objects, new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        for (ParseObject o : objects) {
                            o.put(Response.STRING_KEY_SAVED_TO_CLOUD, true);
                        }
                    } else {
                        Log.d("error", e.getMessage());
                    }
                }
            });
            this.unsyncedResponseCount.setText(objectName + " Synced");
        }
    }

    private void updateLocalResponseCount() throws ParseException {
        Log.d("error", "entered function");
        ParseQuery<ParseObject> q = ParseQuery.getQuery(Response.TABLE_NAME);
        q.whereEqualTo(Response.STRING_KEY_SAVED_TO_CLOUD, false);
        q.fromLocalDatastore();
        Integer responseCount = q.count();
        this.alert(responseCount.toString() + " Responses to Sync");

        if (this.canSyncWithParseCloud()) {
            Log.d("error", "can sync with parse cloud");
            try {
                this.saveResponses(SurveyResponse.TABLE_NAME);
                this.saveResponses(Response.TABLE_NAME);
            } catch (ParseException e) {
                Log.d("error", e.getMessage());
            }
        } else {
            this.alert("You are not connected to WiFi.  Please connect to WiFi and try again.");
        }
    }

    public void alert(String message) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage(message);
        b.setCancelable(true);
        b.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog a = b.create();
        a.show();
    }

    public boolean canSyncWithParseCloud() {
        boolean canSyncWithParseCloud = false;

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null) { // If Wi-Fi is disabled, activeNetwork will be null
            boolean isConnected = activeNetwork.isConnectedOrConnecting();
            boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;

            canSyncWithParseCloud = (isConnected && isWiFi);
        }

        return canSyncWithParseCloud;
    }

}