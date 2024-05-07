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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bijaystudio.notesandpasswordmanager.R;

import java.util.ArrayList;
import java.util.Calendar;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {
    ArrayList<NoteModel> arrNotes;
    Context context;
    SQLiteDbHelper myDbHelper;
    public NotesAdapter(Context context , ArrayList<NoteModel> arrNotes) {
        this.context = context;
        this.arrNotes = arrNotes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View v = LayoutInflater.from(context).inflate(R.layout.note_layout , parent ,false);
       myDbHelper = new SQLiteDbHelper(context);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NoteModel currentModel = arrNotes.get(holder.getAdapterPosition());

        holder.txtTitle.setText(currentModel.title);
        holder.txtBody.setText(currentModel.body);
        holder.txtTiming.setText(currentModel.timing);

        holder.llNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.note_view_dialog_layout);
                TextView txtTiming = dialog.findViewById(R.id.txt_note_view_dialog_timing);
                EditText edtTitle = dialog.findViewById(R.id.edt_simple_note_view_title);
                EditText edtBody = dialog.findViewById(R.id.edt_simple_note_view_body);
                ImageButton btnSave = dialog.findViewById(R.id.btn_simple_note_save);

                edtTitle.setText(currentModel.title);
                edtBody.setText(currentModel.body);
                txtTiming.setText(currentModel.timing);


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
                        arrNotes.set(holder.getAdapterPosition() , new NoteModel(title , body ,timing,currentModel.uniqueCode , currentModel.sameKey));
                        myDbHelper.updateNote( new NoteModel(title , body ,timing,currentModel.uniqueCode,currentModel.sameKey));
                        notifyItemChanged(holder.getAdapterPosition());
                        dialog.dismiss();
                    }
                });




                dialog.show();
            }
        });

        holder.llNote.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("Delete Note")
                        .setMessage("Are you sure want to delete?")
                        .setIcon(R.drawable.baseline_delete_forever_24)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                arrNotes.remove(holder.getAdapterPosition());
                                myDbHelper.deleteNote(currentModel);
//                                notifyItemRemoved(holder.getAdapterPosition());  //isse positon ka error aa raha hai
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {}
                        });
                builder.show();

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrNotes.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout llNote;
        TextView txtTitle , txtBody , txtTiming;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txt_simple_note_title);
            txtBody = itemView.findViewById(R.id.txt_simple_note_body);
            txtTiming = itemView.findViewById(R.id.txt_simple_note_timing);
            llNote = itemView.findViewById(R.id.ll_simple_note_view);
        }
    }
}
