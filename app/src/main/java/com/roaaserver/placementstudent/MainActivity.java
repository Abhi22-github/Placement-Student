package com.roaaserver.placementstudent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.roaaserver.placementstudent.Adapters.CompanyAdapter;
import com.roaaserver.placementstudent.Adapters.SliderAdapter;
import com.roaaserver.placementstudent.Models.CompanyDetailsClass;
import com.roaaserver.placementstudent.Models.TokenClass;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    private RecyclerView companyRecyclerView;
    private CompanyAdapter companyAdapter;
    private LinearLayout messagesLayout, profileLayout, requestsLayout;

    private SliderView sliderView;
    private int[] images = {R.drawable.entrance, R.drawable.college, R.drawable.tpc, R.drawable.company};
    private SliderAdapter sliderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFields();
        initFirebase();
        saveToken();
        messagesLayout.setOnClickListener(view -> sendUserToMessagesActivity());
        profileLayout.setOnClickListener(v -> sendUserToProfileActivity());
        requestsLayout.setOnClickListener(view -> sendUserToRequestActivity());

        setUpCarousal();
    }

    private void setUpCarousal() {
        sliderAdapter = new SliderAdapter(images);
        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.SLIDE);
        sliderView.startAutoCycle();

    }

    private void sendUserToRequestActivity() {
        Intent Intent = new Intent(MainActivity.this, RequestsActivity.class);
        startActivity(Intent);

    }

    private void sendUserToMessagesActivity() {
        Intent Intent = new Intent(MainActivity.this, MessagesActivity.class);
        startActivity(Intent);

    }

    private void sendUserToProfileActivity() {
        Intent Intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(Intent);

    }

    private void initFields() {
        companyRecyclerView = findViewById(R.id.company_recyclerview);
        messagesLayout = findViewById(R.id.post);
        profileLayout = findViewById(R.id.profile);
        requestsLayout = findViewById(R.id.requests);
        sliderView = findViewById(R.id.image_slider);
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    private void CompanyRecyclerAdapter() {
        ArrayList<CompanyDetailsClass> companyList = new ArrayList<CompanyDetailsClass>();
        companyAdapter = new CompanyAdapter(companyList, this);

        companyRecyclerView.setAdapter(companyAdapter);

        companyRecyclerView.setHasFixedSize(true);
        companyRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        firestore.collection("Company Details")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.d(TAG, "onEvent: " + error.getMessage());
                            return;
                        }


                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                companyList.add(dc.getDocument().toObject(CompanyDetailsClass.class));
                                Log.d(TAG, "onEvent: list of added" + dc.getDocument().toObject(CompanyDetailsClass.class));
                            }

                            companyAdapter.notifyDataSetChanged();

                        }

                    }
                });


    }

    @Override
    protected void onStart() {
        super.onStart();
        CompanyRecyclerAdapter();
    }

    private void saveToken() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("avcoe", 0);

        TokenClass tokenClass = new TokenClass(sharedPreferences.getString("token", null));
        firestore.collection("Tokens").document(mAuth.getCurrentUser().getUid()).
                set(tokenClass);
    }
}