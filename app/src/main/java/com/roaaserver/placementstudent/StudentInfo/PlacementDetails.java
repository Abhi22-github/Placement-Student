package com.roaaserver.placementstudent.StudentInfo;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.roaaserver.placementstudent.R;

public class PlacementDetails extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;

    private ImageButton backButton;
    private TextInputLayout placedLayout, companyNameLayout, companyLocationLayout, offerPackageLayout, interviewDateLayout, joiningDateLayout,
            interestedForLayout;
    private TextInputEditText companyName, companyLocation, offerPackage, interviewDate, joiningDate;
    private AutoCompleteTextView placed, interestedFor;
    private MaterialButton saveButton;
    private LinearLayout placedDetailsLayout;
    private ProgressBar progressBar, mainProgressBar;
    private ScrollView scrollView;
    private RelativeLayout part1Pdf, part2Pdf;
    private ImageView pdfStatus;
    private TextView pdfNameString, pdfErrorMessage;
    private ImageButton pdfCancelButton;
    private Uri pdfData;
    private String pdfName="", offerLetterDownloadUri="";

    private String internshipAnsString = "", certificateAnsString = "";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placement_details);
        initFields();
        backButton.setOnClickListener(view -> onBackPressed());
        initFirebase();
        setupFields();
        companyName.addTextChangedListener(watcher);
        companyLocation.addTextChangedListener(watcher);
        offerPackage.addTextChangedListener(watcher);
        interviewDate.addTextChangedListener(watcher);
        joiningDate.addTextChangedListener(watcher);
        placedAdapter();
        interestedAdapter();

        interviewDate.setOnClickListener(v -> setInterviewDate());
        joiningDate.setOnClickListener(v -> setJoiningDate());

        part1Pdf.setOnClickListener(v -> selectPdfFromDevice());

        pdfCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StorageReference offerRef = storageReference.child("Offer Letter").child(mAuth.getUid().toString() + "." + "pdf");
                offerRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            pdfData = null;
                            part2Pdf.setVisibility(View.GONE);
                            pdfStatus.setImageResource(0);
                            offerLetterDownloadUri = "";
                        } else {

                        }
                    }
                });

            }
        });

        placed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                internshipAnsString = adapterView.getItemAtPosition(i).toString();
                if (internshipAnsString.equals("Yes")) {
                    placedDetailsLayout.setVisibility(View.VISIBLE);
                } else {
                    placedDetailsLayout.setVisibility(View.GONE);
                }
                placedLayout.setError("");
                placedLayout.setErrorEnabled(false);
                placedLayout.setBoxBackgroundColor(getColor(R.color.white));
            }
        });


        saveButton.setOnClickListener(view -> checkDetails());
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

    }

    private void setupFields() {
        scrollView.setVisibility(View.GONE);
        mainProgressBar.setVisibility(View.VISIBLE);
        SharedPreferences sharedPreferences = getSharedPreferences("studentDetails", 0);

        if (sharedPreferences.getBoolean(getString(R.string.isPlaced), false)) {
            placedDetailsLayout.setVisibility(View.VISIBLE);

        } else {
            placedDetailsLayout.setVisibility(View.GONE);

        }
        companyName.setText(sharedPreferences.getString(getString(R.string.placedCompanyName), ""));
        companyLocation.setText(sharedPreferences.getString(getString(R.string.companyLocation), ""));
        offerPackage.setText(sharedPreferences.getString(getString(R.string.offeredPackage), ""));
        interviewDate.setText(sharedPreferences.getString(getString(R.string.interviewDate), ""));
        joiningDate.setText(sharedPreferences.getString(getString(R.string.joiningDate), ""));
        String offerLetterUri = sharedPreferences.getString(getString(R.string.offerLetterUri), "");

        StorageReference offerRef = storageReference.child("Offer Letter").child(mAuth.getUid().toString() + "." + "pdf");
        offerRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                offerRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        offerLetterDownloadUri = uri.toString();
                        Log.d(TAG, "onSuccess: " + offerLetterDownloadUri);


                        if (offerLetterDownloadUri.isEmpty()) {

                        } else {
                            try {

                            } catch (Exception e) {

                            }
                            pdfName = sharedPreferences.getString(getString(R.string.offerLetterName), "");
                            part2Pdf.setVisibility(View.VISIBLE);
                            pdfNameString.setText(sharedPreferences.getString(getString(R.string.offerLetterName), ""));
                            pdfStatus.setImageResource(R.drawable.ic_verified_icon);
                        }
                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


        scrollView.setVisibility(View.VISIBLE);
        mainProgressBar.setVisibility(View.GONE);
    }

    private void checkDetails() {
        showProgressDialog();
        boolean allClear = true;

        if (placed.getText().toString().isEmpty()) {
            placedLayout.setError("Please select an answer");
            placedLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
            allClear = false;
        }
        if (placed.getText().toString().equals("Yes")) {
            if (companyName.getText().toString().isEmpty()) {
                companyNameLayout.setError("Please enter company name");
                companyNameLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
            }

            if (companyLocation.getText().toString().isEmpty()) {
                companyLocationLayout.setError("Please enter company location");
                companyLocationLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
            }

            if (offerPackage.getText().toString().isEmpty()) {
                offerPackageLayout.setError("Please enter package offered in LPA");
                offerPackageLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
            }
            if (interviewDate.getText().toString().isEmpty()) {
                interviewDateLayout.setError("Please enter interview date");
                interviewDateLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
            }
            if (joiningDate.getText().toString().isEmpty()) {
                joiningDateLayout.setError("Please enter joining date");
                joiningDateLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                allClear = false;
            }

            try {

                if (pdfData != null || !offerLetterDownloadUri.isEmpty()) {

                    pdfErrorMessage.setVisibility(View.GONE);
                } else {
                    allClear = false;
                    pdfErrorMessage.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                allClear = false;
                pdfErrorMessage.setVisibility(View.VISIBLE);

            }


        }

        if (interestedFor.getText().toString().isEmpty()) {
            interestedForLayout.setError("Please select an answer");
            interestedForLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
            allClear = false;
        }


        if (allClear) {

            uploadPdfToStorage();
            //saveDetailsInSharedPreferences();
        } else {
            hideProgressDialog();
        }
    }

    private void uploadPdfToStorage() {
        showProgressDialog();
        try {
            if (placed.getText().toString().equals("Yes")) {
              //  Toast.makeText(this, "0", Toast.LENGTH_SHORT).show();
                if (offerLetterDownloadUri.isEmpty()) {
                  //  Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
                    StorageReference offerRef = storageReference.child("Offer Letter").child(mAuth.getUid().toString() + "." + "pdf");
                    offerRef.putFile(pdfData).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {

                                offerRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Uri img = uri;
                                        offerLetterDownloadUri = img.toString();
                                        Log.d(TAG, "onSuccess: " + offerLetterDownloadUri);

                                        saveDetailsInSharedPreferences(offerLetterDownloadUri);
                                    }
                                });

                            } else {
                                Log.d(TAG, "onComplete: error while uploading pdf" + task.getException());
                            }
                        }
                    });
                } else {
                    Toast.makeText(this, "2", Toast.LENGTH_SHORT).show();
                    saveDetailsInSharedPreferences(offerLetterDownloadUri);
                }
            } else {
               // Toast.makeText(this, "34", Toast.LENGTH_SHORT).show();
              //  saveDetailsInSharedPreferences("-1");
                saveDetailsInSharedPreferences(offerLetterDownloadUri);
            }
        } catch (Exception e) {
           // Toast.makeText(this, "3", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            Log.e(TAG, "uploadPdfToStorage: ", e);
            hideProgressDialog();
        }
    }

    private void placedAdapter() {

        String[] branchArray = getResources().getStringArray(R.array.answers);
        //change this to material dropdown in final release
        ArrayAdapter branchArrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, branchArray);
        placed.setAdapter(branchArrayAdapter);

    }

    private void interestedAdapter() {
        String[] branchArray = getResources().getStringArray(R.array.interestedFor);
        //change this to material dropdown in final release
        ArrayAdapter branchArrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, branchArray);
        interestedFor.setAdapter(branchArrayAdapter);

    }


    private void saveDetailsInSharedPreferences(String offerLetterDownloadUri) {
        showProgressDialog();
        SharedPreferences sharedPreferences = getSharedPreferences("studentDetails", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean isPlaced = false;
        if (placed.getText().toString().equals("Yes")) {
            isPlaced = true;
        } else {
            isPlaced = false;
        }

        editor.putBoolean(getString(R.string.isPlaced), isPlaced);
        if (isPlaced) {
            editor.putString(getString(R.string.placedCompanyName), companyName.getText().toString());
            editor.putString(getString(R.string.companyLocation), companyLocation.getText().toString());
            editor.putString(getString(R.string.offeredPackage), offerPackage.getText().toString());
            editor.putString(getString(R.string.interviewDate), interviewDate.getText().toString());
            editor.putString(getString(R.string.joiningDate), joiningDate.getText().toString());
            editor.putString(getString(R.string.offerLetterUri), offerLetterDownloadUri);
            editor.putString(getString(R.string.offerLetterName), pdfName);
        } else {
            editor.putString(getString(R.string.placedCompanyName), String.valueOf(-1));
            editor.putString(getString(R.string.companyLocation), String.valueOf(-1));
            editor.putString(getString(R.string.offeredPackage), String.valueOf(-1));
            editor.putString(getString(R.string.interviewDate), String.valueOf(-1));
            editor.putString(getString(R.string.joiningDate), String.valueOf(-1));
            editor.putString(getString(R.string.offerLetterUri), String.valueOf(-1));
            editor.putString(getString(R.string.offerLetterName), String.valueOf(-1));
        }
       // Toast.makeText(this, offerLetterDownloadUri, Toast.LENGTH_SHORT).show();
        editor.putString(getString(R.string.interestedFor), interestedFor.getText().toString());
        editor.putBoolean("placementDetailsCompleted", true);
        editor.apply();

        onBackPressed();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideProgressDialog();
            }
        }, 2000);
    }

    private void setInterviewDate() {
        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();

        MaterialDatePicker materialDatePicker = builder.setTitleText("Select Interview Date")
                .build();
        materialDatePicker.show(getSupportFragmentManager(), "Interview Date");

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                interviewDate.setText(materialDatePicker.getHeaderText());
                interviewDateLayout.setError("");
                interviewDateLayout.setErrorEnabled(false);
            }
        });
    }

    private void setJoiningDate() {
        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();

        MaterialDatePicker materialDatePicker = builder.setTitleText("Select Joining Date")
                .build();
        materialDatePicker.show(getSupportFragmentManager(), "Joining Date");

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                joiningDate.setText(materialDatePicker.getHeaderText());
                joiningDateLayout.setError("");
                joiningDateLayout.setErrorEnabled(false);
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

        if (editable == companyName.getEditableText()) {
            companyNameLayout.setError("");
            companyNameLayout.setErrorEnabled(false);
            companyNameLayout.setBoxBackgroundColor(getColor(R.color.white));
        }

        if (editable == companyLocation.getEditableText()) {
            companyLocationLayout.setError(null);
            companyLocationLayout.setErrorEnabled(false);
            companyLocationLayout.setBoxBackgroundColor(getColor(R.color.white));
        }

        if (editable == offerPackage.getEditableText()) {
            offerPackageLayout.setError("");
            offerPackageLayout.setErrorEnabled(false);
            offerPackageLayout.setBoxBackgroundColor(getColor(R.color.white));
        }

        if (editable == interviewDate.getEditableText()) {
            interviewDateLayout.setError("");
            interviewDateLayout.setErrorEnabled(false);
            interviewDateLayout.setBoxBackgroundColor(getColor(R.color.white));
        }
        if (editable == joiningDate.getEditableText()) {
            joiningDateLayout.setError("");
            joiningDateLayout.setErrorEnabled(false);
            joiningDateLayout.setBoxBackgroundColor(getColor(R.color.white));
        }


    }

    private void selectPdfFromDevice() {

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "PDF FILE SELECT"), 122);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 122 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            pdfStatus.setImageResource(R.drawable.ic_verified_icon);
            pdfData = data.getData();
            pdfName = getFileName(pdfData);
            part2Pdf.setVisibility(View.VISIBLE);
            pdfNameString.setText(pdfName);
            Toast.makeText(this, "" + getFileName(pdfData), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    private void initFields() {
        backButton = findViewById(R.id.back_button);

        saveButton = findViewById(R.id.save_button);
        progressBar = findViewById(R.id.progress_bar);

        scrollView = findViewById(R.id.scroll_view);
        mainProgressBar = findViewById(R.id.main_progress_bar);

        placedLayout = findViewById(R.id.placed_ans_layout);
        placed = findViewById(R.id.placed_autocomplete_textview);

        companyNameLayout = findViewById(R.id.company_name_layout);
        companyName = findViewById(R.id.company_name);
        companyLocationLayout = findViewById(R.id.company_location_layout);
        companyLocation = findViewById(R.id.company_location);
        offerPackageLayout = findViewById(R.id.package_offered_layout);
        offerPackage = findViewById(R.id.package_offered);
        interviewDateLayout = findViewById(R.id.interview_date_layout);
        interviewDate = findViewById(R.id.interview_date);
        joiningDateLayout = findViewById(R.id.joining_date_layout);
        joiningDate = findViewById(R.id.joining_date);

        part1Pdf = findViewById(R.id.part1);
        part2Pdf = findViewById(R.id.part2);
        pdfStatus = findViewById(R.id.pdf_status);
        pdfNameString = findViewById(R.id.pdf_name);
        pdfCancelButton = findViewById(R.id.pdf_delete);
        pdfErrorMessage = findViewById(R.id.pdf_error_message);

        placedDetailsLayout = findViewById(R.id.placed_details_module);

        interestedForLayout = findViewById(R.id.interested_ans_layout);
        interestedFor = findViewById(R.id.interested_autocomplete_textview);
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