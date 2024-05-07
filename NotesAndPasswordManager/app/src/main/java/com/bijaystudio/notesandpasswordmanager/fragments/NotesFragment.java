package com.bijaystudio.notesandpasswordmanager.fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bijaystudio.notesandpasswordmanager.R;
import com.bijaystudio.notesandpasswordmanager.others.NoteModel;
import com.bijaystudio.notesandpasswordmanager.others.NotesAdapter;
import com.bijaystudio.notesandpasswordmanager.others.SQLiteDbHelper;

import java.util.ArrayList;
import java.util.Calendar;

public class NotesFragment extends Fragment {
    RecyclerView recyclerView;
    SQLiteDbHelper myDbHelper;
    NotesAdapter adapter;
    ArrayList<NoteModel> arrNotes = new ArrayList<>();
    ImageButton btnAdd;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        myDbHelper = new SQLiteDbHelper(requireContext());
        btnAdd = view.findViewById(R.id.btn_simple_note_add);
        recyclerView= view.findViewById(R.id.notes_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext() , 2));

        ArrayList<NoteModel> arrFetchNotes = myDbHelper.fetchNotesFromDb();
        arrNotes.addAll(arrFetchNotes);

        adapter = new NotesAdapter(requireContext() , arrNotes);  //context main error ho shakta hai dialog ko getapplicationcontext nahi dena hai
        recyclerView.setAdapter(adapter);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(requireContext());
                dialog.setContentView(R.layout.note_view_dialog_layout);

                EditText edtTitle = dialog.findViewById(R.id.edt_simple_note_view_title);
                EditText edtBody = dialog.findViewById(R.id.edt_simple_note_view_body);
                ImageButton btnSave = dialog.findViewById(R.id.btn_simple_note_save);

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String title="" , body="" , timing="";
                        if(!edtTitle.getText().toString().equals("")){
                            title = edtTitle.getText().toString();
                        }
                        if(!edtBody.getText().toString().equals("")){
                            body = edtBody.getText().toString();
                        }
                        Calendar calendar = Calendar.getInstance();
                        int date =  calendar.get(Calendar.DAY_OF_MONTH);
                        int month = calendar.get(Calendar.MONTH)+1;
                        int year = calendar.get(Calendar.YEAR);
                        timing = date + "/" + month + "/" + year;

                        arrNotes.add(new NoteModel(title , body , timing,String.valueOf(System.currentTimeMillis()) ,"100" ));
                        myDbHelper.addNoteToDb(new NoteModel(title , body , timing,String.valueOf(System.currentTimeMillis()) ,"100" ));
                        //update all the notes

                        adapter.notifyItemInserted(arrNotes.size()-1);
                        recyclerView.scrollToPosition(arrNotes.size()-1);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });


        return view;

    }

}