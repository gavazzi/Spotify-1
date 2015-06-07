package nz.co.zzi.spotify.ui.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Tracks;
import nz.co.zzi.spotify.R;
import nz.co.zzi.spotify.infrastructure.adapters.TopTracksAdapter;
import nz.co.zzi.spotify.infrastructure.models.Artist;
import nz.co.zzi.spotify.infrastructure.models.Track;
import nz.co.zzi.spotify.infrastructure.utils.Utility;

import static android.support.v7.widget.RecyclerView.LayoutManager;

/**
 * Created by joao.gavazzi on 6/06/15.
 */
public class TopTracksFragment extends Fragment implements View.OnClickListener {

    private static final String EXTRA_TOP_TRACKS = "extra-top-tracks";
    private static final String EXTRA_ARTIST = "extra-artist";

    public static Fragment newInstance(final Artist artist) {
        final Fragment fragment = new TopTracksFragment();
        final Bundle args = new Bundle();
        args.putParcelable(EXTRA_ARTIST, artist);
        fragment.setArguments(args);
        return fragment;
    }

    private ProgressBar mProgressBar;
    private TopTracksAdapter mTopTracksAdapter;
    private View mErrorLayout;
    private Artist mArtist;
    private TopTracksRetrieverTask mCurrentAsyncTask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArtist = getArguments().getParcelable(EXTRA_ARTIST);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_top_tracks, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        mErrorLayout = view.findViewById(R.id.error_layout);
        final Button retryButton = (Button) mErrorLayout.findViewById(R.id.retry_button);
        retryButton.setOnClickListener(this);

        final RecyclerView topTracksRecyclerView = (RecyclerView) view.findViewById(R.id.top_tracks_recycler_view);
        final LayoutManager lm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mTopTracksAdapter = new TopTracksAdapter(getActivity());

        topTracksRecyclerView.setAdapter(mTopTracksAdapter);
        topTracksRecyclerView.setLayoutManager(lm);
        topTracksRecyclerView.setHasFixedSize(true);

        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_TOP_TRACKS)) {
            final Track[] tracks = Utility.convertParcelableArrayToGivenInstance(
                    savedInstanceState.getParcelableArray(EXTRA_TOP_TRACKS),
                    Track[].class);

            mTopTracksAdapter.setTracks(tracks);
            mProgressBar.setVisibility(View.GONE);
        } else {
            retrieveTracks();
        }
    }

    private void retrieveTracks() {
        mCurrentAsyncTask = new TopTracksRetrieverTask();
        mCurrentAsyncTask.execute(mArtist.getId());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        final Track[] tracks = mTopTracksAdapter.getTracks();
        if (tracks != null) {
            outState.putParcelableArray(EXTRA_TOP_TRACKS, tracks);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mCurrentAsyncTask != null && !mCurrentAsyncTask.isCancelled()) {
            mCurrentAsyncTask.cancel(false);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.retry_button) {
            retrieveTracks();
        }
    }

    private class TopTracksRetrieverTask extends AsyncTask<String, Void, Track[]> {

        private static final int IDEAL_SIZE_SMALL_IN_PIXEL = 200;
        private static final int IDEAL_SIZE_LARGE_IN_PIXEL = 600;

        @Override
        protected void onPreExecute() {
            if (mProgressBar.getVisibility() == View.GONE) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            if (mErrorLayout.getVisibility() == View.VISIBLE) {
                mErrorLayout.setVisibility(View.GONE);
            }
        }

        @Override
        protected Track[] doInBackground(final String... params) {
            final String artistId = params[0];
            //Use array because we don't need to add up
            //more tracks later, what will result
            //some KB less in memory
            Track[] topTracks = null;
            try {
                final SpotifyApi spotifyApi = new SpotifyApi();
                final SpotifyService service = spotifyApi.getService();

                //TODO create location screen in stage 2
                final Map<String, Object> query = new ArrayMap<>();
                query.put("country", "US");

                final Tracks tracks = service.getArtistTopTrack(artistId, query);
                if (tracks != null) {
                    final List<kaaes.spotify.webapi.android.models.Track> listOfSpotifyTracks = tracks.tracks;
                    topTracks = new Track[listOfSpotifyTracks.size()];
                    for (int i = 0; i < topTracks.length; i++) {
                        final kaaes.spotify.webapi.android.models.Track track = listOfSpotifyTracks.get(i);

                        final Image largeImage = Utility.extractIdealSizeThumbnail(
                                IDEAL_SIZE_LARGE_IN_PIXEL,
                                track.album.images);

                        final Image smallImage = Utility.extractIdealSizeThumbnail(
                                IDEAL_SIZE_SMALL_IN_PIXEL,
                                track.album.images);

                        final String smallImageUrl = (smallImage != null) ? smallImage.url : null;
                        final String largeImageUrl = (largeImage != null) ? largeImage.url : null;
                        topTracks[i] = new Track(track.name,
                                                 track.album.name,
                                                 smallImageUrl,
                                                 largeImageUrl,
                                                 track.preview_url);
                    }
                }
            } catch (Exception ignore) {
            }

            return topTracks;
        }

        @Override
        protected void onPostExecute(final Track[] tracks) {
            mCurrentAsyncTask = null;
            mProgressBar.setVisibility(View.GONE);

            if (tracks == null) {
                mErrorLayout.setVisibility(View.VISIBLE);
            } else {
                mTopTracksAdapter.setTracks(tracks);
            }
        }
    }
}
