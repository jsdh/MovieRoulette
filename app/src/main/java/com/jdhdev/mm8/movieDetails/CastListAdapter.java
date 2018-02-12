package com.jdhdev.mm8.movieDetails;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jdhdev.mm8.R;
import com.jdhdev.mm8.data.source.remote.imdbapi.Model.CastMemeber;
import com.squareup.picasso.Picasso;

import java.util.List;


public class CastListAdapter extends RecyclerView.Adapter<CastListAdapter.BadgeViewHolder> {
    private List<CastMemeber> data;
    private LayoutInflater inflater;
    private Context context;
    private Drawable genericAvatar;

    static class BadgeViewHolder extends RecyclerView.ViewHolder {
        ImageView photo;
        TextView actor;
        TextView character;

        public BadgeViewHolder(View v) {
            super(v);
            photo = v.findViewById(R.id.photo);
            actor = v.findViewById(R.id.actor_name);
            character = v.findViewById(R.id.character_name);
        }
    }

    public CastListAdapter(Context ctx, List<CastMemeber> data) {
        this.data = data;
        inflater = LayoutInflater.from(ctx);
        context = ctx;
        genericAvatar = ctx.getDrawable(R.drawable.ic_generic_person);
        genericAvatar.setColorFilter(0x75ffffff, PorterDuff.Mode.SRC_IN);
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).actor.hashCode();
    }

    public void setData(List<CastMemeber> cdata) {
        data = cdata;
        notifyDataSetChanged();
    }

    @Override
    public BadgeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BadgeViewHolder(inflater.inflate(R.layout.cast_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(BadgeViewHolder holder, int position) {
        CastMemeber person = data.get(position);
        holder.actor.setText(person.actor);
        holder.character.setText(person.character);

        holder.photo.setImageDrawable(genericAvatar);
        if (!TextUtils.isEmpty(person.imageUrl)) {
            Picasso.with(context)
                    .load(person.imageUrl)
                    .into(holder.photo);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
