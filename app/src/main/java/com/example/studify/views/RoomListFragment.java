package com.example.studify.views;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.studify.R;
import com.example.studify.databinding.FragmentRoomListBinding;
import com.example.studify.models.RoomModel;
import com.example.studify.models.UserProfileModel;
import com.example.studify.viewmodel.UserViewModel;
import com.example.studify.viewmodel.RoomViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class RoomListFragment extends Fragment implements View.OnClickListener {
    private FragmentRoomListBinding binding;
    private UserViewModel UserViewModel;
    private NavController navController;
    private RoomViewModel RoomViewModel;
    private RoomModel room;
    private String Admin_User;
    private String roomID;
    private ArrayList<String> user_IDs;
    private FirebaseAuth firebaseAuth;
    private com.google.firebase.firestore.FieldValue FieldValue;
    private FirebaseFirestore db;
    private static final String TAG = "RoomListFragment";
    private DocumentReference docRef;
    private CollectionReference colRef;
    private ArrayList<RoomModel> Open_Rooms=new ArrayList<>();

    public RoomListFragment() {

        firebaseAuth = FirebaseAuth.getInstance();
        System.out.println("Inside Constructor");
        db = FirebaseFirestore.getInstance();
        colRef = db.collection("rooms");
        //Query query = colRef.whereEqualTo("Admin_User",firebaseAuth.getCurrentUser().getUid());
        /*db.collection("rooms")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                //System.out.println(document.get("admin_User"));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });*/
        db.collection("rooms")
                .whereEqualTo("admin_User", firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        System.out.println("Inside OnComplete Query");
                        if (task.isSuccessful()) {
                            System.out.println("Inside Task Query");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                System.out.println("Inside Document Snapshot");
                                System.out.println("Admin User:"+document.get("admin_User"));
                                room = document.toObject(RoomModel.class);
                                System.out.println("ROOM ID:"+room.getRoomID());
                                Open_Rooms.add(room);
                                System.out.println("ROOM ID in the ROOMListFragment:"+room.getRoomID());
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentRoomListBinding.inflate(getLayoutInflater());

        UserViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        RoomViewModel = new ViewModelProvider(this).get(com.example.studify.viewmodel.RoomViewModel.class);

        return binding.getRoot();

    }

    @Override
    public void onStart() {
        super.onStart();
        binding.createRoomButton.setOnClickListener(this);
        binding.joinRoomButton.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // LiveData Observer for UserProfileModel
        UserViewModel.getUserProfileLiveData().observe(getViewLifecycleOwner(), new Observer<UserProfileModel>() {
            @Override
            public void onChanged(UserProfileModel userProfileModel) {
                if (userProfileModel != null) {
                    binding.WelcomeCard.setText("Welcome, " + userProfileModel.getName() + "!");
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void onClick(View view) {
        int id = view.getId();

        if (id == binding.createRoomButton.getId()) {
            Log.i(TAG, "Create Room Button Clicked");
            Navigation.findNavController(view).navigate(R.id.action_roomListFragment_to_taskListFragment);
        }
        if (id == binding.joinRoomButton.getId()) {
            db = FirebaseFirestore.getInstance();
            roomID = binding.hashId.getText().toString().trim();
            Bundle result = new Bundle();
            result.putString("RoomID", roomID);
            getParentFragmentManager().setFragmentResult("RoomIDdata", result);
            if (TextUtils.isEmpty(roomID)) {
                Log.w(TAG, "Invalid ID enter");
                binding.hashId.setError("ID is required");

            } else {
                Log.i(TAG, "Join Room Activated");

                docRef = db.collection("rooms").document(roomID);
                System.out.println("Docuement Ref:"+docRef);
                System.out.println("Room ID:"+roomID);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            System.out.println("Inside Task");
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                System.out.println("Inside document exists");
                                System.out.println(document.getData());
                                room = document.toObject(RoomModel.class);
                                user_IDs =  room.getUser_IDs();
                                System.out.println("Admin User (Inside Snapshot)"+room.getAdmin_User());
                                Admin_User = room.getAdmin_User();
                                if(user_IDs.contains(firebaseAuth.getCurrentUser().getUid())==false)
                                {
                                    db.collection("rooms").document(document.getId()).update("user_IDs", FieldValue.arrayUnion(firebaseAuth.getCurrentUser().getUid()));
                                    db.collection("rooms").document(document.getId()).update("roomUserCount", FieldValue.increment(1));
                                }

                                System.out.println("ROOM ID:"+room.getRoomID());
                                /*docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        System.out.println("Inside Document Snapshot");
                                        room = documentSnapshot.toObject(RoomModel.class);
                                        user_IDs =  room.getUser_IDs();
                                        System.out.println("Admin User (Inside Snapshot)"+room.getAdmin_User());
                                        Admin_User = room.getAdmin_User();
                                        if(user_IDs.contains(firebaseAuth.getCurrentUser().getUid())==false)
                                        {
                                            db.collection("rooms").document(document.getId()).update("user_IDs", FieldValue.arrayUnion(firebaseAuth.getCurrentUser().getUid()));
                                            db.collection("rooms").document(document.getId()).update("roomUserCount", FieldValue.increment(1));
                                        }
                                    }
                                });*/
                                //System.out.println("Admin User:"+room.getAdmin_User());
                                System.out.println("Admin_User:"+Admin_User);
                                System.out.println("Current User:"+FirebaseAuth.getInstance().getCurrentUser().getUid());
                                if(Admin_User.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    System.out.println("Admin User Fragment Activated");
                                    Navigation.findNavController(view).navigate(R.id.action_roomListFragment_to_roomadminFragment);
                                }
                                else{
                                    System.out.println("Inside Else");
                                    Navigation.findNavController(view).navigate(R.id.action_roomListFragment_to_roomFragment);
                                }


                            } else {
                                binding.hashId.setError("Document does not exist");
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            binding.hashId.setError("Request Failed:" + task.getException());
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
            }
        }
    }
}


