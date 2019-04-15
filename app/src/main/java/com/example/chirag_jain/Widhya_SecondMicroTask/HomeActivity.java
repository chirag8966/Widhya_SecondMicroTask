package com.example.chirag_jain.Widhya_SecondMicroTask;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.chirag_jain.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity {


    private static final int MY_REQUEST_CODE = 7117; //Any number you want
    List<AuthUI .IdpConfig> providers;
    private Button btn_sign_out;
    ImageView photoUrl;
    TextView name, email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btn_sign_out = (Button)findViewById(R.id.btn_sign_out);
        photoUrl = (ImageView) findViewById(R.id.photoUrl);
        email = (TextView) findViewById(R.id.email);
        name = (TextView) findViewById(R.id.name);
        btn_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Logout
                AuthUI.getInstance().signOut(HomeActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                btn_sign_out.setEnabled(false);
                                showSignInOptions();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HomeActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        //Init provider
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(), //Email builder
                new AuthUI.IdpConfig.GoogleBuilder().build() //Google builder
        );
        showSignInOptions();
    }

    private void showSignInOptions() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.MyTheme)
                .build(),MY_REQUEST_CODE
        );
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MY_REQUEST_CODE)
        {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(resultCode == RESULT_OK)
            {
                //get user
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                //show email on toast
                Toast.makeText(this,"Welcome "+user.getDisplayName(),Toast.LENGTH_SHORT).show();
                //set button signout
                btn_sign_out.setEnabled(true);
                /* Name, email address, and profile photo Url
                    String personName = user.getDisplayName();
                    String personPhotoUrl = user.getPhotoUrl().toString();
                    String user_email = user.getEmail();
                    name.setText(personName);
                    email.setText(user_email);
                    Glide.with(getApplicationContext())
                            .load(personPhotoUrl)
                            .thumbnail(0.5f).circleCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(photoUrl);
                }*/

            }
            Toast.makeText(this,""+response.getError().getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
}
