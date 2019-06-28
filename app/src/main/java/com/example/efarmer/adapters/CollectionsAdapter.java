package com.example.efarmer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.efarmer.R;
import com.example.efarmer.models.CollectionPOJO;

import java.util.ArrayList;



/**
 * Created by Hosanna on 02/11/2016.
 */
public class CollectionsAdapter extends BaseAdapter {
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;
    ArrayList<CollectionPOJO> myList = new ArrayList<CollectionPOJO>();
    LayoutInflater inflater;
    Context context;

    public CollectionsAdapter(Context context, ArrayList<CollectionPOJO> myList) {
        this.myList = myList;
        this.context = context;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public CollectionPOJO getItem(int position) {
        return myList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;
        View view;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.mycollections_list_item, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        CollectionPOJO currentListData = getItem(position);
        mViewHolder.tvid.setText(currentListData.getId());
        mViewHolder.tvTitle.setText(currentListData.getTitle());
        mViewHolder.tvDuration.setText(currentListData.getDuration());
        mViewHolder.tvUrl.setText(currentListData.getUrl());


        if(currentListData.getTitle() != null)
        {
            //Log.e("TaxPayerAdapter", "No image");
            char myChar = currentListData.getTitle().charAt(0);
            String str = Character.toString(myChar).toUpperCase();
            int tColor = mColorGenerator.getColor(str);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(str, tColor);
            mViewHolder.ivIcon.setImageDrawable(drawable);
        }

        return convertView;
    }

    private class MyViewHolder {


        TextView tvTitle, tvDuration, tvid, tvUrl;
        ImageView ivIcon;

        public MyViewHolder(View item) {
            tvid = (TextView)item.findViewById(R.id.tv_id);
            tvTitle = (TextView)item.findViewById(R.id.tv_title);
            tvUrl = (TextView)item.findViewById(R.id.tv_url);
            ivIcon = (ImageView) item.findViewById(R.id.ivIcon);
            tvDuration = (TextView)item.findViewById(R.id.tv_duration);
        }
    }

}
