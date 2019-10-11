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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private Button saveDetails;
    private EditText userName,userNIC;
    private TextView userLocation;
    private CircleImageView userProfileImage;

    private ProgressDialog Loadingbar;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private StorageReference userProfileImageRef;

    String currentuserid;
    Double latitude, longitude;
    String address;

    final static int PLACE_PICKER_REQUEST = 1;
    final static int Gallery_Pick = 9999;
    public Uri imageUri=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        initializeFields();

        mAuth = FirebaseAuth.getInstance();
        currentuserid = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        Loadingbar = new ProgressDialog(this);

        retriveUserInfo();

        userLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(currentuserid))
                        {
                            dataSnapshot.child(currentuserid).child("maindocmarks").getRef().removeValue();
                            dataSnapshot.child(currentuserid).child("adddocmarks").getRef().removeValue();
                            dataSnapshot.child(currentuserid).child("electionregmarks").getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                Intent intent = new Intent(SetupActivity.this,PlacePickerActivity.class);
                startActivityForResult(intent,PLACE_PICKER_REQUEST);
            }
        });

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });

        saveDetails.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               saveUserDetails();
            }
        });

    }

    private void retriveUserInfo() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(currentuserid))
                {
                    String usrname = dataSnapshot.child(currentuserid).child("username").getValue(String.class);
                    String usrnic = dataSnapshot.child(currentuserid).child("nic").getValue(String.class);
                    String usraddress = dataSnapshot.child(currentuserid).child("address").getValue(String.class);
                    String profileImage = dataSnapshot.child(currentuserid).child("profileImage").getValue(String.class);

                    Picasso.with(SetupActivity.this).load(profileImage).placeholder(R.drawable.ic_sync).into(userProfileImage);
                    userName.setText(usrname);
                    userLocation.setText(usraddress);
                    userNIC.setText(usrnic);
                    userNIC.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveUserDetails()
    {
        String uName = userName.getText().toString();
        String uNIC = userNIC.getText().toString();
        String uAddress = userLocation.getText().toString();

        if(imageUri==null)
        {
            Toast.makeText(this, "Please Select Your Profile Image..", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(uName))
        if(TextUtils.isEmpty(uName))
        {
            Toast.makeText(this, "Please Enter Your Name..", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(uNIC))
        {
            Toast.makeText(this, "Please Enter Your NIC..", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(uAddress))
        {
            Toast.makeText(this, "Please Select Your Location..", Toast.LENGTH_SHORT).show();
        }
        else {
            Loadingbar.setTitle("Saving");
            Loadingbar.setMessage("Please Wait While We are Saving Your Details..");
            Loadingbar.show();
            Loadingbar.setCanceledOnTouchOutside(true);


            final StorageReference filePath = userProfileImageRef.child(currentuserid + ".jpg");

            final UploadTask uploadTask = filePath.putFile(imageUri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String message = e.getMessage();
                    Loadingbar.dismiss();
                    Toast.makeText(SetupActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(SetupActivity.this, "Profile Image uploaded Successfully..", Toast.LENGTH_SHORT).show();
                    Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if(!task.isSuccessful())
                            {
                                throw task.getException();
                            }

                            return filePath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful())
                            {
                                String downloadUrl = task.getResult().toString();
                                saveToDatabase(uName,uNIC ,uAddress,downloadUrl);
                            }
                        }
                    });
                }
            });

        }
    }

    private void saveToDatabase(String uName, String uNIC, String uAddress, String downloadUrl) {
        HashMap userMap = new HashMap();
        userMap.put("username", uName);
        userMap.put("nic", uNIC);
        userMap.put("address", uAddress);
        userMap.put("lat", latitude);
        userMap.put("lng", longitude);
        userMap.put("profileImage", downloadUrl);

        usersRef.child(currentuserid).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SetupActivity.this, "Successfully Updated Details..", Toast.LENGTH_SHORT).show();
                    Loadingbar.dismiss();
                    SendUserToMainActivity();
                } else {
                    String message = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this, "Error Occured " + message, Toast.LENGTH_SHORT).show();
                    Loadingbar.dismiss();
                }

            }
        });
    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(SetupActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void initializeFields()
    {
        userLocation = (TextView)findViewById(R.id.user_location);
        userName = (EditText) findViewById(R.id.user_name);
        userNIC = (EditText) findViewById(R.id.user_nic);
        userProfileImage = (CircleImageView)findViewById(R.id.profile_image);

        saveDetails = (Button)findViewById(R.id.save_user);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                latitude = extras.getDouble("lati");
                longitude = extras.getDouble("longti");
                address = extras.getString("address");

                if(longitude.toString().isEmpty()||latitude.toString().isEmpty())
                {
                    Toast.makeText(this, "Couldn't Get Specific Location Informtion.. ", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SetupActivity.this,PlacePickerActivity.class);
                    startActivityForResult(intent,PLACE_PICKER_REQUEST);
                }

                userLocation.setText(address);
        }

        if(requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null)
        {
            Uri ImageUri = data.getData();

            CropImage.activity(ImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK)
            {
                imageUri = result.getUri();
                userProfileImage.setImageURI(imageUri);
            }
        }

    }

}
