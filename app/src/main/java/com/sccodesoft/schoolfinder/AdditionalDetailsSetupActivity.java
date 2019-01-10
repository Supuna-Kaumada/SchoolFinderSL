package com.sccodesoft.schoolfinder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AdditionalDetailsSetupActivity extends AppCompatActivity {

    private ProgressDialog loadingBar;

    private RadioButton radio_rmTitleDeeds, radio_rmGiftCert, radio_rmGovAward, radio_rmTempleDevala, radio_rmDeclaration, radio_rmLoans, radio_rmLeaseBond, radio_rmother;
    private RadioButton radio_ApplSpouse, radio_MotherFather, radio_OtherOwn;
    private RadioButton radio_rmyMorethanFive, radio_rmyFiveFour, radio_rmyFourThree, radio_rmyThreeTwo, radio_rmyTwoOne, radio_rmyOneSixm, radio_rmyLessthanSix;

    private LinearLayout rmOtherLayout, rmOwnLayout, rmTimeLayout;
    private LinearLayout elec_father_Layout, elec_mother_Layout, elec_leagalParent_Layout;

    private RadioGroup rg_ResidenceMain, rg_residenceOwn, rg_residenceTime;

    private CheckBox rmother_electricity, rmother_water, rmother_taxbills, rmother_birthcertif;
    private CheckBox ra_nicdl, ra_ftelebill, ra_schleaving, ra_marrycertif, ra_samurdicard, ra_lifeinsurance, ra_childbirthcertf;
    private CheckBox elecreg_father, elecreg_mother, elecreg_leagParent;

    private Spinner spinnerFatherElecRegTime, spinnerMotherElecRegTime, spinnerleagParentElecRegTime;

    private Button UpdateAddDet;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    String currentuserid;

    private double ResMainDocMarks = 0.0;
    private double ResAddDocMarks = 0.0;
    private double ElectionRegMarks = 0.0;

    String[] resPR;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_details_setup);

        Bundle arguments = getIntent().getExtras();
        resPR = arguments.getStringArray("respr");

        loadingBar = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        currentuserid = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        UpdateAddDet = (Button) findViewById(R.id.update_addtionDet_button);

        rmOtherLayout = (LinearLayout) findViewById(R.id.rmOtherLayout);
        rmOwnLayout = (LinearLayout) findViewById(R.id.rmOwnLayout);
        rmTimeLayout = (LinearLayout) findViewById(R.id.rmTimeLayout);

        elec_father_Layout = (LinearLayout) findViewById(R.id.elec_father_Layout);
        elec_mother_Layout = (LinearLayout) findViewById(R.id.elec_mother_Layout);
        elec_leagalParent_Layout = (LinearLayout) findViewById(R.id.elec_leagalParent_Layout);

        radio_rmTitleDeeds = (RadioButton) findViewById(R.id.radio_rmTitleDeeds);
        radio_rmGiftCert = (RadioButton) findViewById(R.id.radio_rmGiftCert);
        radio_rmGovAward = (RadioButton) findViewById(R.id.radio_rmGovAward);
        radio_rmTempleDevala = (RadioButton) findViewById(R.id.radio_rmTempleDevala);
        radio_rmDeclaration = (RadioButton) findViewById(R.id.radio_rmDeclaration);
        radio_rmLoans = (RadioButton) findViewById(R.id.radio_rmLoans);
        radio_rmLeaseBond = (RadioButton) findViewById(R.id.radio_rmLeaseBond);
        radio_rmother = (RadioButton) findViewById(R.id.radio_rmother);

        radio_ApplSpouse = (RadioButton) findViewById(R.id.radio_ApplSpouse);
        radio_MotherFather = (RadioButton) findViewById(R.id.radio_MotherFather);
        radio_OtherOwn = (RadioButton) findViewById(R.id.radio_OtherOwn);

        radio_rmyMorethanFive = (RadioButton) findViewById(R.id.radio_rmyMorethanFive);
        radio_rmyFiveFour = (RadioButton) findViewById(R.id.radio_rmyFiveFour);
        radio_rmyFourThree = (RadioButton) findViewById(R.id.radio_rmyFourThree);
        radio_rmyThreeTwo = (RadioButton) findViewById(R.id.radio_rmyThreeTwo);
        radio_rmyTwoOne = (RadioButton) findViewById(R.id.radio_rmyTwoOne);
        radio_rmyOneSixm = (RadioButton) findViewById(R.id.radio_rmyOneSixm);
        radio_rmyLessthanSix = (RadioButton) findViewById(R.id.radio_rmyLessthanSix);

        rg_ResidenceMain = (RadioGroup) findViewById(R.id.residenceMain);
        rg_residenceOwn = (RadioGroup) findViewById(R.id.residenceOwn);
        rg_residenceTime = (RadioGroup) findViewById(R.id.residenceTime);

        rmother_electricity = (CheckBox) findViewById(R.id.rmother_electricity);
        rmother_water = (CheckBox) findViewById(R.id.rmother_water);
        rmother_taxbills = (CheckBox) findViewById(R.id.rmother_taxbills);
        rmother_birthcertif = (CheckBox) findViewById(R.id.rmother_birthcertif);

        ra_nicdl = (CheckBox) findViewById(R.id.ra_nicdl);
        ra_ftelebill = (CheckBox) findViewById(R.id.ra_ftelebill);
        ra_schleaving = (CheckBox) findViewById(R.id.ra_schleaving);
        ra_marrycertif = (CheckBox) findViewById(R.id.ra_marrycertif);
        ra_samurdicard = (CheckBox) findViewById(R.id.ra_samurdicard);
        ra_lifeinsurance = (CheckBox) findViewById(R.id.ra_lifeinsurance);
        ra_childbirthcertf = (CheckBox) findViewById(R.id.ra_childbirthcertf);

        elecreg_father = (CheckBox) findViewById(R.id.elecreg_father);
        elecreg_mother = (CheckBox) findViewById(R.id.elecreg_mother);
        elecreg_leagParent = (CheckBox) findViewById(R.id.elecreg_leagParent);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.electionreg_time, android.R.layout.simple_spinner_item);
        spinnerFatherElecRegTime = (Spinner) findViewById(R.id.spinnerFatherElecRegTime);
        spinnerFatherElecRegTime.setAdapter(adapter);
        spinnerMotherElecRegTime = (Spinner) findViewById(R.id.spinnerMotherElecRegTime);
        spinnerMotherElecRegTime.setAdapter(adapter);
        spinnerleagParentElecRegTime = (Spinner) findViewById(R.id.spinnerleagParentElecRegTime);
        spinnerleagParentElecRegTime.setAdapter(adapter);

        elecreg_father.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true || (isChecked == false && elecreg_mother.isChecked())) {
                    elec_leagalParent_Layout.setVisibility(View.GONE);
                    spinnerFatherElecRegTime.setVisibility(View.VISIBLE);
                }
                if (isChecked == false && !elecreg_mother.isChecked()) {
                    elec_leagalParent_Layout.setVisibility(View.VISIBLE);
                    spinnerFatherElecRegTime.setVisibility(View.GONE);
                }
            }
        });

        elecreg_mother.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true || (isChecked == false && elecreg_father.isChecked())) {
                    elec_leagalParent_Layout.setVisibility(View.GONE);
                    spinnerMotherElecRegTime.setVisibility(View.VISIBLE);
                }
                if (isChecked == false && !elecreg_father.isChecked()) {
                    elec_leagalParent_Layout.setVisibility(View.VISIBLE);
                    spinnerMotherElecRegTime.setVisibility(View.GONE);
                }
            }
        });

        elecreg_leagParent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    elec_father_Layout.setVisibility(View.GONE);
                    elec_mother_Layout.setVisibility(View.GONE);
                }
                if (isChecked == false) {
                    elec_father_Layout.setVisibility(View.VISIBLE);
                    elec_mother_Layout.setVisibility(View.VISIBLE);
                }
            }
        });


        rg_ResidenceMain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == radio_rmTitleDeeds.getId() || checkedId == radio_rmGiftCert.getId() || checkedId == radio_rmGovAward.getId() || checkedId == radio_rmTempleDevala.getId() || checkedId == radio_rmDeclaration.getId() || checkedId == radio_rmLoans.getId()) {
                    rmOwnLayout.setVisibility(View.VISIBLE);
                    rmTimeLayout.setVisibility(View.GONE);
                    rmOtherLayout.setVisibility(View.GONE);
                } else if (checkedId == radio_rmother.getId()) {
                    rmOtherLayout.setVisibility(View.VISIBLE);
                    rmOwnLayout.setVisibility(View.GONE);
                    rmTimeLayout.setVisibility(View.GONE);
                } else {
                    rmTimeLayout.setVisibility(View.VISIBLE);
                    rmOtherLayout.setVisibility(View.GONE);
                    rmOwnLayout.setVisibility(View.GONE);
                }
            }
        });

        rg_residenceOwn.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == radio_OtherOwn.getId()) {
                    rmTimeLayout.setVisibility(View.GONE);
                } else {
                    rmTimeLayout.setVisibility(View.VISIBLE);
                }
            }
        });


        UpdateAddDet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkMainDocuments();
                checkAdditionalDocuments();
                checkElectionRegister();

                Double TotalResidenceMarks = ResAddDocMarks + ResMainDocMarks + ElectionRegMarks;

                saveAddtionalDetails();

                Toast.makeText(AdditionalDetailsSetupActivity.this, "Current Marks are " + ResAddDocMarks + " + " + ResMainDocMarks + " + " + ElectionRegMarks + " = " + TotalResidenceMarks, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveAddtionalDetails() {
        loadingBar.setTitle("Saving");
        loadingBar.setMessage("Please Wait..");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();

        HashMap addtionalDetMap = new HashMap();
        addtionalDetMap.put("adddocmarks", ResAddDocMarks);
        addtionalDetMap.put("maindocmarks", ResMainDocMarks);
        addtionalDetMap.put("electionregmarks", ElectionRegMarks);

      usersRef.child(currentuserid).updateChildren(addtionalDetMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AdditionalDetailsSetupActivity.this, "Additional Details Added Successfully.", Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                    sendUserToFinalResult();
                } else {
                    String message = task.getException().getMessage();
                    Toast.makeText(AdditionalDetailsSetupActivity.this, "Error Occured " + message, Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }

            }
        });
    }

    private void sendUserToFinalResult() {
        Intent intent = new Intent(this,FinalResultsActivity.class);
        Bundle arguments = new Bundle();
        arguments.putStringArray("respr", resPR);
        intent.putExtras(arguments);
        startActivity(intent);
    }

    private void checkElectionRegister() {
        ElectionRegMarks = 0.0;

        if (elecreg_father.isChecked()) {

            if (spinnerFatherElecRegTime.getSelectedItem().toString().equals("5 Years")) {
                ElectionRegMarks = ElectionRegMarks + (5 * 2.5);
            } else if (spinnerFatherElecRegTime.getSelectedItem().toString().equals("4 Years")) {
                ElectionRegMarks = ElectionRegMarks + (4 * 2.5);
            } else if (spinnerFatherElecRegTime.getSelectedItem().toString().equals("3 Years")) {
                ElectionRegMarks = ElectionRegMarks + (3 * 2.5);
            } else if (spinnerFatherElecRegTime.getSelectedItem().toString().equals("2 Years")) {
                ElectionRegMarks = ElectionRegMarks + (2 * 2.5);
            } else if (spinnerFatherElecRegTime.getSelectedItem().toString().equals("1 Year")) {
                ElectionRegMarks = ElectionRegMarks + (1 * 2.5);
            }
        }
        if (elecreg_mother.isChecked()) {


            if (spinnerMotherElecRegTime.getSelectedItem().toString().equals("5 Years")) {
                ElectionRegMarks = ElectionRegMarks + (5 * 2.5);
            } else if (spinnerMotherElecRegTime.getSelectedItem().toString().equals("4 Years")) {
                ElectionRegMarks = ElectionRegMarks + (4 * 2.5);
            } else if (spinnerMotherElecRegTime.getSelectedItem().toString().equals("3 Years")) {
                ElectionRegMarks = ElectionRegMarks + (3 * 2.5);
            } else if (spinnerMotherElecRegTime.getSelectedItem().toString().equals("2 Years")) {
                ElectionRegMarks = ElectionRegMarks + (2 * 2.5);
            } else if (spinnerMotherElecRegTime.getSelectedItem().toString().equals("1 Year")) {
                ElectionRegMarks = ElectionRegMarks + (1 * 2.5);
            }

        }
        if (elecreg_leagParent.isChecked()) {


            if (spinnerleagParentElecRegTime.getSelectedItem().toString().equals("5 Years")) {
                ElectionRegMarks = ElectionRegMarks + (5 * 5);
            } else if (spinnerleagParentElecRegTime.getSelectedItem().toString().equals("4 Years")) {
                ElectionRegMarks = ElectionRegMarks + (4 * 5);
            } else if (spinnerleagParentElecRegTime.getSelectedItem().toString().equals("3 Years")) {
                ElectionRegMarks = ElectionRegMarks + (3 * 5);
            } else if (spinnerleagParentElecRegTime.getSelectedItem().toString().equals("2 Years")) {
                ElectionRegMarks = ElectionRegMarks + (2 * 5);
            } else if (spinnerleagParentElecRegTime.getSelectedItem().toString().equals("1 Year")) {
                ElectionRegMarks = ElectionRegMarks + (1 * 5);
            }
        }

    }

    private void checkAdditionalDocuments() {
        ResAddDocMarks = 0.0;

        if (ra_nicdl.isChecked()) {
            ResAddDocMarks = ResAddDocMarks + 1;
        }
        if (ra_ftelebill.isChecked()) {
            ResAddDocMarks = ResAddDocMarks + 1;
        }
        if (ra_schleaving.isChecked()) {
            ResAddDocMarks = ResAddDocMarks + 1;
        }
        if (ra_marrycertif.isChecked()) {
            ResAddDocMarks = ResAddDocMarks + 1;
        }
        if (ra_samurdicard.isChecked()) {
            ResAddDocMarks = ResAddDocMarks + 1;
        }
        if (ra_lifeinsurance.isChecked()) {
            ResAddDocMarks = ResAddDocMarks + 1;
        }
        if (ra_childbirthcertf.isChecked()) {
            ResAddDocMarks = ResAddDocMarks + 1;
        }

        if (ResAddDocMarks >= 5) {
            ResAddDocMarks = 5;
        }

    }


    private void checkMainDocuments() {
        if (rg_ResidenceMain.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please Select Main Document You Have as a proof of Residency..", Toast.LENGTH_SHORT).show();
        } else if ((rg_ResidenceMain.getCheckedRadioButtonId() == radio_rmTitleDeeds.getId() || rg_ResidenceMain.getCheckedRadioButtonId() == radio_rmGiftCert.getId() || rg_ResidenceMain.getCheckedRadioButtonId() == radio_rmGovAward.getId() || rg_ResidenceMain.getCheckedRadioButtonId() == radio_rmTempleDevala.getId() || rg_ResidenceMain.getCheckedRadioButtonId() == radio_rmDeclaration.getId() || rg_ResidenceMain.getCheckedRadioButtonId() == radio_rmLoans.getId())) {
            if (rg_residenceOwn.getCheckedRadioButtonId() == radio_ApplSpouse.getId()) {
                checkTimeCondition(30.0);
            } else if (rg_residenceOwn.getCheckedRadioButtonId() == radio_MotherFather.getId()) {
                checkTimeCondition(23.0);
            }
        } else if (rg_ResidenceMain.getCheckedRadioButtonId() == radio_rmLeaseBond.getId()) {
            checkTimeCondition(12);
        } else if (rg_ResidenceMain.getCheckedRadioButtonId() == radio_rmother.getId()) {
            ResMainDocMarks = 0;
            if (rmother_birthcertif.isChecked()) {
                ResMainDocMarks = ResMainDocMarks + 1.5;
            }
            if (rmother_taxbills.isChecked()) {
                ResMainDocMarks = ResMainDocMarks + 1.5;
            }
            if (rmother_water.isChecked()) {
                ResMainDocMarks = ResMainDocMarks + 1.5;
            }
            if (rmother_electricity.isChecked()) {
                ResMainDocMarks = ResMainDocMarks + 1.5;
            }

            if (ResMainDocMarks < 4.5) {
                ResMainDocMarks = 0;
            }

        }
    }

    private void checkTimeCondition(double v) {
        if (rg_residenceTime.getCheckedRadioButtonId() == radio_rmyMorethanFive.getId()) {
            ResMainDocMarks = v;
            Toast.makeText(this, "Marks : " + ResMainDocMarks, Toast.LENGTH_SHORT).show();
        } else if (rg_residenceTime.getCheckedRadioButtonId() == radio_rmyFiveFour.getId()) {
            ResMainDocMarks = (v * 80) / 100;
            Toast.makeText(this, "Marks : " + ResMainDocMarks, Toast.LENGTH_SHORT).show();
        } else if (rg_residenceTime.getCheckedRadioButtonId() == radio_rmyFourThree.getId()) {
            ResMainDocMarks = (v * 60) / 100;
            Toast.makeText(this, "Marks : " + ResMainDocMarks, Toast.LENGTH_SHORT).show();
        } else if (rg_residenceTime.getCheckedRadioButtonId() == radio_rmyThreeTwo.getId()) {
            ResMainDocMarks = (v * 40) / 100;
            Toast.makeText(this, "Marks : " + ResMainDocMarks, Toast.LENGTH_SHORT).show();
        } else if (rg_residenceTime.getCheckedRadioButtonId() == radio_rmyTwoOne.getId()) {
            ResMainDocMarks = (v * 20) / 100;
            Toast.makeText(this, "Marks : " + ResMainDocMarks, Toast.LENGTH_SHORT).show();
        } else if (rg_residenceTime.getCheckedRadioButtonId() == radio_rmyOneSixm.getId()) {
            ResMainDocMarks = (v * 10) / 100;
            Toast.makeText(this, "Marks : " + ResMainDocMarks, Toast.LENGTH_SHORT).show();
        } else if (rg_residenceTime.getCheckedRadioButtonId() == radio_rmyLessthanSix.getId()) {
            ResMainDocMarks = (v * 5) / 100;
            Toast.makeText(this, "Marks : " + ResMainDocMarks, Toast.LENGTH_SHORT).show();
        }

    }
}
