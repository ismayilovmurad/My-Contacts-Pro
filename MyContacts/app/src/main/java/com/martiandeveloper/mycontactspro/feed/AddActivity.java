package com.martiandeveloper.mycontactspro.feed;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.martiandeveloper.mycontactspro.R;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

public class AddActivity extends AppCompatActivity implements View.OnClickListener, OnItemSelectedListener {

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference databaseReference;

    // UI Components
    private MaterialEditText fullNameET, phone_1ET,
            phone_2ET, emailET, instagramET,
            facebookET, twitterET, linkedinET,
            snapchatET, skypeET, websiteET;
    private Button saveBTN;
    private AVLoadingIndicatorView progress;
    private ImageView expandIV, backIV;
    private LinearLayout expandLL;
    private AppCompatSpinner categorySP;
    private CheckBox hideCB;
    private ConstraintLayout container, mainContainer;
    private ScrollView scrollView;

    // Variables
    private String mUid, category, fullName,
            phone_1, phone_2, email,
            instagram, facebook, twitter,
            linkedin, snapchat, skype, website, type, type2;
    private boolean expand;
    boolean hide;

    // Ads
    private AdView bannerAd;
    private AdRequest bannerAdRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        // Ads
        MobileAds.initialize(this, getResources().getString(R.string.banner_ad));
        bannerAd = findViewById(R.id.adView);
        bannerAdRequest = new AdRequest.Builder().build();
        bannerAd.loadAd(bannerAdRequest);

        // Firebase
        handleFirebase();

        // UI Components
        fullNameET = findViewById(R.id.fullNameET);
        phone_1ET = findViewById(R.id.phone_1ET);
        phone_2ET = findViewById(R.id.phone_2ET);
        emailET = findViewById(R.id.emailET);
        instagramET = findViewById(R.id.instagramET);
        facebookET = findViewById(R.id.facebookET);
        twitterET = findViewById(R.id.twitterET);
        linkedinET = findViewById(R.id.linkedinET);
        snapchatET = findViewById(R.id.snapchatET);
        skypeET = findViewById(R.id.skypeET);
        websiteET = findViewById(R.id.websiteET);
        saveBTN = findViewById(R.id.saveBTN);
        progress = findViewById(R.id.progress);
        expandIV = findViewById(R.id.expandIV);
        expandLL = findViewById(R.id.expandLL);
        categorySP = findViewById(R.id.categorySP);
        hideCB = findViewById(R.id.hideCB);
        backIV = findViewById(R.id.backIV);
        container = findViewById(R.id.container);
        scrollView = findViewById(R.id.scrollView);
        mainContainer = findViewById(R.id.mainContainer);

        // OnClickListeners
        saveBTN.setOnClickListener(this);
        expandIV.setOnClickListener(this);
        backIV.setOnClickListener(this);
        hideCB.setOnClickListener(this);

        // Methods
        progress.smoothToHide();
        collapse(expandLL);
        setSpinner();

        type = getIntent().getStringExtra("type");
        type2 = getIntent().getStringExtra("type2");
        Log.d("typee", type);

        if (!type.equals("new")) {

            getDataFromDatabase();

        }
    }

    private void getDataFromDatabase() {
        databaseReference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(mUid)
                .child("Contacts")
                .child(type);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                try {
                    String category = dataSnapshot.child("category").getValue().toString();

                    Log.d("Categoryy", category);

                    ArrayList<String> list = new ArrayList<>();
                    for (String s : getResources().getStringArray(R.array.category)) {
                        list.add(s);
                    }

                    categorySP.setSelection(list.indexOf(category));

                    if(dataSnapshot.child("hide").getValue().toString().equals("true")){
                        hideCB.setChecked(true);
                        handleCheckBox(hideCB);
                    }else{
                        hideCB.setChecked(false);
                        handleCheckBox(hideCB);
                    }

                    String email = dataSnapshot.child("email").getValue().toString();
                    String facebook = dataSnapshot.child("facebook").getValue().toString();
                    String fullName = dataSnapshot.child("fullName").getValue().toString();
                    String instagram = dataSnapshot.child("instagram").getValue().toString();
                    String linkedin = dataSnapshot.child("linkedin").getValue().toString();
                    String phone_1 = dataSnapshot.child("phone 1").getValue().toString();
                    String phone_2 = dataSnapshot.child("phone 2").getValue().toString();
                    String skype = dataSnapshot.child("skype").getValue().toString();
                    String snapchat = dataSnapshot.child("snapchat").getValue().toString();
                    String twitter = dataSnapshot.child("twitter").getValue().toString();
                    String website = dataSnapshot.child("website").getValue().toString();

                    if (!TextUtils.isEmpty(email)) {
                        emailET.setText(email);
                    }

                    if (!TextUtils.isEmpty(facebook)) {
                        facebookET.setText(facebook);
                    }

                    if (!TextUtils.isEmpty(fullName)) {
                        fullNameET.setText(fullName);
                    }

                    if (!TextUtils.isEmpty(instagram)) {
                        instagramET.setText(instagram);
                    }

                    if (!TextUtils.isEmpty(linkedin)) {
                        linkedinET.setText(linkedin);
                    }

                    if (!TextUtils.isEmpty(phone_1)) {
                        phone_1ET.setText(phone_1);
                    }

                    if (!TextUtils.isEmpty(phone_2)) {
                        phone_2ET.setText(phone_2);
                    }

                    if (!TextUtils.isEmpty(skype)) {
                        skypeET.setText(skype);
                    }

                    if (!TextUtils.isEmpty(snapchat)) {
                        snapchatET.setText(snapchat);
                    }

                    if (!TextUtils.isEmpty(twitter)) {
                        twitterET.setText(twitter);
                    }

                    if (!TextUtils.isEmpty(website)) {
                        websiteET.setText(website);
                    }
                } catch (Exception e) {
                    //
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                showSnackBar("Error", R.color.colorError);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveBTN:
                save();
                break;
            case R.id.expandIV:
                handleExpand();
                break;
            case R.id.backIV:
                back();
                break;
            case R.id.hideCB:
                handleCheckBox(hideCB);
                break;
        }
    }

    private void save() {
        showProgress();
        getTexts();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(phone_1)) {
            hideProgress();
            showSnackBar("Required fields cannot be empty!", R.color.colorError);
        } else {

            fullName = fullName.substring(0, 1).toUpperCase() + fullName.substring(1);

            if (FeedActivity.list2.size() != 0) {

                if (type.equals("new")) {
                    if (FeedActivity.list2.contains(fullName)) {
                        hideProgress();
                        showSnackBar(fullName + " is already in the contact!", R.color.colorError);
                    } else {
                        lastSave();
                    }
                } else {
                    lastSave();
                }
            } else {
                lastSave();
            }
        }
    }

    private void lastSave() {
        if (mUid != null) {

            if (type.equals("new")) {

                databaseReference = FirebaseDatabase
                        .getInstance()
                        .getReference()
                        .child(mUid)
                        .child("Contacts")
                        .child(fullName);

                databaseReference.child("category").setValue(category);
                databaseReference.child("hide").setValue(String.valueOf(hide));
                databaseReference.child("fullName").setValue(fullName);
                databaseReference.child("phone 1").setValue(phone_1);
                databaseReference.child("phone 2").setValue(phone_2);
                databaseReference.child("email").setValue(email);
                databaseReference.child("instagram").setValue(instagram);
                databaseReference.child("facebook").setValue(facebook);
                databaseReference.child("twitter").setValue(twitter);
                databaseReference.child("linkedin").setValue(linkedin);
                databaseReference.child("snapchat").setValue(snapchat);
                databaseReference.child("skype").setValue(skype);
                databaseReference.child("website").setValue(website).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            hideProgress();
                            resetEditTexts();
                            showSnackBar(fullName + " successfully added to the contact!", R.color.colorSuccess);
                        } else {
                            hideProgress();
                            showSnackBar("Error: " + task.getException().getLocalizedMessage(), R.color.colorError);
                        }
                    }
                });
            } else {
                if (fullName.equals(type)) {
                    databaseReference = FirebaseDatabase
                            .getInstance()
                            .getReference()
                            .child(mUid)
                            .child("Contacts")
                            .child(fullName);

                    databaseReference.child("category").setValue(category);
                    databaseReference.child("hide").setValue(String.valueOf(hide));
                    databaseReference.child("fullName").setValue(fullName);
                    databaseReference.child("phone 1").setValue(phone_1);
                    databaseReference.child("phone 2").setValue(phone_2);
                    databaseReference.child("email").setValue(email);
                    databaseReference.child("instagram").setValue(instagram);
                    databaseReference.child("facebook").setValue(facebook);
                    databaseReference.child("twitter").setValue(twitter);
                    databaseReference.child("linkedin").setValue(linkedin);
                    databaseReference.child("snapchat").setValue(snapchat);
                    databaseReference.child("skype").setValue(skype);
                    databaseReference.child("website").setValue(website).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                hideProgress();
                                showSnackBar(fullName + " successfully updated!", R.color.colorSuccess);
                            } else {
                                hideProgress();
                                showSnackBar("Error: " + task.getException().getLocalizedMessage(), R.color.colorError);
                            }
                        }
                    });
                } else {
                    databaseReference = FirebaseDatabase
                            .getInstance()
                            .getReference()
                            .child(mUid)
                            .child("Contacts")
                            .child(fullName);

                    databaseReference.child("category").setValue(category);
                    databaseReference.child("hide").setValue(String.valueOf(hide));
                    databaseReference.child("fullName").setValue(fullName);
                    databaseReference.child("phone 1").setValue(phone_1);
                    databaseReference.child("phone 2").setValue(phone_2);
                    databaseReference.child("email").setValue(email);
                    databaseReference.child("instagram").setValue(instagram);
                    databaseReference.child("facebook").setValue(facebook);
                    databaseReference.child("twitter").setValue(twitter);
                    databaseReference.child("linkedin").setValue(linkedin);
                    databaseReference.child("snapchat").setValue(snapchat);
                    databaseReference.child("skype").setValue(skype);
                    databaseReference.child("website").setValue(website).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                deleteContact(type);
                                hideProgress();
                                showSnackBar(fullName + " successfully updated!", R.color.colorSuccess);
                            } else {
                                hideProgress();
                                showSnackBar("Error: " + task.getException().getLocalizedMessage(), R.color.colorError);
                            }
                        }
                    });
                }
            }
        }
    }

    private void deleteContact(String changedContact) {
        databaseReference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(mUid)
                .child("Contacts")
                .child(changedContact);
        databaseReference.removeValue();
    }

    private void getTexts() {
        if (phone_2ET.getText() != null) {
            phone_2 = phone_2ET.getText().toString().trim();
        } else {
            phone_2 = "N/A";
        }

        if (emailET.getText() != null) {
            email = emailET.getText().toString().trim();
        } else {
            email = "N/A";
        }

        if (instagramET.getText() != null) {
            instagram = instagramET.getText().toString().trim();
        } else {
            instagram = "N/A";
        }

        if (facebookET.getText() != null) {
            facebook = facebookET.getText().toString().trim();
        } else {
            facebook = "N/A";
        }

        if (twitterET.getText() != null) {
            twitter = twitterET.getText().toString().trim();
        } else {
            twitter = "N/A";
        }

        if (linkedinET.getText() != null) {
            linkedin = linkedinET.getText().toString().trim();
        } else {
            linkedin = "N/A";
        }

        if (snapchatET.getText() != null) {
            snapchat = snapchatET.getText().toString().trim();
        } else {
            snapchat = "N/A";
        }

        if (skypeET.getText() != null) {
            skype = skypeET.getText().toString().trim();
        } else {
            skype = "N/A";
        }

        if (websiteET.getText() != null) {
            website = websiteET.getText().toString().trim();
        } else {
            website = "N/A";
        }

        fullName = fullNameET.getText().toString().trim();
        phone_1 = phone_1ET.getText().toString().trim();
    }

    private void showSnackBar(String message, int color) {
        Snackbar snackbar = Snackbar.make(mainContainer, message + "", Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();

        snackBarView.setBackgroundColor(ContextCompat.getColor(AddActivity.this, color));
        snackbar.show();
    }

    private void resetEditTexts() {
        fullNameET.setText("");
        phone_1ET.setText("");
        phone_2ET.setText("");
        emailET.setText("");
        instagramET.setText("");
        facebookET.setText("");
        twitterET.setText("");
        linkedinET.setText("");
        snapchatET.setText("");
        skypeET.setText("");
        websiteET.setText("");

        categorySP.setSelection(0);
        hideCB.setChecked(false);
        hide = false;
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        category = item;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void handleFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            mUid = mUser.getUid();
        }
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
        if(type2.equals("feed")) {
            Intent intent = new Intent(AddActivity.this, FeedActivity.class);
            startActivity(intent);
            AddActivity.this.finish();
        }
        else{
            Intent intent = new Intent(AddActivity.this, HiddenActivity.class);
            startActivity(intent);
            AddActivity.this.finish();
        }
    }

    private void setSpinner() {
        categorySP.setOnItemSelectedListener(this);
        categorySP.getBackground().setColorFilter(getResources().getColor(R.color.colorError), PorterDuff.Mode.SRC_ATOP);
        ArrayList<String> categories = new ArrayList<>();
        for (String category : getResources().getStringArray(R.array.category)) {
            categories.add(category);
        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        categorySP.setAdapter(dataAdapter);
    }

    private void handleCheckBox(View v) {
        if (((CheckBox) v).isChecked()) {
            hide = true;
        } else {
            hide = false;
        }
    }

    private void hideProgress() {
        progress.smoothToHide();
        container.setAlpha(1.0f);
        scrollView.setAlpha(1.0f);
        container.setClickable(true);
        scrollView.setClickable(true);
    }

    private void showProgress() {
        progress.smoothToShow();
        container.setAlpha(0.5f);
        scrollView.setAlpha(0.5f);
        container.setClickable(false);
        scrollView.setClickable(false);
    }
}
