package com.example.thegymbuddyapp;

public class workoutPOJO {

    public String name;
    public String description;


    public workoutPOJO(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String name() {
        return this.name;
    }

    public String description() {
        return this.description;
    }


}


