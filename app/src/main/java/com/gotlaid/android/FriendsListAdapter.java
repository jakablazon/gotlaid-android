package com.gotlaid.android;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gotlaid.android.utils.FileUtils;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by zigapk on 5.7.2016.
 */
public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.ViewHolder> {

    private ArrayList<Friend> friends = new ArrayList<>();
    private View[] views; //holder views, used to make them (un)selected
    private TextView[] textViews; //text views, used to make them (un)selected

    public FriendsListAdapter(ArrayList<Friend> friends) {
        this.friends = friends;
        views = new View[friends.size()];
        textViews = new TextView[friends.size()];
    }

    public static FriendsListAdapter friendsListAdapterWithMergeUnselected(ArrayList<Friend> friends,
                                                                           Context context) {
        UnselectedFriendsHolder unselectedFriendsHolder = getSavedUnselectedIdsHolder(context);
        ArrayList<Friend> merged = mergeFriendsWithUnselected(friends, unselectedFriendsHolder);
        return new FriendsListAdapter(merged);
    }

    public FriendsListAdapter() {
        views = new View[friends.size()];
        textViews = new TextView[friends.size()];
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView friendDisplayNameTv;

        public ViewHolder(View v) {
            super(v);
            view = v;
            friendDisplayNameTv = (TextView) v.findViewById(R.id.friendDisplayNameTv);
        }

    }

    public ArrayList<Friend> getFriends() {
        return friends;
    }

    public ArrayList<Friend> getSelectedFriends() {
        ArrayList<Friend> result = new ArrayList<>();
        for (Friend friend : friends) {
            if (friend.selected) result.add(friend);
        }
        return result;
    }

    public Friend getFriendAtPosition(int position) {
        return friends.get(position);
    }

    public void saveUnselectedIds(Context context) {
        try {
            ArrayList<Friend> unselectedFriends = new ArrayList<>();
            for (Friend friend : friends) {
                if (!friend.selected) unselectedFriends.add(friend);
            }
            UnselectedFriendsHolder holder = new UnselectedFriendsHolder(unselectedFriends);
            String json = new Gson().toJson(holder);
            FileUtils.writeToFile("unselected_friends.json", json, context);
        }catch (Exception e){}
    }

    private static UnselectedFriendsHolder getSavedUnselectedIdsHolder(Context context) {
        String json = FileUtils.readFile("unselected_friends.json", context);
        try {
            return new Gson().fromJson(json, UnselectedFriendsHolder.class);
        } catch (Exception e) {
            return new UnselectedFriendsHolder();
        }
    }

    public static ArrayList<Friend> mergeFriendsWithUnselected(ArrayList<Friend> friends,
                                                               UnselectedFriendsHolder unselectedFriends) {
        if (unselectedFriends != null &&
                unselectedFriends.friendIdsSet != null && unselectedFriends.friendIdsSet.size() > 0) {
            for (Friend friend : friends) {
                if (unselectedFriends.friendIdsSet.contains(friend.uuid)) friend.selected = false;
            }
        }
        return friends;
    }

    public void selectAll(){
        for (int i = 0; i < friends.size(); i++) {
            //in case not all of them are initialized (common with recycle view and huge data sets)
            try {
                setSelected(i);
            }catch (Exception e){}
        }
    }

    public void unselectAll(){
        for (int i = 0; i < friends.size(); i++) {
            //in case not all of them are initialized (common with recycle view and huge data sets)
            try {
                setUnselected(i);
            }catch (Exception e){}
        }
    }

    private void toggle(int position){
        if(friends.get(position).selected){
            setUnselected(position);
        }else {
            setSelected(position);
        }
    }

    private void setUnselected(int position){
        friends.get(position).selected = false;
        colorAccordingToSelection(position);
    }

    private void setSelected(int position){
        friends.get(position).selected = true;
        colorAccordingToSelection(position);
    }

    private void colorAccordingToSelection(int position){
        if (friends.get(position).selected){
            views[position].setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            textViews[position].setTextColor(Color.BLACK);
            textViews[position].setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        }else {
            views[position].setBackgroundDrawable(new ColorDrawable(Color.BLACK));
            textViews[position].setTextColor(Color.WHITE);
            textViews[position].setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        }
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    @Override
    public FriendsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.friendDisplayNameTv.setTypeface(MainActivity.workSansExtraBoldTypeface);
        holder.friendDisplayNameTv.setText(friends.get(position).displayName);
        views[position] = holder.view;
        textViews[position] = holder.friendDisplayNameTv;
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //toggle selected
                toggle(position);
            }
        });
        colorAccordingToSelection(position);
    }
}

class Friend {
    String displayName;
    String uuid;
    boolean selected = true;

    public Friend() {
    }

    public Friend(String displayName, String uuid) {
        this.displayName = displayName;
        this.uuid = uuid;
    }
}

//temporary class to save friends lists to json
class UnselectedFriendsHolder {
    /* HashSet is implemented as binary search tree and is therefore very vast to check
     * whether a element is contained or not
     * */
    public HashSet<String> friendIdsSet = new HashSet<>();

    public UnselectedFriendsHolder() {
    }

    public UnselectedFriendsHolder(Friend[] selectedFriends) {
        for (Friend friend : selectedFriends) {
            if (friend.selected) friendIdsSet.add(friend.uuid);
        }
    }

    public UnselectedFriendsHolder(ArrayList<Friend> selectedFriends) {
        for (Friend friend : selectedFriends) {
            if (friend.selected) friendIdsSet.add(friend.uuid);
        }
    }
}