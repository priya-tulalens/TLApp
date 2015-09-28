package com.parse.starter;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.parse.ParseObject;

import java.util.UUID;

/**
 * Created by peter on 8/30/15.
 */
public class SurveyResponse {
    public Survey survey;
    public String id;
    public String uuid;
    public String deviceID;

    public static final String TABLE_NAME = "surveyResponse";
    private static final String STRING_KEY_SURVEY_ID = "surveyID";
    private static final String STRING_KEY_UUID = "UUID";
    private static final String STRING_KEY_DEVICE_ID = "deviceID";
    public static final String STRING_KEY_SAVED_TO_CLOUD = "hasSavedToCloud";

    public SurveyResponse(String surveyID, Activity activity) {
        this.survey = new Survey(surveyID);
        // If this is only saved locally, we won't have an object ID.
        // So we use the UUID as the foreign key mapping responses to a particular surveyResponse
        this.uuid = UUID.randomUUID().toString();
        //this.deviceID = Settings.Secure.ANDROID_ID.toString();

        this.deviceID = null;

        TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);

        if (tm != null) {
            this.deviceID = tm.getDeviceId();
        }

        if (this.deviceID == null || this.deviceID.length() == 0) {
            this.deviceID = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        ParseObject surveyResponse = new ParseObject(this.TABLE_NAME);
        surveyResponse.put(this.STRING_KEY_SURVEY_ID, this.survey.id);
        surveyResponse.put(this.STRING_KEY_UUID, this.uuid);
        surveyResponse.put(this.STRING_KEY_DEVICE_ID, this.deviceID);
        surveyResponse.put(this.STRING_KEY_SAVED_TO_CLOUD, false);

        surveyResponse.pinInBackground();
    }
}
