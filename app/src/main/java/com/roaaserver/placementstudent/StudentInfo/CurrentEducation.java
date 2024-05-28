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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.roaaserver.placementstudent.R;

public class CurrentEducation extends AppCompatActivity {
    private ImageButton backButton;
    private TextInputLayout sem1Layout, sem2Layout, sem3Layout, sem4Layout, sem5Layout, sem6Layout, sem7Layout, sem8Layout, aggregateLayout,
            aggregatePercentageLayout, activeBacklogLayout, previousBacklogLayout, gapLayout, engineeringGapLayout, gapYearLayout;
    private TextInputEditText sem1, sem2, sem3, sem4, sem5, sem6, aggregate, aggregatePercentage, activeBacklog, previousBacklog;
    private MaterialButton saveButton;
    private AutoCompleteTextView sem7, sem8, gap, engineeringGap, gapYear;
    private ProgressBar progressBar, mainProgressBar;
    private ScrollView scrollView;
    private TextView gapYearLabel;
    private String gapAnswer, engineeringGapAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_education);

        initFields();
        setUpFields();
        backButton.setOnClickListener(view -> onBackPressed());
        sem1.addTextChangedListener(watcher);
        sem2.addTextChangedListener(watcher);
        sem3.addTextChangedListener(watcher);
        sem4.addTextChangedListener(watcher);
        sem5.addTextChangedListener(watcher);
        sem6.addTextChangedListener(watcher);
        sem7.addTextChangedListener(watcher);
        sem8.addTextChangedListener(watcher);
        aggregate.addTextChangedListener(watcher);
        aggregatePercentage.addTextChangedListener(watcher);
        activeBacklog.addTextChangedListener(watcher);
        previousBacklog.addTextChangedListener(watcher);

        sem7Adapter();
        sem8Adapter();
        gapAdapter();
        engineeringGapAdapter();
        gapYearAdapter();

        gap.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                engineeringGapAnswer = adapterView.getItemAtPosition(i).toString();

                gapLayout.setError("");
                gapLayout.setErrorEnabled(false);
                gapLayout.setBoxBackgroundColor(getColor(R.color.white));
            }
        });


        engineeringGap.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                engineeringGapAnswer = adapterView.getItemAtPosition(i).toString();
                if (engineeringGapAnswer.equals("Yes")) {
                    gapYearLabel.setVisibility(View.VISIBLE);
                    gapYearLayout.setVisibility(View.VISIBLE);

                } else {
                    gapYearLabel.setVisibility(View.GONE);
                    gapYearLayout.setVisibility(View.GONE);
                }
                gapYear.requestFocus();

                engineeringGapLayout.setError("");
                engineeringGapLayout.setErrorEnabled(false);
                engineeringGapLayout.setBoxBackgroundColor(getColor(R.color.white));
            }
        });

        saveButton.setOnClickListener(view -> checkDetails());

    }

    private void setUpFields() {

        scrollView.setVisibility(View.GONE);
        mainProgressBar.setVisibility(View.VISIBLE);
        SharedPreferences sharedPreferences = getSharedPreferences("studentDetails", 0);
        sem1.setText(sharedPreferences.getString(getString(R.string.sem1), ""));
        sem2.setText(sharedPreferences.getString(getString(R.string.sem2), ""));
        sem3.setText(sharedPreferences.getString(getString(R.string.sem3), ""));
        sem4.setText(sharedPreferences.getString(getString(R.string.sem4), ""));
        sem5.setText(sharedPreferences.getString(getString(R.string.sem5), ""));
        sem6.setText(sharedPreferences.getString(getString(R.string.sem6), ""));
        String sem7String = sharedPreferences.getString(getString(R.string.sem7), "");
        String sem8String = sharedPreferences.getString(getString(R.string.sem8), "");
        if (sem7String.equals("-1")) {
            sem7.setText("Not applicable");
        } else {
            sem7.setText(sem7String);
        }
        if (sem8String.equals("-1")) {
            sem8.setText("Not applicable");
        } else {
            sem8.setText(sem8String);
        }
        aggregate.setText(sharedPreferences.getString(getString(R.string.aggregate), ""));
        aggregatePercentage.setText(sharedPreferences.getString(getString(R.string.aggregatePercentage), ""));
        activeBacklog.setText(sharedPreferences.getString(getString(R.string.activeBacklog), ""));
        previousBacklog.setText(sharedPreferences.getString(getString(R.string.previousBacklog), ""));
        boolean eGapBoolean = sharedPreferences.getBoolean(getString(R.string.engineeringGap), false);
        if (eGapBoolean) {
            gapYearLabel.setVisibility(View.VISIBLE);
            gapYearLayout.setVisibility(View.VISIBLE);
            gapYear.setText(sharedPreferences.getString(getString(R.string.gapYears), ""));
        } else {
            gapYearLabel.setVisibility(View.GONE);
            gapYearLayout.setVisibility(View.GONE);

        }


        scrollView.setVisibility(View.VISIBLE);
        mainProgressBar.setVisibility(View.GONE);


    }

    private void checkDetails() {
        showProgressDialog();
        boolean allClear = true;
        if (sem1.getText().toString().isEmpty()) {
            sem1Layout.setError("Please enter Sem1 SGPA.");
            sem1Layout.setBoxBackgroundColor(getColor(R.color.red_faint));
            allClear = false;
        } else {
            if (Float.parseFloat(sem1.getText().toString()) >= 10 || Float.parseFloat(sem1.getText().toString()) <= 0) {
                sem1Layout.setError("Please enter valid sem1 SGPA.");
                sem1Layout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
            }
        }

        if (sem2.getText().toString().isEmpty()) {
            sem2Layout.setError("Please enter sem2 SGPA.");
            sem2Layout.setBoxBackgroundColor(getColor(R.color.red_faint));
            allClear = false;
        } else {
            if (Float.parseFloat(sem2.getText().toString()) > 10 || Float.parseFloat(sem2.getText().toString()) < 0) {
                sem2Layout.setError("Please enter valid sem2 SGPA.");
                sem2Layout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
            }
        }

        if (sem3.getText().toString().isEmpty()) {
            sem3Layout.setError("Please enter sem3 SGPA.");
            sem3Layout.setBoxBackgroundColor(getColor(R.color.red_faint));
            allClear = false;
        } else {
            if (Float.parseFloat(sem3.getText().toString()) > 10 || Float.parseFloat(sem3.getText().toString()) < 0) {
                sem3Layout.setError("Please enter valid sem3 SGPA.");
                sem3Layout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
            }
        }

        if (sem4.getText().toString().isEmpty()) {
            sem4Layout.setError("Please enter sem4 SGPA.");
            sem4Layout.setBoxBackgroundColor(getColor(R.color.red_faint));
            allClear = false;
        } else {
            if (Float.parseFloat(sem4.getText().toString()) > 10 || Float.parseFloat(sem4.getText().toString()) < 0) {
                sem4Layout.setError("Please enter valid sem4 SGPA.");
                sem4Layout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
            }
        }

        if (sem5.getText().toString().isEmpty()) {
            sem5Layout.setError("Please enter sem5 SGPA.");
            sem5Layout.setBoxBackgroundColor(getColor(R.color.red_faint));
            allClear = false;
        } else {
            if (Float.parseFloat(sem5.getText().toString()) > 10 || Float.parseFloat(sem5.getText().toString()) < 0) {
                sem5Layout.setError("Please enter valid sem5 SGPA.");
                sem5Layout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
            }
        }

        if (sem6.getText().toString().isEmpty()) {
            sem6Layout.setError("Please enter sem6 SGPA.");
            sem6Layout.setBoxBackgroundColor(getColor(R.color.red_faint));
            allClear = false;
        } else {
            if (Float.parseFloat(sem6.getText().toString()) > 10 || Float.parseFloat(sem6.getText().toString()) < 0) {
                sem6Layout.setError("Please enter valid sem6 SGPA.");
                sem6Layout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
            }
        }

        if (sem7.getText().toString().isEmpty()) {
            sem7Layout.setError("Please enter sem7 SGPA.");
            sem7Layout.setBoxBackgroundColor(getColor(R.color.red_faint));
            allClear = false;
        } else {
            try {
                if (sem7.getText().toString().equals("Not applicable")) {

                } else if (Float.parseFloat(sem7.getText().toString()) > 10 || Float.parseFloat(sem7.getText().toString()) < 0) {
                    sem7Layout.setError("Please enter valid sem7 SGPA.");
                    sem7Layout.setBoxBackgroundColor(getColor(R.color.red_faint));
                    allClear = false;
                }
            } catch (NumberFormatException e) {
                sem7Layout.setError("Please enter valid sem7 SGPA.");
                sem7Layout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
                e.printStackTrace();
            }
        }

        if (sem8.getText().toString().isEmpty()) {
            sem8Layout.setError("Please enter sem8 SGPA.");
            sem8Layout.setBoxBackgroundColor(getColor(R.color.red_faint));
            allClear = false;
        } else {
            try {
                if (sem8.getText().toString().equals("Not applicable")) {

                } else if (Float.parseFloat(sem8.getText().toString()) > 10 || Float.parseFloat(sem8.getText().toString()) < 0) {
                    sem8Layout.setError("Please enter valid sem8 SGPA.");
                    sem8Layout.setBoxBackgroundColor(getColor(R.color.red_faint));
                    allClear = false;
                }
            } catch (NumberFormatException e) {
                sem8Layout.setError("Please enter valid sem8 SGPA.");
                sem8Layout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
                e.printStackTrace();
            }
        }


        if (aggregate.getText().toString().isEmpty()) {
            aggregateLayout.setError("Please enter aggregate CGPA.");
            aggregateLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
            allClear = false;
        } else {
            if (Float.parseFloat(aggregate.getText().toString()) > 10 || Float.parseFloat(aggregate.getText().toString()) < 0) {
                aggregateLayout.setError("Please enter valid aggregate CGPA.");
                aggregateLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
            }
        }

        if (aggregatePercentage.getText().toString().isEmpty()) {
            aggregatePercentageLayout.setError("Please enter aggregate CGPA percentage.");
            aggregatePercentageLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
            allClear = false;
        } else {
            if (Float.parseFloat(aggregatePercentage.getText().toString()) >= 100 || Float.parseFloat(aggregatePercentage.getText().toString()) <= 0) {
                aggregatePercentageLayout.setError("Please enter valid aggregate CGPA percentage.");
                aggregatePercentageLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
            }
        }

        if (activeBacklog.getText().toString().isEmpty()) {
            activeBacklogLayout.setError("Please enter active backlog");
            activeBacklogLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
            allClear = false;
        }
        if (previousBacklog.getText().toString().isEmpty()) {
            previousBacklogLayout.setError("Please enter previous backlog");
            previousBacklogLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
            allClear = false;
        }
        if (gap.getText().toString().isEmpty()) {
            gapLayout.setError("Please select an answer");
            gapLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
            allClear = false;
        }
        if (engineeringGap.getText().toString().isEmpty()) {
            engineeringGapLayout.setError("Please select an answer");
            engineeringGapLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
            allClear = false;
        }
        if (engineeringGap.getText().toString().equals("Yes") && gapYear.getText().toString().isEmpty()) {
            gapYearLayout.setError("Please select Gap years.");
            gapYearLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
            allClear = false;
        }

        if (allClear) {
            saveDetailsInSharedPreferences();
        }
        hideProgressDialog();

    }

    private void sem7Adapter() {

        String[] notApplicable = getResources().getStringArray(R.array.not_applicable);
        //change this to material dropdown in final release
        ArrayAdapter notApplicableAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, notApplicable);
        sem7.setAdapter(notApplicableAdapter);
    }

    private void gapAdapter() {

        String[] answers = getResources().getStringArray(R.array.answers);
        //change this to material dropdown in final release
        ArrayAdapter answersAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, answers);
        gap.setAdapter(answersAdapter);
    }

    private void engineeringGapAdapter() {

        String[] answers = getResources().getStringArray(R.array.answers);
        //change this to material dropdown in final release
        ArrayAdapter answersAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, answers);
        engineeringGap.setAdapter(answersAdapter);
    }

    private void gapYearAdapter() {

        String[] answers = getResources().getStringArray(R.array.gapYears);
        //change this to material dropdown in final release
        ArrayAdapter answersAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, answers);
        gapYear.setAdapter(answersAdapter);
    }


    private void sem8Adapter() {

        String[] notApplicable = getResources().getStringArray(R.array.not_applicable);
        //change this to material dropdown in final release
        ArrayAdapter notApplicableAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, notApplicable);
        sem8.setAdapter(notApplicableAdapter);
    }

    private void saveDetailsInSharedPreferences() {
        showProgressDialog();
        SharedPreferences sharedPreferences = getSharedPreferences("studentDetails", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("sem1", sem1.getText().toString());
        editor.putString("sem2", sem2.getText().toString());
        editor.putString("sem3", sem3.getText().toString());
        editor.putString("sem4", sem4.getText().toString());
        editor.putString("sem5", sem5.getText().toString());
        editor.putString("sem6", sem6.getText().toString());

        if (sem7.getText().toString().equals("Not applicable")) {
            editor.putString(getString(R.string.sem7), String.valueOf(-1));

        } else {

            editor.putString(getString(R.string.sem7), sem7.getText().toString());
        }
        if (sem8.getText().toString().equals("Not applicable")) {

            editor.putString(getString(R.string.sem8), String.valueOf(-1));
        } else {
            editor.putString(getString(R.string.sem8), sem8.getText().toString());
        }
        editor.putString(getString(R.string.aggregate), aggregate.getText().toString());
        editor.putString(getString(R.string.aggregatePercentage), aggregatePercentage.getText().toString());
        editor.putString(getString(R.string.activeBacklog), activeBacklog.getText().toString());
        editor.putString(getString(R.string.previousBacklog), previousBacklog.getText().toString());

        boolean gapAns = false;
        if (gap.getText().toString().equals("Yes")) {
            gapAns = true;
        } else {
            gapAns = false;
        }

        boolean engineeringGapAns = false;
        if (engineeringGap.getText().toString().equals("Yes")) {
            engineeringGapAns = true;
        } else {
            engineeringGapAns = false;
        }
        editor.putBoolean(getString(R.string.gap), gapAns);
        editor.putBoolean(getString(R.string.engineeringGap), engineeringGapAns);
        if (engineeringGapAns) {
            editor.putString(getString(R.string.gapYears), gapYear.getText().toString());
        } else {
            editor.putString(getString(R.string.gapYears), String.valueOf(-1));
        }
        editor.putBoolean("currentEducationCompleted", true);
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
        if (editable == sem1.getEditableText()) {
            sem1Layout.setError("");
            sem1Layout.setErrorEnabled(false);
            sem1Layout.setBoxBackgroundColor(getColor(R.color.white));
        }

        if (editable == sem2.getEditableText()) {
            sem2Layout.setError(null);
            sem2Layout.setErrorEnabled(false);
            sem2Layout.setBoxBackgroundColor(getColor(R.color.white));
        }

        if (editable == sem3.getEditableText()) {
            sem3Layout.setError("");
            sem3Layout.setErrorEnabled(false);
            sem3Layout.setBoxBackgroundColor(getColor(R.color.white));
        }

        if (editable == sem4.getEditableText()) {
            sem4Layout.setError("");
            sem4Layout.setErrorEnabled(false);
            sem4Layout.setBoxBackgroundColor(getColor(R.color.white));
        }

        if (editable == sem5.getEditableText()) {
            sem5Layout.setError("");
            sem5Layout.setErrorEnabled(false);
            sem5Layout.setBoxBackgroundColor(getColor(R.color.white));
        }

        if (editable == sem6.getEditableText()) {
            sem6Layout.setError("");
            sem6Layout.setErrorEnabled(false);
            sem6Layout.setBoxBackgroundColor(getColor(R.color.white));
        }

        if (editable == sem7.getEditableText()) {
            sem7Layout.setError("");
            sem7Layout.setErrorEnabled(false);
            sem7Layout.setBoxBackgroundColor(getColor(R.color.white));
        }
        if (editable == sem8.getEditableText()) {
            sem8Layout.setError("");
            sem8Layout.setErrorEnabled(false);
            sem8Layout.setBoxBackgroundColor(getColor(R.color.white));
        }
        if (editable == aggregate.getEditableText()) {
            aggregateLayout.setError("");
            aggregateLayout.setErrorEnabled(false);
            aggregateLayout.setBoxBackgroundColor(getColor(R.color.white));
        }

        if (editable == aggregatePercentage.getEditableText()) {
            aggregatePercentageLayout.setError("");
            aggregatePercentageLayout.setErrorEnabled(false);
            aggregatePercentageLayout.setBoxBackgroundColor(getColor(R.color.white));
        }

        if (editable == activeBacklog.getEditableText()) {
            activeBacklogLayout.setError("");
            activeBacklogLayout.setErrorEnabled(false);
            activeBacklogLayout.setBoxBackgroundColor(getColor(R.color.white));
        }
        if (editable == previousBacklog.getEditableText()) {
            previousBacklogLayout.setError("");
            previousBacklogLayout.setErrorEnabled(false);
            previousBacklogLayout.setBoxBackgroundColor(getColor(R.color.white));
        }


    }

    private void initFields() {
        backButton = findViewById(R.id.back_button);
        sem1Layout = findViewById(R.id.sem1_layout);
        sem2Layout = findViewById(R.id.sem2_layout);
        sem3Layout = findViewById(R.id.sem3_layout);
        sem4Layout = findViewById(R.id.sem4_layout);
        sem5Layout = findViewById(R.id.sem5_layout);
        sem6Layout = findViewById(R.id.sem6_layout);
        sem7Layout = findViewById(R.id.sem7_layout);
        sem8Layout = findViewById(R.id.sem8_layout);
        aggregateLayout = findViewById(R.id.aggregate_cgpa_layout);
        aggregatePercentageLayout = findViewById(R.id.aggregate_percentage_layout);
        activeBacklogLayout = findViewById(R.id.active_backlogs_layout);
        previousBacklogLayout = findViewById(R.id.previous_backlogs_layout);
        sem1 = findViewById(R.id.sem1);
        sem2 = findViewById(R.id.sem2);
        sem3 = findViewById(R.id.sem3);
        sem4 = findViewById(R.id.sem4);
        sem5 = findViewById(R.id.sem5);
        sem6 = findViewById(R.id.sem6);
        sem7 = findViewById(R.id.sem7);
        sem8 = findViewById(R.id.sem8);
        aggregate = findViewById(R.id.aggregate_cgpa);
        aggregatePercentage = findViewById(R.id.aggregate_percentage);
        activeBacklog = findViewById(R.id.active_backlogs);
        previousBacklog = findViewById(R.id.previous_backlogs);

        gapLayout = findViewById(R.id.gap_layout);
        gap = findViewById(R.id.gap_autocomplete_textview);

        engineeringGap = findViewById(R.id.engineering_gap_autocomplete_textview);
        engineeringGapLayout = findViewById(R.id.engineering_gap_layout);

        gapYearLayout = findViewById(R.id.gap_years_layout);
        gapYear = findViewById(R.id.gap_years_autocomplete_textview);

        gapYearLabel = findViewById(R.id.gap_year_label);

        saveButton = findViewById(R.id.save_button);
        progressBar = findViewById(R.id.progress_bar);
        mainProgressBar = findViewById(R.id.main_progress_bar);
        scrollView = findViewById(R.id.scroll_view);
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