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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends AppCompatActivity
{
    private Button ResetPasswordButton;
    private EditText ResetPasswordEmail;

    private FirebaseAuth mAuth;

    private ProgressDialog Loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        Loadingbar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        ResetPasswordButton = (Button)findViewById(R.id.reset_password_button);
        ResetPasswordEmail = (EditText)findViewById(R.id.reset_password_email);

        ResetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String userEmail = ResetPasswordEmail.getText().toString();

                if(TextUtils.isEmpty(userEmail))
                {
                    Toast.makeText(PasswordResetActivity.this, "Please Enter a valid E-mail address..", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Loadingbar.setTitle("Password Reset");
                    Loadingbar.setMessage("Please Wait While We are Generating Your Reset Link..");
                    Loadingbar.show();
                    Loadingbar.setCanceledOnTouchOutside(false);

                    mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                Loadingbar.dismiss();
                                Toast.makeText(PasswordResetActivity.this, "Please check your E-mail Account to Reset Password..", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(PasswordResetActivity.this,LoginActivity.class));
                            }
                            else
                            {
                                Loadingbar.dismiss();
                                String message = task.getException().getMessage();
                                Toast.makeText(PasswordResetActivity.this, "Error Occured.." + message, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });

    }
}
