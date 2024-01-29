package com.iert.ytdisfree;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class MainActivity extends AppCompatActivity {
    TextView addDiaButton, cancelBtn;
    EditText urlEditText;
    EditText titleEditText;

    RecyclerView recyclerView;
    ArrayList<VideoList> urlArrayList;
    MyAdapter myAdapter;
    ProgressDialog progressDialog;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addButton = findViewById(R.id.addbutton);
        Dialog dialog = new Dialog(MainActivity.this);

        dialog.setContentView(R.layout.dialog_add_box);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dia_bg));


        //addButton click
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog pd;
                pd = new ProgressDialog(MainActivity.this);
               pd.setCancelable(false);


                dialog.show();
                dialog.setCancelable(false);
                dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

                addDiaButton = dialog.findViewById(R.id.addData);
                cancelBtn = dialog.findViewById(R.id.cancelDialog);
                urlEditText = dialog.findViewById(R.id.editText);
                titleEditText = dialog.findViewById(R.id.editTitle);
                //click to add url
                addDiaButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!urlEditText.getText().toString().equals("") && !titleEditText.getText().toString().equals("")) {
                            pd.setMessage("Adding data...");
                            pd.show();


                            SimpleDateFormat sdf =new  SimpleDateFormat("dd - MM - yyyy ' ' HH : mm : ss z");
                            String current = sdf.format(new Date());
                            Map<String, Object> url = new HashMap<>();
                            url.put("url", urlEditText.getText().toString());
                            url.put("title", titleEditText.getText().toString());
                            url.put("date", current);
                            //adding data
                            db.collection("url").add(url).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    urlEditText.setText(null);
                                    titleEditText.setText(null);

                                    pd.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(MainActivity.this, "error ocurred", Toast.LENGTH_SHORT).show();
                                    urlEditText.setText(null);
                                    titleEditText.setText(null);
                                }
                            });
                        } else {
                            Toast.makeText(MainActivity.this, "Enter title and url", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();

                    }
                });

            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching data...");
        progressDialog.show();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        urlArrayList = new ArrayList<VideoList>();
        myAdapter = new MyAdapter(MainActivity.this, urlArrayList);

        recyclerView.setAdapter(myAdapter);


        eventChangeListener();


    }

    private void eventChangeListener() {

        db.collection("url").orderBy("date", Query.Direction.DESCENDING)

                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            if (progressDialog.isShowing()){ progressDialog.dismiss();}
                                Log.e("Firestore Error", error.getMessage());
                        }
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            String documentId =dc.getDocument().getId();
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                           VideoList modelList = dc.getDocument().toObject(VideoList.class);
                           modelList.setDocumentId(documentId);
                                urlArrayList.add(modelList);
                            }
                         


                            myAdapter.notifyDataSetChanged();
                            if (progressDialog.isShowing()) progressDialog.dismiss();
                        }
                    }
                });
    }


}