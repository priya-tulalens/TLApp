package com.parse.starter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by peter on 8/26/15.
 */
public class Question {
    public static final String TABLE_NAME = "Question";

    private static final String SURVEY_ID = "A6NVv9zTXz";

    // For sake of consistency
    private static final String STRING_KEY_PROMPT                 = "prompt";
    private static final String STRING_KEY_RESPONSE_TYPE          = "responseType";
    private static final String STRING_KEY_RESPONSE_OPTIONS       = "responseOptions";
    private static final String STRING_KEY_SURVEY_ID              = "surveyID";
    private static final String STRING_KEY_IS_CONDITIONAL         = "isConditional";
    private static final String STRING_KEY_INDEX                  = "index";
    private static final String STRING_KEY_NEXT_INDEX             = "nextIndex";
    private static final String STRING_KEY_CONDITIONAL_NEXT_INDEX = "conditionalNextIndex";
    private static final String STRING_KEY_FREETEXT_TYPE          = "freeTextType";

    public static final int RESPONSE_FREETEXT = 1;
    public static final int RESPONSE_RADIO    = 2;
    public static final int RESPONSE_CHECKBOX = 3;

    private static final int FREETEXT_STRING       = 1;
    private static final int FREETEXT_DATE         = 2;
    private static final int FREETEXT_INTEGER      = 3;
    private static final int FREETEXT_PHONE_NUMBER = 4;

    public static final String FREETEXT_DATE_ERROR_MESSAGE = "Please enter a date in the following format: MM-DD-YYYY";
    public static final String FREETEXT_INTEGER_ERROR_MESSAGE = "Please enter a valid number.";
    public static final String FREETEXT_PHONE_NUMBER_ERROR_MESSAGE = "Please enter a valid phone number (10 or 11 digits).";

    public String id;
    public String prompt;
    public int responseType;
    public Integer freetextType;
    public String[] responseOptions;
    public List<Response> responses;
    public String surveyID;
    public boolean isConditional;
    public Integer index;
    public int nextIndex;
    public Integer conditionalNextIndex;

    // Listing out response options
    public RadioGroup radioGroup;
    public List<CheckBox> checkBoxes;
    public EditText editText;

    public LinearLayout responseLayout;

    public boolean hasBeenSubmitted;
    public int selectedRadioButtonID;

    // This is only implemented so we can go back and answer previous questions
    public Integer lastAnsweredQuestionIndex;

    /* public Question() {} */

    public Question(ParseObject q) {
        this.id = q.getObjectId();
        this.prompt = q.getString(Question.STRING_KEY_PROMPT);
        this.responseType = q.getInt(Question.STRING_KEY_RESPONSE_TYPE);

        if ((Integer)q.getInt(Question.STRING_KEY_FREETEXT_TYPE) != null) {
            this.freetextType = q.getInt(Question.STRING_KEY_FREETEXT_TYPE);
        }

        if (q.getString(Question.STRING_KEY_RESPONSE_OPTIONS) != null) {
            this.responseOptions = q.getString(Question.STRING_KEY_RESPONSE_OPTIONS).split(";");
        }

        this.surveyID = q.getString(Question.STRING_KEY_SURVEY_ID);
        this.isConditional = q.getBoolean(Question.STRING_KEY_IS_CONDITIONAL);
        this.index = q.getInt(Question.STRING_KEY_INDEX);
        this.nextIndex = q.getInt(Question.STRING_KEY_NEXT_INDEX);
        this.conditionalNextIndex = q.getInt(Question.STRING_KEY_CONDITIONAL_NEXT_INDEX);

        this.responses = new ArrayList<Response>();
        this.hasBeenSubmitted = false;
    }

    public void writeResponse() {
        for (Response r : this.responses) {
            r.save();
        }
        this.hasBeenSubmitted = true;
    }

    public static void createNew(
            String prompt,
            int responseType,
            int index,
            int nextIndex,
            boolean isConditional,
            Integer conditionalNextIndex, // Using wrapper class so we can pass null
            String[] responseOptions
    ) {
        ParseObject q = new ParseObject(Question.TABLE_NAME);

        q.put(Question.STRING_KEY_PROMPT, prompt);
        q.put(Question.STRING_KEY_RESPONSE_TYPE, responseType);

        if (responseOptions != null) {
            q.put(Question.STRING_KEY_RESPONSE_OPTIONS, TextUtils.join(";", responseOptions));
        }

        q.put(Question.STRING_KEY_INDEX, index);
        q.put(Question.STRING_KEY_NEXT_INDEX, nextIndex);
        q.put(Question.STRING_KEY_IS_CONDITIONAL, isConditional);

        if (conditionalNextIndex != null) {
            q.put(Question.STRING_KEY_CONDITIONAL_NEXT_INDEX, conditionalNextIndex);
        }

        q.put(Question.STRING_KEY_SURVEY_ID, Question.SURVEY_ID);

        q.saveInBackground();
    }

    public static void createNewFreeText(String prompt, int index) {
        Question.createNew(prompt, Question.RESPONSE_FREETEXT, index, index + 1, false, null, null);
    }

    public static void createNewRadio(String prompt, int index, String[] responseOptions) {
        Question.createNew(prompt, Question.RESPONSE_RADIO, index, index + 1, false, null, responseOptions);
    }

    public static void createNewCheckBox(String prompt, int index, String[] responseOptions) {
        Question.createNew(prompt, Question.RESPONSE_CHECKBOX, index, index + 1, false, null, responseOptions);
    }

    public static void createNewConditional(String prompt, int index, int nextIndex, Integer conditionalNextIndex) {
        Question.createNew(prompt, Question.RESPONSE_RADIO, index, nextIndex, true, conditionalNextIndex, new String[]{"Yes", "No"});
    }

    public static void createNewYesNo(String prompt, int index) {
        Question.createNewConditional(prompt, index, index + 1, null);
    }

    public void setLastAnsweredQuestionIndex(int index) {
        this.lastAnsweredQuestionIndex = index;
    }

    public boolean isAcceptableFreeTextResponse(String responseToTest) {
        String regexString = "";
        boolean success = false;

        switch (this.freetextType) {
            case Question.FREETEXT_STRING:
                // If it's just a freetext string
                // they can write whatever they want
                success = true;
            case Question.FREETEXT_INTEGER:
                // Double escape for this regex (TEST - may need more escapes)
                regexString = "^\\d+$";
                break;
            case Question.FREETEXT_PHONE_NUMBER:
                // Just check that it's a 10 digit string
                regexString = "^(\\d{10}|\\d{11})$";
                // Strip out everything that's not integers from the phone number
                // This makes it a lot easier than dictating a precise phone number format
                responseToTest = responseToTest.replaceAll("[\\D]", "");
                break;
            case Question.FREETEXT_DATE:
                regexString = "^\\d{2}-\\d{2}-\\d{4}$";
                break;
            default:
                break;
        }

        if (!success) {
            Pattern regexPattern = Pattern.compile(regexString);
            // Since it's freetext we can just get the first item in the response list
            Matcher regexMatcher = regexPattern.matcher(responseToTest);
            success = regexMatcher.matches();
        }
        return success;
    }

    public LinearLayout getResponseLayout(Activity activity) {
        // if the response has already been submitted
        // then we already have a layout with our answers!
        if (this.hasBeenSubmitted) {
            // delete existing responses from the DB so we can write new ones
            for (Response r : this.responses) {
                r.deleteFromDB();
            }

            // Initialize as new list to get rid of all existing responses
            this.responses = new ArrayList<Response>();

        } else {
            this.responseLayout = new LinearLayout(activity);
            this.responseLayout.setOrientation(LinearLayout.VERTICAL);

            switch (this.responseType) {
                case Question.RESPONSE_FREETEXT:
                    this.editText = new EditText(activity);

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

                    this.responseLayout.addView(this.editText, lp);
                    this.editText.requestFocus();

                    break;

                case Question.RESPONSE_RADIO:
                     this.radioGroup = new RadioGroup(activity);
                    // Create a radio group, add buttons to it.
                    // Add the radio group to the response layout
                    RadioGroup.LayoutParams lp2 = new RadioGroup.LayoutParams(
                            RadioGroup.LayoutParams.WRAP_CONTENT,
                            RadioGroup.LayoutParams.WRAP_CONTENT
                    );

                    for (String responseOption : this.responseOptions) {
                        RadioButton r = new RadioButton(activity);
                        r.setText(responseOption);
                        r.setTextSize(20);
                        this.radioGroup.addView(r, lp2);
                    }

                    this.responseLayout.addView(this.radioGroup);

                    break;

                case Question.RESPONSE_CHECKBOX:
                    this.checkBoxes = new ArrayList<CheckBox>();
                    // Add the radio group to the response layout

                    LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

                    for (String responseOption : this.responseOptions) {
                        CheckBox c = new CheckBox(activity);
                        c.setText(responseOption);
                        c.setTextSize(20);
                        this.checkBoxes.add(c);
                    }

                    for (CheckBox c : this.checkBoxes) {
                        this.responseLayout.addView(c, lp3);
                    }
                    break;
                default:
                    break;
            }
        }

        return this.responseLayout;
    }

    public void createResponse(String surveyResponseUUID, String content) {
        this.responses.add(new Response(this.id, surveyResponseUUID, content));
    }

    public boolean setAnswer(SurveyActivity surveyActivity) {
        boolean success = false;
        String responseContent = "";
        String errorMessage = "";

        switch (this.responseType) {
            case Question.RESPONSE_FREETEXT:
                responseContent = this.editText.getText().toString();
                if (!responseContent.equalsIgnoreCase("")) {
                    if (this.isAcceptableFreeTextResponse(responseContent)) {
                        this.createResponse(surveyActivity.surveyResponse.uuid, responseContent);
                        success = true;
                    } else {
                        switch (this.freetextType) {
                            case Question.FREETEXT_INTEGER:
                                errorMessage = Question.FREETEXT_INTEGER_ERROR_MESSAGE;
                                break;
                            case Question.FREETEXT_PHONE_NUMBER:
                                errorMessage = Question.FREETEXT_PHONE_NUMBER_ERROR_MESSAGE;
                                break;
                            case Question.FREETEXT_DATE:
                                errorMessage = Question.FREETEXT_DATE_ERROR_MESSAGE;
                                break;
                            default:
                                break;
                        }
                        success = false;
                    }
                } else {
                    errorMessage = "Please enter a response.";
                    success = false;
                }
                break;
            case Question.RESPONSE_RADIO:
                this.selectedRadioButtonID = this.radioGroup.getCheckedRadioButtonId();
                if (this.selectedRadioButtonID != -1) {
                    RadioButton rb = (RadioButton) surveyActivity.findViewById(this.selectedRadioButtonID);
                    this.createResponse(surveyActivity.surveyResponse.uuid, rb.getText().toString());
                    success = true;
                } else {
                    errorMessage = "Please select a response.";
                    success = false;
                }
                break;
            case Question.RESPONSE_CHECKBOX:
                for (CheckBox c : this.checkBoxes) {
                    if (c.isChecked()) {
                        this.createResponse(surveyActivity.surveyResponse.uuid, c.getText().toString());
                    }
                }
                success = true;
                break;
            default:
                break;
        }

        if (!success) {
            surveyActivity.displayAlert(errorMessage);
        }

        return success;
    }

}