package com.example.chadyeo.animetv.free;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chadyeo.animetv.R;
import com.example.chadyeo.animetv.api.Anime;
import com.example.chadyeo.animetv.api.Studio;
import com.example.chadyeo.animetv.loaders.AnimeDetailLoader;
import com.example.chadyeo.animetv.utils.SeasonUtil;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.util.ArrayList;

import static android.view.View.GONE;

public class AnimeDetailActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener {

    private AdView mAdView;

    int episodeNum;
    int dataId;
    long millis;
    boolean noInternet;

    private YouTubePlayerSupportFragment youTubePlayerSupportFragment;
    private YouTubePlayer activePlayer;
    private String videoId;
    String KEY_DEVELOPER = "AIzaSyDDB5WWAfp5u_usqYguNVIMCEhjLegYb28";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAdView = (AdView) findViewById(R.id.adView_detailActivity);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Intent intent = getIntent();
        String id = intent.getStringExtra("ID");
        videoId = intent.getStringExtra("YOUTUBE_ID");

        loadAnimePage(id);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent() != null && getIntent().hasExtra("EPISODE") &&
                getIntent().hasExtra("TIME") && getIntent().hasExtra("ID RESTART")) {
            episodeNum = getIntent().getIntExtra("EPISODE", 0);
            dataId = getIntent().getIntExtra("ID RESTART", 0);
            millis = getIntent().getLongExtra("TIME", 0);

            getIntent().removeExtra("EPISODE");
            getIntent().removeExtra("ID RESTART");
            getIntent().removeExtra("TIME");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(getSupportLoaderManager().getLoader(2) != null) {
            getSupportLoaderManager().destroyLoader(2);
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        activePlayer = youTubePlayer;
        activePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
        if (!wasRestored) {
            activePlayer.cueVideo(videoId);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult error) {
        if (error.isUserRecoverableError()) {
            Log.e(AnimeDetailActivity.class.getSimpleName(), error.toString());
        } else {
            String errorMsg = error.toString();
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadAnimePage(String id) {

        if (getSupportLoaderManager().getLoader(2) == null) {
            getSupportLoaderManager().initLoader(2, null, new PageLoad(this, id));
        } else {
            getSupportLoaderManager().restartLoader(2, null, new PageLoad(this, id));
        }
    }

    public void setYoutubePlayer() {
        if (videoId.isEmpty() || videoId.equals("null")) {
            TextView noYoutubeId_textView = (TextView) findViewById(R.id.noYoutubeId_textView);
            noYoutubeId_textView.setVisibility(View.VISIBLE);
        } else {
            youTubePlayerSupportFragment =
                    (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtubePlayerView);
            youTubePlayerSupportFragment.initialize(KEY_DEVELOPER, this);
        }
    }

    private class PageLoad implements LoaderManager.LoaderCallbacks<Anime> {

        Context context;
        String id;

        public PageLoad(Context context, String id) {
            this.context = context;
            this.id = id;
        }

        @NonNull
        @Override
        public Loader<Anime> onCreateLoader(int id, @Nullable Bundle args) {
            return new AnimeDetailLoader(context, this.id);
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Anime> loader, Anime data) {
            final View progress = findViewById(R.id.anime_page_loading);
            if (data == null) {
                noInternet = true;
                progress.setVisibility(GONE);
                findViewById(R.id.page).setVisibility(GONE);
                findViewById(R.id.page_connect_text).setVisibility(View.VISIBLE);
                findViewById(R.id.page_retry_text).setVisibility(View.VISIBLE);
            } else {
                if(noInternet){
                    noInternet = false;
                    findViewById(R.id.page).setVisibility(View.VISIBLE);
                    findViewById(R.id.page_connect_text).setVisibility(GONE);
                    findViewById(R.id.page_retry_text).setVisibility(GONE);
                }
                getSupportActionBar().setTitle(data.getTitle_romaji());

                //Anime Title
                TextView title = (TextView) findViewById(R.id.anime_name_page);
                title.setText(data.getTitle_romaji());

                //youTube ID
                videoId = String.valueOf(data.getYoutube_id());

                Log.e("YOUTUBE ID IS",  String.valueOf(data.getYoutube_id()));

                //Anime image thumbnail
                ImageView image = (ImageView) findViewById(R.id.anime_page_image);
                String url = data.getImage_url_lge();
                if (url == null) url = data.getImage_url_med();
                if (url == null) url = data.getImage_url_sml();
                Glide.with(image.getContext())
                        .load(url)
                        .centerCrop()
                        .placeholder(R.color.cardview_dark_background)
                        .into(image);

                //Type
                TextView type = (TextView) findViewById(R.id.anime_type_page);
                if(data.getType() != null)type.setText(data.getType());
                else type.setText("?");

                //Episodes
                TextView episodes = (TextView) findViewById(R.id.anime_episodes_page);
                if (data.getTotal_episodes() != 0)
                    episodes.setText(String.valueOf(data.getTotal_episodes()));
                else episodes.setText("?");

                //Duration
                TextView duration = (TextView) findViewById(R.id.anime_duration_page);
                if (data.getDuration() != 0)
                    duration.setText(String.valueOf(data.getDuration()) + " mins");
                else duration.setText("?");

                //Studios
                TextView studios = (TextView) findViewById(R.id.anime_studios_page);
                if(data.getStudio() != null && data.getStudio().size() > 0){
                    String f = "";
                    int c = 0;
                    for(Studio s : data.getStudio()) {
                        String r = s.getStudio_name();
                        r = r.replace(" ", "\u00A0");
                        if (data.getStudio().size() > 1 && s.getMain_studio() == 1 && c != data.getStudio().size() - 1)
                            r += " (Main), ";
                        else if(data.getStudio().size() > 1 && s.getMain_studio() == 1 && c == data.getStudio().size() - 1)
                            r += " (Main)";
                        else if(data.getStudio().size() > 1 && c != data.getStudio().size()-1)
                            r += ", ";
                        f += r;
                        c++;
                    }
                    studios.setText(f);
                } else {
                    studios.setText("No studios added");
                }

                //Genres
                TextView genres = (TextView) findViewById(R.id.anime_genres_page);
                ArrayList<String> filteredGenres = new ArrayList<>();
                for (String s : data.getGenres()) {
                    if (s != null && s.length() != 0 && !s.equals(" ")) filteredGenres.add(s);
                }
                if (filteredGenres.size() > 0) {
                    String r = "";
                    int c = 0;
                    for (String s : filteredGenres) {
                        String t = s;
                        t = t.replace(" ", "\u00A0");
                        if (c != filteredGenres.size() - 1)t += ", ";
                        c++;
                        r += t;
                    }
                    genres.setText(r);
                } else {
                    genres.setText("No genres listed");
                }

                //Description
                TextView desc = (TextView) findViewById(R.id.anime_description_page);
                if(data.getDescription() != null) {
                    String descEdited = data.getDescription();
                    descEdited = descEdited.replace("<br>", "\n");
                    descEdited = descEdited.replaceAll("<.*?>", "");
                    int count = 0;
                    String r = "";
                    for (int i = 0; i < descEdited.length(); i++) {
                        if (descEdited.substring(i, i + 1).equals("\n") && count >= 3) continue;
                        if (descEdited.substring(i, i + 1).equals("\n") && count < 3) count++;
                        else count = 0;
                        r += descEdited.substring(i, i + 1);
                    }
                    descEdited = r;
                    desc.setText(descEdited);
                } else {
                    desc.setText("No description added");
                }

                //Source
                TextView source = (TextView) findViewById(R.id.anime_source_page);
                if(data.getSource() != null)source.setText(data.getSource());
                else source.setText("Other");

                //Premiered
                TextView premiered = (TextView) findViewById(R.id.anime_premiered_page);
                if(String.valueOf(data.getSeason()).length() >= 3){
                    premiered.setText(SeasonUtil.getSeasonText(data.getSeason() % 10) + " 20"+String.valueOf(data.getSeason()).substring(0, 2));
                } else {
                    String year = String.valueOf(data.getStart_date_fuzzy()).length() >= 4 ? String.valueOf(data.getStart_date_fuzzy()).substring(0, 4) : "?";
                    String season = String.valueOf(data.getStart_date_fuzzy()).length() >= 6 ? SeasonUtil.checkMonth(Integer.parseInt(String.valueOf(data.getStart_date_fuzzy()).substring(4, 6))-1) : "?";
                    premiered.setText(season + " " + year);
                }

                //Animate progress bar out and display the data
                if (progress != null) {
                    progress.animate()
                            .alpha(0.0f)
                            .setDuration(500)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    progress.setVisibility(GONE);
                                }
                            });
                }
                findViewById(R.id.page).setVisibility(View.VISIBLE);

                setYoutubePlayer();
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Anime> loader) {

        }
    }
}
