package com.sccodesoft.schoolfinder;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProximityResultsActivity extends AppCompatActivity {
    private TextView proximityMarks;
    private TextView SchoolKeyPr,SchoolNamePr,SchoolAddresPr,SchoolPhonePr,SchoolWebsitePr,SchoolTypePr,SchoolRatingPr,SchoolReligionPr,SchoolMediumPr,SchoolCategoryPr,DistancePr;

    private Button CallPRRes,VisitPRRes,sendEmailReport , DonePr;

    private ProgressDialog Loadingbar;

    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proximity_results);

        Loadingbar = new ProgressDialog(this);

        rootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        proximityMarks = (TextView) findViewById(R.id.proximityMarks);

        SchoolKeyPr = (TextView)findViewById(R.id.SchoolKeyPr);
        SchoolNamePr = (TextView)findViewById(R.id.SchoolNamePR);
        SchoolAddresPr = (TextView)findViewById(R.id.SchoolAddressPR);
        SchoolPhonePr = (TextView)findViewById(R.id.SchoolPhonePR);
        SchoolWebsitePr = (TextView)findViewById(R.id.SchoolWebsitePR);
        SchoolTypePr = (TextView)findViewById(R.id.SchoolTypePR);
        SchoolRatingPr = (TextView)findViewById(R.id.SchoolRatingPR);
        SchoolReligionPr = (TextView)findViewById(R.id.SchoolReligionPR);
        SchoolMediumPr = (TextView)findViewById(R.id.SchoolMediumPR);
        SchoolCategoryPr = (TextView)findViewById(R.id.SchoolCategoryPR);
        DistancePr = (TextView)findViewById(R.id.SchoolDistancePR);

        CallPRRes = (Button)findViewById(R.id.callprresult);
        VisitPRRes = (Button)findViewById(R.id.visitprresult);

        sendEmailReport = (Button)findViewById(R.id.send_email_buttonpr);
        DonePr = (Button)findViewById(R.id.done_buttonpr);

        Bundle arguments = getIntent().getExtras();

        String[] arrayPRReceived = arguments.getStringArray("respr");

        proximityMarks.setText(arrayPRReceived[1]+" Out Of 40");

        DistancePr.setText(arrayPRReceived[2]);

        rootRef.child("Schools").child(arrayPRReceived[0]).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String skey = dataSnapshot.getKey().toString();
                SchoolKeyPr.setText(skey);

                String sname = dataSnapshot.child("name").getValue().toString();
                SchoolNamePr.setText(sname);

                String saddress = dataSnapshot.child("address").getValue().toString();
                SchoolAddresPr.setText(saddress);

                String scategory = dataSnapshot.child("category").getValue(String.class);
                if(scategory.equals("n"))
                {
                    scategory="National School";
                }
                else if(scategory.equals("pro"))
                {
                    scategory="Provincial School";
                }
                else if(scategory.equals("pri"))
                {
                    scategory="Primary School";
                }

                SchoolCategoryPr.setText(scategory);

                String smedium = dataSnapshot.child("medium").getValue(String.class);
                if(smedium.equals("s"))
                {
                    smedium="Sinhala Medium";
                }
                else if(smedium.equals("se"))
                {
                    smedium="Sinhala/English Medium";
                }
                else if(smedium.equals("t"))
                {
                    smedium="Tamil Medium";
                }
                else if(smedium.equals("te"))
                {
                    smedium="Tamil/English Medium";
                }
                else if(smedium.equals("ste"))
                {
                    smedium="Sinhala/Tamil/English Medium";
                }

                SchoolMediumPr.setText(smedium);

                String sphone = dataSnapshot.child("phone").getValue().toString();
                SchoolPhonePr.setText(sphone);

                String srating = dataSnapshot.child("rating").getValue().toString();
                SchoolRatingPr.setText(srating);

                String sreligion = dataSnapshot.child("religion").getValue(String.class);
                if(sreligion.equals("b"))
                {
                    sreligion="Buddhist";
                }
                else if(sreligion.equals("c"))
                {
                    sreligion="Catholic";
                }
                else if(sreligion.equals("h"))
                {
                    sreligion="Hindu";
                }
                else if(sreligion.equals("m"))
                {
                    sreligion="Muslim";
                }

                SchoolReligionPr.setText(sreligion);

                String stype = dataSnapshot.child("type").getValue(String.class);
                if(stype.equals("m"))
                {
                    stype="Mixed School";
                }
                else if(stype.equals("b"))
                {
                    stype="Boys Only School";
                }
                else if(stype.equals("g"))
                {
                    stype="Girls Only School";
                }
                SchoolTypePr.setText(stype);

                String swebsite = dataSnapshot.child("website").getValue(String.class);
                SchoolWebsitePr.setText(swebsite);

                if(SchoolWebsitePr.getText().toString().equals("Not Present"))
                {
                    VisitPRRes.setVisibility(View.GONE);
                }
                if(SchoolPhonePr.getText().toString().equals("Not Present"))
                {
                    CallPRRes.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        CallPRRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!SchoolPhonePr.getText().toString().equals("Not Present")) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProximityResultsActivity.this);
                    alertDialogBuilder.setMessage("Do You Want to Dial " + SchoolPhonePr.getText().toString() + " ?");
                    alertDialogBuilder.setTitle("Dial School");


                    alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            callIntent.setData(Uri.parse("tel:" + SchoolPhonePr.getText().toString()));
                            startActivity(callIntent);
                        }
                    });

                    alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();

                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onShow(DialogInterface arg0) {
                            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.colorPrimaryDark);
                            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.colorPrimaryDark);
                        }
                    });


                    alertDialog.show();

                }
            }

        });

        VisitPRRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!SchoolWebsitePr.getText().toString().equals("Not Present")) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProximityResultsActivity.this);
                    alertDialogBuilder.setMessage("Do You Want to Browse " + SchoolWebsitePr.getText().toString() + " ?");
                    alertDialogBuilder.setTitle("Browse School");


                    alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent callIntent = new Intent(Intent.ACTION_VIEW);
                            callIntent.setData(Uri.parse(SchoolWebsitePr.getText().toString()));
                            startActivity(callIntent);
                        }
                    });

                    alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();

                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @SuppressLint("ResourceAsColor")
                        @Override
                        public void onShow(DialogInterface arg0) {
                            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.colorPrimaryDark);
                            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.colorPrimaryDark);
                        }
                    });


                    alertDialog.show();

                }
            }

        });

        sendEmailReport.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                String fromEmail = "schoolfinderappsl@gmail.com";
                String fromPassword = "schoolfinder9899";
                String toEmail = mAuth.getCurrentUser().getEmail().toString();

                String emailSubject = "School Finder Report";
                String emailBody = ("<h2>Proximity Report</h2>\n " +
                        "<table> <tr><td>"+
                        "Selected School Name </td><td> :" + SchoolNamePr.getText().toString() + "</td></tr><tr><td>"+
                        "Selected School Address </td><td> :" + SchoolAddresPr.getText().toString() + "</td></tr><tr><td>"+
                        "Selected School Phone Number </td><td> :" + SchoolPhonePr.getText().toString() + "</td></tr><tr><td>"+
                        "Selected School Website </td><td> :" + SchoolWebsitePr.getText().toString() + "</td></tr><tr><td>"+
                        "Selected School Gender </td><td> :" + SchoolTypePr.getText().toString() + "</td></tr><tr><td>"+
                        "Selected School Religion </td><td> :" + SchoolReligionPr.getText().toString() + "</td></tr><tr><td>"+
                        "Selected School Medium </td><td> :" + SchoolMediumPr.getText().toString() + "</td></tr><tr><td>"+
                        "Selected School Category </td><td> :" + SchoolCategoryPr.getText().toString() + "</td></tr><tr><td>"+
                        "Selected School Public Rating </td><td> :" + SchoolRatingPr.getText().toString() + "</td></tr><tr><td>"+
                        "Distance From Home </td><td> :" + DistancePr.getText().toString() + "km</td></tr><tr><td>"+
                        "----------------------------------------- </td><td> ------------------------------- </td></tr><tr><td>"+
                        "<b>Marks For Proximity </b></td><td><b> : "+proximityMarks.getText().toString() +"</b></td></tr></table>");

                new SendMailTask(ProximityResultsActivity.this).execute(fromEmail,
                        fromPassword, toEmail, emailSubject, emailBody);
            }
        });

        DonePr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Loadingbar.setTitle("Saving");
                Loadingbar.setMessage("Please Wait While We are Saving Your Search..");
                Loadingbar.show();
                Loadingbar.setCanceledOnTouchOutside(true);

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
                String strDate = mdformat.format(calendar.getTime());

                String searchID = SchoolKeyPr.getText().toString()+strDate;

                HashMap shistoryMap = new HashMap();
                shistoryMap.put("stype", "Proximity Mark");
                shistoryMap.put("school", SchoolNamePr.getText().toString());
                shistoryMap.put("address", SchoolAddresPr.getText().toString());
                shistoryMap.put("marks", arrayPRReceived[1].toString());

                String currentuserid = mAuth.getCurrentUser().getUid();

                rootRef.child("Users").child(currentuserid).child("Searchhistory").child(searchID).updateChildren(shistoryMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            SendUserToMainActivity();
                            Toast.makeText(ProximityResultsActivity.this, "Search saved Successfully..", Toast.LENGTH_SHORT).show();
                            Loadingbar.dismiss();
                            finish();
                        } else {
                            String message = task.getException().getMessage();
                            Loadingbar.dismiss();
                        }

                    }
                });
            }
        });

    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(ProximityResultsActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
    }

}
