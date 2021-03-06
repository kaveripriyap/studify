package com.example.studify.views;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
//import androidx.fragment.app.FragmentManager;

import com.example.studify.R;
import com.example.studify.databinding.FragmentRoomBinding;
import com.example.studify.models.RoomModel;
import com.example.studify.viewmodel.RoomViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;


import java.util.ArrayList;

public class RoomFragment extends Fragment implements View.OnClickListener {
    private @NonNull FragmentRoomBinding binding;
   // private MainActivityViewModel MainActivityViewModel;
    private RoomViewModel roomViewModel;
    private String dummy;
    private NavController navController;
    private String roomID = "";
    private ArrayList<String> task_list = null;
    private FirebaseFirestore db;
    private DocumentReference docRef;
    private ProgressDialog loader;
    private DatabaseReference reference;
    //private CollectionReference reference_col;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String UserID;
    private ArrayList<String> tasks;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private Adapter madapter;
    private DocumentSnapshot documentSnapshot;
    private RoomModel room;
    private FirebaseAuth firebaseAuth;
    private Integer room_user_count;
    private CollectionReference colRef;


    public RoomFragment() {

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.leaveRoom.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRoomBinding.inflate(getLayoutInflater());
        roomViewModel = new ViewModelProvider(this).get(RoomViewModel.class);

        System.out.println("In Room Fragment");
        getParentFragmentManager().setFragmentResultListener("RoomIDdata", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                roomID = result.getString("RoomID");
                System.out.println(roomID.toString());
                roomViewModel.startGroupTimer(roomID);
                db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("rooms").document(roomID);
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        room = documentSnapshot.toObject(RoomModel.class);
                        tasks = room.getTasks_Lists();
                        room_user_count=room.getUser_IDs().size();
                        mRecyclerView = binding.roomRecyclerView;
                        mLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
                        mRecyclerView.setLayoutManager(mLayoutManager);
                        System.out.println(room.getTasks_Lists());
                        madapter = new Adapter(tasks, getContext(),mRecyclerView);
                        mRecyclerView.setAdapter(madapter);
                        binding.RoomIDCard.setText("Room ID: " + roomID);
                    }
                });

            }

        });


        return binding.getRoot();
    }





    public void onClick(View view) {
        int id = view.getId();
        if (id == binding.leaveRoom.getId()) {
            db = FirebaseFirestore.getInstance();
            // RoomViewModel.createRoom(room.getTasks_Lists());
            db.collection("rooms").document(roomID).update("user_IDs", FieldValue.arrayRemove(firebaseAuth.getCurrentUser().getUid()));
            Navigation.findNavController(view).navigate(R.id.action_roomFragment_to_roomListFragment);
        }





    }



//        FirebaseRecyclerOptions<AddTaskModel> options = new FirebaseRecyclerOptions.Builder<AddTaskModel>()
//                .setQuery(reference, AddTaskModel.class)
//                .build();
//
//        FirebaseRecyclerAdapter<String, RoomFragment.MyViewHolder> Adapter = new FirebaseRecyclerAdapter<String, RoomFragment.MyViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull RoomFragment.MyViewHolder holder, final int position, @NonNull final String task) {
//                holder.setTask(task);
//
//                holder.mView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                    }
//                });
//            }
//
//            @NonNull
//            @Override
//            public RoomFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieved_layout, parent, false);
//                return new RoomFragment.MyViewHolder(view);
//            }
//        };
//
//        recyclerView.setAdapter(Adapter);
//        Adapter.startListening();
//        db = FirebaseFirestore.getInstance();
//        //Bundle bundle = new Bundle();
//        // roomID = bundle.getString("roomID"); // Throws a null error
//        DocumentReference docRef = db.collection("rooms").document(roomID);
//        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                AlertDialog.Builder myDialog = new AlertDialog.Builder(getContext());
//                DialogInputTaskBinding dialogBinding = DialogInputTaskBinding.inflate(getLayoutInflater());
//                myDialog.setView(dialogBinding.getRoot());
//
//                final AlertDialog dialog = myDialog.create();
//                dialog.setCancelable(false);
//
//                RoomModel room = documentSnapshot.toObject(RoomModel.class);
//                System.out.println(room.getTasks_Lists());
//                tasks = room.getTasks_Lists();
//                for (String task: tasks) {
//                    String id = reference.push().getKey();
//                    AddTaskModel model = new AddTaskModel(task, "1", "0");
//                    reference.child(id).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//
//                        }
//                    });
//
//                }
//            }
//            @NonNull
//            public RoomFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieved_layout, parent, false);
//                return new RoomFragment.MyViewHolder(view);
//            }
//        });



    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTask(String taskName) {
            TextView taskTextView = mView.findViewById(R.id.taskTv);
            taskTextView.setText(taskName);
        }

        public void setTime(String taskTime) {
            TextView timeTextView = mView.findViewById(R.id.timeTv);
            timeTextView.setText(taskTime);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        roomViewModel.getGroupTimerLeftLiveDate().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String timerLeft) {
                binding.timer.setText(timerLeft);
            }
        });


    }


}
