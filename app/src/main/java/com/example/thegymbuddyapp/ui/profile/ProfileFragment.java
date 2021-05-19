package com.example.thegymbuddyapp.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.thegymbuddyapp.MainActivity;
import com.example.thegymbuddyapp.R;
import com.example.thegymbuddyapp.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

    binding = FragmentProfileBinding.inflate(inflater, container, false);
    View root = binding.getRoot();

        TextView profileName = root.findViewById(R.id.profileNameTextView);
        profileName.setText(MainActivity.profileName);
        TextView dateJoined = root.findViewById(R.id.dateJoinedTextView);
        dateJoined.setText(MainActivity.dateJoined);
        TextView totalWorkouts = root.findViewById(R.id.totalWorkoutsTextView);
        totalWorkouts.setText(MainActivity.totalWorkouts);


        final TextView textView = binding.textNotifications;
        profileViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;

    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}