package com.example.application;

public class Person {
    String ID;
    String PWD;

    public Person(String ID, String PWD) {
        this.ID = ID;
        this.PWD = PWD;
    }

    public Person() {

    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getPWD() {
        return PWD;
    }

    public void setPWD(String PWD) {
        this.PWD = PWD;
    }
}


