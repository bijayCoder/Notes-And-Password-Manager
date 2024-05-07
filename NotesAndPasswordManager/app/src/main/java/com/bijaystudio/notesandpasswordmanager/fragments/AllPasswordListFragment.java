package com.bijaystudio.notesandpasswordmanager.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bijaystudio.notesandpasswordmanager.R;
import com.bijaystudio.notesandpasswordmanager.others.NoteModel;
import com.bijaystudio.notesandpasswordmanager.others.NotesAdapter;
import com.bijaystudio.notesandpasswordmanager.others.PasswordAdapter;
import com.bijaystudio.notesandpasswordmanager.others.PasswordModel;
import com.bijaystudio.notesandpasswordmanager.others.SQLiteDbHelper;

import java.util.ArrayList;
import java.util.Objects;

public class AllPasswordListFragment extends Fragment {
    RecyclerView recyclerView;
    PasswordAdapter adapter;
    SQLiteDbHelper myDbHelper;
    ArrayList<PasswordModel> arrPasswords = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_all_password_list, container, false);
        myDbHelper = new SQLiteDbHelper(requireContext());
        recyclerView = v.findViewById(R.id.rv_pass_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));


        String newUserName = getArguments().getString("newUserName");
        String newUserPassword = getArguments().getString("newUserPassword");
        ArrayList<PasswordModel> arrAllPass = myDbHelper.fetchPasswordFromDb();
        arrPasswords.addAll(arrAllPass);
        if(newUserName == "dummyUserName9112" && newUserPassword== "dummyPassword9112"){
            //dont save it to database
        }else{
            if(!isNewUserExists(arrAllPass , newUserName)){
                arrPasswords.add(new PasswordModel(newUserName , newUserPassword , String.valueOf(System.currentTimeMillis()) , "200"));
                myDbHelper.addPasswordToDb(new PasswordModel(newUserName , newUserPassword , String.valueOf(System.currentTimeMillis()) , "200"));
            }
        }

        adapter = new PasswordAdapter(requireContext() , arrPasswords);
        recyclerView.setAdapter(adapter);

        return v;
    }

    private boolean isNewUserExists(ArrayList<PasswordModel> arrAllPass, String newUserName) {
        for (int i=0;i<arrAllPass.size();i++){
            PasswordModel tempModel = arrAllPass.get(i);
            if(tempModel.getUserName().equals(newUserName)){
                return true;
            }
        }
        return false;
    }
}