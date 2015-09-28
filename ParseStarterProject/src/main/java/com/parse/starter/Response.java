package com.parse.starter;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;
import java.util.UUID;

/**
 * Created by peter on 9/12/15.
 */
public class Response {
    public static final String TABLE_NAME = "Response";
    private String questionID;
    private String uuid;

    public String surveyResponseUUID;
    public String content;

    public static final String STRING_KEY_UUID                  = "UUID";
    private static final String STRING_KEY_QUESTION_ID          = "questionID";
    private static final String STRING_KEY_CONTENT              = "content";
    private static final String STRING_KEY_SURVEY_RESPONSE_UUID = "surveyResponseUUID";
    public static final String STRING_KEY_SAVED_TO_CLOUD        = "hasSavedToCloud";

    public Response(String questionID, String surveyResponseUUID, String content) {
        this.questionID         = questionID;
        this.surveyResponseUUID = surveyResponseUUID;
        this.uuid               = UUID.randomUUID().toString();
        this.content            = content.trim();
    }

    public void deleteFromDB() {
        try {
            // Try to find results from local data store first
            ParseQuery<ParseObject> q = ParseQuery.getQuery(Response.TABLE_NAME);
            q.fromLocalDatastore();
            q.whereEqualTo(STRING_KEY_UUID, this.uuid);
            List<ParseObject> responses = q.find();

            if (responses.size() > 0) {
                for (ParseObject r : responses) {
                    r.deleteEventually();
                }
            }

        } catch (ParseException e) {
            Log.d("error", e.getMessage());
        }
    }

    public void save() {
        ParseObject r = new ParseObject(this.TABLE_NAME);
        r.put(this.STRING_KEY_QUESTION_ID,          this.questionID);
        r.put(this.STRING_KEY_SURVEY_RESPONSE_UUID, this.surveyResponseUUID);
        r.put(this.STRING_KEY_CONTENT,              this.content);
        r.put(this.STRING_KEY_UUID,                 this.uuid);
        r.put(this.STRING_KEY_SAVED_TO_CLOUD,       false);
        r.pinInBackground();
    }

    /*
    public static void saveToDB(String surveyResponseUUID) {
        ParseQuery<ParseObject> q = ParseQuery.getQuery(Response.TABLE_NAME);
        q.whereEqualTo(Response.STRING_KEY_SAVED_TO_CLOUD, false);
        q.fromLocalDatastore();

        try {
            List<ParseObject> responses = q.find();
            int x = responses.size();
            if (responses.size() > 0) {
                for (final ParseObject r : responses) {
                    r.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                r.put(Response.STRING_KEY_SAVED_TO_CLOUD, true);
                            } else {
                                Log.d("error", e.getMessage());
                            }
                        }
                    });
                }
            }
        } catch (ParseException e) {
                Log.d("error", e.getMessage());
        }
    } */
}