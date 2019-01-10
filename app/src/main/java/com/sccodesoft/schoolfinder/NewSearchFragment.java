package com.sccodesoft.schoolfinder;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.round;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewSearchFragment extends Fragment
{
    private View newSearchView;
    private Button newSearchBtn;

    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    private String currentuserid;
    private Double userlat;
    private Double userlng;

    public ArrayList<String> schresultList = new ArrayList<String>();

    private TextView distanceLimit;

    private CheckBox use_preferences;

    private LinearLayout pref_layout;

    private CheckBox gender_boys,gender_girls,gender_mix,
            lang_sinhala,lang_tamil,lang_english,
            sch_national,sch_provincial,sch_primary,
            rel_buddhist,rel_catholic,rel_muslim,rel_hindu;

    private SeekBar distanceLimitBar;

    private ProgressDialog loadingbar;

    public NewSearchFragment()
    {

        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        newSearchView = inflater.inflate(R.layout.fragment_new_search, container, false);

        loadingbar = new ProgressDialog(getContext());

        initializeFields();

        currentuserid = mAuth.getCurrentUser().getUid();

        rootRef.child("Users").child(currentuserid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                userlat = dataSnapshot.child("lat").getValue(Double.class);
                userlng = dataSnapshot.child("lng").getValue(Double.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        use_preferences.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(use_preferences.isChecked())
                {
                    pref_layout.setVisibility(View.VISIBLE);
                }
                else
                {
                    pref_layout.setVisibility(View.GONE);
                }
            }
        });

        distanceLimit.setText(String.valueOf(distanceLimitBar.getProgress()+1));
        distanceLimitBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distanceLimit.setText(String.valueOf(distanceLimitBar.getProgress()+1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                distanceLimit.setText(String.valueOf(distanceLimitBar.getProgress()+1));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                distanceLimit.setText(String.valueOf(distanceLimitBar.getProgress()+1));
            }
        });

        newSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                if(use_preferences.isChecked())
                {
                    if (!gender_mix.isChecked() && !gender_girls.isChecked() && !gender_boys.isChecked()) {
                        Toast.makeText(getActivity(), "Please Select at least one Gender Type", Toast.LENGTH_SHORT).show();
                    } else if (!lang_sinhala.isChecked() && !lang_tamil.isChecked() && !lang_english.isChecked()) {
                        Toast.makeText(getActivity(), "Please Select at least one Language Medium", Toast.LENGTH_SHORT).show();
                    } else if (!sch_national.isChecked() && !sch_provincial.isChecked() && !sch_primary.isChecked()) {
                        Toast.makeText(getActivity(), "Please Select at least one School Type", Toast.LENGTH_SHORT).show();
                    } else if (!rel_buddhist.isChecked() && !rel_catholic.isChecked() && !rel_muslim.isChecked() && !rel_hindu.isChecked()) {
                        Toast.makeText(getActivity(), "Please Select at least one Religion Type", Toast.LENGTH_SHORT).show();
                    } else {
                        searchSchools();
                    }
                }
                else
                {
                    searchSchoolsNoPref();
                }

            }
        });

        return newSearchView;
    }

    private void searchSchoolsNoPref()
    {
        loadingbar.setTitle("Loading");
        loadingbar.setMessage("Please Wait While We are Searching For Schools..");
        loadingbar.show();
        loadingbar.setCanceledOnTouchOutside(false);

        rootRef.child("Schools").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot school : dataSnapshot.getChildren()) {
                    Double lat = school.child("lat").getValue(Double.class);
                    Double lng = school.child("lng").getValue(Double.class);


                    float distance = findNearbySchools(userlat, userlng, lat, lng);
                    if (distance <= (Integer.valueOf(distanceLimit.getText().toString()))*1000)
                    {
                        String skey = school.getKey().toString();

                            schresultList.add(i, skey);
                            schresultList.add(i + 1, String.valueOf(distance));
                            i = i + 2;
                    }
                }

                if (!schresultList.isEmpty())
                {
                    loadingbar.dismiss();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    SchoolResultsFragment schoolResultsFragment = new SchoolResultsFragment();
                    Bundle arguments = new Bundle();
                    arguments.putStringArrayList("res", schresultList);
                    schoolResultsFragment.setArguments(arguments);

                    fragmentTransaction.replace(R.id.frame_container, schoolResultsFragment);
                    fragmentTransaction.commit();
                }
                else
                {
                    loadingbar.dismiss();
                    Toast.makeText(getActivity(), "No Results Found..", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void searchSchools()
    {
        loadingbar.setTitle("Loading");
        loadingbar.setMessage("Please Wait While We are Searching For Schools..");
        loadingbar.show();
        loadingbar.setCanceledOnTouchOutside(false);

        rootRef.child("Schools").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot school : dataSnapshot.getChildren()) {
                    Double lat = school.child("lat").getValue(Double.class);
                    Double lng = school.child("lng").getValue(Double.class);


                    float distance = findNearbySchools(userlat, userlng, lat, lng);
                    if (distance <= (Integer.valueOf(distanceLimit.getText().toString()))*1000)
                    {
                        String skey = school.getKey().toString();
                        String stype = school.child("type").getValue(String.class);
                        String smedium = school.child("medium").getValue(String.class);
                        String scategory = school.child("category").getValue(String.class);
                        String sreligion = school.child("religion").getValue(String.class);

                        if (((gender_boys.isChecked() && (stype.equals("b"))) || (gender_girls.isChecked() && stype.equals("g")) || (gender_mix.isChecked() && (stype.equals("m")))) &&
                                ((lang_sinhala.isChecked() && (smedium.equals("s") || smedium.equals("se") || smedium.equals("ste"))) || (lang_tamil.isChecked() && ((smedium.equals("t") || smedium.equals("te") || smedium.equals("ste")))) || (lang_english.isChecked() && (smedium.equals("se") || smedium.equals("te") || smedium.equals("ste")))) &&
                                ((sch_national.isChecked() && scategory.equals("n")) || (sch_provincial.isChecked() && (scategory.equals("pro"))) || (sch_primary.isChecked() && (scategory.equals("pri")))) &&
                                ((rel_buddhist.isChecked() && sreligion.equals("b")) || (rel_catholic.isChecked() && sreligion.equals("c")) || (rel_muslim.isChecked() && sreligion.equals("m")) || (rel_hindu.isChecked() && sreligion.equals("h")))
                                )
                        {
                            schresultList.add(i, skey);
                            schresultList.add(i + 1, String.valueOf(distance));
                            i = i + 2;
                        }
                    }
                }

                if (!schresultList.isEmpty())
                {
                    loadingbar.dismiss();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    SchoolResultsFragment schoolResultsFragment = new SchoolResultsFragment();
                    Bundle arguments = new Bundle();
                    arguments.putStringArrayList("res", schresultList);
                    schoolResultsFragment.setArguments(arguments);

                    fragmentTransaction.replace(R.id.frame_container, schoolResultsFragment);
                    fragmentTransaction.commit();
                }
                else
                {
                    loadingbar.dismiss();
                    Toast.makeText(getActivity(), "No Results Found For Your Preferences..", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private float findNearbySchools(Double lat1, Double lng1, Double lat2, Double lng2)
    {
        Location loc1 = new Location("");
        loc1.setLatitude(lat1);
        loc1.setLongitude(lng1);

        Location loc2 = new Location("");
        loc2.setLatitude(lat2);
        loc2.setLongitude(lng2);

        float distanceInMeters = loc1.distanceTo(loc2);

        return distanceInMeters;
    }


    private void initializeFields()
    {
        rootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        use_preferences = (CheckBox)newSearchView.findViewById(R.id.use_preferences);

        pref_layout = (LinearLayout)newSearchView.findViewById(R.id.pref_layout);

        gender_boys = (CheckBox)newSearchView.findViewById(R.id.gender_boys);
        gender_girls = (CheckBox)newSearchView.findViewById(R.id.gender_girls);
        gender_mix = (CheckBox)newSearchView.findViewById(R.id.gender_mix);

        lang_sinhala = (CheckBox)newSearchView.findViewById(R.id.lang_sinhala);
        lang_tamil = (CheckBox)newSearchView.findViewById(R.id.lang_tamil);
        lang_english = (CheckBox)newSearchView.findViewById(R.id.lang_english);

        sch_national = (CheckBox)newSearchView.findViewById(R.id.sch_national);
        sch_provincial = (CheckBox)newSearchView.findViewById(R.id.sch_provincial);
        sch_primary = (CheckBox)newSearchView.findViewById(R.id.sch_primary);

        rel_buddhist = (CheckBox)newSearchView.findViewById(R.id.rel_buddhist);
        rel_catholic = (CheckBox)newSearchView.findViewById(R.id.rel_catholic);
        rel_muslim = (CheckBox)newSearchView.findViewById(R.id.rel_muslim);
        rel_hindu = (CheckBox)newSearchView.findViewById(R.id.rel_hindu);

        distanceLimit = (TextView)newSearchView.findViewById(R.id.distanceLimit);
        distanceLimitBar = (SeekBar)newSearchView.findViewById(R.id.distanceLimitBar);
        distanceLimitBar.getProgressDrawable().setColorFilter(Color.parseColor("#3b5998"), PorterDuff.Mode.SRC_IN);
        distanceLimitBar.getThumb().setColorFilter(Color.parseColor("#3b5998"), PorterDuff.Mode.SRC_IN);

        newSearchBtn = (Button)newSearchView.findViewById(R.id.new_search_button);
    }

}
