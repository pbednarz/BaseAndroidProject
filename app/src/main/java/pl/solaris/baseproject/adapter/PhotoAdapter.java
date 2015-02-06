package pl.solaris.baseproject.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.db.Album;
import com.example.db.Photo;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.solaris.baseproject.R;
import pl.solaris.baseproject.service.Service;

/**
 * PGS-Software
 * Created by pbednarz on 2015-02-03.
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private Album album;
    private WeakReference<OnItemClickListener> mListener;

    public PhotoAdapter(Album album, OnItemClickListener listener) {
        this.album = album;
        this.mListener = new WeakReference<>(listener);
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photo, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Photo photo = album.getPhotos().get(position);
        Service.getInstance(holder.image.getContext()).getPicasso().load(photo.url).placeholder(R.drawable.placeholder).into(holder.image);
        holder.title.setText(String.format("Item %d", position));
        holder.image.setOnClickListener(view -> {
            if (mListener.get() != null) mListener.get().onClick(view, position);
        });
    }

    public Photo getItem(int position) {
        return album.getPhotos().get(position);
    }

    @Override
    public int getItemCount() {
        return (album != null && album.getPhotos() != null) ? album.getPhotos().size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.title)
        public TextView title;

        @InjectView(R.id.image)
        public ImageView image;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }

}
