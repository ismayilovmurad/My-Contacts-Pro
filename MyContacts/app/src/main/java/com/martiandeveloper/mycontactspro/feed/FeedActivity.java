package com.martiandeveloper.mycontactspro.feed;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.martiandeveloper.mycontactspro.R;
import com.martiandeveloper.mycontactspro.auth.MainActivity;
import com.martiandeveloper.mycontactspro.sticky.MyAdapter;
import com.martiandeveloper.mycontactspro.sticky.RecyclerSectionItemDecoration;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FeedActivity extends AppCompatActivity implements View.OnClickListener {

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceDelete;

    // UI Components
    private ConstraintLayout mainContainer;
    private RecyclerView mRecyclerView;
    private LinearLayout emptyLL;
    private LinearLayout no_internetLL;
    private AVLoadingIndicatorView progress;
    private Button refreshBTN;

    // Variables
    private MyAdapter adapter;
    public static String mUid, title;
    public static ArrayList<String> list1;
    public static ArrayList<String> list2;
    public static ArrayList<String> list3;

    // Ads
    private AdView bannerAd;
    private InterstitialAd interstitialAd;
    private AdRequest bannerAdRequest, interstitialAdRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        setToolbar();
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

        // UI Components
        mainContainer = findViewById(R.id.mainContainer);
        mRecyclerView = findViewById(R.id.mRecyclerView);
        emptyLL = findViewById(R.id.emptyLL);
        no_internetLL = findViewById(R.id.no_internetLL);
        progress = findViewById(R.id.progress);
        refreshBTN = findViewById(R.id.refreshBTN);

        // OnClickListeners
        refreshBTN.setOnClickListener(this);

        // Methods
        progress.smoothToHide();
        checkInternetConnection();
    }

    @Override
    public void onBackPressed() {
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
        }
        super.onBackPressed();
    }

    private void getDataFromDatabase() {
        progress.smoothToShow();

        setAuth();

        if (mUid != null) {

            list1 = new ArrayList<>();

            databaseReference = FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child(mUid)
                    .child("Contacts");

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    showData(dataSnapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    progress.smoothToHide();
                }
            });

        } else {
            showSnackBar("Error while getting data!", R.color.colorError);
        }

    }

    private void setRecycler(DataSnapshot dataSnapshot) {
        String[] array = list1.toArray(new String[list1.size()]);
        Arrays.sort(array);
        list2 = new ArrayList<>(Arrays.asList(array));
        list3 = new ArrayList<>();

        for (String s : list2) {
            try {
                list3.add(dataSnapshot.child(s).child("category").getValue().toString());
            } catch (Exception e) {
                //
            }
        }

        if (list2.size() == 0) {
            emptyLL.setVisibility(View.VISIBLE);
        }

        adapter = new MyAdapter(FeedActivity.this, list2, list3, "feed");
        mRecyclerView.setAdapter(adapter);

        RecyclerSectionItemDecoration sectionItemDecoration =
                new RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen.header),
                        true,
                        getSectionCallback(list2));
        mRecyclerView.addItemDecoration(sectionItemDecoration);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(FeedActivity.this));

        progress.smoothToHide();
    }

    private void showSnackBar(String message, int color) {
        Snackbar snackbar = Snackbar.make(mainContainer, message + "", Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();

        snackBarView.setBackgroundColor(ContextCompat.getColor(FeedActivity.this, color));
        snackbar.show();
    }

    private void setAuth() {
        mAuth = FirebaseAuth.getInstance();
        if (mAuth != null) {
            mUser = mAuth.getCurrentUser();
            if (mUser != null) {
                mUid = mUser.getUid();
            }
        }
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

            getDataFromDatabase();
        } else {
            no_internetLL.setVisibility(View.VISIBLE);
        }
    }

    private void showData(DataSnapshot dataSnapshot) {

        for (DataSnapshot ds : dataSnapshot.getChildren()) {

            if (ds.getKey() != null) {
                if(ds.child("hide").getValue() != null){

                    if(ds.child("hide").getValue().equals("false")){
                        list1.add(ds.getKey());
                    }
                }
            }
        }
        setRecycler(dataSnapshot);
    }

    private RecyclerSectionItemDecoration.SectionCallback getSectionCallback(final List<String> people) {
        return new RecyclerSectionItemDecoration.SectionCallback() {
            @Override
            public boolean isSection(int position) {
                return position == 0
                        || people.get(position)
                        .charAt(0) != people.get(position - 1)
                        .charAt(0);
            }

            @Override
            public CharSequence getSectionHeader(int position) {
                return people.get(position)
                        .subSequence(0,
                                1);
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_contact:
                Intent add_intent = new Intent(FeedActivity.this, AddActivity.class);
                add_intent.putExtra("type", "new");
                add_intent.putExtra("type2", "feed");
                startActivity(add_intent);
                FeedActivity.this.finish();
                break;
            case R.id.refresh:
                Intent intent = getIntent();
                finish();
                startActivity(intent);
                break;
            case R.id.hidden:
                showHiddenActivity();
                break;
            case R.id.share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Check out this application on Google Play:\nhttps://play.google.com/store/apps/details?id=com.martiandeveloper.mycontactspro";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My Contacts");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                break;
            case R.id.rate:
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.martiandeveloper.mycontactspro");
                Intent puan_ver_intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(puan_ver_intent);
                break;
            case R.id.log_out:
                log_out();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void showHiddenActivity(){
        Intent intent = new Intent(FeedActivity.this, HiddenActivity.class);
        startActivity(intent);
        FeedActivity.this.finish();
    }

    private void log_out() {
        mAuth.signOut();
        Intent intent = new Intent(FeedActivity.this, MainActivity.class);
        startActivity(intent);
        FeedActivity.this.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.refreshBTN:
                no_internetLL.setVisibility(View.INVISIBLE);
                checkInternetConnection();
                break;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        TextView rowTV = v.findViewById(R.id.rowTV);
        title = rowTV.getText().toString();
        menu.add(0, v.getId(), 0, "Edit");
        menu.add(0, v.getId(), 0, "Share");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Edit") {
            Intent add_intent = new Intent(FeedActivity.this, AddActivity.class);
            add_intent.putExtra("type", title);
            add_intent.putExtra("type2", "feed");
            startActivity(add_intent);
            FeedActivity.this.finish();
        }
        else if (item.getTitle() == "Share") {

            progress.smoothToShow();

            databaseReference = FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child(mUid)
                    .child("Contacts")
                    .child(title);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    progress.smoothToHide();
                    if(dataSnapshot.child("phone 1").getValue() != null) {
                        shareContact(title, dataSnapshot.child("phone 1").getValue().toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    progress.smoothToHide();
                }
            });
        } else {
            return false;
        }
        return true;
    }

    private void shareContact(String name, String phone_1){

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = name + ": " + phone_1 + "\n\nDownload on Google Play:\nhttps://play.google.com/store/apps/details?id=com.martiandeveloper.mycontactspro";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My Contacts");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }
}