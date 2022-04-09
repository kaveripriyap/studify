package com.example.studify.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.studify.models.AuthAppRepository;
import com.example.studify.models.RoomAppRepository;
import com.example.studify.models.UserAppRepository;

public class RoomViewModel extends AndroidViewModel {
    private AuthAppRepository authAppRepository;
    private RoomAppRepository roomAppRepository;
    private UserAppRepository userAppRepository;

    public RoomViewModel(@NonNull Application application) {
        super(application);

        authAppRepository = new AuthAppRepository(application);
        roomAppRepository = new RoomAppRepository(application);
        userAppRepository = new UserAppRepository(application);
    }

    public void createRoom(){ roomAppRepository.createRoom();}

    public void joinRoom(String roomID){roomAppRepository.joinRoom(roomID);}


    public void addTask(String taskName) { roomAppRepository.addTask(taskName); }


    public void updateTask(String taskName) { roomAppRepository.updateTask(taskName); }
}
