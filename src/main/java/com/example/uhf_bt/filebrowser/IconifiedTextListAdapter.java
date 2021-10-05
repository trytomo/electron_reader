package com.example.uhf_bt.filebrowser;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class IconifiedTextListAdapter extends BaseAdapter {
    private Context mContext = null;
    private List<com.example.uhf_bt.filebrowser.IconifiedText> mItems = new ArrayList<com.example.uhf_bt.filebrowser.IconifiedText>();

    public IconifiedTextListAdapter(Context context) {
        mContext = context;
    }

    public void addItem(com.example.uhf_bt.filebrowser.IconifiedText it) {
        mItems.add(it);
    }

    public void setListItems(List<com.example.uhf_bt.filebrowser.IconifiedText> lit) {
        mItems = lit;
    }

    public int getCount() {
        return mItems.size();
    }

    public Object getItem(int position) {
        return mItems.get(position);
    }

    public boolean areAllItemsSelectable() {
        return false;
    }

    public boolean isSelectable(int position) {
        return mItems.get(position).isSelectable();
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        com.example.uhf_bt.filebrowser.IconifiedTextView btv;
        if (convertView == null) {
            btv = new com.example.uhf_bt.filebrowser.IconifiedTextView(mContext, mItems.get(position));
        } else {
            btv = (com.example.uhf_bt.filebrowser.IconifiedTextView) convertView;
            btv.setText(mItems.get(position).getText());
            btv.setIcon(mItems.get(position).getIcon());
        }
        return btv;
    }
}
