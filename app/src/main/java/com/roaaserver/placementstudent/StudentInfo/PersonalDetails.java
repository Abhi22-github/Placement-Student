package com.roaaserver.placementstudent.StudentInfo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hardik.clickshrinkeffect.ClickShrinkEffect;
import com.roaaserver.placementstudent.R;

public class PersonalDetails extends AppCompatActivity {
    private static final String TAG = "PersonalDetails";

    private RelativeLayout maleButton, femaleButton;
    private ImageButton backButton;
    private TextInputLayout nameLayout, contactLayout, erpLayout, prnLayout, birthDateLayout, addressLayout;
    private TextInputEditText name, contact, erp, prn, birthDate, address;
    private MaterialButton saveButton;
    private ProgressBar progressBar;
    private ScrollView scrollView;
    private ProgressBar mainProgressBar;
    private TextView genderMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);

        initFields();

        setUpFields();

        maleFemaleButtonAppearance();
        birthDate.setOnClickListener(view -> setBirthDate());

        name.addTextChangedListener(watcher);
        contact.addTextChangedListener(watcher);
        erp.addTextChangedListener(watcher);
        prn.addTextChangedListener(watcher);
        birthDate.addTextChangedListener(watcher);
        address.addTextChangedListener(watcher);

        backButton.setOnClickListener(view -> onBackPressed());
        saveButton.setOnClickListener(view -> checkDataFields());
    }

    private void setUpFields() {

        scrollView.setVisibility(View.GONE);
        mainProgressBar.setVisibility(View.VISIBLE);
        SharedPreferences sharedPreferences = getSharedPreferences("studentDetails", 0);
        name.setText(sharedPreferences.getString(getString(R.string.name), ""));
        contact.setText(sharedPreferences.getString(getString(R.string.contactNo), ""));
        address.setText(sharedPreferences.getString(getString(R.string.address), ""));
        erp.setText(sharedPreferences.getString(getString(R.string.erpNo), ""));
        prn.setText(sharedPreferences.getString(getString(R.string.prnNo), ""));
        birthDate.setText(sharedPreferences.getString(getString(R.string.birthDate), ""));
        try {
            if (sharedPreferences.getString(getString(R.string.gender), "").equals("Male")) {
                maleButton.setSelected(true);
            } else {
                femaleButton.setSelected(false);
            }
        } catch (Exception e) {

        }
        scrollView.setVisibility(View.VISIBLE);
        mainProgressBar.setVisibility(View.GONE);
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("StudentInformation");
//        query.whereEqualTo("firebaseId", mAuth.getCurrentUser().getUid());
//        query.getFirstInBackground(new GetCallback<ParseObject>() {
//            @Override
//            public void done(ParseObject parseObject, ParseException e) {
//                if (e == null) {
//                    name.setText(parseObject.getString(getString(R.string.name)));
//                    contact.setText(parseObject.getString(getString(R.string.contactNo)));
//                    erp.setText(parseObject.getString(getString(R.string.erpNo)));
//                    prn.setText(parseObject.getString(getString(R.string.prnNo)));
//                    birthDate.setText(parseObject.getString(getString(R.string.birthDate)));
//                    if (parseObject.getString(getString(R.string.gender)).equals("Male")) {
//                        maleButton.setSelected(true);
//                    } else {
//                        femaleButton.setSelected(false);
//                    }
//
//                } else {
//
//                }
//                scrollView.setVisibility(View.VISIBLE);
//                mainProgressBar.setVisibility(View.GONE);
//
//            }
//        });


    }

    private void setBirthDate() {
        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();

        MaterialDatePicker materialDatePicker = builder.setTitleText("Select Birth Date")
                .build();
        materialDatePicker.show(getSupportFragmentManager(), "Birth Date");

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                birthDate.setText(materialDatePicker.getHeaderText());
                birthDateLayout.setError("");
                birthDateLayout.setErrorEnabled(false);
            }
        });
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
        if (editable == name.getEditableText()) {
            nameLayout.setError("");
            nameLayout.setErrorEnabled(false);
            nameLayout.setBoxBackgroundColor(getColor(R.color.white));
        }

        if (editable == address.getEditableText()) {
            addressLayout.setError("");
            addressLayout.setErrorEnabled(false);
            addressLayout.setBoxBackgroundColor(getColor(R.color.white));
        }

        if (editable == contact.getEditableText()) {
            contactLayout.setError(null);
            contactLayout.setErrorEnabled(false);
            contactLayout.setBoxBackgroundColor(getColor(R.color.white));
        }

        if (editable == erp.getEditableText()) {
            erpLayout.setError("");
            erpLayout.setErrorEnabled(false);
            erpLayout.setBoxBackgroundColor(getColor(R.color.white));
        }

        if (editable == prn.getEditableText()) {
            prnLayout.setError("");
            prnLayout.setErrorEnabled(false);
            prnLayout.setBoxBackgroundColor(getColor(R.color.white));
        }

        if (editable == birthDate.getEditableText()) {
            birthDateLayout.setError("");
            birthDateLayout.setErrorEnabled(false);
            birthDateLayout.setBoxBackgroundColor(getColor(R.color.white));
        }


    }

    private void checkDataFields() {
        try {
            showProgressDialog();
            boolean allClear = true;
            if (name.getText().toString().isEmpty()) {
                nameLayout.setError("Please enter name.");
                nameLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
            }
            if (contact.getText().toString().isEmpty()) {
                contactLayout.setError("Please enter contact no.");
                contactLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
            } else {
                if (contact.getText().toString().length() != 10) {
                    contactLayout.setError("Please enter valid contact no.");
                    contactLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                    allClear = false;
                }
            }
            if (address.getText().toString().isEmpty()) {
                addressLayout.setError("Please enter address");
                addressLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
            }

            if (erp.getText().toString().isEmpty()) {
                erpLayout.setError("Please enter erp no.");
                erpLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
            }
            if (prn.getText().toString().isEmpty()) {
                prnLayout.setError("Please enter prn no.");
                prnLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
            }
            if (birthDate.getText().toString().isEmpty()) {
                birthDateLayout.setError("Please select birth date.");
                birthDateLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
            }
            if (!maleButton.isSelected() && !femaleButton.isSelected()) {
                allClear = false;
                genderMessage.setVisibility(View.VISIBLE);
            }
            if (allClear) {
                saveDetailsInSharedPreferences();
            }
            hideProgressDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveDetailsInSharedPreferences() {
        showProgressDialog();
        SharedPreferences sharedPreferences = getSharedPreferences("studentDetails", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.name), name.getText().toString());
        editor.putString(getString(R.string.address), address.getText().toString());
        editor.putString(getString(R.string.contactNo), contact.getText().toString());
        editor.putString(getString(R.string.erpNo), erp.getText().toString());
        editor.putString(getString(R.string.prnNo), prn.getText().toString());
        editor.putString(getString(R.string.birthDate), birthDate.getText().toString());
        String gender = "";
        gender = maleButton.isSelected() ? "Male" : "Female";
        editor.putString(getString(R.string.gender), gender);
        editor.putBoolean("personalDetailsCompleted", true);
        editor.apply();


//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("StudentInformation");
//        query.whereEqualTo("firebaseId", mAuth.getCurrentUser().getUid());
//        query.getFirstInBackground(new GetCallback<ParseObject>() {
//            @Override
//            public void done(ParseObject object, ParseException e) {
//                if (e == null) {
//                    Log.d(TAG, "done: value is coming");
//                    object.put(getString(R.string.name), name.getText().toString());
//                    object.put(getString(R.string.id), mAuth.getCurrentUser().getUid());
//                    object.put(getString(R.string.contactNo), contact.getText().toString());
//                    object.put(getString(R.string.erpNo), erp.getText().toString());
//                    object.put(getString(R.string.prnNo), prn.getText().toString());
//                    object.put(getString(R.string.birthDate), birthDate.getText().toString());
//                    String gender = "";
//                    gender = maleButton.isSelected() ? "Male" : "Female";
//                    object.put(getString(R.string.gender), gender);
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
//                        parseObject.put(getString(R.string.name), name.getText().toString());
//                        parseObject.put(getString(R.string.id), mAuth.getCurrentUser().getUid());
//                        parseObject.put(getString(R.string.contactNo), contact.getText().toString());
//                        parseObject.put(getString(R.string.erpNo), erp.getText().toString());
//                        parseObject.put(getString(R.string.prnNo), prn.getText().toString());
//                        parseObject.put(getString(R.string.birthDate), birthDate.getText().toString());
//                        String gender = "";
//                        gender = maleButton.isSelected() ? "Male" : "Female";
//                        parseObject.put(getString(R.string.gender), gender);
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


        onBackPressed();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideProgressDialog();
            }
        }, 2000);
    }


    private void initFields() {
        maleButton = findViewById(R.id.male_button);
        femaleButton = findViewById(R.id.female_button);
        backButton = findViewById(R.id.back_button);
        nameLayout = findViewById(R.id.name_layout);
        contactLayout = findViewById(R.id.contact_no_layout);
        addressLayout = findViewById(R.id.address_layout);
        erpLayout = findViewById(R.id.erp_no_layout);
        prnLayout = findViewById(R.id.prn_no_layout);
        birthDateLayout = findViewById(R.id.birth_date_layout);
        name = findViewById(R.id.name);
        contact = findViewById(R.id.contact_no);
        address = findViewById(R.id.address);
        erp = findViewById(R.id.erp_no);
        prn = findViewById(R.id.prn_no);
        birthDate = findViewById(R.id.birth_date);
        saveButton = findViewById(R.id.save_button);
        progressBar = findViewById(R.id.progress_bar);
        scrollView = findViewById(R.id.scroll_view);
        mainProgressBar = findViewById(R.id.main_progress_bar);
        genderMessage = findViewById(R.id.gender_error_message);

        new ClickShrinkEffect(maleButton);
        new ClickShrinkEffect(femaleButton);
    }

    private void maleFemaleButtonAppearance() {
        maleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maleButton.setSelected(true);
                femaleButton.setSelected(false);
                genderMessage.setVisibility(View.GONE);
            }
        });
        femaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                femaleButton.setSelected(true);
                maleButton.setSelected(false);
                genderMessage.setVisibility(View.GONE);
            }
        });
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