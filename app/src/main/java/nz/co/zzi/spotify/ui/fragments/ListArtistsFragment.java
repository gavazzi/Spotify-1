package nz.co.zzi.spotify.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import nz.co.zzi.spotify.R;
import nz.co.zzi.spotify.infrastructure.adapters.ArtistsAdapter;
import nz.co.zzi.spotify.infrastructure.models.Artist;
import nz.co.zzi.spotify.infrastructure.utils.Utility;

/**
 * Created by joao.gavazzi on 6/06/15.
 */
public class ListArtistsFragment extends Fragment {

    private static final String EXTRA_ARTISTS = "extra-artists";

    private static final int MINIMAL_PAUSE_TIME_IN_MILLIS = 500;
    private static final int QUERY_SERVER_MESSAGE = 1;

    public interface Callback {
        void onArtistSelected(final Artist artist);
    }

    private static Callback mStubCallback = new Callback() {
        @Override
        public void onArtistSelected(Artist artist) {

        }
    };

    private EditText mSearchEditText;
    private TextView mNoResultsTextView;
    private ArtistsAdapter mArtistsAdapter;
    private ProgressBar mProgressBar;

    private QueryArtistAsyncTask mCurrentAsyncTask;
    private Handler mHandler;
    private SpotifyApi mSpotifyApi;
    private Callback mCallback = mStubCallback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(mSearchQueryCallback);
        mSpotifyApi = new SpotifyApi();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_artists, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView artistsRecyclerView = (RecyclerView) view.findViewById(R.id.artists_recycler_view);
        final LinearLayoutManager lm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mArtistsAdapter = new ArtistsAdapter(getActivity());
        mArtistsAdapter.setArtistClickListener(
                new ArtistsAdapter.OnArtistClickListener() {
                    @Override
                    public void onClick(Artist artist) {
                        mCallback.onArtistSelected(artist);
                    }
                });

        artistsRecyclerView.setAdapter(mArtistsAdapter);
        artistsRecyclerView.setLayoutManager(lm);
        artistsRecyclerView.setHasFixedSize(true);

        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mNoResultsTextView = (TextView) view.findViewById(R.id.no_results_text_view);

        mSearchEditText = (EditText) view.findViewById(R.id.search_edit_text);
        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_ARTISTS)) {
            final Artist[] artists = Utility.convertParcelableArrayToGivenInstance(
                    savedInstanceState.getParcelableArray(EXTRA_ARTISTS),
                    Artist[].class);

            mArtistsAdapter.addArtists(Arrays.asList(artists));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!mArtistsAdapter.getArtists().isEmpty()) {
            final List<Artist> artists = mArtistsAdapter.getArtists();
            outState.putParcelableArray(EXTRA_ARTISTS, artists.toArray(new Artist[artists.size()]));
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        mSearchEditText.addTextChangedListener(mSearchWatcher);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = Utility.requireInstanceOf(activity, Callback.class);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = mStubCallback;
    }

    //Avoid fire up a request when the user is still typing,
    //wait a minimal internal before query the server
    private TextWatcher mSearchWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            mHandler.removeMessages(QUERY_SERVER_MESSAGE);
            mHandler.sendEmptyMessageDelayed(QUERY_SERVER_MESSAGE, MINIMAL_PAUSE_TIME_IN_MILLIS);
        }
    };

    private Handler.Callback mSearchQueryCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what != QUERY_SERVER_MESSAGE) {
                throw new IllegalArgumentException("Invalid message type.");
            }

            if (mCurrentAsyncTask != null && !mCurrentAsyncTask.isCancelled()) {
                mCurrentAsyncTask.cancel(false);
            }

            final String query = mSearchEditText.getText().toString();
            if (!TextUtils.isEmpty(query)) {
                mCurrentAsyncTask = new QueryArtistAsyncTask(mSpotifyApi);
                mCurrentAsyncTask.execute(query);
            } else {
                mProgressBar.setVisibility(View.GONE);
                mNoResultsTextView.setVisibility(View.GONE);
                mArtistsAdapter.clear();
            }

            return true;
        }
    };

    private class QueryArtistAsyncTask extends AsyncTask<String, Void, List<Artist>> {

        private static final int IDEAL_IMAGE_SIZE_IN_PX = 200;

        //Avoid to create this object all the time,
        //instead inject it from the fragment
        private SpotifyApi mSpotifyApi;

        public QueryArtistAsyncTask(SpotifyApi spotifyApi) {
            mSpotifyApi = spotifyApi;
        }

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            mNoResultsTextView.setVisibility(View.GONE);
            mArtistsAdapter.clear();
        }

        @Override
        protected List<Artist> doInBackground(String... params) {
            final String query = params[0];

            List<Artist> artists = null;
            try {
                final SpotifyService spotify = mSpotifyApi.getService();
                final ArtistsPager results = spotify.searchArtists(query);
                if (results != null) {
                    artists = new ArrayList<>();
                    for (final kaaes.spotify.webapi.android.models.Artist artist : results.artists.items) {
                        final Image image = Utility.extractIdealSizeThumbnail(IDEAL_IMAGE_SIZE_IN_PX, artist.images);
                        artists.add(new Artist(artist.id, (image != null) ? image.url : null, artist.name));
                    }
                }
            } catch (Exception ignore) {}

            return artists;
        }

        @Override
        protected void onPostExecute(final List<Artist> artists) {
            mProgressBar.setVisibility(View.GONE);

            if(artists == null) {
                final Context context = getActivity();
                if(context != null) {
                    Toast.makeText(context, R.string.connection_error_message, Toast.LENGTH_LONG).show();
                }
            } else {
                mNoResultsTextView.setVisibility(!artists.isEmpty() ? View.GONE : View.VISIBLE);

                if(!artists.isEmpty()) {
                    mArtistsAdapter.addArtists(artists);
                }
            }
        }
    }
}
