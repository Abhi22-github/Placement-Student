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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.roaaserver.placementstudent.R;

public class OtherInformation extends AppCompatActivity {
    private ImageButton backButton;
    private TextInputLayout companyLayout, positionLayout, durationLayout, certificateLayout, internshipAnsLayout, certificateAnsLayout, japaneseLayout, jlptLayout;
    private TextInputEditText company, position, duration, certificate;
    private AutoCompleteTextView internshipAns, certificateAns, japaneseAns, jlpt;
    private MaterialButton saveButton;
    private LinearLayout internshipLayout, certificateDetailsLayout;
    private ProgressBar progressBar, mainProgressBar;
    private ScrollView scrollView;

    private String internshipAnsString = "", certificateAnsString = "", japaneseAnsString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_information);
        initFields();
        backButton.setOnClickListener(view -> onBackPressed());
        internshipAdapter();
        certificateAdapter();
        setupFields();
        japaneseAdapter();
        jlptAdapter();

        company.addTextChangedListener(watcher);
        position.addTextChangedListener(watcher);
        duration.addTextChangedListener(watcher);
        certificate.addTextChangedListener(watcher);

        internshipAns.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                internshipAnsString = adapterView.getItemAtPosition(i).toString();
                if (internshipAnsString.equals("Yes")) {
                    internshipLayout.setVisibility(View.VISIBLE);
                } else {
                    internshipLayout.setVisibility(View.GONE);
                }
                internshipAnsLayout.setError("");
                internshipAnsLayout.setErrorEnabled(false);
                internshipAnsLayout.setBoxBackgroundColor(getColor(R.color.white));
            }
        });

        certificateAns.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                certificateAnsString = adapterView.getItemAtPosition(i).toString();
                if (certificateAnsString.equals("Yes")) {
                    certificateDetailsLayout.setVisibility(View.VISIBLE);
                } else {
                    certificateDetailsLayout.setVisibility(View.GONE);
                }
                certificateAnsLayout.setError("");
                certificateAnsLayout.setErrorEnabled(false);
                certificateAnsLayout.setBoxBackgroundColor(getColor(R.color.white));
            }
        });

        japaneseAns.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                japaneseAnsString = adapterView.getItemAtPosition(i).toString();
                if (japaneseAnsString.equals("Yes")) {
                    jlptLayout.setVisibility(View.VISIBLE);
                } else {
                    jlptLayout.setVisibility(View.GONE);
                }
                japaneseLayout.setError("");
                japaneseLayout.setErrorEnabled(false);
                japaneseLayout.setBoxBackgroundColor(getColor(R.color.white));
            }
        });

        saveButton.setOnClickListener(view -> checkDetails());
    }

    private void setupFields() {
        scrollView.setVisibility(View.GONE);
        mainProgressBar.setVisibility(View.VISIBLE);
        SharedPreferences sharedPreferences = getSharedPreferences("studentDetails", 0);
        if (sharedPreferences.getBoolean(getString(R.string.internshipAns), false)) {
            internshipLayout.setVisibility(View.VISIBLE);

        } else {
            internshipLayout.setVisibility(View.GONE);

        }
        if (sharedPreferences.getBoolean(getString(R.string.certificationAns), false)) {
            certificateDetailsLayout.setVisibility(View.VISIBLE);

        } else {
            certificateDetailsLayout.setVisibility(View.GONE);

        }
        if (sharedPreferences.getBoolean(getString(R.string.japaneseAns), false)) {
            jlptLayout.setVisibility(View.VISIBLE);

        } else {
            jlptLayout.setVisibility(View.GONE);

        }
        jlpt.setText(sharedPreferences.getString(getString(R.string.jlpt), ""));
        company.setText(sharedPreferences.getString(getString(R.string.internshipCompanyName), ""));
        position.setText(sharedPreferences.getString(getString(R.string.position), ""));
        duration.setText(sharedPreferences.getString(getString(R.string.duration), ""));
        certificate.setText(sharedPreferences.getString(getString(R.string.certification), ""));

        scrollView.setVisibility(View.VISIBLE);
        mainProgressBar.setVisibility(View.GONE);
    }

    private void checkDetails() {
        showProgressDialog();
        boolean allClear = true;
        if (internshipAns.getText().toString().isEmpty()) {
            internshipAnsLayout.setError("Please select an answer");
            internshipAnsLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
            allClear = false;
        }
        if (certificateAns.getText().toString().isEmpty()) {
            certificateAnsLayout.setError("Please select an answer");
            certificateAnsLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
            allClear = false;
        }
        if (internshipAnsString.equals("Yes")) {
            if (company.getText().toString().isEmpty()) {
                companyLayout.setError("Please enter company name");
                companyLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
            }
            if (position.getText().toString().isEmpty()) {
                positionLayout.setError("Please enter your internship position");
                positionLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
            }
            if (duration.getText().toString().isEmpty()) {
                durationLayout.setError("Please enter your internship duration");
                durationLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
            }
        }
        if (japaneseAns.getText().toString().isEmpty()) {
            japaneseLayout.setError("Please select answer.");
            japaneseLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
            allClear = false;
        }

        if (japaneseAns.getText().toString().equals("Yes")) {
            if (jlpt.getText().toString().isEmpty()) {
                jlptLayout.setError("Please select JLPT level");
                jlptLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
            }
        }

        if (certificateAnsString.equals("Yes")) {
            if (certificate.getText().toString().isEmpty()) {
                certificateLayout.setError("Please provide certificates.");
                certificateLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
            }
        }
        if (allClear) {
            saveDetailsInSharedPreferences();
        }
        hideProgressDialog();

    }

    private void internshipAdapter() {

        String[] branchArray = getResources().getStringArray(R.array.answers);
        //change this to material dropdown in final release
        ArrayAdapter branchArrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, branchArray);
        internshipAns.setAdapter(branchArrayAdapter);

    }

    private void certificateAdapter() {

        String[] branchArray = getResources().getStringArray(R.array.answers);
        //change this to material dropdown in final release
        ArrayAdapter branchArrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, branchArray);
        certificateAns.setAdapter(branchArrayAdapter);

    }

    private void japaneseAdapter() {

        String[] branchArray = getResources().getStringArray(R.array.answers);
        //change this to material dropdown in final release
        ArrayAdapter branchArrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, branchArray);
        japaneseAns.setAdapter(branchArrayAdapter);

    }

    private void jlptAdapter() {

        String[] branchArray = getResources().getStringArray(R.array.jlpt);
        //change this to material dropdown in final release
        ArrayAdapter branchArrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, branchArray);
        jlpt.setAdapter(branchArrayAdapter);

    }


    private void saveDetailsInSharedPreferences() {
        showProgressDialog();
        SharedPreferences sharedPreferences = getSharedPreferences("studentDetails", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean intAns = false;
        if (internshipAnsString.equals("Yes")) {
            intAns = true;
        } else {
            intAns = false;
        }

        boolean certifAns = false;
        if (certificateAnsString.equals("Yes")) {
            certifAns = true;
        } else {
            certifAns = false;
        }

        boolean japanAns = false;
        if (japaneseAns.getText().toString().equals("Yes")) {
            japanAns = true;
        } else {
            japanAns = false;
        }
        editor.putBoolean(getString(R.string.japaneseAns), japanAns);
        if (japanAns) {
            editor.putString(getString(R.string.jlpt), jlpt.getText().toString().trim());
        } else {
            editor.putString(getString(R.string.jlpt), String.valueOf(-1));
        }
        if (intAns) {
            editor.putString(getString(R.string.internshipCompanyName), company.getText().toString().trim());
            editor.putString(getString(R.string.position), position.getText().toString().trim());
            editor.putString(getString(R.string.duration), duration.getText().toString().trim());
        } else {
            editor.putString(getString(R.string.internshipCompanyName), String.valueOf(-1));
            editor.putString(getString(R.string.position), String.valueOf(-1));
            editor.putString(getString(R.string.duration), String.valueOf(-1));
        }
        editor.putBoolean(getString(R.string.internshipAns), intAns);
        editor.putBoolean(getString(R.string.certificationAns), certifAns);
        if (certifAns) {
            editor.putString(getString(R.string.certification), certificate.getText().toString().trim());
        } else {
            editor.putString(getString(R.string.certification), String.valueOf(-1));
        }
        editor.putBoolean("otherInformationCompleted", true);
        editor.apply();

        onBackPressed();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideProgressDialog();
            }
        }, 2000);
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

        if (editable == company.getEditableText()) {
            companyLayout.setError("");
            companyLayout.setErrorEnabled(false);
            companyLayout.setBoxBackgroundColor(getColor(R.color.white));
        }

        if (editable == position.getEditableText()) {
            positionLayout.setError(null);
            positionLayout.setErrorEnabled(false);
            positionLayout.setBoxBackgroundColor(getColor(R.color.white));
        }

        if (editable == duration.getEditableText()) {
            durationLayout.setError("");
            durationLayout.setErrorEnabled(false);
            durationLayout.setBoxBackgroundColor(getColor(R.color.white));
        }

        if (editable == certificate.getEditableText()) {
            certificateLayout.setError("");
            certificateLayout.setErrorEnabled(false);
            certificateLayout.setBoxBackgroundColor(getColor(R.color.white));
        }


    }

    private void initFields() {
        backButton = findViewById(R.id.back_button);
        companyLayout = findViewById(R.id.company_layout);
        positionLayout = findViewById(R.id.postion_layout);
        durationLayout = findViewById(R.id.duration_layout);
        certificateLayout = findViewById(R.id.certificate_layout);
        company = findViewById(R.id.company);
        position = findViewById(R.id.position);
        duration = findViewById(R.id.duration);
        certificate = findViewById(R.id.certificate);

        internshipAnsLayout = findViewById(R.id.internship_ans_layout);
        internshipAns = findViewById(R.id.internship_autocomplete_textview);
        internshipLayout = findViewById(R.id.internship_details);

        japaneseLayout = findViewById(R.id.japanese_ans_layout);
        japaneseAns = findViewById(R.id.japanese_autocomplete_textview);
        jlptLayout = findViewById(R.id.jlpt_ans_layout);
        jlpt = findViewById(R.id.jlpt_autocomplete_textview);

        certificateAnsLayout = findViewById(R.id.certificate_ans_layout);
        certificateAns = findViewById(R.id.certificate_autocomplete_textview);
        certificateDetailsLayout = findViewById(R.id.certificate_details);

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