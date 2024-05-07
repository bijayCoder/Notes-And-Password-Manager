package com.bijaystudio.notesandpasswordmanager.others;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SQLiteDbHelper extends SQLiteOpenHelper {
    //for notes data
    private static final String NOTES_TABLE = "NotesTable";
    private static final String KEY_TITLE = "title";
    private static final String KEY_BODY = "body";
    private static final String KEY_TIMING = "timing";
    private static final String KEY_UNIQUE_CODE_NOTE = "uniqueCode";
    private static final String KEY_SAME_NOTE = "sameKey";

    //for usernam password data
    private static final String PASSWORD_TABLE = "PasswordTable";
    private static final String KEY_USERNAME = "userName";
    private static final String KEY_PASSWORD = "passWord";
    private static final String KEY_UNIQUE_CODE_PASSWORD = "uniqueCodePass";
    private static final String KEY_SAME_PASSWORD = "sameKeyPass";

    //for encrypt-decrypt passwrod data
    private static final String ENCRYPT_PASSWORD_TABLE = "EncryptedPasswordTable";
    private static final String KEY_PASSWORD_ENCRYPT = "encryptPass";
    private static final String KEY_UNIQUE_CODE_ENCRYPT = "uniqueCodeEncryptPass";


    public SQLiteDbHelper(Context context) {
        super(context, "MyDatabase", null, 3);
    } //if you add any new table or set any new attribute , update the version to see changes


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + NOTES_TABLE +
                    "(" + KEY_TITLE + " TEXT," + KEY_BODY + " TEXT," + KEY_TIMING + " TEXT," + KEY_UNIQUE_CODE_NOTE + " TEXT," + KEY_SAME_NOTE + " TEXT"+")" );
        db.execSQL("CREATE TABLE " + PASSWORD_TABLE +
                "(" + KEY_USERNAME + " TEXT," + KEY_PASSWORD + " TEXT," + KEY_UNIQUE_CODE_PASSWORD + " TEXT," + KEY_SAME_PASSWORD + " TEXT"+")" );
        db.execSQL("CREATE TABLE " + ENCRYPT_PASSWORD_TABLE +
                "(" + KEY_PASSWORD_ENCRYPT + " TEXT," + KEY_UNIQUE_CODE_ENCRYPT + " TEXT" + ")" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PASSWORD_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ENCRYPT_PASSWORD_TABLE);
        onCreate(db);

    }
    public void addNoteToDb(NoteModel model) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_TITLE , model.title);
        cv.put(KEY_BODY , model.body);
        cv.put(KEY_TIMING , model.timing);
        cv.put(KEY_UNIQUE_CODE_NOTE , model.uniqueCode);
        cv.put(KEY_SAME_NOTE , model.sameKey);
        database.insert(NOTES_TABLE , null , cv);
    }
    public void addEncryptPasswordToDb(EncryptPasswordModel model) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_PASSWORD_ENCRYPT , model.password);
        cv.put(KEY_UNIQUE_CODE_ENCRYPT , model.uniqueCode);
        database.insert(ENCRYPT_PASSWORD_TABLE , null , cv);
    }
    public void addPasswordToDb(PasswordModel model) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_USERNAME , model.userName);
        cv.put(KEY_PASSWORD , model.passWord);
        cv.put(KEY_UNIQUE_CODE_PASSWORD , model.uniqueCode);
        cv.put(KEY_SAME_PASSWORD , model.sameKey);
        database.insert(PASSWORD_TABLE , null , cv);
    }
    public void updateNote(NoteModel updateNote) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_TITLE , updateNote.title);
        cv.put(KEY_BODY , updateNote.body);
        cv.put(KEY_TIMING , updateNote.timing);
        cv.put(KEY_UNIQUE_CODE_NOTE , updateNote.uniqueCode);  //isko update karne ka zarrorat nahi hai
        cv.put(KEY_SAME_NOTE , updateNote.sameKey);
        database.update(NOTES_TABLE , cv , KEY_UNIQUE_CODE_NOTE + " = " + updateNote.uniqueCode , null);
    }

    public void deleteNote(NoteModel currentNote) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(NOTES_TABLE , KEY_UNIQUE_CODE_NOTE + " = " + currentNote.uniqueCode , null);

    }
    public void deletePassword(PasswordModel currentNote) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(PASSWORD_TABLE , KEY_UNIQUE_CODE_PASSWORD + " = " + currentNote.uniqueCode , null);

    }
    public ArrayList<NoteModel> fetchNotesFromDb(){
        SQLiteDatabase database = this.getReadableDatabase();
        ArrayList<NoteModel> arrNotes = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM "+NOTES_TABLE , null);
        while (cursor.moveToNext()){
            NoteModel nm = new NoteModel();
            nm.title = cursor.getString(0);
            nm.body = cursor.getString(1);
            nm.timing = cursor.getString(2);
            nm.uniqueCode = cursor.getString(3);
            nm.sameKey = cursor.getString(4);

            arrNotes.add(nm);
        }
        return arrNotes;
    }

    public ArrayList<PasswordModel> fetchPasswordFromDb(){
        SQLiteDatabase database = this.getReadableDatabase();
        ArrayList<PasswordModel> arrPasswords = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM "+PASSWORD_TABLE , null);
        while (cursor.moveToNext()){
            PasswordModel pm = new PasswordModel();
            pm.userName = cursor.getString(0);
            pm.passWord = cursor.getString(1);
            pm.uniqueCode = cursor.getString(2);
            pm.sameKey = cursor.getString(3);

            arrPasswords.add(pm);
        }
        return arrPasswords;
    }

    public ArrayList<EncryptPasswordModel> fetchEncryptPasswordList(){
        SQLiteDatabase database = this.getReadableDatabase();
        ArrayList<EncryptPasswordModel> arrPasswords = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM "+ENCRYPT_PASSWORD_TABLE , null);
        while (cursor.moveToNext()){
            EncryptPasswordModel pm = new EncryptPasswordModel();
            pm.password = cursor.getString(0);
            pm.uniqueCode = cursor.getString(1);

            arrPasswords.add(pm);
        }
        return arrPasswords;

    }

    public void deleteNotesAndPasswordTable() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(NOTES_TABLE , KEY_SAME_NOTE +" = 100" , null);
        database.delete(PASSWORD_TABLE ,  KEY_SAME_PASSWORD+" = 200", null);
    }
}

