package nz.co.zzi.spotify.infrastructure.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import nz.co.zzi.spotify.R;
import nz.co.zzi.spotify.infrastructure.models.Track;

/**
 * Created by joao.gavazzi on 7/06/15.
 */
public class TopTracksAdapter extends RecyclerView.Adapter<TopTracksAdapter.TopTrackHolder> {

    private Track [] mTracks;
    private Context mContext;
    private LayoutInflater mInflater;

    public TopTracksAdapter(final Context context) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    public void setTracks(Track[] tracks) {
        mTracks = tracks;
        notifyDataSetChanged();
    }

    public Track[] getTracks() {
        return mTracks;
    }

    @Override
    public TopTrackHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View root = mInflater.inflate(R.layout.list_item_top_track, parent, false);
        return new TopTrackHolder(root);
    }

    @Override
    public void onBindViewHolder(TopTrackHolder holder, int position) {
        holder.bind(mTracks[position]);
    }

    @Override
    public int getItemCount() {
        return (mTracks != null) ? mTracks.length : 0;
    }

    class TopTrackHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mThumbnailImageView;
        private TextView mNameTextView;
        private TextView mAlbumTextView;

        public TopTrackHolder(View root) {
            super(root);
            root.setOnClickListener(this);
            mThumbnailImageView = (ImageView) root.findViewById(R.id.thumbnails_image_view);
            mNameTextView = (TextView) root.findViewById(R.id.name_text_view);
            mAlbumTextView = (TextView) root.findViewById(R.id.album_text_view);
        }

        void bind(final Track track) {
            mNameTextView.setText(track.getName());
            mAlbumTextView.setText(track.getAlbum());

            final Picasso picasso = Picasso.with(mContext);
            picasso.cancelRequest(mThumbnailImageView);
            if(!TextUtils.isEmpty(track.getSmallImageUrl())) {
                picasso.load(track.getSmallImageUrl()).into(mThumbnailImageView);
            } else {
                mThumbnailImageView.setImageResource(R.drawable.bg_not_picture_place_holder);
            }
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(mContext, "{COMING SOON ON STAGE 2}", Toast.LENGTH_SHORT).show();
        }
    }
}
