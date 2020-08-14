package com.martiandeveloper.mycontactspro.feed;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.IDNA;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.martiandeveloper.mycontactspro.R;
import com.martiandeveloper.mycontactspro.sticky.MyAdapter;
import com.martiandeveloper.mycontactspro.sticky.RecyclerSectionItemDecoration;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Arrays;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener {

    private String title;
    private AVLoadingIndicatorView progress;
    private ImageView expandIV, backIV, callIV, emailIV;
    private LinearLayout expandLL;
    private ConstraintLayout mainContainer;
    private Button deleteBTN;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    private boolean expand;
    String uid;
    String type;

    private TextView fullNameTV, phone_1TV, categoryTV, phone_2TV,
            emailTV, instagramTV, facebookTV, twitterTV,
            linkedinTV, snapchatTV, skypeTV, websiteTV;

    // Ads
    private AdView bannerAd;
    private AdRequest bannerAdRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        // Ads
        MobileAds.initialize(this, getResources().getString(R.string.banner_ad));
        bannerAd = findViewById(R.id.adView);
        bannerAdRequest = new AdRequest.Builder().build();
        bannerAd.loadAd(bannerAdRequest);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        fullNameTV = findViewById(R.id.fullNameTV);
        phone_1TV = findViewById(R.id.phone_1TV);
        categoryTV = findViewById(R.id.categoryTV);
        phone_2TV = findViewById(R.id.phone_2TV);
        emailTV = findViewById(R.id.emailTV);
        instagramTV = findViewById(R.id.instagramTV);
        facebookTV = findViewById(R.id.facebookTV);
        twitterTV = findViewById(R.id.twitterTV);
        linkedinTV = findViewById(R.id.linkedinTV);
        snapchatTV = findViewById(R.id.snapchatTV);
        skypeTV = findViewById(R.id.skypeTV);
        websiteTV = findViewById(R.id.websiteTV);
        expandIV = findViewById(R.id.expandIV);
        expandLL = findViewById(R.id.expandLL);
        backIV = findViewById(R.id.backIV);
        deleteBTN = findViewById(R.id.deleteBTN);
        callIV = findViewById(R.id.callIV);
        emailIV = findViewById(R.id.emailIV);

        mainContainer = findViewById(R.id.mainContainer);

        title = getIntent().getStringExtra("title");
        type = getIntent().getStringExtra("type");
        progress = findViewById(R.id.progress);

        expandIV.setOnClickListener(this);
        backIV.setOnClickListener(this);
        deleteBTN.setOnClickListener(this);
        callIV.setOnClickListener(this);
        emailIV.setOnClickListener(this);

        if (title != null) {

            try {
                databaseReference = FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child(uid)
                        .child("Contacts")
                        .child(title);

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        try {

                            fullNameTV.setText(dataSnapshot.child("fullName").getValue().toString());
                            phone_1TV.setText(dataSnapshot.child("phone 1").getValue().toString());
                            phone_2TV.setText(dataSnapshot.child("phone 2").getValue().toString());
                            emailTV.setText(dataSnapshot.child("email").getValue().toString());
                            instagramTV.setText(dataSnapshot.child("instagram").getValue().toString());
                            facebookTV.setText(dataSnapshot.child("facebook").getValue().toString());
                            twitterTV.setText(dataSnapshot.child("twitter").getValue().toString());
                            linkedinTV.setText(dataSnapshot.child("linkedin").getValue().toString());
                            snapchatTV.setText(dataSnapshot.child("snapchat").getValue().toString());
                            skypeTV.setText(dataSnapshot.child("skype").getValue().toString());
                            websiteTV.setText(dataSnapshot.child("website").getValue().toString());
                            categoryTV.setText(dataSnapshot.child("category").getValue().toString());


                        } catch (Exception e) {
                            //
                        }
                        progress.smoothToHide();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        progress.smoothToHide();
                        showSnackBar("Error: while getting data!", R.color.colorError);
                    }
                });
            } catch (Exception e) {
                //
            }
        }

        collapse(expandLL);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.expandIV:
                handleExpand();
                break;
            case R.id.backIV:
                back();
                break;
            case R.id.deleteBTN:
                delete();
                break;
            case R.id.callIV:
                call();
                break;
            case R.id.emailIV:
                email();
                break;
        }
    }

    private void call() {

        if (phone_1TV.getText().toString() != null) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(InfoActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 4);

            } else {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone_1TV.getText().toString()));
                startActivity(intent);
            }
        }
    }

    private void email() {

        if(emailTV.getText().toString() != null) {
            Uri uri = Uri.parse("mailto:" + emailTV.getText().toString())
                    .buildUpon()
                    .appendQueryParameter("subject", "Subject")
                    .appendQueryParameter("body", "body")
                    .build();

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);
            startActivity(Intent.createChooser(emailIntent, "Send via"));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 4) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone_1TV.getText().toString()));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(InfoActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 4);

                }
                startActivity(intent);
            }
        }
    }

    private void delete(){
        databaseReference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(uid)
                .child("Contacts")
                .child(title);

        onBackPressed();
        databaseReference.removeValue();
    }

    private void showSnackBar(String message, int color) {
        Snackbar snackbar = Snackbar.make(mainContainer, message + "", Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();

        snackBarView.setBackgroundColor(ContextCompat.getColor(InfoActivity.this, color));
        snackbar.show();
    }

    @Override
    public void onBackPressed() {
        back();
    }

    public static void expand(final View v) {
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? WindowManager.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Expansion speed of 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Collapse speed of 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    private void handleExpand() {
        if (expand) {
            expand = false;
            expandIV.animate().rotation(0).setDuration(500).start();
            collapse(expandLL);
        } else {
            expand = true;
            expandIV.animate().rotation(180).setDuration(500).start();
            expand(expandLL);
        }
    }

    private void back() {
        if(type.equals("feed")) {
            Intent intent = new Intent(InfoActivity.this, FeedActivity.class);
            startActivity(intent);
            InfoActivity.this.finish();
        }
        else{
            Intent intent = new Intent(InfoActivity.this, HiddenActivity.class);
            startActivity(intent);
            InfoActivity.this.finish();
        }
    }
}
