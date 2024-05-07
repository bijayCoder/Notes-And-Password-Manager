package com.bijaystudio.notesandpasswordmanager.others;

public class EncryptPasswordModel {
    String password , uniqueCode;
    public EncryptPasswordModel() {
    }

    public String getPassword() {
        return password;
    }

    public EncryptPasswordModel(String password, String uniqueCode) {
        this.password = password;
        this.uniqueCode = uniqueCode;
    }


}
