package com.martiandeveloper.mycontactspro.auth;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.martiandeveloper.mycontactspro.R;
import com.martiandeveloper.mycontactspro.feed.FeedActivity;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wang.avi.AVLoadingIndicatorView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    // UI Components
    private MaterialEditText emailET, passwordET;
    private Button log_inBTN;
    private TextView sign_upTV, forgot_passwordTV;
    private AVLoadingIndicatorView progress;
    private LinearLayout containerLL;
    private FrameLayout frameLayout;
    private ConstraintLayout mainContainer;

    // Ads
    private AdView bannerAd;
    private InterstitialAd interstitialAd;
    private AdRequest bannerAdRequest, interstitialAdRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Ads
        MobileAds.initialize(this, getResources().getString(R.string.banner_ad));
        bannerAd = findViewById(R.id.adView);
        bannerAdRequest = new AdRequest.Builder().build();
        bannerAd.loadAd(bannerAdRequest);

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad));
        interstitialAdRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(interstitialAdRequest);
        interstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdClosed() {
                super.onAdClosed();

                onBackPressed();

                interstitialAd.loadAd(interstitialAdRequest);
            }
        });

        // Firebase
        handleFirebase();

        // UI Components
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        log_inBTN = findViewById(R.id.log_inBTN);
        sign_upTV = findViewById(R.id.sign_upTV);
        forgot_passwordTV = findViewById(R.id.forgot_passwordTV);
        progress = findViewById(R.id.progress);
        containerLL = findViewById(R.id.containerLL);
        frameLayout = findViewById(R.id.frameLayout);
        mainContainer = findViewById(R.id.mainContainer);

        // OnClickListeners
        log_inBTN.setOnClickListener(this);
        sign_upTV.setOnClickListener(this);
        forgot_passwordTV.setOnClickListener(this);
        //

        // Methods
        progress.smoothToHide();
    }

    private void handleFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (mUser != null && mUser.isEmailVerified()) {
                    goToActivityWithExtra(FeedActivity.class);
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.log_inBTN:
                logIn();
                break;
            case R.id.sign_upTV:
                goToActivity(SignupActivity.class);
                break;
            case R.id.forgot_passwordTV:
                showPasswordDialog();
                break;
        }
    }

    private void goToActivity(Class activity) {
        Intent intent = new Intent(MainActivity.this, activity);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        MainActivity.this.finish();
    }

    private void goToActivityWithExtra(Class activity){
        Intent intent = new Intent(MainActivity.this, activity);
        intent.putExtra("type", "nothing");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        MainActivity.this.finish();
    }

    private void logIn() {
        showProgress();

        try {
            String email = emailET.getText().toString().trim();
            String password = passwordET.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                hideProgress();
                showSnackBar(getResources().getString(R.string.fill), R.color.colorError);
            } else {
                mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        mUser = authResult.getUser();

                        if (mUser.isEmailVerified()) {
                            hideProgress();
                            goToActivityWithExtra(FeedActivity.class);
                        } else {
                            hideProgress();
                            showSnackBar(getResources().getString(R.string.verif2), R.color.colorError);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideProgress();
                        showSnackBar("Error: " + e.getLocalizedMessage(), R.color.colorError);
                    }
                });
            }
        } catch (Exception e) {
            hideProgress();
            showSnackBar("Error: " + e.getLocalizedMessage(), R.color.colorError);
        }
    }

    private void showSnackBar(String message, int color) {
        Snackbar snackbar = Snackbar.make(mainContainer, message + "", Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();

        snackBarView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, color));
        snackbar.show();
    }

    private void showPasswordDialog() {
        final AlertDialog dialog_forgot_password = new AlertDialog.Builder(MainActivity.this).create();
        View view = getLayoutInflater().inflate(R.layout.dialog_forgot_password, null);

        final LinearLayout innerContainer = view.findViewById(R.id.innerContainer);
        final MaterialEditText emailET = view.findViewById(R.id.emailET);
        final Button sendBTN = view.findViewById(R.id.sendBTN);
        final Button cancelBTN = view.findViewById(R.id.cancelBTN);
        final AVLoadingIndicatorView dialog_progress = view.findViewById(R.id.progress);

        dialog_progress.smoothToHide();

        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_forgot_password.dismiss();
            }
        });

        sendBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressForDialog(innerContainer, dialog_progress);

                try {
                    String email = emailET.getText().toString().trim();

                    if (TextUtils.isEmpty(email)) {
                        hideProgressForDialog(innerContainer, dialog_progress);
                        showErrorForDialog(getResources().getString(R.string.enter_email), emailET);
                    } else {
                        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    hideProgressForDialog(innerContainer, dialog_progress);
                                    dialog_forgot_password.dismiss();
                                    showSnackBar(getResources().getString(R.string.reset), R.color.colorSuccess);
                                } else {
                                    hideProgressForDialog(innerContainer, dialog_progress);
                                    showErrorForDialog("Error: " + task.getException().getLocalizedMessage(), emailET);
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    hideProgressForDialog(innerContainer, dialog_progress);
                    showErrorForDialog("Error: " + e.getLocalizedMessage(), emailET);
                }
            }
        });

        dialog_forgot_password.setView(view);
        dialog_forgot_password.show();
    }

    private void showErrorForDialog(String message, MaterialEditText emailET) {
        emailET.setError(message);
        emailET.setUnderlineColor(R.color.colorError);
    }

    private void hideProgressForDialog(LinearLayout innerContainer, AVLoadingIndicatorView dialog_progress) {
        dialog_progress.smoothToHide();
        innerContainer.setAlpha(1.0f);
    }

    private void showProgressForDialog(LinearLayout innerContainer, AVLoadingIndicatorView dialog_progress) {
        dialog_progress.smoothToShow();
        innerContainer.setAlpha(0.5f);
    }

    private void hideProgress() {
        progress.smoothToHide();
        containerLL.setAlpha(1.0f);
        frameLayout.setAlpha(1.0f);
        containerLL.setClickable(true);
        frameLayout.setClickable(true);
    }

    private void showProgress() {
        progress.smoothToShow();
        containerLL.setAlpha(0.5f);
        frameLayout.setAlpha(0.5f);
        containerLL.setClickable(false);
        frameLayout.setClickable(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mUser != null && mUser.isEmailVerified()) {
            mAuth.addAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuth != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
}
