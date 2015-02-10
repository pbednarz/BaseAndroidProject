package pl.solaris.baseproject.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import pl.solaris.baseproject.R;

/**
 * Created by pbednarz on 2015-02-05.
 */
public class NavigationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String[] mDataset;
    private OnItemClickListener mListener;

    public NavigationAdapter(String[] myDataset, OnItemClickListener listener) {
        mDataset = myDataset;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (TYPE.findByOrdinal(viewType)) {
            case HEADER:
                vh = new ViewHolderHeader(inflater.inflate(R.layout.item_header, parent, false));
                break;
            default:
                View v = inflater.inflate(R.layout.item_drawer, parent, false);
                TextView tv = (TextView) v.findViewById(android.R.id.text1);
                vh = new ViewHolderItem(tv);
                break;
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (TYPE.findByOrdinal(getItemViewType(position))) {
            case HEADER:
                ViewHolderHeader headerVh = (ViewHolderHeader) holder;
                break;
            default:
                ViewHolderItem itemVh = (ViewHolderItem) holder;
                itemVh.mTextView.setText(mDataset[position - 1]);
                itemVh.mTextView.setOnClickListener(view -> mListener.onClick(view, position - 1));
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return TYPE.HEADER.ordinal();
            default:
                return TYPE.ITEM.ordinal();
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.length + 1;
    }

    public static enum TYPE {
        HEADER,
        ITEM;

        public static TYPE findByOrdinal(final int ordinal) {
            try {
                return values()[ordinal];
            } catch (Exception e) {
                return ITEM;
            }
        }
    }

    public static class ViewHolderItem extends RecyclerView.ViewHolder {
        public final TextView mTextView;

        public ViewHolderItem(TextView v) {
            super(v);
            mTextView = v;
        }
    }

    public static class ViewHolderHeader extends RecyclerView.ViewHolder {
        public final ImageButton mProfileImage;
        public final TextView mUserName;

        public ViewHolderHeader(View v) {
            super(v);
            mProfileImage = (ImageButton) v.findViewById(R.id.profile_image);
            mUserName = (TextView) v.findViewById(R.id.profile_name);
        }
    }
}