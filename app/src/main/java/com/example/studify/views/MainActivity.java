package com.example.studify.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.studify.R;
import com.example.studify.databinding.ActivityMainBinding;
import com.example.studify.viewmodel.UserViewModel;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "SUCCESSFUL";
    private ActivityMainBinding binding;
    private UserViewModel UserViewModel;
    private NavController navController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Creates link for navigation between NavBar and Fragment
        NavHostFragment navHostFragment = ((NavHostFragment) (getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment)));
        NavigationUI.setupWithNavController(binding.bottomNavView, navHostFragment.getNavController());
        navController = navHostFragment.getNavController();
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController,
                                             @NonNull NavDestination navDestination,
                                             @Nullable Bundle bundle) {
                if (navDestination.getId() == R.id.roomFragment || navDestination.getId() == R.id.editProfileFragment ) {
                    binding.bottomNavView.setVisibility(View.GONE);
                } else {
                    binding.bottomNavView.setVisibility(View.VISIBLE);
                }
            }
        });

        // LiveData Observer - If registered, redirect to main page
        UserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        UserViewModel.getLoggedOutLiveData().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean) {
                    Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

}
