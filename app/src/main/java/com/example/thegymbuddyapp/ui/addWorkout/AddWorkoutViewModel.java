package com.example.thegymbuddyapp.ui.addWorkout;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddWorkoutViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AddWorkoutViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}