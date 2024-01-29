package com.iert.ytdisfree;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

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


    }

    @Override
    public int getItemCount() {
        return urlArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView url;
        Button delete;

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
