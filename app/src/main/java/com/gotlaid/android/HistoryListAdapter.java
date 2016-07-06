package com.gotlaid.android;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gotlaid.android.data.Action;

import java.util.ArrayList;

/**
 * Created by zigapk on 6.7.2016.
 */
public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.ViewHolder>{
    private ArrayList<Action> actions = new ArrayList<>();

    public HistoryListAdapter(ArrayList<Action> actions) {
        this.actions = actions;
    }

    public HistoryListAdapter() {
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView textView;

        public ViewHolder(View v) {
            super(v);
            view = v;
            textView = (TextView) v.findViewById(R.id.historyItemTv);
        }

    }

    public void addItem(int position, Action action) {
        actions.add(position, action);
        notifyItemInserted(position);
    }

    public void addItem(Action action) {
        addItem(actions.size(), action);
    }

    public ArrayList<Action> getActions() {
        return actions;
    }

    public void removeItem(int position) {
        actions.remove(position);
        notifyItemRemoved(position);
    }

    public Action getActionAtPosition(int position) {
        return actions.get(position);
    }

    @Override
    public int getItemCount() {
        return actions.size();
    }

    @Override
    public HistoryListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.textView.setTypeface(MainActivity.workSansExtraBoldTypeface);
        holder.textView.setText(actions.get(position).getDisplayString());
    }
}
