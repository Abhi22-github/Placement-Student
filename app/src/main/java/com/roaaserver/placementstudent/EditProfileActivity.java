package com.roaaserver.placementstudent;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.roaaserver.placementstudent.Models.RequestInfoClass;
import com.roaaserver.placementstudent.Models.StudentInfoClass;
import com.roaaserver.placementstudent.Models.TokenClass;
import com.roaaserver.placementstudent.Nofifications.FcmNotificationsSender;

import java.util.ArrayList;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";

    private ImageButton backButton;
    private StudentInfoClass studentInfoClass;
    private ArrayList<String> selectedItemList;

    private TextInputEditText sem1, sem2, sem3, sem4, sem5, sem6, sem7, sem8, cgpa, percentage, activeBacklog, previousBacklog;
    private TextInputLayout sem1Layout, sem2Layout, sem3Layout, sem4Layout, sem5Layout, sem6Layout, sem7Layout, sem8Layout, cgpaLayout, percentageLayout,
            activeBacklogLayout, previousBacklogLayout;
    private RelativeLayout addRequestLayout;
    private MaterialButton addRequestButton;
    private ProgressBar buttonProgressBar;
    private boolean sem1Flag, sem2Flag, sem3Flag, sem4Flag, sem5Flag, sem6Flag, sem7Flag, sem8Flag, cgpaFlag, percentageFlag, activeBacklogFlag,
            previousBacklogFlag;
    private int modifyCount;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initFields();
        getDataFromIntent();
        sem1.addTextChangedListener(watcher);
        sem2.addTextChangedListener(watcher);
        sem3.addTextChangedListener(watcher);
        sem4.addTextChangedListener(watcher);
        sem5.addTextChangedListener(watcher);
        sem6.addTextChangedListener(watcher);
        sem7.addTextChangedListener(watcher);
        sem8.addTextChangedListener(watcher);
        cgpa.addTextChangedListener(watcher);
        percentage.addTextChangedListener(watcher);
        activeBacklog.addTextChangedListener(watcher);
        previousBacklog.addTextChangedListener(watcher);

        initFirebase();
        backButton.setOnClickListener(v -> onBackPressed());
        addRequestButton.setOnClickListener(v -> CheckDetails());
    }

    private void CheckDetails() {
        showProgressDialog();
        boolean allClear = true;
        if (sem1.getText().toString().isEmpty() && sem1Flag) {
            sem1Layout.setError("Field can't be empty");
            allClear = false;
        }
        if (sem2.getText().toString().isEmpty() && sem2Flag) {
            sem2Layout.setError("Field can't be empty");
            allClear = false;
        }
        if (sem3.getText().toString().isEmpty() && sem3Flag) {
            sem3Layout.setError("Field can't be empty");
            allClear = false;
        }
        if (sem4.getText().toString().isEmpty() && sem4Flag) {
            sem4Layout.setError("Field can't be empty");
            allClear = false;
        }
        if (sem5.getText().toString().isEmpty() && sem5Flag) {
            sem5Layout.setError("Field can't be empty");
            allClear = false;
        }
        if (sem6.getText().toString().isEmpty() && sem6Flag) {
            sem6Layout.setError("Field can't be empty");
            allClear = false;
        }
        if (sem7.getText().toString().isEmpty() && sem7Flag) {
            sem7Layout.setError("Field can't be empty");
            allClear = false;
        }
        if (sem8.getText().toString().isEmpty() && sem8Flag) {
            sem8Layout.setError("Field can't be empty");
            allClear = false;
        }
        if (cgpa.getText().toString().isEmpty() && cgpaFlag) {
            cgpaLayout.setError("Field can't be empty");
            allClear = false;
        }
        if (percentage.getText().toString().isEmpty() && percentageFlag) {
            percentageLayout.setError("Field can't be empty");
            allClear = false;
        }
        if (activeBacklog.getText().toString().isEmpty() && activeBacklogFlag) {
            activeBacklogLayout.setError("Field can't be empty");
            allClear = false;
        }
        if (previousBacklog.getText().toString().isEmpty() && previousBacklogFlag) {
            previousBacklogLayout.setError("Field can't be empty");
            allClear = false;
        }
        if (allClear) {
            addRequestToDatabase();
        }
        hideProgressDialog();
    }

    private void addRequestToDatabase() {
        RequestInfoClass requestInfoClass = new RequestInfoClass();
        requestInfoClass.setImage(studentInfoClass.getImage());
        requestInfoClass.setName(studentInfoClass.getName());
        requestInfoClass.setErpNo(studentInfoClass.getErpNo());
        requestInfoClass.setId(studentInfoClass.getId());
        requestInfoClass.setDepartment(studentInfoClass.getBranch());
        requestInfoClass.setModifyCount(modifyCount);
        requestInfoClass.setTime(null);
        requestInfoClass.setStatus(1);
        if (sem1Flag) {
            requestInfoClass.setSem1(Float.parseFloat(sem1.getText().toString()));
        }
        if (sem2Flag) {
            requestInfoClass.setSem2(Float.parseFloat(sem2.getText().toString()));
        }
        if (sem3Flag) {
            requestInfoClass.setSem3(Float.parseFloat(sem3.getText().toString()));
        }
        if (sem4Flag) {
            requestInfoClass.setSem4(Float.parseFloat(sem4.getText().toString()));
        }
        if (sem5Flag) {
            requestInfoClass.setSem5(Float.parseFloat(sem5.getText().toString()));
        }
        if (sem6Flag) {
            requestInfoClass.setSem6(Float.parseFloat(sem6.getText().toString()));
        }
        if (sem7Flag) {
            requestInfoClass.setSem7(Float.parseFloat(sem7.getText().toString()));
        }
        if (sem8Flag) {
            requestInfoClass.setSem8(Float.parseFloat(sem8.getText().toString()));
        }
        if (cgpaFlag) {
            requestInfoClass.setAggregate(Float.parseFloat(cgpa.getText().toString()));
        }
        if (percentageFlag) {
            requestInfoClass.setAggregatePercentage(Float.parseFloat(percentage.getText().toString()));
        }
        if (activeBacklogFlag) {
            requestInfoClass.setActiveBacklog(Integer.parseInt(activeBacklog.getText().toString()));
        }
        if (previousBacklogFlag) {
            requestInfoClass.setPreviousBacklog(Integer.parseInt(previousBacklog.getText().toString()));
        }

        firestore.collection("Requests").document(mAuth.getCurrentUser().getUid())
                .set(requestInfoClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    hideProgressDialog();
                    sendNotificationToStudents("Request is Pending", "Dear Student your request has been" +
                            " created");
                    onBackPressed();
                } else {
                    Log.d(TAG, "onComplete: " + task.getException());
                }

            }
        });

    }

    private void sendNotificationToStudents(String title, String body) {

        firestore.collection("Tokens").document(mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: " + studentInfoClass.getId());
                    DocumentSnapshot documentSnapshot = task.getResult();
                    TokenClass tokenClass = documentSnapshot.toObject(TokenClass.class);
                    FcmNotificationsSender notificationsSender = new FcmNotificationsSender(tokenClass.getToken(),
                            title, body, getApplicationContext(), EditProfileActivity.this);
                    notificationsSender.SendNotifications();
                    Log.d(TAG, "onComplete: " + tokenClass.getToken());
                } else {

                    Log.d(TAG, "onComplete: " + task.getException());
                }
            }
        });

    }

    private void getDataFromIntent() {
        try {
            studentInfoClass = (StudentInfoClass) getIntent().getSerializableExtra("studentDetails");
            selectedItemList = getIntent().getExtras().getStringArrayList("list");
            modifyCount = selectedItemList.size();
            setUpFields();
        } catch (Exception e) {
            Log.d(TAG, "getDataFromIntent: some problem while getting data from intent");
            Toast.makeText(EditProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpFields() {
        if (selectedItemList.contains("Sem 1")) {
            sem1Layout.setVisibility(View.VISIBLE);
            sem1.setText(String.valueOf(studentInfoClass.getSem1()));
            sem1Flag = true;
        }
        if (selectedItemList.contains("Sem 2")) {
            sem2Layout.setVisibility(View.VISIBLE);
            sem2.setText(String.valueOf(studentInfoClass.getSem2()));
            sem2Flag = true;
        }
        if (selectedItemList.contains("Sem 3")) {
            sem3Layout.setVisibility(View.VISIBLE);
            sem3.setText(String.valueOf(studentInfoClass.getSem3()));
            sem3Flag = true;
        }
        if (selectedItemList.contains("Sem 4")) {
            sem4Layout.setVisibility(View.VISIBLE);
            sem4.setText(String.valueOf(studentInfoClass.getSem4()));
            sem4Flag = true;
        }
        if (selectedItemList.contains("Sem 5")) {
            sem5Layout.setVisibility(View.VISIBLE);
            sem5.setText(String.valueOf(studentInfoClass.getSem5()));
            sem5Flag = true;
        }
        if (selectedItemList.contains("Sem 6")) {
            sem6Layout.setVisibility(View.VISIBLE);
            sem6.setText(String.valueOf(studentInfoClass.getSem6()));
            sem6Flag = true;
        }
        if (selectedItemList.contains("Sem 7")) {
            sem7Layout.setVisibility(View.VISIBLE);
            sem7.setText(String.valueOf(studentInfoClass.getSem7()));
            sem7Flag = true;
        }
        if (selectedItemList.contains("Sem 8")) {
            sem8Layout.setVisibility(View.VISIBLE);
            sem8.setText(String.valueOf(studentInfoClass.getSem8()));
            sem8Flag = true;
        }
        if (selectedItemList.contains("BE Aggregate")) {
            cgpaLayout.setVisibility(View.VISIBLE);
            cgpa.setText(String.valueOf(studentInfoClass.getAggregate()));
            cgpaFlag = true;
        }
        if (selectedItemList.contains("BE Aggregate Percentage")) {
            percentageLayout.setVisibility(View.VISIBLE);
            percentage.setText(String.valueOf(studentInfoClass.getAggregatePercentage()));
            percentageFlag = true;
        }
        if (selectedItemList.contains("Active Backlog")) {
            activeBacklogLayout.setVisibility(View.VISIBLE);
            activeBacklog.setText(String.valueOf(studentInfoClass.getActiveBacklog()));
            activeBacklogFlag = true;
        }
        if (selectedItemList.contains("Previous Backlog")) {
            previousBacklogLayout.setVisibility(View.VISIBLE);
            previousBacklog.setText(String.valueOf(studentInfoClass.getPreviousBacklog()));
            previousBacklogFlag = true;
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
        cgpaLayout = findViewById(R.id.aggregate_cgpa_layout);
        percentageLayout = findViewById(R.id.aggregate_percentage_layout);
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
        cgpa = findViewById(R.id.aggregate_cgpa);
        percentage = findViewById(R.id.aggregate_percentage);
        activeBacklog = findViewById(R.id.active_backlogs);
        previousBacklog = findViewById(R.id.previous_backlogs);

        addRequestButton = findViewById(R.id.request_button);
        buttonProgressBar = findViewById(R.id.progress_bar);
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    private void showProgressDialog() {
        buttonProgressBar.setVisibility(View.VISIBLE);
        addRequestButton.setText("");
        addRequestButton.setEnabled(false);
    }

    private void hideProgressDialog() {
        buttonProgressBar.setVisibility(View.GONE);
        addRequestButton.setText("Add Request");
        addRequestButton.setEnabled(true);
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
        }

        if (editable == sem2.getEditableText()) {
            sem2Layout.setError(null);
            sem2Layout.setErrorEnabled(false);
        }

        if (editable == sem3.getEditableText()) {
            sem3Layout.setError("");
            sem3Layout.setErrorEnabled(false);
        }

        if (editable == sem4.getEditableText()) {
            sem4Layout.setError("");
            sem4Layout.setErrorEnabled(false);
        }

        if (editable == sem5.getEditableText()) {
            sem5Layout.setError("");
            sem5Layout.setErrorEnabled(false);
        }

        if (editable == sem6.getEditableText()) {
            sem6Layout.setError("");
            sem6Layout.setErrorEnabled(false);
        }

        if (editable == sem7.getEditableText()) {
            sem7Layout.setError("");
            sem7Layout.setErrorEnabled(false);
        }
        if (editable == sem8.getEditableText()) {
            sem8Layout.setError("");
            sem8Layout.setErrorEnabled(false);
        }
        if (editable == cgpa.getEditableText()) {
            cgpaLayout.setError("");
            cgpaLayout.setErrorEnabled(false);
        }

        if (editable == percentage.getEditableText()) {
            percentageLayout.setError("");
            percentageLayout.setErrorEnabled(false);
        }

        if (editable == activeBacklog.getEditableText()) {
            activeBacklogLayout.setError("");
            activeBacklogLayout.setErrorEnabled(false);
        }
        if (editable == previousBacklog.getEditableText()) {
            previousBacklogLayout.setError("");
            previousBacklogLayout.setErrorEnabled(false);
        }


    }

}