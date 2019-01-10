package com.sccodesoft.schoolfinder;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SchoolResultsFragment extends Fragment {

    private View schoolResultsView;
    private RecyclerView resultList;

    final private List<Schools> schoolsArrayList = new ArrayList<Schools>();

    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;
    String currentuserid;

    SchoolResultsAdapter resultsAdapter;

    private ImageButton spinnerdropdown;

    public SchoolResultsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        schoolResultsView = inflater.inflate(R.layout.fragment_school_results, container, false);

        spinnerdropdown = schoolResultsView.findViewById(R.id.spinnerdropdown);

        rootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentuserid = mAuth.getCurrentUser().getUid();

        resultList = (RecyclerView) schoolResultsView.findViewById(R.id.school_results_list_recycler);

        Bundle arguments = getArguments();

        ArrayList<String> arrayReceived = arguments.getStringArrayList("res");


        for(int i=0; i<arrayReceived.size();i=i+2)
        {
            String tem = arrayReceived.get(i);
            final String dist = arrayReceived.get(i+1);
            rootRef.child("Schools").child(tem).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String skey = tem;

                    String sname = dataSnapshot.child("name").getValue().toString();
                    String saddress = dataSnapshot.child("address").getValue().toString();
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

                    String sphone = dataSnapshot.child("phone").getValue().toString();
                    String srating = dataSnapshot.child("rating").getValue().toString();
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

                    String swebsite = dataSnapshot.child("website").getValue(String.class);

                    schoolsArrayList.add(new Schools(skey,saddress,scategory,smedium,sname,sphone,srating,sreligion,stype,swebsite,dist));

                    if(!schoolsArrayList.isEmpty())
                    {
                        resultsAdapter = new SchoolResultsAdapter(getContext(), schoolsArrayList);
                        resultList.setLayoutManager(new LinearLayoutManager(getActivity()));
                        resultList.setAdapter(resultsAdapter);
                        resultsAdapter.notifyDataSetChanged();
                        setupSort();

                        resultsAdapter.setOnItemClickListener(new SchoolResultsAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                String schlkey = schoolsArrayList.get(position).key;
                                String schdist = schoolsArrayList.get(position).dist;
                                sortByDistance();
                                resultsAdapter.notifyDataSetChanged();

                                for(int j=0;j<schoolsArrayList.size();j++)
                                {
                                    String schlk = schoolsArrayList.get(j).key;
                                    if(schlk==schlkey)
                                    {
                                        Integer Proximitymarks = 40 - (4*j);

                                        if(Proximitymarks<0)
                                        {
                                            Proximitymarks = 0;
                                        }

                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                                        alertDialogBuilder.setMessage("You Have Taken "+ Proximitymarks + " Marks Out Of 40  for the Proximity for your School. \n\n Do You Want To Calculate Full Marks ?");
                                        alertDialogBuilder.setTitle("School Finder");

                                        Integer finalProximitymarks = Proximitymarks;

                                        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                String[] resPR = {schlkey, finalProximitymarks.toString(),schdist};


                                                rootRef.child("Users").child(currentuserid).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if(!dataSnapshot.hasChild("maindocmarks")||!dataSnapshot.hasChild("adddocmarks")||!dataSnapshot.hasChild("electionregmarks"))
                                                        {
                                                            Toast.makeText(getContext(), "Plese Fill Additional Details First..", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(getActivity(),AdditionalDetailsSetupActivity.class);
                                                            Bundle arguments = new Bundle();
                                                            arguments.putStringArray("respr", resPR);
                                                            intent.putExtras(arguments);
                                                            startActivity(intent);
                                                        }
                                                        else if(dataSnapshot.hasChild("maindocmarks") && dataSnapshot.hasChild("adddocmarks") && dataSnapshot.hasChild("electionregmarks"))
                                                        {
                                                            AlertDialog.Builder alertDialogBuilder1 = new AlertDialog.Builder(getContext());
                                                            alertDialogBuilder1.setMessage("Do you want to update your additional details or use current settings?");
                                                            alertDialogBuilder1.setTitle("School Finder");

                                                            alertDialogBuilder1.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    Toast.makeText(getContext(), "Plese Fill Additional Details First..", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(getActivity(),AdditionalDetailsSetupActivity.class);
                                                                    Bundle arguments = new Bundle();
                                                                    arguments.putStringArray("respr", resPR);
                                                                    intent.putExtras(arguments);
                                                                    startActivity(intent);
                                                                }
                                                            });

                                                            alertDialogBuilder1.setNegativeButton("Use Current Settings", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    Intent intent = new Intent(getActivity(),FinalResultsActivity.class);
                                                                    Bundle arguments = new Bundle();
                                                                    arguments.putStringArray("respr", resPR);
                                                                    intent.putExtras(arguments);
                                                                    startActivity(intent);
                                                                }
                                                            });

                                                            AlertDialog alertDialog1 = alertDialogBuilder1.create();

                                                            alertDialog1.setOnShowListener( new DialogInterface.OnShowListener() {
                                                                @SuppressLint("ResourceAsColor")
                                                                @Override
                                                                public void onShow(DialogInterface arg0) {
                                                                    alertDialog1.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.colorPrimaryDark);
                                                                    alertDialog1.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.colorPrimaryDark);
                                                                }
                                                            });
                                                            alertDialog1.show();


                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        });

                                        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                String[] resPR = {schlkey, finalProximitymarks.toString(),schdist};

                                                Intent intent = new Intent(getActivity(),ProximityResultsActivity.class);
                                                Bundle arguments = new Bundle();
                                                arguments.putStringArray("respr", resPR);
                                                intent.putExtras(arguments);
                                                startActivity(intent);

                                            }
                                        });

                                        AlertDialog alertDialog = alertDialogBuilder.create();

                                        alertDialog.setOnShowListener( new DialogInterface.OnShowListener() {
                                            @SuppressLint("ResourceAsColor")
                                            @Override
                                            public void onShow(DialogInterface arg0) {
                                                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.colorPrimaryDark);
                                                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.colorPrimaryDark);
                                            }
                                        });
                                        alertDialog.show();

                                        break;
                                    }

                                }
                            }
                        });

                    }

                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });


        }



        return schoolResultsView;
    }


    @Override
    public void onStart() {
        super.onStart();



    }

    private void setupSort() {
        Spinner spinner = (Spinner) schoolResultsView.findViewById(R.id.spinner);

        spinnerdropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.performClick();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.sort_types,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    sortByDistance();
                    resultsAdapter.notifyDataSetChanged();
                }
                else if(position==1)
                {
                    sortByReligion();
                    resultsAdapter.notifyDataSetChanged();
                }
                else if(position==2)
                {
                    sortByGender();
                    resultsAdapter.notifyDataSetChanged();
                }
                else if(position==3)
                {
                    sortByCategory();
                    resultsAdapter.notifyDataSetChanged();
                }
                else if(position==4)
                {
                    sortByMedium();
                    resultsAdapter.notifyDataSetChanged();
                }
                else if(position==5)
                {
                    sortByRating();
                    resultsAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void sortByRating()
    {
        Collections.sort(schoolsArrayList,  (l1, l2) -> l2.getRating().compareTo(l1.getRating()));
    }

    private void sortByMedium()
    {
        Collections.sort(schoolsArrayList,  (l1, l2) -> l1.getMedium().compareTo(l2.getMedium()));
    }

    private void sortByCategory()
    {
        Collections.sort(schoolsArrayList,  (l1, l2) -> l1.getCategory().compareTo(l2.getCategory()));
    }

    private void sortByGender()
    {
        Collections.sort(schoolsArrayList,  (l1, l2) -> l1.getType().compareTo(l2.getType()));
    }

    private void sortByReligion()
    {
        Collections.sort(schoolsArrayList,  (l1, l2) -> l1.getReligion().compareTo(l2.getReligion()));
    }

    private void sortByDistance()
    {
        Collections.sort(schoolsArrayList, (Schools l1, Schools l2) -> {
            if ( Float.valueOf(l1.getDist()) >  Float.valueOf(l2.getDist())) {
                return 1;
            } else if ( Float.valueOf(l1.getDist()) <  Float.valueOf(l2.getDist())) {
                return -1;
            } else {
                return 0;
            }
        });
    }
}