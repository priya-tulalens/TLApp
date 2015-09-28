package com.parse.starter;

/**
 * Created by peter on 9/4/15.
 */
public class Data {
    int index;

    public Data() {
        this.index = 1;
    }

    public void create() {
        this.createNewQuestions();
    }

    private void createNewSurvey() {}

    private void freeText(String prompt) {
        Question.createNewFreeText(prompt, this.index++);
    }

    private void radio(String prompt, String[] responseOptions) {
        Question.createNewRadio(prompt, this.index++, responseOptions);
    }

    private void checkBox(String prompt, String[] responseOptions) {
        Question.createNewCheckBox(prompt, this.index++, responseOptions);
    }

    private void conditional(String prompt, int questionsToSkipForNo, int questionsToSkipForYes) {
        Question.createNewConditional(prompt, this.index++, this.index + questionsToSkipForNo, this.index + questionsToSkipForYes);
    }

    private void yesNo(String prompt) {
        Question.createNewYesNo(prompt, this.index++);
    }

    private void createNewQuestions() {
        this.freeText("1) House address or landmark");
        this.freeText("2)  Area name");
        this.freeText("3) First Name");
        this.freeText("4) Last Name");
        this.freeText("5) Husband or father's name");
        this.freeText("6) Age in completed years");
        this.radio("7) Marital Status", new String[]{"Single", "Married", "Divorced", "Widowed"});
        this.freeText("8) Occupation");
        this.radio("9) Highest level of education", new String[]{"High School", "Bachelors", "Masters", "Doctorate"});
        this.radio("10) Woman's religion", new String[]{"Religion 1", "Religion 2", "Religion 3"});
        this.checkBox("11) What languages do you speak?", new String[]{"Tamil", "Urdu", "Hindi", "English"});
        this.checkBox("12) Can you read and write?", new String[]{"Read", "Write"});
        this.conditional("13) Do you own a mobile phone?", 0, 2);
        this.conditional("14) Does someone in your house own a mobile phone?", 1, 0);
        this.freeText("15) If yes, who owns the phone?");
        this.freeText("16) If yes, can you please share your number?");
        this.freeText("17) What is your individual income per day on average?");
        this.freeText("18) How many people live in your household currently?");
        this.freeText("19) What is your household income on average per day?");
        this.freeText("20) How many children do you have that are alive?");
        this.conditional("21) Are you pregnant?", 0, 0);
        this.freeText("22) How many months pregnant are you?");
        this.freeText("23) Have you received prenatal care during this pregnancy at a facility?");
        this.freeText("24) How many prenatal care visits have you completed in total during this pregnancy?");
        this.freeText("25) How many months pregnant were you when you first received prenatal care?");
        this.freeText("26) How many months pregnant were you when you last received prenatal care?");
        this.freeText("27) When did you last receive prenatal care? (Record month, day, year)");
        this.checkBox("28) For which of the following services did you go to the health center for in the last two years:", new String[] {"Option 1", "Option 2"});
        this.freeText("29) If you didn't go to the health facility for any of these reasons, why did you last go?");
        this.freeText("30) What was the date you last received these services? (Record month, day, year)");
        this.freeText("31) When did the doctor ask you to return for reproductive health services?");
        this.freeText("32) What was the name of the health facility where you last went to?");
        this.freeText("33) What was the address of the health facility?");
        this.freeText("34) How far was the health facility from your house in km?");
        this.radio("35) What type of facility is this?", new String[] {"Option 1", "Option 2"});
        this.freeText("36) Why did you go to this particular health facility (do not read options when you ask the question)?");
        this.freeText("37) Was the doctor present when you arrived at the clinic?");
        this.freeText("38) How many minutes did you spend with the doctor?");
        this.freeText("39) How many minutes did you spend taking tests?");
        this.freeText("40) How many minutes did you wait in total?");
        this.radio("41) Do you think the waiting time was:", new String[] {"Option 1", "Option 2"});
        this.freeText("42) What type of provider did you see for the consultation?");
        this.radio("43) What was the provider’s gender?:", new String[] {"Male", "Female"});
        this.freeText("44) How did the provider treat you?");
        this.yesNo("45) Did he/she address all of your issues?");
        this.yesNo("46) Did the provider explain what he/she was doing during the check up?");
        this.yesNo("47) Did you pay any fee for your services?");
        this.freeText("48) How much did you pay for the consultation");
        this.conditional("49) Did you receive diagnostic tests?", 2, 0);
        this.yesNo("50) Did you receive diagnostic tests at this facility?");
        this.freeText("51) How much did you pay for the diagnostic tests");
        this.conditional("52) Did you receive scanning?", 2, 0);
        this.yesNo("53) Did you receive scanning at this facility?");
        this.freeText("54) How much did you pay for the scanning");
        this.conditional("55) Did you receive other services?", 3, 0);
        this.freeText("56) What service did you receive?");
        this.freeText("57) How much did you pay for this service?");
        this.freeText("58) In total, how much did you pay for this service?");
        this.radio("59) Was the cost: too much, reasonable, minimal", new String[] {"Too Much", "Reasonable", "Minimal"});
        this.freeText("60) How much did you pay in bribes for services you received?");
        this.yesNo("61) Did you have privacy during the exam?");
        this.yesNo("62) Were you satisfied with the cleanliness of the facility");
        this.radio("63) How would you rate your overall experience (read options below)?:", new String[] {"Option 1", "Option 2", "Option 3"});
        this.freeText("64) What did you like about the services?");
        this.freeText("65)  What can be improved at this facility?");
        this.yesNo("66) Are you aware of any other health centers that provide the services you received other than this on in your area?");
        this.freeText("67) How many other facilities?");
        this.yesNo("68) Do you have information on the quality of these facilities?");
        this.freeText("69) Where did you get this information?");
        this.yesNo("70) Would you like more information on the different facilities and their quality in your area?");
        this.yesNo("71) Did the last facility you received care from ever ask you for feedback on the quality of care you received or your overall experience?");
    }
}