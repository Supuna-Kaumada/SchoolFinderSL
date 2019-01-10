package com.sccodesoft.schoolfinder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private Button loginButton, signupButton;
    private EditText userEmail, userPassword;
    private TextView forgotPassword;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        initializeFields();

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                sendUserToRegisterActivity();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                allowUserToLogin();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToResetPassword();
            }
        });
    }

    private void sendUserToResetPassword()
    {
        Intent intent = new Intent(LoginActivity.this,PasswordResetActivity.class);
        startActivity(intent);
    }

    private void allowUserToLogin()
    {
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please Enter Your Email..", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please Enter Your Password..", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Login");
            loadingBar.setMessage("Please Wait..");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful())
                        {

                            sendUserToValidateActivity();
                            Toast.makeText(LoginActivity.this, "Logged In Successfully..", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "Error Login Invalid Username Or Password", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }

                    });

        }

    }



    private void sendUserToRegisterActivity()
    {
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }

    private void initializeFields()
    {
        loginButton = (Button)findViewById(R.id.login_button);
        userEmail = (EditText)findViewById(R.id.login_user_email);
        userPassword = (EditText)findViewById(R.id.login_user_password);
        signupButton = (Button) findViewById(R.id.sign_up_button);
        forgotPassword = (TextView)findViewById(R.id.login_forgot_password);
        loadingBar = new ProgressDialog(this);
    }

    private void sendUserToValidateActivity()
    {
        Intent intent = new Intent(LoginActivity.this,ValidateUser.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}