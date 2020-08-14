package com.martiandeveloper.mycontactspro.auth;

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
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wang.avi.AVLoadingIndicatorView;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    // UI Components
    private MaterialEditText emailET, passwordET, confirm_passwordET;
    private Button sign_upBTN;
    private TextView log_inTV;
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
        setContentView(R.layout.activity_signup);
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
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // UI Components
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        confirm_passwordET = findViewById(R.id.confirm_passwordET);
        sign_upBTN = findViewById(R.id.sign_upBTN);
        log_inTV = findViewById(R.id.log_inTV);
        progress = findViewById(R.id.progress);
        containerLL = findViewById(R.id.containerLL);
        frameLayout = findViewById(R.id.frameLayout);
        mainContainer = findViewById(R.id.mainContainer);

        // OnClickListeners
        sign_upBTN.setOnClickListener(this);
        log_inTV.setOnClickListener(this);

        // Methods
        progress.smoothToHide();
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
            case R.id.sign_upBTN:
                signUp();
                break;
            case R.id.log_inTV:
                goToActivity(MainActivity.class);
                break;
        }
    }

    private void goToActivity(Class activity) {
        Intent intent = new Intent(SignupActivity.this, activity);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        SignupActivity.this.finish();
    }

    private void signUp() {
        showProgress();

        try {
            String email = emailET.getText().toString().trim();
            String password = passwordET.getText().toString().trim();
            String confirm_password = confirm_passwordET.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirm_password)) {
                hideProgress();
                showSnackBar(getResources().getString(R.string.fill), R.color.colorError);
            } else if (!password.matches(confirm_password)) {
                hideProgress();
                showSnackBar(getResources().getString(R.string.not_same), R.color.colorError);
            } else {
                mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        mUser = authResult.getUser();

                        if (mUser != null) {
                            // Email verification
                            mUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        formatEditTexts();
                                        hideProgress();
                                        showSnackBar(getResources().getString(R.string.verify), R.color.colorSuccess);
                                    } else {
                                        hideProgress();
                                        showSnackBar("Error: " + task.getException().getLocalizedMessage(), R.color.colorError);
                                    }

                                }
                            });
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

        snackBarView.setBackgroundColor(ContextCompat.getColor(SignupActivity.this, color));
        snackbar.show();
    }

    private void formatEditTexts() {
        emailET.setText("");
        passwordET.setText("");
        confirm_passwordET.setText("");
    }

    private void showProgress() {
        progress.smoothToShow();
        containerLL.setAlpha(0.5f);
        frameLayout.setAlpha(0.5f);
        containerLL.setClickable(false);
        frameLayout.setClickable(false);
    }

    private void hideProgress() {
        progress.smoothToHide();
        containerLL.setAlpha(1.0f);
        frameLayout.setAlpha(1.0f);
        containerLL.setClickable(true);
        frameLayout.setClickable(true);
    }
}
