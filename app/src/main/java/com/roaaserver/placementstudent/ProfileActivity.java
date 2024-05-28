package com.roaaserver.placementstudent;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.roaaserver.placementstudent.Models.StudentInfoClass;
import com.roaaserver.placementstudent.utilities.EditMultipleChoiceFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements EditMultipleChoiceFragment.onMultiChoiceListener {
    private static final String TAG = "ProfileActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;

    private CircleImageView profileImage;
    private ImageButton backButton;
    private ProgressBar imageProgress;
    private TextView name, email, contact, address, erp, prn, dob, gender, ssc, sscCollege, sscYear, hsc, hscCollege, hscYear, diploma,
            diplomaCollege, diplomaYear, year, branch, classField, division,
            aggregate, aggregatePercentage, activeBacklog, prevBacklog, sem1, sem2, sem3, sem4, sem5, sem6, sem7, sem8, gap, engineeringGap, gapYears,
            isPlaced, placedCompanyName, companyLocation, packageOffered, interviewDate, joiningDate,
            internshipAns, certificateAns, company, position, duration, certificate, japanese, jlpt;
    private LinearLayout internshipLayout, certificateLayout, hscLayout, diplomaLayout, gapLayout, placedLayout, japaneseLayout;
    private LinearLayout resumeLayout;
    private TextView resumeName, offerLetterName;
    private ImageButton resumeChangeButton, offerLetterChangeButton;
    private ProgressBar progressBar;


    private StudentInfoClass studentInfoClass;
    private TextView edit;

    private Uri pdfData;
    private String pdfName;
    private String resumeDownloadUri, offerLetterDownloadUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initFields();

        initFirebase();

        backButton.setOnClickListener(view -> onBackPressed());
        getDataFromFirebase();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment multiChoiceDialog = new EditMultipleChoiceFragment();
                multiChoiceDialog.setCancelable(false);
                multiChoiceDialog.show(getSupportFragmentManager(), "MultiChoice Dialog");
            }
        });

        resumeName.setOnClickListener(v -> sendUserToPDFViewerActivity(studentInfoClass.getResumeLink(), studentInfoClass.getResumeName()));
        offerLetterName.setOnClickListener(v -> sendUserToPDFViewerActivity(studentInfoClass.getOfferLetterLink(), studentInfoClass.getOfferLetterName()));

        resumeChangeButton.setOnClickListener(v -> changeResume());
        offerLetterChangeButton.setOnClickListener(v -> changeOfferLetter());
    }

    private void changeResume() {
        selectPdfFromDevice();
    }

    private void changeOfferLetter() {
        selectOfferLetterPdfFromDevice();
    }


    private void selectPdfFromDevice() {

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "PDF FILE SELECT"), 122);
    }

    private void selectOfferLetterPdfFromDevice() {

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "PDF FILE SELECT"), 222);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 122 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            pdfData = data.getData();
            pdfName = getFileName(pdfData);
            // resumeName.setText(pdfName);
            Toast.makeText(this, "" + getFileName(pdfData), Toast.LENGTH_SHORT).show();
            new MaterialAlertDialogBuilder(ProfileActivity.this)
                    .setTitle("Are you sure")
                    .setMessage("Do you want to upload the resume")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressBar.setVisibility(View.VISIBLE);
                            uploadResumeToStorage(pdfName);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }
        if (requestCode == 222 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            pdfData = data.getData();
            pdfName = getFileName(pdfData);

            new MaterialAlertDialogBuilder(ProfileActivity.this)
                    .setTitle("Are you sure")
                    .setMessage("Do you want to upload the resume")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressBar.setVisibility(View.VISIBLE);
                            uploadOfferLetterToStorage(pdfName);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }

    }

    private void uploadResumeToStorage(String pdfName) {
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

                            WriteBatch writeBatch = firestore.batch();

                            DocumentReference offerLetterRef = firestore.collection("Students Information")
                                    .document(mAuth.getCurrentUser().getUid());
                            writeBatch.update(offerLetterRef, "resumeLink", resumeDownloadUri);
                            writeBatch.update(offerLetterRef,"resumeName",pdfName);
                            writeBatch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        onBackPressed();
                                    }else{
                                        Toast.makeText(ProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            // linkDataToObject(resumeDownloadUri);
                        }
                    });

                } else {
                    Log.d(TAG, "onComplete: error while uploading pdf" + task.getException());
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void uploadOfferLetterToStorage(String pdfName) {
        StorageReference resumeRef = storageReference.child("Resume").child(mAuth.getUid().toString() + "." + "pdf");
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

                            WriteBatch writeBatch = firestore.batch();

                            DocumentReference offerLetterRef = firestore.collection("Students Information")
                                    .document(mAuth.getCurrentUser().getUid());
                            writeBatch.update(offerLetterRef, "offerLetterLink", offerLetterDownloadUri);
                            writeBatch.update(offerLetterRef,"offerLetterName",pdfName);
                            writeBatch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        onBackPressed();
                                    }else{
                                        Toast.makeText(ProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                           // linkDataToObject(resumeDownloadUri);
                        }
                    });

                } else {
                    Log.d(TAG, "onComplete: error while uploading pdf" + task.getException());
                }
                progressBar.setVisibility(View.GONE);
            }
        });
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

    private void sendUserToPDFViewerActivity(String Link, String name) {
        Intent intent = new Intent(this, pdfViewerActivity.class);
        intent.putExtra("pdfUrl", Link);
        intent.putExtra("pdfName", name);
        startActivity(intent);
    }

    private void initFields() {
        //resumeLayout = findViewById(R.id.resume_layout);
        resumeName = findViewById(R.id.resume_name);
        resumeChangeButton = findViewById(R.id.resume_change_button);
        edit = findViewById(R.id.edit);
        backButton = findViewById(R.id.back_button);
        profileImage = findViewById(R.id.profile_image_view);
        imageProgress = findViewById(R.id.profile_image_progress_bar);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        contact = findViewById(R.id.contact);
        address = findViewById(R.id.address);
        erp = findViewById(R.id.erp);
        prn = findViewById(R.id.prn);
        dob = findViewById(R.id.dob);
        gender = findViewById(R.id.gender);
        ssc = findViewById(R.id.ssc);
        sscCollege = findViewById(R.id.ssc_college);
        sscYear = findViewById(R.id.ssc_year);
        hsc = findViewById(R.id.hsc);
        hscCollege = findViewById(R.id.hsc_college);
        hscYear = findViewById(R.id.hsc_year);
        hscLayout = findViewById(R.id.hsc_layout);
        diploma = findViewById(R.id.diploma);
        diplomaCollege = findViewById(R.id.diploma_college);
        diplomaYear = findViewById(R.id.diploma_year);
        diplomaLayout = findViewById(R.id.diploma_layout);
        year = findViewById(R.id.year);
        branch = findViewById(R.id.branch);
        classField = findViewById(R.id.class_field);
        division = findViewById(R.id.division);
        gap = findViewById(R.id.gap);
        engineeringGap = findViewById(R.id.engineering_gap);
        gapYears = findViewById(R.id.gap_years);
        gapLayout = findViewById(R.id.engineering_gap_years_layout);
        aggregate = findViewById(R.id.aggregate);
        aggregatePercentage = findViewById(R.id.aggregate_percentage);
        activeBacklog = findViewById(R.id.active_backlogs);
        prevBacklog = findViewById(R.id.prev_backlogs);
        sem1 = findViewById(R.id.sem1);
        sem2 = findViewById(R.id.sem2);
        sem3 = findViewById(R.id.sem3);
        sem4 = findViewById(R.id.sem4);
        sem5 = findViewById(R.id.sem5);
        sem6 = findViewById(R.id.sem6);
        sem7 = findViewById(R.id.sem7);
        sem8 = findViewById(R.id.sem8);
        isPlaced = findViewById(R.id.is_placed);
        placedLayout = findViewById(R.id.placed_company_layout);
        placedCompanyName = findViewById(R.id.placed_company_name);
        companyLocation = findViewById(R.id.company_location);
        packageOffered = findViewById(R.id.offered_package);
        interviewDate = findViewById(R.id.interview_date);
        joiningDate = findViewById(R.id.joining_date);
        internshipAns = findViewById(R.id.internship_ans);
        offerLetterName = findViewById(R.id.offer_letter_name);
        offerLetterChangeButton = findViewById(R.id.offer_letter_change_button);
        internshipLayout = findViewById(R.id.internship_details_layout);
        company = findViewById(R.id.company_name);
        position = findViewById(R.id.position);
        duration = findViewById(R.id.duration);
        certificateAns = findViewById(R.id.certificated_ans);
        certificateLayout = findViewById(R.id.certificate_layout);
        certificate = findViewById(R.id.certificate);
        japanese = findViewById(R.id.japanese_ans);
        japaneseLayout = findViewById(R.id.japanese_layout);
        jlpt = findViewById(R.id.jlpt);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    private void getDataFromFirebase() {
        firestore.collection("Students Information").document(mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    studentInfoClass = task.getResult().toObject(StudentInfoClass.class);
                    setUpFields();
                } else {
                    Log.d(TAG, "onComplete: failed to get student data from firebase" + task.getException());
                }
            }
        });
    }

    private void setUpFields() {
        imageProgress.setVisibility(View.VISIBLE);
        if (!studentInfoClass.getImage().isEmpty()) {
            Picasso.get().load(studentInfoClass.getImage()).into(profileImage, new Callback() {
                @Override
                public void onSuccess() {
                    imageProgress.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    imageProgress.setVisibility(View.GONE);
                }
            });
        }
        resumeName.setText(studentInfoClass.getResumeName());
        name.setText(studentInfoClass.getName());
        email.setText(studentInfoClass.getEmail());
        contact.setText(studentInfoClass.getContactNo());
        address.setText(studentInfoClass.getAddress());
        erp.setText(studentInfoClass.getErpNo());
        prn.setText(studentInfoClass.getPrnNo());
        dob.setText(studentInfoClass.getBirthDate());
        gender.setText(studentInfoClass.getGender());

        ssc.setText(String.valueOf(studentInfoClass.getSscPercentage()) + "%");
        sscCollege.setText(studentInfoClass.getSscCollege());
        sscYear.setText(String.valueOf(studentInfoClass.getSscPassoutYear()));

        if (studentInfoClass.getHscPercentage() == -1) {
            hsc.setText("Not Applicable");
            hscLayout.setVisibility(View.GONE);
        } else {
            hsc.setText(String.valueOf(studentInfoClass.getHscPercentage()) + "%");
            hscLayout.setVisibility(View.VISIBLE);
        }
        hscCollege.setText(studentInfoClass.getHscCollege());
        hscYear.setText(String.valueOf(studentInfoClass.getHscPassoutYear()));
        if (studentInfoClass.getDiplomaPercentage() == -1) {
            diploma.setText("Not Applicable");
            diplomaLayout.setVisibility(View.GONE);
        } else {
            diploma.setText(String.valueOf(studentInfoClass.getDiplomaPercentage()) + "%");
            diplomaLayout.setVisibility(View.VISIBLE);
        }
        diplomaCollege.setText(studentInfoClass.getDiplomaCollege());
        diplomaYear.setText(String.valueOf(studentInfoClass.getDiplomaPassoutYear()));
        year.setText(String.valueOf(studentInfoClass.getGraduationYear()));
        branch.setText(studentInfoClass.getBranch());
        //classField.setText(studentInfoClass.getClassDetail());
        division.setText(studentInfoClass.getDivision());

        aggregate.setText(String.valueOf(studentInfoClass.getAggregate()));
        aggregatePercentage.setText(String.valueOf(studentInfoClass.getAggregatePercentage()));
        if (studentInfoClass.getActiveBacklog() == 0) {
            activeBacklog.setText("No Backlog");
        } else {
            activeBacklog.setText(String.valueOf(studentInfoClass.getActiveBacklog()));
        }
        if (studentInfoClass.getPreviousBacklog() == 0) {
            prevBacklog.setText("No Backlog");
        } else {
            prevBacklog.setText(String.valueOf(studentInfoClass.getPreviousBacklog()));
        }

        sem1.setText(String.valueOf(studentInfoClass.getSem1()));
        sem2.setText(String.valueOf(studentInfoClass.getSem2()));
        sem3.setText(String.valueOf(studentInfoClass.getSem3()));
        sem4.setText(String.valueOf(studentInfoClass.getSem4()));
        sem5.setText(String.valueOf(studentInfoClass.getSem5()));
        sem6.setText(String.valueOf(studentInfoClass.getSem6()));
        if (studentInfoClass.getSem7() == -1) {
            sem7.setText("Not applicable");
        } else {
            sem7.setText(String.valueOf(studentInfoClass.getSem7()));
        }
        if (studentInfoClass.getSem8() == -1) {
            sem8.setText("Not applicable");
        } else {
            sem8.setText(String.valueOf(studentInfoClass.getSem8()));
        }

        if (studentInfoClass.isGapPresent()) {
            gap.setText("Yes");
        } else {
            gap.setText("No");
        }
        if (studentInfoClass.isEngineeringGapPresent()) {
            engineeringGap.setText("Yes");
            gapLayout.setVisibility(View.VISIBLE);
        } else {
            engineeringGap.setText("No");
            gapLayout.setVisibility(View.GONE);
        }

        gapYears.setText(studentInfoClass.getGapYears());

        if (studentInfoClass.isPlaced()) {
            isPlaced.setText("Yes");
            placedLayout.setVisibility(View.VISIBLE);
        } else {
            isPlaced.setText("No");
            placedLayout.setVisibility(View.GONE);
        }

        placedCompanyName.setText(studentInfoClass.getPlacedCompanyName());
        companyLocation.setText(studentInfoClass.getPlacedCompanyLocation());
        packageOffered.setText(studentInfoClass.getOfferedPackage() + " LPA");
        interviewDate.setText(studentInfoClass.getInterviewDate());
        joiningDate.setText(studentInfoClass.getJoiningDate());
        offerLetterName.setText(studentInfoClass.getOfferLetterName());


        if (studentInfoClass.isInternshipPresent()) {
            internshipAns.setText("Yes");
            internshipLayout.setVisibility(View.VISIBLE);

        } else {
            internshipAns.setText("No");
            internshipLayout.setVisibility(View.GONE);
        }
        company.setText(studentInfoClass.getInternshipCompanyName());
        position.setText(studentInfoClass.getInternshipPosition());
        duration.setText(studentInfoClass.getInternshipDuration());
        if (studentInfoClass.isCertificatePresent()) {
            certificateAns.setText("Yes");
            certificateLayout.setVisibility(View.VISIBLE);

        } else {
            certificateAns.setText("No");
            certificateLayout.setVisibility(View.GONE);
        }
        certificate.setText(studentInfoClass.getCertification());
        if (studentInfoClass.isJapaneseCertificationPresent()) {
            japanese.setText("Yes");
            japaneseLayout.setVisibility(View.VISIBLE);
        } else {
            japanese.setText("No");
            japaneseLayout.setVisibility(View.GONE);
        }
        jlpt.setText(studentInfoClass.getJlpt());

    }


    @Override
    public void onPositiveButtonClicked(String[] list, ArrayList<String> selectedItemList) {
        Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
        intent.putExtra("studentDetails", studentInfoClass);
        intent.putStringArrayListExtra("list", selectedItemList);
        startActivity(intent);
    }

    @Override
    public void onNegativeButtonClicked() {

    }


}