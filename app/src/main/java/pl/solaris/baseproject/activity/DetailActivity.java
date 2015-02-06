package pl.solaris.baseproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import pl.solaris.baseproject.R;
import pl.solaris.baseproject.service.Service;

/**
 * PGS-Software
 * Created by pbednarz on 2015-02-03.
 */
public class DetailActivity extends ActionBarActivity {

    public static final String EXTRA_IMAGE = "DetailActivity:image";
    public static final String EXTRA_TITLE = "DetailActivity:title";
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.photo)
    ImageView photo;
    @InjectView(R.id.root)
    RelativeLayout contentRoot;
    @InjectView(R.id.text)
    TextView text;
    @InjectView(R.id.fabBtn)
    ImageView fabBtn;

    public static void launch(FragmentActivity activity, View transitionView, String url, String title) {
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity, transitionView, EXTRA_IMAGE);
        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putExtra(EXTRA_IMAGE, url);
        intent.putExtra(EXTRA_TITLE, title);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
        activity.overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.inject(this);
        toolbar.setTitle(getIntent().getStringExtra(EXTRA_TITLE));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ViewCompat.setTransitionName(photo, EXTRA_IMAGE);
        Service.getInstance(this).getPicasso().load(getIntent().getStringExtra(EXTRA_IMAGE)).into(photo);
        if (savedInstanceState == null) {
            text.setAlpha(0.0f);
            fabBtn.setTranslationY(getResources().getDimensionPixelOffset(R.dimen.btn_fab_margins)
                    + getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));
            contentRoot.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    contentRoot.getViewTreeObserver().removeOnPreDrawListener(this);
                    text.animate()
                            .alpha(1.0f)
                            .setInterpolator(new OvershootInterpolator(1.f))
                            .setStartDelay(300)
                            .setDuration(900)
                            .start();
                    fabBtn.animate()
                            .translationY(0)
                            .setInterpolator(new OvershootInterpolator(1.f))
                            .setStartDelay(500)
                            .setDuration(320)
                            .start();
                    return true;
                }
            });
        }
    }

    @OnClick(R.id.fabBtn)
    public void shareBtnClicked() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text.getText());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}
