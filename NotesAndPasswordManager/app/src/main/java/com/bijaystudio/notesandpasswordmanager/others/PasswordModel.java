package com.bijaystudio.notesandpasswordmanager.others;

public class PasswordModel {
    String userName , passWord , uniqueCode , sameKey;

    public PasswordModel() {}

    public String getUserName() {
        return userName;
    }


    public PasswordModel(String userName, String passWord, String uniqueCode, String sameKey) {
        this.userName = userName;
        this.passWord = passWord;
        this.uniqueCode = uniqueCode;
        this.sameKey = sameKey;
    }
}
