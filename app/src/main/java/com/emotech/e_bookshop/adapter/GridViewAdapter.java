package com.emotech.e_bookshop.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.emotech.e_bookshop.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by muham_000 on 19/09/2017.
 */

public class GridViewAdapter extends ArrayAdapter<ImageItem> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<ImageItem> data = new ArrayList<ImageItem>();

    public GridViewAdapter(Context context, int layoutResourceId, ArrayList<ImageItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.namaBuku = (TextView) row.findViewById(R.id.namaBuku);
            holder.image = (ImageView) row.findViewById(R.id.image);
            holder.hargaBuku = (TextView) row.findViewById(R.id.hargaBuku);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        ImageItem item = data.get(position);
        holder.namaBuku.setText(item.getNamaBuku());
        //holder.image.setImageBitmap(item.getImage());
        Picasso.with(context).load(item.getImage()).into(holder.image);
        holder.hargaBuku.setText(item.getHargaBuku());
        return row;
    }

    static class ViewHolder {
        TextView namaBuku;
        TextView hargaBuku;
        ImageView image;
    }

}
