package com.sccodesoft.schoolfinder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private Button createAccountButton;
    private EditText userRegEmail, userRegPassword, userRegConfPassword ;
    private TextView alreadyhaveAccount;

    private FirebaseAuth mAuth;


    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        initializeFields();

        alreadyhaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                sendUserToLoginActivity();
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                createNewAccount();
            }
        });

    }


    private void createNewAccount()
    {
        String email = userRegEmail.getText().toString();
        String password = userRegPassword.getText().toString();
        String cofpassword = userRegConfPassword.getText().toString();

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please Enter Your Email..", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please Enter A Password..", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(cofpassword))
        {
            Toast.makeText(this, "Please Confirm Your Password..", Toast.LENGTH_SHORT).show();
        }
        else if (!(cofpassword).equals(password))
        {
            Toast.makeText(this, "Confirm Password Does Not Match..", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please Wait..");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                String currentUserID = mAuth.getCurrentUser().getUid();

                                sendUserToValidateActivity();
                                Toast.makeText(RegisterActivity.this, "Account Created Successfully..", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            else
                            {
                                String message = task.getException().toString();
                                Toast.makeText(RegisterActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }

                        }
                    });
        }
    }

    private void sendUserToLoginActivity()
    {
        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    private void sendUserToValidateActivity()
    {
        Intent intent = new Intent(RegisterActivity.this,ValidateUser.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void initializeFields()
    {
        createAccountButton = (Button)findViewById(R.id.reg_create_act_btn);
        userRegEmail = (EditText)findViewById(R.id.reg_email);
        userRegPassword = (EditText)findViewById(R.id.reg_password);
        userRegConfPassword = (EditText)findViewById(R.id.reg_confpassword);
        alreadyhaveAccount = (TextView)findViewById(R.id.reg_already_have_account);
        loadingBar = new ProgressDialog(this);
    }
}
