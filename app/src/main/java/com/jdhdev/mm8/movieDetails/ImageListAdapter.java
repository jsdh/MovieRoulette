package com.jdhdev.mm8.movieDetails;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jdhdev.mm8.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ImageViewHolder> {
    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView picView;
        public ImageViewHolder(View root) {
            super(root);
            picView = (ImageView) root;
        }
    }

    private LayoutInflater inflater;
    private List<String> data;
    private Context context;

    public ImageListAdapter(Context ctx, List<String> images) {
        context = ctx;
        data = images;
        inflater = LayoutInflater.from(context);
    }

    public void setData(Set<String> images) {
        data = new ArrayList<>(images);
        notifyDataSetChanged();
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(inflater.inflate(R.layout.image_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        holder.picView.setImageDrawable(null);

        Picasso.with(context)
                .load(data.get(position))
                .into(holder.picView);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
