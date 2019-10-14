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
import android.util.Log;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FinalResultsActivity extends AppCompatActivity {

    private TextView totResultMarks;
    private TextView maindocMarks;
    private TextView adddocMarks;
    private TextView electionRegMarks;
    private TextView proximityMarks;
    private TextView SchoolKeyF,SchoolNameF,SchoolAddresF,SchoolPhoneF,SchoolWebsiteF,SchoolTypeF,SchoolRatingF,SchoolReligionF,SchoolMediumF,SchoolCategoryF,DistanceF;

    private ProgressDialog Loadingbar;

    private Button CallFRes,VisitFRes, sendEmailReport , DoneF;

    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_results);

        initializeFields();

        Loadingbar = new ProgressDialog(this);

        rootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        String currentuserid = mAuth.getUid().toString();

        Bundle arguments = getIntent().getExtras();
        String[] arrayPRReceived = arguments.getStringArray("respr");

        proximityMarks.setText(arrayPRReceived[1]);

        DistanceF.setText(arrayPRReceived[2]);

        rootRef.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.hasChild(currentuserid))
                {
                    double maidoc = dataSnapshot.child(currentuserid).child("maindocmarks").getValue(double.class);
                    double adddoc = dataSnapshot.child(currentuserid).child("adddocmarks").getValue(double.class);
                    double electionreg = dataSnapshot.child(currentuserid).child("electionregmarks").getValue(double.class);

                    double totalresult = maidoc + adddoc + electionreg + Double.valueOf(arrayPRReceived[1]);

                    maindocMarks.setText((String.valueOf(maidoc)));
                    adddocMarks.setText((String.valueOf(adddoc)));
                    electionRegMarks.setText((String.valueOf(electionreg)));
                    totResultMarks.setText((String.valueOf(totalresult)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        rootRef.child("Schools").child(arrayPRReceived[0]).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String skey = dataSnapshot.getKey().toString();
                SchoolKeyF.setText(skey);

                String sname = dataSnapshot.child("name").getValue().toString();
                SchoolNameF.setText(sname);

                String saddress = dataSnapshot.child("address").getValue().toString();
                SchoolAddresF.setText(saddress);

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

                SchoolCategoryF.setText(scategory);

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

                SchoolMediumF.setText(smedium);

                String sphone = dataSnapshot.child("phone").getValue().toString();
                SchoolPhoneF.setText(sphone);

                String srating = dataSnapshot.child("rating").getValue().toString();
                SchoolRatingF.setText(srating);

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

                SchoolReligionF.setText(sreligion);

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
                SchoolTypeF.setText(stype);

                String swebsite = dataSnapshot.child("website").getValue(String.class);
                SchoolWebsiteF.setText(swebsite);

                if(SchoolPhoneF.getText().toString().equals("Not Present"))
                {
                    CallFRes.setVisibility(View.GONE);
                }
                if(SchoolWebsiteF.getText().toString().equals("Not Present"))
                {
                    VisitFRes.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        CallFRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!SchoolPhoneF.getText().toString().equals("Not Present")) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FinalResultsActivity.this);
                    alertDialogBuilder.setMessage("Do You Want to Dial " + SchoolPhoneF.getText().toString() + " ?");
                    alertDialogBuilder.setTitle("Dial School");


                    alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            callIntent.setData(Uri.parse("tel:" + SchoolPhoneF.getText().toString()));
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

        VisitFRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!SchoolWebsiteF.getText().toString().equals("Not Present")) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FinalResultsActivity.this);
                    alertDialogBuilder.setMessage("Do You Want to Browse " + SchoolWebsiteF.getText().toString() + " ?");
                    alertDialogBuilder.setTitle("Browse School");


                    alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent callIntent = new Intent(Intent.ACTION_VIEW);
                            callIntent.setData(Uri.parse(SchoolWebsiteF.getText().toString()));
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
                String emailBody = ("<h1>Final Report</h1>\n " +
                        "<table> <tr><td>"+
                        "Selected School Name </td><td> :" + SchoolNameF.getText().toString() + "</td></tr><tr><td>"+
                        "Selected School Address </td><td> :" + SchoolAddresF.getText().toString() + "</td></tr><tr><td>"+
                        "Selected School Phone Number </td><td> :" + SchoolPhoneF.getText().toString() + "</td></tr><tr><td>"+
                        "Selected School Website </td><td> :" + SchoolWebsiteF.getText().toString() + "</td></tr><tr><td>"+
                        "Selected School Gender </td><td> :" + SchoolTypeF.getText().toString() + "</td></tr><tr><td>"+
                        "Selected School Religion </td><td> :" + SchoolReligionF.getText().toString() + "</td></tr><tr><td>"+
                        "Selected School Medium </td><td> :" + SchoolMediumF.getText().toString() + "</td></tr><tr><td>"+
                        "Selected School Category </td><td> :" + SchoolCategoryF.getText().toString() + "</td></tr><tr><td>"+
                        "Selected School Public Rating </td><td> :" + SchoolRatingF.getText().toString() + "</td></tr><tr><td>"+
                        "Distance From Home </td><td> :" + DistanceF.getText().toString() + "km</td></tr><tr><td>"+
                        "----------------------------------------- </td><td> ------------------------------- </td></tr><tr><td>"+
                        "Marks For Main Documents </td><td> :" + maindocMarks.getText().toString() + "</td></tr><tr><td>"+
                "Marks For Additional Documents </td><td> :" + adddocMarks.getText().toString() + "</td></tr><tr><td>"+
                "Marks For Electoral Register  </td><td> :"+ electionRegMarks.getText().toString() + "</td></tr><tr><td>"+
                "Marks For Proximity </td><td> :" + proximityMarks.getText().toString()+ "</td></tr><tr><td>"+
                "<b>Total Marks </b></td><td><b> : "+totResultMarks.getText().toString() +"</b></td></tr></table>");

                new SendMailTask(FinalResultsActivity.this).execute(fromEmail,
                        fromPassword, toEmail, emailSubject, emailBody);
            }
        });

        DoneF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveHistory(currentuserid);

            }
        });

    }

    private void saveHistory(String currentuserid) {

        Loadingbar.setTitle("Saving");
        Loadingbar.setMessage("Please Wait While We are Saving Your Search..");
        Loadingbar.show();
        Loadingbar.setCanceledOnTouchOutside(true);

        String[] datetime = {null};

        String url = "http://worldtimeapi.org/api/timezone/Asia/Colombo";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("Error", e.getMessage());
                Toast.makeText(FinalResultsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                FinalResultsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            final String mMessage = response.body().string();
                            JSONObject responseObj = new JSONObject(mMessage);
                            datetime[0] = responseObj.getString("datetime");

                            String strDateTime = datetime[0].replaceAll("[^a-zA-Z0-9_-]", "");

                            String searchID = SchoolKeyF.getText().toString()+strDateTime;

                            HashMap shistoryMap = new HashMap();
                            shistoryMap.put("stype", "Full Mark");
                            shistoryMap.put("school", SchoolNameF.getText().toString());
                            shistoryMap.put("address", SchoolAddresF.getText().toString());
                            shistoryMap.put("marks", totResultMarks.getText().toString());

                            rootRef.child("Users").child(currentuserid).child("Searchhistory").child(searchID).updateChildren(shistoryMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {

                                        SendUserToMainActivity();
                                        Toast.makeText(FinalResultsActivity.this, "Search saved Successfully..", Toast.LENGTH_SHORT).show();
                                        Loadingbar.dismiss();

                                    } else {
                                        Loadingbar.dismiss();
                                        String message = task.getException().getMessage();
                                    }

                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });

    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(FinalResultsActivity.this,ValidateUser.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
    }

    private void initializeFields()
    {
        totResultMarks = (TextView)findViewById(R.id.totMarksF);
        maindocMarks = (TextView)findViewById(R.id.maindocMarksF);
        adddocMarks = (TextView)findViewById(R.id.adddocMarksF);
        electionRegMarks= (TextView)findViewById(R.id.electionregMarksF);;
        proximityMarks = (TextView)findViewById(R.id.proximityMarksF);

        SchoolKeyF = (TextView)findViewById(R.id.SchoolKeyF);
        SchoolNameF = (TextView)findViewById(R.id.SchoolNameF);
        SchoolAddresF = (TextView)findViewById(R.id.SchoolAddressF);
        SchoolPhoneF = (TextView)findViewById(R.id.SchoolPhoneF);
        SchoolWebsiteF = (TextView)findViewById(R.id.SchoolWebsiteF);
        SchoolTypeF = (TextView)findViewById(R.id.SchoolTypeF);
        SchoolRatingF = (TextView)findViewById(R.id.SchoolRatingF);
        SchoolReligionF = (TextView)findViewById(R.id.SchoolReligionF);
        SchoolMediumF = (TextView)findViewById(R.id.SchoolMediumF);
        SchoolCategoryF = (TextView)findViewById(R.id.SchoolCategoryF);
        DistanceF = (TextView)findViewById(R.id.SchoolDistanceF);

        CallFRes = (Button)findViewById(R.id.callresultF);
        VisitFRes = (Button)findViewById(R.id.visitresultF);

        sendEmailReport = (Button)findViewById(R.id.send_email_buttonf);
        DoneF = (Button)findViewById(R.id.done_buttonf);
    }
}
