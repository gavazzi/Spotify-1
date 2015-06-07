package nz.co.zzi.spotify.infrastructure.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import nz.co.zzi.spotify.R;
import nz.co.zzi.spotify.infrastructure.models.Artist;

/**
 * Created by joao.gavazzi on 6/06/15.
 */
public class ArtistsAdapter extends RecyclerView.Adapter<ArtistsAdapter.ArtistHolder> {

    private final List<Artist> mArtists;
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;

    private OnArtistClickListener mArtistClickListener;

    public ArtistsAdapter(final Context context) {
        mContext = context;
        mArtists = new ArrayList<>();
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setArtistClickListener(OnArtistClickListener artistClickListener) {
        mArtistClickListener = artistClickListener;
    }

    //TODO implement pagination on stage 2
    public void addArtists(final List<Artist> artists) {
        mArtists.addAll(artists);
        notifyDataSetChanged();
    }

    public List<Artist> getArtists() {
        return mArtists;
    }

    public void clear() {
        mArtists.clear();
        notifyDataSetChanged();
    }

    @Override
    public ArtistHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        final View root = mLayoutInflater.inflate(R.layout.list_item_artist, viewGroup, false);
        return new ArtistHolder(root);
    }

    @Override
    public void onBindViewHolder(ArtistHolder artistHolder, int position) {
        artistHolder.bind(mArtists.get(position));
    }

    @Override
    public int getItemCount() {
        return mArtists.size();
    }

    class ArtistHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mThumbnailsImageView;
        private TextView mNameTextView;
        private Artist mCurrentArtist;

        public ArtistHolder(final View root) {
            super(root);
            root.setOnClickListener(this);
            mThumbnailsImageView = (ImageView) root.findViewById(R.id.thumbnails_image_view);
            mNameTextView = (TextView) root.findViewById(R.id.name_text_view);
        }

        void bind(final Artist artist) {
            mCurrentArtist = artist;

            final Picasso picasso = Picasso.with(mContext);

            //Cancel previous requests (in case of there are any)
            //to prevent unnecessary data usage in case of fast scroll
            picasso.cancelRequest(mThumbnailsImageView);
            if(artist.getImageUrl() != null) {
                picasso.load(artist.getImageUrl()).into(mThumbnailsImageView);
            } else {
                mThumbnailsImageView.setImageResource(R.drawable.bg_not_picture_place_holder);
            }

            mNameTextView.setText(artist.getName());
        }

        @Override
        public void onClick(View v) {
            if(mArtistClickListener != null) {
                mArtistClickListener.onClick(mCurrentArtist);
            }
        }
    }

    public interface OnArtistClickListener {
        void onClick(Artist artist);
    }
}
