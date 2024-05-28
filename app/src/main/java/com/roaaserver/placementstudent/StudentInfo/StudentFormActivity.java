package com.roaaserver.placementstudent.StudentInfo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.roaaserver.placementstudent.MainActivity;
import com.roaaserver.placementstudent.Models.StudentDetailsClass;
import com.roaaserver.placementstudent.Models.StudentInfoClass;
import com.roaaserver.placementstudent.Models.TokenClass;
import com.roaaserver.placementstudent.Nofifications.FcmNotificationsSender;
import com.roaaserver.placementstudent.PendingVerificationPage;
import com.roaaserver.placementstudent.R;


public class StudentFormActivity extends AppCompatActivity {
    private static final String TAG = "StudentFormActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;

    private RelativeLayout personalDetails, educationDetails, currentEducation, placementDetails, otherInformation, resume;
    private MaterialButton saveButton;
    private ImageButton backButton;
    private ImageView personalStatus, educationStatus, currentEducationStatus, placementStatus, otherStatus, resumeStatus;
    private ProgressBar progressBar;
    private Uri pdfData;
    private String resumeDownloadUri, pdfName, offerLetterUri, offerLetterName;
    private TextView errorMessage, resumeName;
    private RelativeLayout part1Resume, part2Resume;
    private ImageButton resumeDeleteButton;

    private boolean allCompleted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_form);

        initFields();
        initFirebase();

        personalDetails.setOnClickListener(view -> sendToPersonalDetails());
        educationDetails.setOnClickListener(view -> sendToEducationDetails());
        currentEducation.setOnClickListener(view -> sendToCurrentEducation());
        otherInformation.setOnClickListener(view -> sendToOtherInformation());
        placementDetails.setOnClickListener(view -> sendToPlacementDetails());
        part1Resume.setOnClickListener(view -> selectPdfFromDevice());
        backButton.setOnClickListener(view -> onBackPressed());

        resumeDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                part2Resume.setVisibility(View.GONE);
                pdfData = null;
                setUpStatusIcons();
            }
        });

        saveButton.setOnClickListener(view -> checkDetails());


    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

    }


    private void checkDetails() {
        showProgressDialog();
        SharedPreferences sharedPreferences = this.getSharedPreferences("studentDetails", 0);
        boolean allClear = true;
        if (!sharedPreferences.getBoolean("personalDetailsCompleted", false)) {
            allClear = false;
        }
        if (!sharedPreferences.getBoolean("educationDetailsCompleted", false)) {
            allClear = false;
        }
        if (!sharedPreferences.getBoolean("currentEducationCompleted", false)) {
            allClear = false;
        }
        if (!sharedPreferences.getBoolean("otherInformationCompleted", false)) {
            allClear = false;
        }
        if (!sharedPreferences.getBoolean("placementDetailsCompleted", false)) {
            allClear = false;
        }
        try {
            if (pdfData.toString().isEmpty()) {
                allClear = false;

            }
        } catch (Exception e) {
            allClear = false;

        }
        if (allClear) {
            errorMessage.setVisibility(View.GONE);
            uploadPdfToStorage();
        } else {
            errorMessage.setVisibility(View.VISIBLE);
            hideProgressDialog();
        }
    }

    private void uploadPdfToStorage() {
        StorageReference resumeRef = storageReference.child("Resume").child(mAuth.getUid().toString() + "." + "pdf");
        StorageReference offerRef = storageReference.child("Offer Letter").child(mAuth.getUid().toString() + "." + "pdf");
        resumeRef.putFile(pdfData).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {

                    resumeRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri img = uri;
                            resumeDownloadUri = img.toString();
                            Log.d(TAG, "onSuccess: " + resumeDownloadUri);

                            linkDataToObject(resumeDownloadUri);
                        }
                    });

                } else {
                    Log.d(TAG, "onComplete: error while uploading pdf" + task.getException());
                }
            }
        });
    }

    private void linkDataToObject(String resumeDownloadUri) {
        showProgressDialog();
        SharedPreferences sharedPreferences = this.getSharedPreferences("studentDetails", 0);
        StudentInfoClass studentInfoClass = new StudentInfoClass();

        studentInfoClass.setId(mAuth.getCurrentUser().getUid());
        Log.d(TAG, "linkDataToObject: id" + mAuth.getCurrentUser().getUid());
        studentInfoClass.setImage(sharedPreferences.getString("image", ""));
        studentInfoClass.setEmail(sharedPreferences.getString("email", ""));

//        Log.d(TAG, "linkDataToObject: email"+sharedPreferences.getString("email", ""));
//        Log.d(TAG, "linkDataToObject: image"+sharedPreferences.getString("image", ""));

        studentInfoClass.setName(sharedPreferences.getString(getString(R.string.name), ""));
        studentInfoClass.setContactNo(sharedPreferences.getString(getString(R.string.contactNo), ""));
        studentInfoClass.setAddress(sharedPreferences.getString(getString(R.string.address), ""));
        studentInfoClass.setErpNo(sharedPreferences.getString(getString(R.string.erpNo), ""));
        studentInfoClass.setPrnNo(sharedPreferences.getString(getString(R.string.prnNo), ""));
        studentInfoClass.setBirthDate(sharedPreferences.getString(getString(R.string.birthDate), ""));
        studentInfoClass.setGender(sharedPreferences.getString(getString(R.string.gender), ""));

        Log.d(TAG, "linkDataToObject: setName" + sharedPreferences.getString(getString(R.string.name), ""));
        Log.d(TAG, "linkDataToObject: setContactNo" + sharedPreferences.getString(getString(R.string.contactNo), ""));
        Log.d(TAG, "linkDataToObject: setAddress" + sharedPreferences.getString(getString(R.string.address), ""));
        Log.d(TAG, "linkDataToObject: setErpNo" + sharedPreferences.getString(getString(R.string.erpNo), ""));
        Log.d(TAG, "linkDataToObject: setPrnNo" + sharedPreferences.getString(getString(R.string.prnNo), ""));
        Log.d(TAG, "linkDataToObject: setBirthDate" + sharedPreferences.getString(getString(R.string.birthDate), ""));
        Log.d(TAG, "linkDataToObject: setGender" + sharedPreferences.getString(getString(R.string.gender), ""));


        studentInfoClass.setSscPercentage(Float.parseFloat(sharedPreferences.getString(getString(R.string.ssc), "")));
        studentInfoClass.setSscPassoutYear(Integer.parseInt(sharedPreferences.getString(getString(R.string.sscYear), "")));
        studentInfoClass.setSscCollege(sharedPreferences.getString(getString(R.string.sscCollege), ""));

        studentInfoClass.setHscPercentage(Float.parseFloat(sharedPreferences.getString(getString(R.string.hsc), "")));
        studentInfoClass.setHscPassoutYear(Integer.parseInt(sharedPreferences.getString(getString(R.string.hscYear), "")));
        studentInfoClass.setHscCollege(sharedPreferences.getString(getString(R.string.hscCollege), ""));

        studentInfoClass.setDiplomaPercentage(Float.parseFloat(sharedPreferences.getString(getString(R.string.diploma), "")));
        studentInfoClass.setDiplomaPassoutYear(Integer.parseInt(sharedPreferences.getString(getString(R.string.diplomaYear), "")));
        studentInfoClass.setDiplomaCollege(sharedPreferences.getString(getString(R.string.diplomaCollege), ""));

        studentInfoClass.setGraduationYear(Integer.parseInt(sharedPreferences.getString(getString(R.string.year), "")));
        studentInfoClass.setBranch(sharedPreferences.getString("branch", ""));
        //  studentInfoClass.setClassDetail(sharedPreferences.getString("class", ""));
         studentInfoClass.setDivision(sharedPreferences.getString("division", ""));

//        Log.d(TAG, "linkDataToObject: setSscPercentage"+sharedPreferences.getString(getString(R.string.ssc), ""));
//        Log.d(TAG, "linkDataToObject: setSscPassoutYear"+sharedPreferences.getString(getString(R.string.sscYear), ""));
//        Log.d(TAG, "linkDataToObject: setSscCollege"+sharedPreferences.getString(getString(R.string.sscCollege), ""));
//        Log.d(TAG, "linkDataToObject: setHscPercentage"+sharedPreferences.getString(getString(R.string.hsc), ""));
//        Log.d(TAG, "linkDataToObject: setHscPassoutYear"+sharedPreferences.getString(getString(R.string.hscYear), ""));
//        Log.d(TAG, "linkDataToObject: setHscCollege"+sharedPreferences.getString(getString(R.string.hscCollege), ""));
//        Log.d(TAG, "linkDataToObject: setDiplomaPercentage"+sharedPreferences.getString(getString(R.string.diploma), ""));
//        Log.d(TAG, "linkDataToObject: setDiplomaPassoutYear"+sharedPreferences.getString(getString(R.string.diplomaYear), ""));
//        Log.d(TAG, "linkDataToObject: setDiplomaCollege"+sharedPreferences.getString(getString(R.string.diplomaCollege), ""));
//        Log.d(TAG, "linkDataToObject: setGraduationYear"+sharedPreferences.getString(getString(R.string.year), ""));
//        Log.d(TAG, "linkDataToObject: setBranch"+sharedPreferences.getString(getString(R.string.branch), ""));


        studentInfoClass.setSem1(Float.parseFloat(sharedPreferences.getString(getString(R.string.sem1), "")));
        studentInfoClass.setSem2(Float.parseFloat(sharedPreferences.getString(getString(R.string.sem2), "")));
        studentInfoClass.setSem3(Float.parseFloat(sharedPreferences.getString(getString(R.string.sem3), "")));
        studentInfoClass.setSem4(Float.parseFloat(sharedPreferences.getString(getString(R.string.sem4), "")));
        studentInfoClass.setSem5(Float.parseFloat(sharedPreferences.getString(getString(R.string.sem5), "")));
        studentInfoClass.setSem6(Float.parseFloat(sharedPreferences.getString(getString(R.string.sem6), "")));
        studentInfoClass.setSem7(Float.parseFloat(sharedPreferences.getString(getString(R.string.sem7), "")));
        studentInfoClass.setSem8(Float.parseFloat(sharedPreferences.getString(getString(R.string.sem8), "")));
        studentInfoClass.setAggregate(Float.parseFloat(sharedPreferences.getString(getString(R.string.aggregate), "")));
        studentInfoClass.setAggregatePercentage(Float.parseFloat(sharedPreferences.getString(getString(R.string.aggregatePercentage), "")));
        studentInfoClass.setActiveBacklog(Integer.parseInt(sharedPreferences.getString(getString(R.string.activeBacklog), "")));
        studentInfoClass.setPreviousBacklog(Integer.parseInt(sharedPreferences.getString(getString(R.string.previousBacklog), "")));
        studentInfoClass.setGapPresent(sharedPreferences.getBoolean(getString(R.string.gap), false));
        studentInfoClass.setEngineeringGapPresent(sharedPreferences.getBoolean(getString(R.string.engineeringGap), false));
        studentInfoClass.setGapYears(sharedPreferences.getString(getString(R.string.gapYears), ""));

//        Log.d(TAG, "linkDataToObject: setSem1"+sharedPreferences.getString(getString(R.string.sem1), ""));
//        Log.d(TAG, "linkDataToObject: setSem2"+sharedPreferences.getString(getString(R.string.sem2), ""));
//        Log.d(TAG, "linkDataToObject: setSem3"+sharedPreferences.getString(getString(R.string.sem3), ""));
//        Log.d(TAG, "linkDataToObject: setSem4"+sharedPreferences.getString(getString(R.string.sem4), ""));
//        Log.d(TAG, "linkDataToObject: setSem5"+sharedPreferences.getString(getString(R.string.sem5), ""));
//        Log.d(TAG, "linkDataToObject: setSem6"+sharedPreferences.getString(getString(R.string.sem6), ""));
//        Log.d(TAG, "linkDataToObject: setSem7"+sharedPreferences.getString(getString(R.string.sem7), ""));
//        Log.d(TAG, "linkDataToObject: setSem8"+sharedPreferences.getString(getString(R.string.sem8), ""));
//        Log.d(TAG, "linkDataToObject: setAggregate"+sharedPreferences.getString(getString(R.string.aggregate), ""));
//        Log.d(TAG, "linkDataToObject: setAggregatePercentage"+sharedPreferences.getString(getString(R.string.aggregatePercentage), ""));
//        Log.d(TAG, "linkDataToObject: setActiveBacklog"+sharedPreferences.getString(getString(R.string.activeBacklog), ""));
//        Log.d(TAG, "linkDataToObject: setPreviousBacklog"+sharedPreferences.getString(getString(R.string.previousBacklog), ""));
//        Log.d(TAG, "linkDataToObject: setGapPresent"+sharedPreferences.getBoolean(getString(R.string.gap), false));
//        Log.d(TAG, "linkDataToObject: setEngineeringGapPresent"+sharedPreferences.getBoolean(getString(R.string.engineeringGap), false));
//        Log.d(TAG, "linkDataToObject: setGapYears"+sharedPreferences.getString(getString(R.string.gapYears), ""));


        studentInfoClass.setPlaced(sharedPreferences.getBoolean(getString(R.string.isPlaced), false));
        studentInfoClass.setPlacedCompanyName(sharedPreferences.getString(getString(R.string.placedCompanyName), ""));
        studentInfoClass.setPlacedCompanyLocation(sharedPreferences.getString(getString(R.string.companyLocation), ""));
        studentInfoClass.setOfferedPackage(Float.parseFloat(sharedPreferences.getString(getString(R.string.offeredPackage), "")));
        studentInfoClass.setInterviewDate(sharedPreferences.getString(getString(R.string.interviewDate), ""));
        studentInfoClass.setJoiningDate(sharedPreferences.getString(getString(R.string.joiningDate), ""));
        studentInfoClass.setOfferLetterName(sharedPreferences.getString(getString(R.string.offerLetterName), ""));
        studentInfoClass.setOfferLetterLink(sharedPreferences.getString(getString(R.string.offerLetterUri), ""));
        studentInfoClass.setInterestedFor(sharedPreferences.getString(getString(R.string.interestedFor), ""));

//        Log.d(TAG, "linkDataToObject: setPlaced"+sharedPreferences.getBoolean(getString(R.string.isPlaced), false));
//        Log.d(TAG, "linkDataToObject: setPlacedCompanyName"+sharedPreferences.getString(getString(R.string.placedCompanyName), ""));
//        Log.d(TAG, "linkDataToObject: setPlacedCompanyLocation"+sharedPreferences.getString(getString(R.string.companyLocation), ""));
//        Log.d(TAG, "linkDataToObject: setOfferedPackage"+sharedPreferences.getString(getString(R.string.offeredPackage), ""));
//        Log.d(TAG, "linkDataToObject: setInterviewDate"+sharedPreferences.getString(getString(R.string.interviewDate), ""));
//        Log.d(TAG, "linkDataToObject: setJoiningDate"+sharedPreferences.getString(getString(R.string.joiningDate), ""));
//        Log.d(TAG, "linkDataToObject: setOfferLetterName"+sharedPreferences.getString(getString(R.string.offerLetterName), ""));
//        Log.d(TAG, "linkDataToObject: setOfferLetterLink"+sharedPreferences.getString(getString(R.string.offerLetterUri), ""));
//        Log.d(TAG, "linkDataToObject: setInterestedFor"+sharedPreferences.getString(getString(R.string.interestedFor), ""));

        studentInfoClass.setInternshipPresent(sharedPreferences.getBoolean(getString(R.string.internshipAns), false));
        studentInfoClass.setInternshipCompanyName(sharedPreferences.getString("company", ""));
        studentInfoClass.setInternshipPosition(sharedPreferences.getString("position", ""));
        studentInfoClass.setInternshipDuration(sharedPreferences.getString("duration", ""));
        studentInfoClass.setJapaneseCertificationPresent(sharedPreferences.getBoolean(getString(R.string.japaneseAns), false));
        studentInfoClass.setJlpt(sharedPreferences.getString(getString(R.string.jlpt), ""));
        studentInfoClass.setCertificatePresent(sharedPreferences.getBoolean(getString(R.string.certificationAns), false));
        studentInfoClass.setCertification(sharedPreferences.getString("certificates", ""));
        studentInfoClass.setVerified(false);
        studentInfoClass.setActive(false);
        studentInfoClass.setResumeLink(resumeDownloadUri);
        studentInfoClass.setResumeName(pdfName);
        studentInfoClass.setProfileCompleted(true);


//        Log.d(TAG, "linkDataToObject: setInternshipPresent"+sharedPreferences.getBoolean(getString(R.string.internshipAns), false));
//        Log.d(TAG, "linkDataToObject: setInternshipCompanyName"+sharedPreferences.getString(getString(R.string.internshipCompanyName), ""));
//        Log.d(TAG, "linkDataToObject: setInternshipPosition"+sharedPreferences.getString(getString(R.string.position), ""));
//        Log.d(TAG, "linkDataToObject: setInternshipDuration"+sharedPreferences.getString(getString(R.string.duration), ""));
//        Log.d(TAG, "linkDataToObject: setJapaneseCertificationPresent"+sharedPreferences.getBoolean(getString(R.string.japaneseAns), false));
//        Log.d(TAG, "linkDataToObject: setJlpt"+sharedPreferences.getString(getString(R.string.jlpt), ""));
//        Log.d(TAG, "linkDataToObject: setCertificatePresent"+sharedPreferences.getBoolean(getString(R.string.certificationAns), false));
//        Log.d(TAG, "linkDataToObject: setCertification"+sharedPreferences.getString(getString(R.string.certification), ""));

        studentInfoClass.setStatus(1);
        addDetailsInFirebase(studentInfoClass);
        hideProgressDialog();

    }

    private void addDetailsInFirebase(StudentInfoClass studentInfoClass) {
        firestore.collection("Students Information").document(mAuth.getCurrentUser().getUid())
                .set(studentInfoClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    checkIfVerified();
                } else {
                    Log.d(TAG, "onComplete: fail to store data" + task.getException());
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressDialog();
                    }
                }, 2000);
            }
        });
    }

    private void checkIfVerified() {
        Log.d(TAG, "checkIfVerified: " + mAuth.getCurrentUser().getUid());
        firestore.collection("Students").document(mAuth.getCurrentUser().getUid()).get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        StudentDetailsClass studentDetailsClass = documentSnapshot.toObject(StudentDetailsClass.class);
                        if (studentDetailsClass.isVerified()) {
                            sendToMainActivity();
                        } else {
                            sendUserToPendingVerificationPage();
                        }


                    }
                });
    }

    private void sendUserToPendingVerificationPage() {
        FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/Admins",
                "Verification is pending", "New Student has registered and waiting for verification", getApplicationContext(), StudentFormActivity.this);
        notificationsSender.SendNotifications();
        Intent intent = new Intent(StudentFormActivity.this, PendingVerificationPage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
       // sendNotificationToStudents("Congratulation", "Your have completed your profile. Please wait patiently while we are verifying the your details");

    }

//    private void sendNotificationToStudents(String title, String body) {
//
//        firestore.collection("Tokens").document(mAuth.getCurrentUser().getUid())
//                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    // Log.d(TAG, "onComplete: " + studentInfoClass.getId());
//                    DocumentSnapshot documentSnapshot = task.getResult();
//                    TokenClass tokenClass = documentSnapshot.toObject(TokenClass.class);
//                    FcmNotificationsSender notificationsSender = new FcmNotificationsSender(tokenClass.getToken(),
//                            title, body, getApplicationContext(), StudentFormActivity.this);
//                    notificationsSender.SendNotifications();
//
//                    Log.d(TAG, "onComplete: " + tokenClass.getToken());
//                } else {
//
//                    Log.d(TAG, "onComplete: " + task.getException());
//                }
//            }
//        });
//
//    }


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
            resumeStatus.setImageResource(R.drawable.ic_verified_icon);
            pdfData = data.getData();
            pdfName = getFileName(pdfData);
            part2Resume.setVisibility(View.VISIBLE);
            resumeName.setText(pdfName);
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

    private void sendToMainActivity() {
        Intent intent = new Intent(StudentFormActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    private void sendToPersonalDetails() {
        Intent userNameIntent = new Intent(StudentFormActivity.this, PersonalDetails.class);
        //userNameIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(userNameIntent);
        // finish();
    }

    private void sendToEducationDetails() {
        Intent userNameIntent = new Intent(StudentFormActivity.this, EducationDetails.class);
        //userNameIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(userNameIntent);
        // finish();
    }

    private void sendToCurrentEducation() {
        Intent userNameIntent = new Intent(StudentFormActivity.this, CurrentEducation.class);
        //userNameIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(userNameIntent);
        // finish();
    }

    private void sendToPlacementDetails() {
        Intent userNameIntent = new Intent(StudentFormActivity.this, PlacementDetails.class);
        //userNameIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(userNameIntent);
        // finish();
    }

    private void sendToOtherInformation() {
        Intent userNameIntent = new Intent(StudentFormActivity.this, OtherInformation.class);
        //userNameIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(userNameIntent);
        // finish();
    }


    private void initFields() {
        personalDetails = findViewById(R.id.personal_details);
        educationDetails = findViewById(R.id.education_details);
        currentEducation = findViewById(R.id.current_education);
        otherInformation = findViewById(R.id.other_information);
        resume = findViewById(R.id.resume);
        saveButton = findViewById(R.id.save_button);
        backButton = findViewById(R.id.back_button);
        personalStatus = findViewById(R.id.personal_details_status);
        educationStatus = findViewById(R.id.educational_details_status);
        currentEducationStatus = findViewById(R.id.current_education_status);
        otherStatus = findViewById(R.id.other_information_status);
        resumeStatus = findViewById(R.id.resume_status);
        progressBar = findViewById(R.id.progress_bar);
        errorMessage = findViewById(R.id.pdf_error_message);
        placementDetails = findViewById(R.id.placement_information);
        placementStatus = findViewById(R.id.placement_information_status);
        resumeName = findViewById(R.id.resume_name);
        part1Resume = findViewById(R.id.part1);
        part2Resume = findViewById(R.id.part2);
        resumeDeleteButton = findViewById(R.id.resume_delete);

    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpStatusIcons();
    }

    private void setUpStatusIcons() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("studentDetails", 0);
        if (sharedPreferences.getBoolean("personalDetailsCompleted", false)) {
            personalStatus.setImageResource(R.drawable.ic_verified_icon);
        }
        if (sharedPreferences.getBoolean("educationDetailsCompleted", false)) {
            educationStatus.setImageResource(R.drawable.ic_verified_icon);
        }
        allCompleted = false;

        if (sharedPreferences.getBoolean("currentEducationCompleted", false)) {
            currentEducationStatus.setImageResource(R.drawable.ic_verified_icon);
        }
        if (sharedPreferences.getBoolean("placementDetailsCompleted", false)) {
            placementStatus.setImageResource(R.drawable.ic_verified_icon);
        }
        if (sharedPreferences.getBoolean("otherInformationCompleted", false)) {
            otherStatus.setImageResource(R.drawable.ic_verified_icon);
        }
        try {
            if (!pdfData.toString().isEmpty()) {
                resumeStatus.setImageResource(R.drawable.ic_verified_icon);
            } else {
                resumeStatus.setImageResource(0);
            }
        } catch (Exception e) {
            Log.d(TAG, "setUpStatusIcons: " + e);
            resumeStatus.setImageResource(0);
        }
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