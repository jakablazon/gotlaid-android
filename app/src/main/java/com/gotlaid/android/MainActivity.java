package com.gotlaid.android;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private static Button gotLaidButton;
    private static TextView letYourFriendsKnowTv;
    private static Typeface workSansExtraBoldTypeface;
    private static AccessToken fbAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        //check for firebase and fb user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        fbAccessToken = AccessToken.getCurrentAccessToken();
        if (user != null && fbAccessToken != null) {
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
            workSansExtraBoldTypeface = Typeface.createFromAsset(getAssets(), "fonts/WorkSans-ExtraBold.ttf");

            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);
            mViewPager.setCurrentItem(1);

            fillFbFriendList();
        } else {
            //launch login activity
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
    }

    private void fillFbFriendList(){
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                            /* handle the result */
                    }
                }
        ).executeAsync();
    }


    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {}

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            int sectionNmuber = getArguments().getInt(ARG_SECTION_NUMBER);
            switch (sectionNmuber){
                case 0:
                    return inflater.inflate(R.layout.fragment_friends_list, container, false);
                case 1:
                    View rootView = inflater.inflate(R.layout.fragment_main, container, false);
                    gotLaidButton = (Button) rootView.findViewById(R.id.gotLaidButton);
                    gotLaidButton.setTypeface(workSansExtraBoldTypeface);
                    letYourFriendsKnowTv =  (TextView) rootView.findViewById(R.id.letYourFriendsKnowTv);
                    letYourFriendsKnowTv.setTypeface(workSansExtraBoldTypeface);
                    letYourFriendsKnowTv.setText(
                            getResources().getQuantityString(R.plurals.let_friends_know, 345, 345));
                    return rootView;
                default:
                    return inflater.inflate(R.layout.fragment_history, container, false);
            }

        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
