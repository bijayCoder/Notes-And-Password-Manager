package com.bijaystudio.notesandpasswordmanager.others;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bijaystudio.notesandpasswordmanager.R;

import java.util.ArrayList;

public class PasswordAdapter extends RecyclerView.Adapter<PasswordAdapter.ViewHolder> {
    SQLiteDbHelper myDbHelper;
    Context context;
    ArrayList<PasswordModel> arrPassWord;
    public PasswordAdapter(Context context , ArrayList<PasswordModel> arrPassWord){
        this.context = context;
        this.arrPassWord = arrPassWord;
    }

    @NonNull
    @Override
    public PasswordAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.password_recycler_layout , parent , false);
        myDbHelper = new SQLiteDbHelper(context);
        return new PasswordAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PasswordAdapter.ViewHolder holder, int position) {
        PasswordModel currentModel = arrPassWord.get(holder.getAdapterPosition());
        holder.txtUserName.setText(currentModel.userName);

        holder.rlPassList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_usernamepass_show);
                TextView txtUserName = dialog.findViewById(R.id.txt_username_dialog_userpass);
                TextView txtUserPassword = dialog.findViewById(R.id.txt_password_dialog_userpass);
                AppCompatButton btnOk = dialog.findViewById(R.id.btn_ok_dialog_show);
                ImageButton btnCopyUsername = dialog.findViewById(R.id.btn_copy_username);
                ImageButton btnCopyPassword = dialog.findViewById(R.id.btn_copy_password);

                txtUserName.setText(currentModel.userName);
                txtUserPassword.setText(currentModel.passWord);
                btnCopyUsername.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setClipboard(context , txtUserName.getText().toString());
                        Toast.makeText(context , "Username Copied" , Toast.LENGTH_SHORT).show();
                    }
                });
                btnCopyPassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setClipboard(context , txtUserPassword.getText().toString());
                        Toast.makeText(context , "Password Copied" , Toast.LENGTH_SHORT).show();

                    }
                });

                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
        holder.rlPassList.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("Delete Password")
                        .setMessage("Are you sure want to delete?")
                        .setIcon(R.drawable.baseline_delete_forever_24)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                arrPassWord.remove(holder.getAdapterPosition());
                                myDbHelper.deletePassword(currentModel);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {}
                        });
                builder.show();

                return  true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrPassWord.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtUserName;
        RelativeLayout rlPassList;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName = itemView.findViewById(R.id.txt_username_rv_list);
            rlPassList = itemView.findViewById(R.id.relative_layout_password_list);
        }
    }

    private void setClipboard(Context context, String text) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
    }
}
