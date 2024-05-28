package com.roaaserver.placementstudent.StudentInfo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.roaaserver.placementstudent.R;


public class EducationDetails extends AppCompatActivity {
    private static final String TAG = "EducationDetails";
    private ImageButton backButton;
    private TextInputLayout sscLayout, hscLayout, diplomaLayout, yearLayout, branchLayout, classLayout, divisionLayout, sscYearLayout,
            sscCollegeLayout, hscYearLayout, hscCollegeLayout, diplomaYearLayout, diplomaCollegeLayout;
    private TextInputEditText ssc, year, sscYear, hscYear, diplomaYear, sscCollege, hscCollege, diplomaCollege;
    private AutoCompleteTextView branch, hsc, diploma, classField, division;
    private MaterialButton saveButton;
    private ProgressBar progressBar, mainProgressBar;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_details);

        initFields();
        backButton.setOnClickListener(view -> onBackPressed());
        branchAdapter();
        hscAdapter();
        diplomaAdapter();
        classesAdapter();
        divisionAdapter();

        setUpFields();

        ssc.addTextChangedListener(watcher);
        sscYear.addTextChangedListener(watcher);
        hsc.addTextChangedListener(watcher);
        hscYear.addTextChangedListener(watcher);
        diploma.addTextChangedListener(watcher);
        year.addTextChangedListener(watcher);
        classField.addTextChangedListener(watcher);
        division.addTextChangedListener(watcher);
        branch.addTextChangedListener(watcher);
        division.addTextChangedListener(watcher);

        saveButton.setOnClickListener(view -> checkDetails());

        hsc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hscYearLayout.setError("");
                hscYearLayout.setBoxBackgroundColor(getColor(R.color.fargrey));
                hscYearLayout.setEnabled(false);
                hscYearLayout.setErrorEnabled(false);
                hscCollegeLayout.setError("");
                hscCollegeLayout.setBoxBackgroundColor(getColor(R.color.fargrey));
                hscCollegeLayout.setEnabled(false);
                hscCollegeLayout.setErrorEnabled(false);
            }
        });

        diploma.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                diplomaYearLayout.setBoxBackgroundColor(getColor(R.color.fargrey));
                diplomaYearLayout.setError("");
                diplomaYearLayout.setEnabled(false);
                diplomaYearLayout.setErrorEnabled(false);
                diplomaCollegeLayout.setBoxBackgroundColor(getColor(R.color.fargrey));
                diplomaYearLayout.setError("");
                diplomaCollegeLayout.setEnabled(false);
                diplomaCollegeLayout.setErrorEnabled(false);
            }
        });
    }

    private void setUpFields() {

        scrollView.setVisibility(View.GONE);
        mainProgressBar.setVisibility(View.VISIBLE);
        SharedPreferences sharedPreferences = getSharedPreferences("studentDetails", 0);
        ssc.setText(sharedPreferences.getString(getString(R.string.ssc), ""));
        sscYear.setText(sharedPreferences.getString(getString(R.string.sscYear), ""));
        sscCollege.setText(sharedPreferences.getString(getString(R.string.sscCollege), ""));


        String hscString = sharedPreferences.getString(getString(R.string.hsc), "");
        if (hscString.equals("-1")) {
            hsc.setText("Not applicable");
            hscYearLayout.setError("");
            hscYearLayout.setBoxBackgroundColor(getColor(R.color.fargrey));
            hscYearLayout.setEnabled(false);
            hscYearLayout.setErrorEnabled(false);
            hscCollegeLayout.setError("");
            hscCollegeLayout.setBoxBackgroundColor(getColor(R.color.fargrey));
            hscCollegeLayout.setEnabled(false);
            hscCollegeLayout.setErrorEnabled(false);
        } else {
            hscYear.setText(sharedPreferences.getString(getString(R.string.hscYear), ""));
            hscCollege.setText(sharedPreferences.getString(getString(R.string.hscCollege), ""));
        }

        String diplomaString = sharedPreferences.getString(getString(R.string.diploma), "");
        if (diplomaString.equals("-1")) {
            diploma.setText("Not applicable");
            diplomaYearLayout.setBoxBackgroundColor(getColor(R.color.fargrey));
            diplomaYearLayout.setError("");
            diplomaYearLayout.setEnabled(false);
            diplomaYearLayout.setErrorEnabled(false);
            diplomaCollegeLayout.setBoxBackgroundColor(getColor(R.color.fargrey));
            diplomaYearLayout.setError("");
            diplomaCollegeLayout.setEnabled(false);
            diplomaCollegeLayout.setErrorEnabled(false);
        } else {
            diplomaYear.setText(sharedPreferences.getString(getString(R.string.diplomaYear), ""));
            diplomaCollege.setText(sharedPreferences.getString(getString(R.string.diplomaCollege), ""));
        }

        year.setText(sharedPreferences.getString(getString(R.string.year), ""));
        branch.setText(sharedPreferences.getString(getString(R.string.branch), ""));
        classField.setText(sharedPreferences.getString(getString(R.string.classField), ""));
        division.setText(sharedPreferences.getString(getString(R.string.division), ""));

        scrollView.setVisibility(View.VISIBLE);
        mainProgressBar.setVisibility(View.GONE);


    }

    private void branchAdapter() {

        String[] branchArray = getResources().getStringArray(R.array.department);
        //change this to material dropdown in final release
        ArrayAdapter branchArrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, branchArray);
        branch.setAdapter(branchArrayAdapter);

    }

    private void hscAdapter() {

        String[] notApplicable = getResources().getStringArray(R.array.not_applicable);
        //change this to material dropdown in final release
        ArrayAdapter notApplicableAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, notApplicable);
        hsc.setAdapter(notApplicableAdapter);

    }

    private void classesAdapter() {

        String[] classes = getResources().getStringArray(R.array.classes);
        //change this to material dropdown in final release
        ArrayAdapter classArrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, classes);
        classField.setAdapter(classArrayAdapter);

    }

    private void divisionAdapter() {

        String[] divisionArray = getResources().getStringArray(R.array.divisions);
        //change this to material dropdown in final release
        ArrayAdapter divisionArrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, divisionArray);
        division.setAdapter(divisionArrayAdapter);

    }

    private void diplomaAdapter() {

        String[] notApplicable = getResources().getStringArray(R.array.not_applicable);
        //change this to material dropdown in final release
        ArrayAdapter notApplicableAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, notApplicable);
        diploma.setAdapter(notApplicableAdapter);
    }

    private void checkDetails() {
        try {
            showProgressDialog();

            boolean allClear = true;
            if (ssc.getText().toString().isEmpty()) {
                sscLayout.setError("Please enter SSC marks.");
                sscLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
            } else {
                try {
                    if (Float.parseFloat(ssc.getText().toString()) >= 100 && Float.parseFloat(hsc.getText().toString()) <= 0) {
                        sscLayout.setError("Please enter valid SSC marks.");
                        sscLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                        allClear = false;
                    }
                } catch (NumberFormatException e) {
                    sscLayout.setError("Please enter valid SSC marks.");
                    sscLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                    allClear = false;
                    e.printStackTrace();
                }
            }


            if (sscYear.getText().toString().isEmpty()) {
                sscYearLayout.setError("Please enter passout year.");
                sscYearLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
            } else {
                try {
                    if (sscYear.getText().toString().length() != 4) {
                        sscYearLayout.setError("Please enter valid passout year.");
                        sscYearLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (sscCollege.getText().toString().isEmpty()) {
                Toast.makeText(this, "------------", Toast.LENGTH_SHORT).show();
                sscCollegeLayout.setError("Please enter SSC college name.");
                sscCollegeLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
            }

            if (hsc.getText().toString().isEmpty()) {
                hscLayout.setError("Please enter HSC marks.");
                hscLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
            } else {
                try {
                    if (hsc.getText().toString().equals("Not applicable")) {

                    } else if (Float.parseFloat(hsc.getText().toString()) >= 100 || Float.parseFloat(hsc.getText().toString()) <= 0) {
                        hscLayout.setError("Please enter valid HSC marks.");
                        hscLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                        allClear = false;
                    }
                } catch (NumberFormatException e) {
                    hscLayout.setError("Please enter valid HSC marks.");
                    hscLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                    allClear = false;
                    e.printStackTrace();
                }
            }

            if (!hsc.getText().toString().equals("Not applicable")) {
                if (hscYear.getText().toString().isEmpty()) {
                    hscYearLayout.setError("Please enter passout year.");
                    hscYearLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                    allClear = false;
                } else {
                    if (hscYear.getText().toString().length() != 4) {
                        hscYearLayout.setError("Please enter valid passout year.");
                        hscYearLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                        allClear = false;
                    }
                }
                if (hscCollege.getText().toString().isEmpty()) {
                    hscCollegeLayout.setError("Please enter HSC college name.");
                    hscCollegeLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                    allClear = false;
                }
            }

            if (diploma.getText().toString().isEmpty()) {
                diplomaLayout.setError("Please enter Diploma marks.");
                diplomaLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
            } else {
                try {
                    if (diploma.getText().toString().equals("Not applicable")) {

                    } else if (Float.parseFloat(diploma.getText().toString()) >= 100 || Float.parseFloat(diploma.getText().toString()) <= 0) {
                        diplomaLayout.setError("Please enter valid Diploma marks.");
                        diplomaLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                        allClear = false;
                    }
                } catch (NumberFormatException e) {
                    diplomaLayout.setError("Please enter valid Diploma marks.");
                    diplomaLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                    allClear = false;
                    e.printStackTrace();
                }
            }
            if (!diploma.getText().toString().equals("Not applicable")) {
                if (diplomaYear.getText().toString().isEmpty()) {
                    diplomaYearLayout.setError("Please enter passout year.");
                    diplomaYearLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                    allClear = false;
                } else {
                    if (diplomaYear.getText().toString().length() != 4) {
                        diplomaYearLayout.setError("Please enter valid passout year.");
                        diplomaYearLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                    }
                }

                if (diplomaCollege.getText().toString().isEmpty()) {
                    diplomaCollegeLayout.setError("Please enter Diploma college name.");
                    diplomaCollegeLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                    allClear = false;
                }
            }


            if (year.getText().toString().isEmpty()) {
                yearLayout.setError("Please enter graduation year.");
                yearLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
            } else {
                if (year.getText().toString().length() != 4) {
                    yearLayout.setError("Please enter valid year.");
                    yearLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                }
            }


            if (branch.getText().toString().isEmpty()) {
                branchLayout.setError("Please select the branch.");
                branchLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
            }
//            if (classField.getText().toString().isEmpty()) {
//                classLayout.setError("Please select the class.");
//                classLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
//                allClear = false;
//            }
            if (division.getText().toString().isEmpty()) {
                divisionLayout.setError("Please select division.");
                divisionLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
            }

            if (allClear) {
                saveDetailsInSharedPreferences();
            }
            hideProgressDialog();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void saveDetailsInSharedPreferences() {
        showProgressDialog();
        SharedPreferences sharedPreferences = getSharedPreferences("studentDetails", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.ssc), ssc.getText().toString().trim());
        editor.putString(getString(R.string.sscYear), sscYear.getText().toString().trim());
        editor.putString(getString(R.string.sscCollege), sscCollege.getText().toString().trim());
        if (hsc.getText().toString().equals("Not applicable")) {
            editor.putString(getString(R.string.hsc), String.valueOf(-1));
            editor.putString(getString(R.string.hscYear), String.valueOf(-1));
            editor.putString(getString(R.string.hscCollege), String.valueOf(-1));

        } else {

            editor.putString(getString(R.string.hsc), hsc.getText().toString().trim());
            editor.putString(getString(R.string.hscYear), hscYear.getText().toString().trim());
            editor.putString(getString(R.string.hscCollege), hscCollege.getText().toString().trim());
        }
        if (diploma.getText().toString().equals("Not applicable")) {

            editor.putString(getString(R.string.diploma), String.valueOf(-1));
            editor.putString(getString(R.string.diplomaYear), String.valueOf(-1));
            editor.putString(getString(R.string.diplomaCollege), String.valueOf(-1));
        } else {
            editor.putString(getString(R.string.diploma), diploma.getText().toString().trim());
            editor.putString(getString(R.string.diplomaYear), diplomaYear.getText().toString().trim());
            editor.putString(getString(R.string.diplomaCollege), diplomaCollege.getText().toString().trim());
        }

        editor.putString(getString(R.string.year), year.getText().toString().trim());
        editor.putString(getString(R.string.branch), branch.getText().toString().trim());
        // editor.putString(getString(R.string.classField), classField.getText().toString());
        editor.putString(getString(R.string.division), division.getText().toString());
        editor.putBoolean("educationDetailsCompleted", true);
        editor.apply();

        onBackPressed();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideProgressDialog();
            }
        }, 2000);

//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("StudentInformation");
//        query.whereEqualTo("firebaseId", mAuth.getCurrentUser().getUid());
//        query.getFirstInBackground(new GetCallback<ParseObject>() {
//            @Override
//            public void done(ParseObject object, ParseException e) {
//                if (e == null) {
//                    Log.d(TAG, "done: value is coming");
//                    object.put(getString(R.string.ssc), Float.parseFloat(ssc.getText().toString().trim()));
//                    object.put(getString(R.string.hsc), Float.parseFloat(hsc.getText().toString().trim()));
//                    object.put(getString(R.string.diploma), Float.parseFloat(diploma.getText().toString().trim()));
//                    object.put(getString(R.string.year), Integer.parseInt(year.getText().toString().trim()));
//                    object.put(getString(R.string.branch), branch.getText().toString());
//                    object.put(getString(R.string.classField), classField.getText().toString());
//                    object.put(getString(R.string.division), division.getText().toString());
//                    object.saveInBackground(f -> {
//                        if (f != null) {
//
//                            Log.d(TAG, "saveDetailsInSharedPreferences: " + e.getLocalizedMessage());
//                        } else {
//                            Log.d("PersonalDetails", "Object saved.");
//                            onBackPressed();
//                        }
//                    });
//                    hideProgressDialog();
//                } else {
//                    // Something is wrong
//                    Log.d(TAG, "done: value is coming with ex" + e.getLocalizedMessage());
//                    if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
//                        //object is note created so create it
//                        ParseObject parseObject = new ParseObject("StudentInformation");
//                        parseObject.put(getString(R.string.ssc), Float.parseFloat(ssc.getText().toString().trim()));
//                        parseObject.put(getString(R.string.hsc), Float.parseFloat(hsc.getText().toString().trim()));
//                        parseObject.put(getString(R.string.diploma), Float.parseFloat(diploma.getText().toString().trim()));
//                        parseObject.put(getString(R.string.year), Integer.parseInt(year.getText().toString().trim()));
//                        parseObject.put(getString(R.string.branch), branch.getText().toString());
//                        parseObject.put(getString(R.string.classField), classField.getText().toString());
//                        parseObject.put(getString(R.string.division), division.getText().toString());
//                        parseObject.saveInBackground(f -> {
//                            if (f != null) {
//                                onBackPressed();
//                                Log.d(TAG, "saveDetailsInSharedPreferences: " + e.getLocalizedMessage());
//                            } else {
//                                Log.d("PersonalDetails", "Object saved.");
//                            }
//                        });
//
//
//                    } else {
//                        Log.d(TAG, "done: " + e.getLocalizedMessage());
//                    }
//                    hideProgressDialog();
//                }
//            }
//        });
    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            setInputFieldsToNormal(editable);
        }

    };

    private void setInputFieldsToNormal(Editable editable) {
        if (editable == ssc.getEditableText()) {
            sscLayout.setError("");
            sscLayout.setErrorEnabled(false);
            sscLayout.setBoxBackgroundColor(getColor(R.color.white));
        }

        if (editable == hsc.getEditableText()) {
            hscLayout.setError(null);
            hscLayout.setErrorEnabled(false);
            hscLayout.setBoxBackgroundColor(getColor(R.color.white));
            if (!hsc.getText().toString().equals("Not applicable")) {
                hscYearLayout.setError(null);
                hscYearLayout.setErrorEnabled(false);
                hscYearLayout.setBoxBackgroundColor(getColor(R.color.white));
                hscYearLayout.setEnabled(true);
                hscCollegeLayout.setError(null);
                hscCollegeLayout.setBoxBackgroundColor(getColor(R.color.white));
                hscCollegeLayout.setEnabled(true);
                hscCollegeLayout.setErrorEnabled(false);
            }
        }

        if (editable == diploma.getEditableText()) {
            diplomaLayout.setError("");
            diplomaLayout.setErrorEnabled(false);
            diplomaLayout.setBoxBackgroundColor(getColor(R.color.white));
            if (!diploma.getText().toString().equals("Not applicable")) {
                diplomaYearLayout.setError(null);
                diplomaYearLayout.setErrorEnabled(false);
                diplomaYearLayout.setBoxBackgroundColor(getColor(R.color.white));
                diplomaYearLayout.setEnabled(true);
                diplomaCollegeLayout.setError(null);
                diplomaCollegeLayout.setBoxBackgroundColor(getColor(R.color.white));
                diplomaCollegeLayout.setEnabled(true);
                diplomaCollegeLayout.setErrorEnabled(false);
            }
        }

        if (editable == year.getEditableText()) {
            yearLayout.setError("");
            yearLayout.setErrorEnabled(false);
            yearLayout.setBoxBackgroundColor(getColor(R.color.white));
        }

        if (editable == branch.getEditableText()) {
            branchLayout.setError("");
            branchLayout.setErrorEnabled(false);
            branchLayout.setBoxBackgroundColor(getColor(R.color.white));
        }

        if (editable == classField.getEditableText()) {
            classLayout.setError("");
            classLayout.setErrorEnabled(false);
            classLayout.setBoxBackgroundColor(getColor(R.color.white));
        }

        if (editable == division.getEditableText()) {
            divisionLayout.setError("");
            divisionLayout.setErrorEnabled(false);
            divisionLayout.setBoxBackgroundColor(getColor(R.color.white));
        }


    }

    private void initFields() {
        backButton = findViewById(R.id.back_button);

        sscLayout = findViewById(R.id.ssc_layout);
        sscYearLayout = findViewById(R.id.ssc_passout_layout);
        sscCollegeLayout = findViewById(R.id.ssc_college_layout);

        hscYearLayout = findViewById(R.id.hsc_passout_layout);
        hscLayout = findViewById(R.id.hsc_layout);
        hscCollegeLayout = findViewById(R.id.hsc_college_layout);

        diplomaLayout = findViewById(R.id.diploma_layout);
        diplomaYearLayout = findViewById(R.id.diploma_passout_layout);
        diplomaCollegeLayout = findViewById(R.id.diploma_college_layout);


        yearLayout = findViewById(R.id.year_layout);
        branchLayout = findViewById(R.id.branch_layout);
        classLayout = findViewById(R.id.class_layout);
        divisionLayout = findViewById(R.id.division_layout);

        ssc = findViewById(R.id.ssc);
        sscYear = findViewById(R.id.ssc_passout);
        sscCollege = findViewById(R.id.ssc_college);

        hscYear = findViewById(R.id.hsc_passout);
        hsc = findViewById(R.id.hsc);
        hscCollege = findViewById(R.id.hsc_college);

        diploma = findViewById(R.id.diploma);
        diplomaYear = findViewById(R.id.diploma_passout);
        diplomaCollege = findViewById(R.id.diploma_college);

        year = findViewById(R.id.year);
        classField = findViewById(R.id.class_field);
        division = findViewById(R.id.division);
        branch = findViewById(R.id.branch_autocomplete_textview);
        saveButton = findViewById(R.id.save_button);
        progressBar = findViewById(R.id.progress_bar);
        scrollView = findViewById(R.id.scroll_view);
        mainProgressBar = findViewById(R.id.main_progress_bar);
    }

    private void showProgressDialog() {
        progressBar.setVisibility(View.VISIBLE);
        saveButton.setText("");
        saveButton.setEnabled(false);
    }

    private void hideProgressDialog() {
        progressBar.setVisibility(View.GONE);
        saveButton.setText("Save");
        saveButton.setEnabled(true);
    }
}