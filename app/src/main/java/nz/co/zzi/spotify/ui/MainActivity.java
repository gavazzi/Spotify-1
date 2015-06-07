package nz.co.zzi.spotify.ui;

import android.content.Intent;
import android.os.Bundle;

import nz.co.zzi.spotify.BaseActivity;
import nz.co.zzi.spotify.R;
import nz.co.zzi.spotify.infrastructure.models.Artist;
import nz.co.zzi.spotify.ui.fragments.ListArtistsFragment;

public class MainActivity extends BaseActivity implements ListArtistsFragment.Callback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null) {
            replaceFragment(R.id.container, new ListArtistsFragment(), false);
        }
    }

    @Override
    public void onArtistSelected(Artist artist) {
        final Intent intent = new Intent(this, TopTracksActivity.class);
        intent.putExtra(TopTracksActivity.EXTRA_ARTIST, artist);
        startActivity(intent);
    }
}
