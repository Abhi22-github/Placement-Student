package com.roaaserver.placementstudent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.roaaserver.placementstudent.Models.StudentDetailsClass;
import com.roaaserver.placementstudent.Models.StudentInfoClass;
import com.roaaserver.placementstudent.StudentInfo.StudentFormActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import ir.shahabazimi.instagrampicker.InstagramPicker;

public class RegisterStudent extends AppCompatActivity {
    private static final String TAG = "CreateAccountActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private StorageReference userImageRef;

    private ImageButton backButton;
    private TextInputLayout emailLayout, passwordLayout, nameLayout;
    private TextInputEditText email, password, name;
    private MaterialButton registerButton;
    private ProgressBar progressBar;
    private RelativeLayout addImageLayout;
    private ImageView addImageView;
    private CircleImageView addCircleImageView;
    private TextView imageErrorMessage, loginButton, errorMessage;

    private Uri imageUri;
    private String downloadUri = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_student);

        initFields();
        initFirebase();

        backButton.setOnClickListener(view -> onBackPressed());
        registerButton.setOnClickListener(view -> checkDetails());

        name.addTextChangedListener(textWatcher);
        email.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);

        addImageLayout.setOnClickListener(view -> selectImageFromDevice());
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterStudent.this, StudentLogin.class);
                startActivity(intent);
            }
        });
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userImageRef = FirebaseStorage.getInstance().getReference().child("User Images");

    }


    private void selectImageFromDevice() {
        InstagramPicker instagramPicker = new InstagramPicker(RegisterStudent.this);
        instagramPicker.show(1, 1, address -> {
            imageUri = Uri.parse(address);
            Log.d(TAG, "onActivityResult: " + imageUri);
            addCircleImageView.setImageURI(imageUri);
            addImageView.setVisibility(View.GONE);
            imageErrorMessage.setVisibility(View.GONE);
        });
    }

    private void checkDetails() {
        String emailString, passwordString, nameString;
        emailString = email.getText().toString();
        passwordString = password.getText().toString();
        nameString = name.getText().toString();

        String regex = "^(.+)@(.+)$";
        //Compile regular expression to get the pattern
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(emailString);
        boolean checkAll = true;
        if (emailString.isEmpty() && passwordString.isEmpty() && nameString.isEmpty() && imageUri == null) {
            emailLayout.setError("Please enter email.");
            passwordLayout.setError("Please enter Password.");
            nameLayout.setError("Please enter name");
            imageErrorMessage.setVisibility(View.VISIBLE);
            emailLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
            passwordLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
            nameLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
            checkAll = false;
        }
        if (nameString.isEmpty()) {
            nameLayout.setError("Please enter name.");
            nameLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
            checkAll = false;
        }
        if (emailString.isEmpty()) {
            emailLayout.setError("Please enter email.");
            emailLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
            checkAll = false;
        } else {
            if (!matcher.matches()) {
                emailLayout.setError("Please enter valid email.");
                emailLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                checkAll = false;
            }
        }
        if (passwordString.isEmpty()) {
            passwordLayout.setError("Please enter Password.");
            passwordLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
            checkAll = false;
        } else {
            if (passwordString.length() < 6) {
                passwordLayout.setError("Password should have minimum 6 characters.");
                passwordLayout.setBoxBackgroundColor(getColor(R.color.red_faint));
                checkAll = false;
            }
        }
        if (imageUri == null) {
            checkAll = false;
            imageErrorMessage.setVisibility(View.VISIBLE);
        }
        if (checkAll) {
            createUserAccount();
        }
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            setUpFieldsToNormalAfterError(s);
        }
    };

    private void setUpFieldsToNormalAfterError(Editable s) {
        if (s == name.getEditableText()) {
            nameLayout.setError("");
            nameLayout.setErrorEnabled(false);
            nameLayout.setBoxBackgroundColor(getColor(R.color.white));
        }
        if (s == email.getEditableText()) {
            emailLayout.setError("");
            emailLayout.setErrorEnabled(false);
            emailLayout.setBoxBackgroundColor(getColor(R.color.white));
        }
        if (s == password.getEditableText()) {
            passwordLayout.setError("");
            passwordLayout.setErrorEnabled(false);
            passwordLayout.setBoxBackgroundColor(getColor(R.color.white));
        }
    }

    private void createUserAccount() {
        showProgressDialog();
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            uploadProfileImageToFirebaseStorage(task);
                        } else {
                            Log.d(TAG, "onComplete: " + task.getException());
                            errorMessage.setVisibility(View.VISIBLE);
                            errorMessage.setText(task.getException().getMessage().toString());

                            hideProgressDialog();
                        }
                    }
                });
    }

    private void uploadProfileImageToFirebaseStorage(Task<AuthResult> task) {
        showProgressDialog();
        Log.d(TAG, "UploadProfileImageToFirebaseStorage: before bitmap");
        String docID = task.getResult().getUser().getUid();
        if (imageUri != null) {
            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            //here you can choose quality factor in third parameter(ex. i choosen 25)
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] fileInBytes = baos.toByteArray();
            Log.d(TAG, "UploadProfileImageToFirebaseStorage: after bitmap");
            StorageReference fileRef = userImageRef.child(docID + ".jpg");
            fileRef.putBytes(fileInBytes)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: profile image saved in storage");

                                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Uri img = uri;
                                        downloadUri = img.toString();
                                        Log.d(TAG, "onSuccess: " + downloadUri);
                                        saveUserDetailsInFirestore(downloadUri, docID);
                                    }
                                });
                            } else {
                                Log.d(TAG, "onComplete: failed to store profile image in storage " + task.getException());
                                hideProgressDialog();
                            }
                        }
                    });
        } else {
            saveUserDetailsInFirestore(null, docID);
        }
    }

    private void saveUserDetailsInFirestore(String downloadUri, String docID) {
        SharedPreferences sharedPreferences = getSharedPreferences("studentDetails", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("image", downloadUri);
        editor.putString("email", email.getText().toString());
        editor.apply();
        StudentDetailsClass studentDetailsClass = new StudentDetailsClass();
        studentDetailsClass.setEmail(email.getText().toString());
        studentDetailsClass.setStudentID(docID);
        studentDetailsClass.setVerified(false);
        studentDetailsClass.setImage(downloadUri);
        studentDetailsClass.setUserName(name.getText().toString());
        firestore.collection("Students").document(docID).
                set(studentDetailsClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task1) {
                if (task1.isSuccessful()) {
                    // sendUserToUserNameActivity();
//                    if (studentDetailsClass.isVerified()) {
//                        sendUserToMainActivity();
//                    } else {
//                        sendUserToPendingVerificationPage();
                    checkIfProfileIsCompleted();
//                    }
                } else {
                    Log.d(TAG, "onComplete: " + task1.getException());
                    hideProgressDialog();
                }

            }
        });
    }

    private void checkIfProfileIsCompleted() {
        String currentUserID = mAuth.getCurrentUser().getUid();
        DocumentReference userStoreRef = firestore.collection("Students Information").document(currentUserID);
        userStoreRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot documentSnapshot = task.getResult();
                    StudentInfoClass studentInfoClass = documentSnapshot.toObject(StudentInfoClass.class);
                    try {
                        if (studentInfoClass.isProfileCompleted()) {
                            Log.d(TAG, "verifyUserExistence: --------------");
                            checkIfUserIsVerified();
                        } else {
                            // progressBar.setVisibility(View.GONE);
                            //mainContentLayout.setVisibility(View.VISIBLE);
                            sendUserToProfileFormActivity();
                        }
                    } catch (Exception e) {
                        sendUserToProfileFormActivity();
                    }
                } else {
                    // progressBar.setVisibility(View.GONE);
                    // mainContentLayout.setVisibility(View.VISIBLE);
                    sendUserToProfileFormActivity();
                    Log.d(TAG, "Failed with: ", task.getException());
                }
            }
        });
    }

    private void checkIfUserIsVerified() {

        Log.d(TAG, "checkIfUserIsVerified: " + mAuth.getCurrentUser().getUid());
        firestore.collection("Students").document(mAuth.getCurrentUser().getUid()).get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        StudentDetailsClass studentDetailsClass = documentSnapshot.toObject(StudentDetailsClass.class);
                        if (studentDetailsClass.isVerified()) {
                            sendUserToMainActivity();
                        } else {
                            sendUserToPendingVerificationPage();
                        }


                    }
                });

    }

//    private void sendUserToUserNameActivity() {
//        Intent intent = new Intent(CreateAccountActivity.this, UsernameActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        AdminLogin.adminLoginActivity.finish();
//        finish();
//    }

//    private void checkIfUserIsVerified() {
//
//        Log.d(TAG, "checkIfUserIsVerified: " + mAuth.getCurrentUser().getUid());
//        firestore.collection("Users").document(mAuth.getCurrentUser().getUid()).get().
//                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        DocumentSnapshot documentSnapshot = task.getResult();
//                        AdminDetailsClass adminDetailsClass = documentSnapshot.toObject(AdminDetailsClass.class);
//
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                              hideProgressDialog();
//                            }
//                        }, 2000);
//                    }
//                });
//    }

    private void sendUserToPendingVerificationPage() {
        Intent intent = new Intent(RegisterStudent.this, PendingVerificationPage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        StudentLogin.adminLoginActivity.finish();
        finish();
    }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(RegisterStudent.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        StudentLogin.adminLoginActivity.finish();
        finish();
    }

    private void initFields() {
        backButton = findViewById(R.id.back_button);

        addImageLayout = findViewById(R.id.add_image_button);
        addImageView = findViewById(R.id.add_image_view);
        addCircleImageView = findViewById(R.id.add_image_circle_image_view);

        name = findViewById(R.id.username);
        nameLayout = findViewById(R.id.username_layout);

        emailLayout = findViewById(R.id.email_layout);
        email = findViewById(R.id.email);


        passwordLayout = findViewById(R.id.password_layout);
        password = findViewById(R.id.password);
        imageErrorMessage = findViewById(R.id.image_error_message);

        errorMessage = findViewById(R.id.error_message);

//        confirmPasswordLayout = findViewById(R.id.confirm_password_layout);
//        confirmPassword = findViewById(R.id.confirm_password);

        registerButton = findViewById(R.id.register_button);
        progressBar = findViewById(R.id.progress_bar);
        loginButton = findViewById(R.id.login_button);
    }

    private void showProgressDialog() {
        progressBar.setVisibility(View.VISIBLE);
        registerButton.setText("");
        registerButton.setEnabled(false);
    }

    private void hideProgressDialog() {
        progressBar.setVisibility(View.GONE);
        registerButton.setText("Create an account");
        registerButton.setEnabled(true);
    }

    private void sendUserToProfileFormActivity() {
        Intent Intent = new Intent(RegisterStudent.this, StudentFormActivity.class);
        Intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent);
        StudentLogin.adminLoginActivity.finish();
        finish();
    }
}