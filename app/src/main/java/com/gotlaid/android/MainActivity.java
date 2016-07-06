package com.gotlaid.android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gotlaid.android.data.Action;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    public static Context context;
    private ViewPager mViewPager;
    private static RecyclerView mFriendsRecyclerView;
    private static FriendsListAdapter mFriendsAdapter;
    private static RecyclerView.LayoutManager mLayoutManager;

    private static Button gotLaidButton;
    private static TextView letYourFriendsKnowTv;
    public static Typeface workSansExtraBoldTypeface;
    private static AccessToken fbAccessToken;
    private static String fbUserId;
    private static String fbUserDisplayName;
    private static String fbUserFirstName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

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
            mViewPager.setOffscreenPageLimit(2);
            mViewPager.setCurrentItem(1);

            fbUserId = Profile.getCurrentProfile().getId();
            fbUserDisplayName = Profile.getCurrentProfile().getName();
            fbUserFirstName = Profile.getCurrentProfile().getFirstName();
            fillFbFriendList();
        } else {
            //launch login activity
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
    }

    //save selected friends when user leaves
    @Override
    protected void onPause(){
        super.onPause();
        try {
            mFriendsAdapter.saveUnselectedIds(getApplicationContext());
        }catch (Exception e){}
    }

    public void unselectAll(View v){
        mFriendsAdapter.unselectAll();
    }

    public void selectAll(View v){
        mFriendsAdapter.selectAll();
    }

    public void uploadAction(View v){
        ArrayList<Friend> selectedFriends = mFriendsAdapter.getSelectedFriends();
        Action action = new Action(fbUserDisplayName, fbUserFirstName, fbUserId);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getInstance().getReference();

        for (Friend friend : selectedFriends){
            String key = myRef.child(friend.uuid).push().getKey();
            myRef.child(friend.uuid).child(key).setValue(action);
        }
    }

    public void fillFbFriendList() {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            JSONArray data = response.getJSONObject().getJSONArray("data");
                            ArrayList<Friend> friends = new ArrayList<>();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                friends.add(new Friend(obj.getString("name"), obj.getString("id")));
                            }

                            //initialize FriendsListAdapter
                            mFriendsRecyclerView =
                                    (RecyclerView) findViewById(R.id.friendsListRecyclerView);
                            mLayoutManager = new LinearLayoutManager(MainActivity.this);
                            mFriendsRecyclerView.setLayoutManager(mLayoutManager);
                            mFriendsAdapter = FriendsListAdapter.
                                    friendsListAdapterWithMergeUnselected(friends, getApplicationContext());
                            mFriendsRecyclerView.setAdapter(mFriendsAdapter);

                            findViewById(R.id.friendsListProgresBar).setVisibility(View.GONE);
                            findViewById(R.id.friendsListRecyclerViewHolder).setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }


    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

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
            switch (sectionNmuber) {
                case 0:
                    View rootFriendsView =
                            inflater.inflate(R.layout.fragment_friends_list, container, false);
                    ((TextView) rootFriendsView.findViewById(R.id.unselectFriendsAllButton)).
                            setTypeface(workSansExtraBoldTypeface);
                    ((TextView) rootFriendsView.findViewById(R.id.selectFriendsAllButton)).
                            setTypeface(workSansExtraBoldTypeface);
                    return rootFriendsView;
                case 1:
                    View rootView = inflater.inflate(R.layout.fragment_main, container, false);
                    gotLaidButton = (Button) rootView.findViewById(R.id.gotLaidButton);
                    gotLaidButton.setTypeface(workSansExtraBoldTypeface);
                    letYourFriendsKnowTv = (TextView) rootView.findViewById(R.id.letYourFriendsKnowTv);
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
