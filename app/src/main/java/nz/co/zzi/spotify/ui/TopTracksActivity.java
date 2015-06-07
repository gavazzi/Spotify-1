package nz.co.zzi.spotify.ui;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import nz.co.zzi.spotify.BaseActivity;
import nz.co.zzi.spotify.R;
import nz.co.zzi.spotify.infrastructure.models.Artist;
import nz.co.zzi.spotify.ui.fragments.TopTracksFragment;

/**
 * Created by joao.gavazzi on 7/06/15.
 */
public class TopTracksActivity extends BaseActivity {

    public static final String EXTRA_ARTIST = "extra-artist";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_tracks);

        final Artist artist = getIntent().getParcelableExtra(EXTRA_ARTIST);
        final ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setSubtitle(artist.getName());
            actionBar.setDisplayShowHomeEnabled(true);
        }

        if(savedInstanceState == null) {
            replaceFragment(R.id.container, TopTracksFragment.newInstance(artist), false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled = false;
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                handled = true;
                break;
        }

        return handled || super.onOptionsItemSelected(item);
    }
}
