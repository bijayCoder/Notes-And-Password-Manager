package com.bijaystudio.notesandpasswordmanager.others;

public class NoteModel {

    String title , body , timing, uniqueCode , sameKey;

    public NoteModel(){}
    public NoteModel(String title, String body, String timing, String uniqueCode, String sameKey) {
        this.title = title;
        this.body = body;
        this.timing = timing;
        this.uniqueCode = uniqueCode;
        this.sameKey = sameKey;
    }

}
