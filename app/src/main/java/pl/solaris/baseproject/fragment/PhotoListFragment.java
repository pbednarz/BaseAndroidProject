package pl.solaris.baseproject.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.db.Photo;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.solaris.baseproject.R;
import pl.solaris.baseproject.activity.DetailActivity;
import pl.solaris.baseproject.adapter.OnItemClickListener;
import pl.solaris.baseproject.adapter.PhotoAdapter;
import pl.solaris.baseproject.db.DatabaseManager;
import pl.solaris.baseproject.service.Service;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.schedulers.Schedulers;

/**
 * Created by pbednarz on 2015-02-05.
 */
public class PhotoListFragment extends Fragment implements OnItemClickListener {

    public static final String ALBUM_NAME_KEY = "album_name";
    @InjectView(R.id.recycler_view)
    RecyclerView recyclerView;
    @InjectView(R.id.swipe_refresh)
    SwipeRefreshLayout refreshLayout;
    private PhotoAdapter mAdapter;
    private Subscription databaseSubscription;
    private Subscription retrofitSubscription;

    public static PhotoListFragment newInstance(String album) {
        PhotoListFragment fragment = new PhotoListFragment();
        Bundle args = new Bundle();
        args.putString(ALBUM_NAME_KEY, album);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(
                R.layout.fragment_photo_list, container, false);
        ButterKnife.inject(this, view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        mAdapter = new PhotoAdapter(null, this);
        recyclerView.setAdapter(mAdapter);
        refreshLayout.setOnRefreshListener(this::loadAlbum);
        refreshLayout.setColorSchemeColors(R.color.primary_dark,
                R.color.primary,
                R.color.accent,
                R.color.accent_dark);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        databaseSubscription = AppObservable.bindFragment(this,
                DatabaseManager.getInstance(getActivity().getApplicationContext())
                        .getAlbumObservable(getArguments().getString(ALBUM_NAME_KEY))
                        .subscribeOn(Schedulers.io()))
                .subscribe(album -> {
                            if (album != null) {
                                mAdapter.setAlbum(album);
                                mAdapter.notifyDataSetChanged();
                            }
                        },
                        exception -> loadAlbum(), this::loadAlbum);
    }

    private void loadAlbum() {
        refreshLayout.setRefreshing(true);
        retrofitSubscription = AppObservable.bindFragment(this, Service.getInstance(getActivity())
                .getNewClientInstance().getAlbum(getArguments().getString(ALBUM_NAME_KEY).toLowerCase())
                .doOnNext(album -> DatabaseManager.getInstance(getActivity().getApplicationContext())
                        .saveAlbum(album))
                .subscribeOn(Schedulers.io()))
                .subscribe(album -> {
                            mAdapter.setAlbum(album);
                            mAdapter.notifyDataSetChanged();
                        },
                        exception -> {
                            exception.printStackTrace();
                            refreshLayout.setRefreshing(false);
                        }, () -> refreshLayout.setRefreshing(false));
    }

    @Override
    public void onDestroyView() {
        recyclerView = null;
        refreshLayout = null;
        mAdapter = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (databaseSubscription != null) {
            databaseSubscription.unsubscribe();
            databaseSubscription = null;
        }

        if (retrofitSubscription != null) {
            retrofitSubscription.unsubscribe();
            retrofitSubscription = null;
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view, int position) {
        Photo photo = mAdapter.getItem(position);
        DetailActivity.launch(getActivity(), view, photo.url, String.format("Title %d", photo.id));
    }
}
