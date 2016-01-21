package com.almareng.earthquakemonitor.list;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.almareng.earthquakemonitor.R;

public class EarthquakeAdapter extends RecyclerView.Adapter<EarthquakeAdapter.EarthquakeViewHolder> {
    // Database Columns
    public static final int COL_EQ_MAGNITUDE = 1;
    public static final int COL_EQ_PLACE= 2;
    public static final int COL_EQ_TIME_DATE = 3;
    public static final int COL_EQ_LATITUDE = 4;
    public static final int COL_EQ_LONGITUDE = 5;
    public static final int COL_EQ_DEPTH = 6;
    public static final int COL_EQ_DISTANCE = 7;

    private final Context mContext;
    private final OnItemClickListener mOnItemClickListener;
    private final View mEmptyView;
    private final ItemChoiceManager mItemChoiceManager;
    private Cursor mCursor;
    private boolean mUseTodayLayout;

    public interface OnItemClickListener {
        void onClick(final Earthquake earthquake, final View view);
    }

    public EarthquakeAdapter(final Context context,
                             final OnItemClickListener onItemClickListener,
                             final View emptyView,
                             final int choiceMode) {
        mContext = context;
        mOnItemClickListener = onItemClickListener;
        mEmptyView = emptyView;
        mItemChoiceManager = new ItemChoiceManager(this);
        mItemChoiceManager.setChoiceMode(choiceMode);
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public void swapCursor(final Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public EarthquakeViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.earthquake_list_item, viewGroup, false);

        return new EarthquakeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final EarthquakeViewHolder holder, final int position) {
        mCursor.moveToPosition(position);

        final Double magnitude = mCursor.getDouble(EarthquakeListActivity.COL_EQ_MAGNITUDE);

        holder.magnitudeText.setText(String.valueOf(magnitude));
        holder.placeText.setText(mCursor.getString(EarthquakeListActivity.COL_EQ_PLACE));

        if(magnitude >= 0 && magnitude <= 3.5){
            holder.magnitudeText.setTextColor(ContextCompat.getColor(mContext, android.R.color.holo_green_dark));
            holder.placeText.setTextColor(ContextCompat.getColor(mContext, android.R.color.holo_green_dark));
            holder.placeText.setAlpha(1f);

            holder.distanceText.setTextColor(ContextCompat.getColor(mContext, android.R.color.holo_green_dark));
        }
        else if(magnitude >= 6.5){
            holder.magnitudeText.setTextColor(ContextCompat.getColor(mContext, android.R.color.holo_red_dark));
            holder.placeText.setTextColor(ContextCompat.getColor(mContext, android.R.color.holo_red_dark));
            holder.placeText.setAlpha(1f);

            holder.distanceText.setTextColor(ContextCompat.getColor(mContext, android.R.color.holo_red_dark));
        }
        else{
            holder.magnitudeText.setTextColor(ContextCompat.getColor(mContext, android.R.color.black));
            holder.placeText.setTextColor(ContextCompat.getColor(mContext, android.R.color.black));
            holder.placeText.setAlpha(0.65f);

            holder.distanceText.setTextColor(ContextCompat.getColor(mContext, android.R.color.black));
        }

        String distanceToEarthquake = String.valueOf(mCursor.getDouble(EarthquakeListActivity.COL_EQ_DISTANCE));

        if (distanceToEarthquake.isEmpty()) {
            holder.distanceText.setVisibility(View.GONE);
        } else {
            holder.distanceText.setVisibility(View.VISIBLE);
            holder.distanceText.setText(distanceToEarthquake);

            if(distanceToEarthquake.contains(".")) {
                distanceToEarthquake = distanceToEarthquake.substring(0, distanceToEarthquake.lastIndexOf("."));
            }

            holder.distanceText.setText(String.format(mContext.getString(R.string.distance_format),
                                                      distanceToEarthquake));
        }

        mItemChoiceManager.onBindViewHolder(holder, position);
    }

    public void onRestoreInstanceState(final Bundle savedInstanceState) {
        mItemChoiceManager.onRestoreInstanceState(savedInstanceState);
    }

    public void onSaveInstanceState(final Bundle outState) {
        mItemChoiceManager.onSaveInstanceState(outState);
    }

    public void setUseTodayLayout(final boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
    }

    public int getSelectedItemPosition() {
        return mItemChoiceManager.getSelectedItemPosition();
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }

        return mCursor.getCount();
    }

    public class EarthquakeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView magnitudeText;
        public TextView placeText;
        public TextView distanceText;

        public EarthquakeViewHolder(final View convertView) {
            super(convertView);
            magnitudeText = (TextView) convertView.findViewById(R.id.earthquake_list_item_magnitude_text);
            placeText = (TextView) convertView.findViewById(R.id.earthquake_list_item_place_text);
            distanceText = (TextView) convertView.findViewById(R.id.earthquake_list_item_distance_text);

            convertView.setOnClickListener(this);
        }

        @Override
        public void onClick(final View view) {
            mCursor.moveToPosition(getAdapterPosition());

            final Earthquake earthquake = new Earthquake(mCursor.getDouble(COL_EQ_MAGNITUDE),
                                                         mCursor.getString(COL_EQ_PLACE),
                                                         mCursor.getString(COL_EQ_TIME_DATE),
                                                         mCursor.getString(COL_EQ_LONGITUDE),
                                                         mCursor.getString(COL_EQ_LATITUDE),
                                                         mCursor.getString(COL_EQ_DEPTH),
                                                         mCursor.getString(COL_EQ_DISTANCE));

            mOnItemClickListener.onClick(earthquake, view);
            mItemChoiceManager.onClick(this);
        }
    }

    public void selectView(final RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof EarthquakeViewHolder) {
            final EarthquakeViewHolder earthquakeViewHolder = (EarthquakeViewHolder) viewHolder;
            earthquakeViewHolder.onClick(earthquakeViewHolder.itemView);
        }
    }
}
