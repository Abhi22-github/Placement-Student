package com.roaaserver.placementstudent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.roaaserver.placementstudent.Adapters.OfflineRequestAdapter;
import com.roaaserver.placementstudent.Models.RequestInfoClass;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RequestsActivity extends AppCompatActivity {
    private static final String TAG = "RequestsActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    private LinearLayout firstRequestLayout;
    private TextView requestBranch, requesterName,modifyCount;
    private RequestInfoClass requestInfoClass;


    private ImageButton backButton;
    private RecyclerView requestsRecyclerView, deviceRequestRecyclerView;
    private FirestoreRecyclerAdapter firestoreRequestsRecyclerAdapter;
    private ArrayList<RequestInfoClass> requestList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        initFields();
        initFirebase();
        setupFirstRequest();

        setUpList();


        backButton.setOnClickListener(view -> onBackPressed());
    }

    private void setupFirstRequest() {

        firestore.collection("Requests").document(mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        Log.e(TAG, "onComplete: error------------------------------------------", task.getException());
                        requestInfoClass = task.getResult().toObject(RequestInfoClass.class);
                        firstRequestLayout.setVisibility(View.VISIBLE);
//                        requestId.setText(requestInfoClass.getId());
                        requesterName.setText(requestInfoClass.getName());
                        requestBranch.setText(requestInfoClass.getDepartment());
                        modifyCount.setText(String.valueOf(requestInfoClass.getModifyCount()));


                        // firstRequestLayout.setVisibility(View.GONE);

                        firstRequestLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(RequestsActivity.this, RequestDetailActivity.class);
                                intent.putExtra("requestDetails", requestInfoClass);
                                startActivity(intent);
                            }
                        });
                    }
                } else {
                    Log.e(TAG, "onComplete: error------------------------", task.getException());
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupFirstRequest();
        // firestoreRequestsRecyclerAdapter.startListening();

    }

    private void setUpList() {
        SharedPreferences sharedPreferences = getSharedPreferences("requestDetails", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        //get single request which was deleted
        String json = sharedPreferences.getString("deletedRequest", "");
        RequestInfoClass requestInfoClassFromDevice = gson.fromJson(json, RequestInfoClass.class);


        //get the arraylist from device
        String serializedObject = sharedPreferences.getString("requestList", "");

        if (!serializedObject.isEmpty()) {

            Type type = new TypeToken<List<RequestInfoClass>>() {
            }.getType();
            requestList = gson.fromJson(serializedObject, type);
            for(int i=0;i<requestList.size();i++){
                Log.d(TAG, "setUpList: list before after feteching ------"+requestList.get(i).hashCode());
            }
        }

        for(int i=0;i<requestList.size();i++){
            Log.d(TAG, "setUpList: list before ------"+requestList.get(i).hashCode());
        }

        //check if it containes the request if not then add
        int flag = 1;

        try {

            for (RequestInfoClass r : requestList) {
                if (r.getTime() != null && r.getTime().compareTo(requestInfoClassFromDevice.getTime()) == 0) {
                    flag = 0;
                    break;
                }
            }

            if (flag == 1 && requestInfoClassFromDevice.getTime() != null) {
                Log.d(TAG, "setUpList: successfull----" + requestInfoClassFromDevice.hashCode());
                requestList.add(requestInfoClassFromDevice);
            }

        } catch (Exception e) {
            Log.d(TAG, "setUpList: size of list6" + requestList.size());
        }

        for(int i=0;i<requestList.size();i++){
            Log.d(TAG, "setUpList: list after ------"+requestList.get(i).hashCode());
        }

        // store the updated arraylist in device
        String jsonList = gson.toJson(requestList);
        editor.putString("requestList", jsonList);
        editor.commit();

        if (requestList.size() != 0) {
            setUpOfflineRecyclerView(requestList);
        }

    }

    private void setUpOfflineRecyclerView(ArrayList<RequestInfoClass> requestList) {
        deviceRequestRecyclerView.setHasFixedSize(true);
        deviceRequestRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        deviceRequestRecyclerView.setAdapter(new OfflineRequestAdapter(this, requestList));
    }

//    private void setupRequestsRecyclerView() {
//
//        //Query
//        Query query = firestore.collection("Requests");
//        //Recycler options
//        FirestoreRecyclerOptions<RequestInfoClass> options = new FirestoreRecyclerOptions.Builder<RequestInfoClass>()
//                .setQuery(query, RequestInfoClass.class)
//                .build();
//        Log.e(TAG, "onBindViewHolder---------------------------------------: " + 2);
//
//        firestoreRequestsRecyclerAdapter = new FirestoreRecyclerAdapter<RequestInfoClass, RequestViewHolder>(options) {
//            @NonNull
//            @Override
//            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_layout, parent, false);
//                Log.e(TAG, "onBindViewHolder---------------------------------------: " + 1);
//                return new RequestViewHolder(view);
//
//
//            }
//
//
//            @Override
//            protected void onBindViewHolder(@NonNull RequestViewHolder holder, int position, @NonNull RequestInfoClass model) {
//                holder.setRequestData(model.getId(), model.getName());
//                Log.e(TAG, "onBindViewHolder---------------------------------------: " + model.getId());
//            }
//
//
//            @Override
//            public void onDataChanged() {
//                super.onDataChanged();
//                //   messageRecyclerView.smoothScrollToPosition(messageRecyclerView.getBottom());
//            }
//        };
//        requestsRecyclerView.setAdapter(firestoreRequestsRecyclerAdapter);
//    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    private void initFields() {
        backButton = findViewById(R.id.back_button);
        firstRequestLayout = findViewById(R.id.requests_cardview);
       // requestId = findViewById(R.id.requests_id);
        requesterName = findViewById(R.id.name);
        requestBranch = findViewById(R.id.branch);
        modifyCount = findViewById(R.id.modify_count);
        requestsRecyclerView = findViewById(R.id.requests_recycler_view);
        deviceRequestRecyclerView = findViewById(R.id.offline_requests_recycler_view);

        requestsRecyclerView.setHasFixedSize(true);
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}