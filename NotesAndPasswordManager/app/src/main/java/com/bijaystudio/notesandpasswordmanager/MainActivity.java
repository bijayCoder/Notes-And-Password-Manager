package com.bijaystudio.notesandpasswordmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bijaystudio.notesandpasswordmanager.fragments.AllPasswordListFragment;
import com.bijaystudio.notesandpasswordmanager.fragments.NotesFragment;
import com.bijaystudio.notesandpasswordmanager.others.EncryptPasswordModel;
import com.bijaystudio.notesandpasswordmanager.others.SQLiteDbHelper;
import com.google.android.material.navigation.NavigationView;

import org.jasypt.util.password.StrongPasswordEncryptor;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    SQLiteDbHelper myDbHelper;
    StrongPasswordEncryptor encryptor ;
    String upperSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String lowerSet = "abcdefghijklmmopqrstuvwxyz";
    String numberSet = "0123456789";
    String specialChars = "!@#$%&*";
    NavigationView navView;
    Toolbar toolbar;
    Bundle bundleForNewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        //get data from store password dialog from main activity
        bundleForNewUser = new Bundle();
        bundleForNewUser.putString("newUserName", "dummyUserName9112");
        bundleForNewUser.putString("newUserPassword", "dummyPassword9112");
        //end of getting data

        //initialization of some classes  and drawer link to toolbar
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myDbHelper = new SQLiteDbHelper(MainActivity.this);
        encryptor = new StrongPasswordEncryptor();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this , drawerLayout , toolbar , R.string.OpenDrawer , R.string.CloseDrawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setTitle("Notes Manager");
        loadFragment(new NotesFragment() , 0);
        //end of initialization

        //handle clicks of items in navigation drawer
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if(id == R.id.menu_nav_generate_pass){
                    generatePassword();
                    
                } else if (id == R.id.menu_nav_store_pass) {
                    storePassword();
                    
                } else if (id == R.id.menu_nav_encrypt_pass) {
                    encryptPassword();
                    
                } else if (id==R.id.menu_nav_decrypt_pass) {
                    decryptPassword();
                    
                } else if (id==R.id.menu_nav_see_all_pass) {
                    loadFragment(new AllPasswordListFragment() ,1);
                    Objects.requireNonNull(getSupportActionBar()).setTitle("All Passwords");
                } else if (id == R.id.menu_nav_notes) {
                    loadFragment(new NotesFragment() , 1);
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Notes Manager");
                }else if (id==R.id.menu_nav_about){
                    Intent intent = new Intent(MainActivity.this , ProjectAboutActivity.class);
                    startActivity(intent);
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }


        });
        //end of navigation drawer handling


    }

    //method to decrypt password
    private void decryptPassword() {
        //create a custom dialog box
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_encrypt_decrypt);

        //get the reference of the fields present in dialog box
        TextView txtTitle = dialog.findViewById(R.id.txt_encrypt_decrypt);
        EditText edtInputPassword = dialog.findViewById(R.id.edt_encrypt_decrypt);
        AppCompatButton btnDecrypt = dialog.findViewById(R.id.btn_encrypt_decrypt);
        TextView txtDecryptedPass = dialog.findViewById(R.id.txt_show_encrypt_decrypt);
        ImageButton btnCopyPass = dialog.findViewById(R.id.btn_copy_encrypt_decrypt);
        //end of getting reference

        //set some predefined values to dialog box
        txtTitle.setText("Password Decrypter");
        edtInputPassword.setHint("Enter Encrypted Password");
        btnDecrypt.setText("Decrypt");
        //end

        //when we click on Decrypt button
        btnDecrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtInputPassword.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Input Password can't be empty", Toast.LENGTH_SHORT).show();
                }else {
                    String encryptedPass = edtInputPassword.getText().toString();
                    String inputPass = "";
                    ArrayList<EncryptPasswordModel> arrPass = myDbHelper.fetchEncryptPasswordList();
                    for (int i=0;i<arrPass.size();i++){
                        if(encryptor.checkPassword(arrPass.get(i).getPassword() , encryptedPass)){
                            inputPass = arrPass.get(i).getPassword();
                        }
                    }
                    if(inputPass.isEmpty()){
                        Toast.makeText(MainActivity.this, "Not a valid Encrypted Password", Toast.LENGTH_SHORT).show();
                    }else {
                        txtDecryptedPass.setText(inputPass);
                        Toast.makeText(MainActivity.this, "Password Decrypted.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //when we click on copy icon present in dialog box
        btnCopyPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClipboard(MainActivity.this , txtDecryptedPass.getText().toString());
                Toast.makeText(MainActivity.this, "Copied To Clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        //this line will visible the dialog box to the user
        dialog.show();

    }


    //method to encrypt password
    private void encryptPassword() {
        //create a custom dialog box
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_encrypt_decrypt);

        //get the reference of the fields present in dialog box
        TextView txtTitle = dialog.findViewById(R.id.txt_encrypt_decrypt);
        EditText edtInputPassword = dialog.findViewById(R.id.edt_encrypt_decrypt);
        AppCompatButton btnEncrypt = dialog.findViewById(R.id.btn_encrypt_decrypt);
        TextView txtEncryptedPass = dialog.findViewById(R.id.txt_show_encrypt_decrypt);
        ImageButton btnCopyPass = dialog.findViewById(R.id.btn_copy_encrypt_decrypt);
        //end of getting reference

        //set some predefined values to dialog box
        txtTitle.setText("Password Encrypter");
        edtInputPassword.setHint("Enter Input Password");
        btnEncrypt.setText("Encrypt");
        //end

        btnEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtInputPassword.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Input Password can't be empty", Toast.LENGTH_SHORT).show();
                }else {
                    String inputPass = edtInputPassword.getText().toString();
                    String encryptedPass = encryptor.encryptPassword(inputPass);
                    myDbHelper.addEncryptPasswordToDb(new EncryptPasswordModel(inputPass , String.valueOf(System.currentTimeMillis())));
                    Toast.makeText(MainActivity.this, "Password Encrypted", Toast.LENGTH_SHORT).show();
                    txtEncryptedPass.setText(encryptedPass);
                }
            }
        });
        btnCopyPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClipboard(MainActivity.this , txtEncryptedPass.getText().toString());
                Toast.makeText(MainActivity.this, "Copied To Clipboard", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();

    }


    //method of generate password which is called when we click on generate password from drawer
    private void generatePassword() {
        //creating a dialog box for generate a password and get reference of different fields present on dialog box
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_generate_password);
        EditText edtPasswordLength = dialog.findViewById(R.id.edt_password_length);
        CheckBox cbLowerCase = dialog.findViewById(R.id.cb_lowercase);
        CheckBox cbUpperCase = dialog.findViewById(R.id.cb_uppercase);
        CheckBox cbNumbers = dialog.findViewById(R.id.cb_numbers);
        CheckBox cbSpecialChar = dialog.findViewById(R.id.cb_special_char);
        TextView txtGeneratedPass = dialog.findViewById(R.id.txt_generated_pass);
        AppCompatButton btnGenerate = dialog.findViewById(R.id.btn_generate_pass);
        ImageButton btnCopy = dialog.findViewById(R.id.btn_copy_generated_password);
        //end

        //when we click on generate button
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int lengthOfPass =8;
                String password = "";
                if(!edtPasswordLength.getText().toString().isEmpty()){
                    lengthOfPass = Integer.parseInt(edtPasswordLength.getText().toString()) ;
                }
                String finalPass = generateRecursive(password , cbLowerCase.isChecked() , cbUpperCase.isChecked() , cbNumbers.isChecked() , cbSpecialChar.isChecked() , lengthOfPass);
                txtGeneratedPass.setText(finalPass);
            }
        });

        //when we click on copy icon
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClipboard(MainActivity.this ,txtGeneratedPass.getText().toString() );
                Toast.makeText(MainActivity.this, "Copied to clipboard.", Toast.LENGTH_SHORT).show();
            }
        });

        //to show the dialog box
        dialog.show();
    }


    //method of generating password using recursion
    private String generateRecursive(String password , Boolean lowerCase , Boolean uppderCase , Boolean numbers , Boolean specialChar, int lengthOfPass) {
        if (uppderCase){
            password += getSingleRandomData(upperSet);
        }
        if (lowerCase){
            password += getSingleRandomData(lowerSet);
        }
        if (numbers){
            password += getSingleRandomData(numberSet);
        }
        if (specialChar){
            password += getSingleRandomData(specialChars);
        }
        if(password.length() < lengthOfPass){
           return generateRecursive(password, lowerCase, uppderCase, numbers, specialChar, lengthOfPass);
        }
        return password;
    }


    //method of get a single character from a string set
    private String getSingleRandomData(String stringSet) {
        Random random = new Random();
        int index = random.nextInt(stringSet.length());
        return String.valueOf(stringSet.charAt(index));
    }


    //method of store password in database
    private void storePassword() {
        //dialog box for store password
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_username_password);
        EditText edtUserName = dialog.findViewById(R.id.edt_username_dialog_userpass);
        EditText edtUserPassword = dialog.findViewById(R.id.edt_password_dialog_userpass);
        AppCompatButton btnOk = dialog.findViewById(R.id.btn_save_dialog_userpass);
        ImageButton btnShowHide = dialog.findViewById(R.id.btn_store_password_dialog_showhide);
        final int[] flag = {0};
        //end

        //when we click on the eye icon
        btnShowHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag[0] ==0){
                    edtUserPassword.setTransformationMethod(null);
                    flag[0] =1;
                }else {
                    edtUserPassword.setTransformationMethod(new PasswordTransformationMethod());
                    flag[0] =0;
                }
            }
        });

        //when we click on ok button after filled username and password
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!edtUserName.getText().toString().equals("") && !edtUserPassword.getText().toString().equals("")){
                        String userName = edtUserName.getText().toString();
                        String password = edtUserPassword.getText().toString();

                        bundleForNewUser = new Bundle();
                        bundleForNewUser.putString("newUserName", userName);
                        bundleForNewUser.putString("newUserPassword", password);

                        loadFragment(new AllPasswordListFragment() , 1);
                    dialog.dismiss();
                }else {
                    Toast.makeText(MainActivity.this , "Username or Password can not be Empty" , Toast.LENGTH_LONG).show();
                }



            }
        });
        //to show the dialog box
        dialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu_option , menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.menu_option_about) {
            Intent intent = new Intent(MainActivity.this , ProjectAboutActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    //method to delete all the data from database


    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    //method to load a fragment and go to the particular fragment
    private void loadFragment(Fragment fragment, int flag) {
        //passing arguments to fragment
        fragment.setArguments(bundleForNewUser);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if(flag ==0){
            ft.add(R.id.container , fragment);
        }else {
            ft.replace(R.id.container , fragment);
        }
        ft.commit();
    }


    //method to copy some text to the clipboard which you can paste anywhere you want.
    private void setClipboard(Context context, String text) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
    }
}