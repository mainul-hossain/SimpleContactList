package com.mainulhossain.simplecontactlist.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mainulhossain.simplecontactlist.listener.OnLoadMoreListener;
import com.mainulhossain.simplecontactlist.R;
import com.mainulhossain.simplecontactlist.model.Contact;

import java.util.List;

/**
 * Created by Mainul on 3/31/2017.
 */

public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final int ITEM_VIEW = 0, LOAD_MORE_VIEW = 1;

    private OnLoadMoreListener mOnLoadMoreListener;

    private List<Contact> mList;

    private boolean mWithFooter = false;

    public ContactAdapter(List<Contact> mList)
    {
        this.mList = mList;
    }

    @Override
    public int getItemViewType(int position) {

        if (mWithFooter && isPositionFooter(position))
            return LOAD_MORE_VIEW;
        return ITEM_VIEW;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == LOAD_MORE_VIEW)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_more_layout, parent, false);
            return new LoadMoreHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_custom_row, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType())
        {
            case ITEM_VIEW:

                ViewHolder viewHolder = (ViewHolder) holder;
                Contact mContact = mList.get(position);


                viewHolder.tvCount.setText(String.valueOf(mContact.getId()));
                viewHolder.tvName.setText(mContact.getName());
                viewHolder.tvNumber.setText(mContact.getNumber());

                break;
            case LOAD_MORE_VIEW:

                LoadMoreHolder loadMoreHolder = (LoadMoreHolder) holder;
                loadMoreHolder.loadMoreBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (mOnLoadMoreListener != null)
                        {
                            mOnLoadMoreListener.onLoadMore();
                        }
                    }
                });

                break;
        }
    }

    @Override
    public int getItemCount() {

        int itemCount = mList.size();

        if (mWithFooter)
        {
            itemCount++;
        }
        return itemCount;
    }

    public boolean isPositionFooter(int position)
    {
        return position == getItemCount() - 1 && mWithFooter;
    }

    public void setWithFooter(boolean value)
    {
        mWithFooter = value;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName, tvNumber, tvCount;

        ViewHolder(View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.name);
            tvNumber = (TextView) itemView.findViewById(R.id.number);
            tvCount = (TextView) itemView.findViewById(R.id.contact_count);
        }
    }

    private static class LoadMoreHolder extends RecyclerView.ViewHolder {

        Button loadMoreBtn;

        public LoadMoreHolder(View itemView) {
            super(itemView);

            loadMoreBtn = (Button) itemView.findViewById(R.id.load_more);
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener)
    {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }
}
