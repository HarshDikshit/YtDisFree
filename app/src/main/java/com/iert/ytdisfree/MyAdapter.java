package com.iert.ytdisfree;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    Context context;
    ArrayList<VideoList> urlArrayList;
    private MyViewHolder holder;
    private int position;

    public MyAdapter(Context context, ArrayList<VideoList> urlArrayList) {
        this.context = context;
        this.urlArrayList = urlArrayList;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);


        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        this.holder = holder;
        this.position = position;

        TextView title;


        VideoList url = urlArrayList.get(position);

        holder.title.setText(url.title);
        holder.url.setText(url.url);
        ProgressDialog progressDialog;

        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        AlertDialog.Builder alert= new AlertDialog.Builder(context);
        alert.setTitle("Delete");
        alert.setMessage("Are you sure ?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.setMessage("Deleting data...");
                progressDialog.show();
                db.collection("url").document(urlArrayList.get(position).getDocumentId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                        urlArrayList.remove(position);
                        notifyDataSetChanged();
                        progressDialog.dismiss();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show();
                    }
                });


            }

        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog= alert.create();
        holder.delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // int pos =position;

                alertDialog.show();
            }
        });

        Dialog dialog = new Dialog(context);

        dialog.setContentView(R.layout.dialog_add_box);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.dia_bg));

holder.title.setOnLongClickListener(new View.OnLongClickListener() {
    @Override
    public boolean onLongClick(View v) {
        dialog.show();
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;


        holder.addDiaButton = dialog.findViewById(R.id.addData);
        holder.cancelBtn = dialog.findViewById(R.id.cancelDialog);
        holder.urlEditText = dialog.findViewById(R.id.editText);
        holder.titleEditText = dialog.findViewById(R.id.editTitle);

        holder.addDiaButton.setText("Update");
        //click to add url
        holder.addDiaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!holder.urlEditText.getText().toString().equals("") && !holder.titleEditText.getText().toString().equals("")) {
                    ProgressDialog pd;
                    pd = new ProgressDialog(context);
                    pd.setCancelable(false);
                   pd.setMessage("Updating data...");
                    pd.show();


                    SimpleDateFormat sdf =new  SimpleDateFormat("dd - MM - yyyy ' ' HH : mm : ss z");
                    String current = sdf.format(new Date());
                    Map<String, Object> url = new HashMap<>();
                    url.put("url", holder.urlEditText.getText().toString());
                    url.put("title", holder.titleEditText.getText().toString());
                    url.put("date", current);
                    //adding data
                    db.collection("url") .document(urlArrayList.get(position).getDocumentId())
                           .set(url)

                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    holder.urlEditText.setText(null);
                                    holder.titleEditText.setText(null);
                                    pd.dismiss();




                                }
                                })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(context, "error ocurred", Toast.LENGTH_SHORT).show();
                                    holder.urlEditText.setText(null);
                                    holder.titleEditText.setText(null);
                                }
                            });

                } else {
                    Toast.makeText(context, "Enter title and url", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        return false;
    }
});

    }

    @Override
    public int getItemCount() {
        return urlArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView url;
        Button delete, addDiaButton, cancelBtn;
        EditText urlEditText,titleEditText;




        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            url = itemView.findViewById(R.id.link);
            delete = itemView.findViewById(R.id.delete);
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, MainActivity2.class);
                    intent.putExtra("url", url.getText().toString());
                    intent.putExtra("title", title.getText().toString());
                    context.startActivity(intent);
                    Toast.makeText(context, title.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
