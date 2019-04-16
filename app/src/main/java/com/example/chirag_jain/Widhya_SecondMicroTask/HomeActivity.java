package com.example.chirag_jain.Widhya_SecondMicroTask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
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
import com.firebase.ui.auth.ErrorCodes;
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
    List<AuthUI.IdpConfig> providers;
    private Button btn_sign_out;
    ImageView photoUrl;
    TextView name, email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btn_sign_out = (Button) findViewById(R.id.btn_sign_out);
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
                        Toast.makeText(HomeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

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
                        .setLogo(R.drawable.logo)
                        .build(), MY_REQUEST_CODE
        );
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                //get user
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String provider_name = user.getProviders().get(0);
                Log.d("my tag", provider_name);
                //show email on toast
                Toast.makeText(this, "Welcome " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                //set button signout
                btn_sign_out.setEnabled(true);
                if (provider_name.equals("google.com")) {
                    getData();
                } else {
                    String personName = user.getDisplayName();
                    name.setText(personName);
                    Glide.with(this).load(R.drawable.logo).into(photoUrl);
                }
            } else
                {   setContentView(R.layout.background);
                    String s = String.valueOf(isNetworkAvailable(this));
                    if( s.equals("false")){
                        new AlertDialog.Builder(this)
                                .setMessage("please connect to the internet and try again")
                                .setCancelable(false)
                                .setPositiveButton("Restart", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent i = getBaseContext().getPackageManager()
                                                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(i);
                                    }
                                })
                                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        System.exit(0);
                                    }
                                })
                                .show();
                    }
                    else
                    {
                        finish();
                    }
                }

            }

        }

    private void getData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //Name, email address, and profile photo Url
        String personName = user.getDisplayName();
        String personPhotoUrl = user.getPhotoUrl().toString();
        name.setText(personName);
        Glide.with(getApplicationContext())
                .load(personPhotoUrl)
                .thumbnail(0.5f).circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(photoUrl);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.exit(0);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != connectivity) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }


}


