package com.example.exowithads;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import static com.example.exowithads.Samples.AD_TAG_URI;
import static com.example.exowithads.Samples.MP4_URI;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;


public class MainActivity extends AppCompatActivity {

    PlayerView playerView,newPlayerView;
    SimpleExoPlayer player;
    TimeBar mProgressBar;
    boolean playWhenReady = true;
    int currentWindow = 0;
    long playbackPosition = 0;

    ImaAdsLoader adsLoader;
    private AdView mAdView;


    ImageView btn_fwd,btn_rewind;


    LinearLayout root,unlock_panel,middle_Layout,middle_left_layout,middle_right_layout ;


    ImageView btnLock_Unlock;
    ImageButton btnSound,fullScreen;

    boolean flag = true;
    boolean flag_vol = true;
    boolean flag_fullScreen = true;
    boolean flag_buff=false;


    static float currentVolume;

    private AudioManager mAudioManager;



    private ProgressBar loader;

    ImageView value_volOrBrightness;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerView = findViewById(R.id.player_view);
        playerView.setControllerShowTimeoutMs(5000);
        ImaAdsLoader.Builder imaAdsLoaderBuilder = new ImaAdsLoader.Builder(this);
        imaAdsLoaderBuilder.setVastLoadTimeoutMs(60000);
        adsLoader = imaAdsLoaderBuilder.buildForAdTag(AD_TAG_URI);
       // adsLoader = new ImaAdsLoader(this, AD_TAG_URI);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();



        root = (LinearLayout) findViewById(R.id.root);
        root.setVisibility(View.VISIBLE);


        middle_Layout=findViewById(R.id.middle);
        middle_left_layout=findViewById(R.id.middle_left);
        middle_right_layout=findViewById(R.id.middle_right);



        mProgressBar=findViewById(R.id.exo_progress);

        //banner ads

        String android_id= Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        MobileAds.initialize(this,"ca-app-pub-3940256099942544~3347511713");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(android_id).build();
        mAdView.loadAd(adRequest);


        btnLock_Unlock = (ImageButton) findViewById(R.id.lock);

        btn_fwd=findViewById(R.id.fwd);
        btn_rewind=findViewById(R.id.rew);
        btnSound=findViewById(R.id.btn_vol);


        mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        value_volOrBrightness=findViewById(R.id.value);

        fullScreen=findViewById(R.id.exo_fullscreen_icon);

        loader=findViewById(R.id.loading_indicator);



//lock-unlock
        btnLock_Unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(flag)
                {

                    // Toast.makeText(getApplicationContext(), "Lock", Toast.LENGTH_LONG).show();
                    btnLock_Unlock.setImageDrawable(getResources().getDrawable(R.drawable.hplib_ic_unlock));
                    root.setVisibility(View.INVISIBLE);
                    flag=false;
                }
                else
                {
                    //  Toast.makeText(getApplicationContext(), "UnLock", Toast.LENGTH_LONG).show();
                    btnLock_Unlock.setImageDrawable(getResources().getDrawable(R.drawable.hplib_ic_lock));
                    root.setVisibility(View.VISIBLE);
                    flag=true;

                }
                return;

            }
        });

        //mute-unmute

        btnSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(flag_vol)
                {
                    // Toast.makeText(getApplicationContext(), "Mute "+flag_vol, Toast.LENGTH_SHORT).show();

                    currentVolume=0;
                    player.setVolume(currentVolume);
                    Toast.makeText(getApplicationContext(),"mute after vol"+currentVolume,Toast.LENGTH_SHORT).show();
                    btnSound.setImageDrawable(getResources().getDrawable(R.drawable.hplib_volume_mute));
                    flag_vol=false;
                }
                else
                {
                    // Toast.makeText(getApplicationContext(), "Full VOlume "+flag_vol, Toast.LENGTH_SHORT).show();
                    player.setVolume(currentVolume);
                    btnSound.setImageDrawable(getResources().getDrawable(R.drawable.hplib_volume));
                    flag_vol=true;
                }
            }
        });



        //fullscreen toggle

        fullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag_fullScreen) {
                    //Call full Screen Method here

//                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)
//                            playerView.getLayoutParams();
//                    params.width=params.MATCH_PARENT;
//                    params.height=params.MATCH_PARENT;
//                    playerView.setLayoutParams(params);
//

                    //playerView.switchTargetView(player,playerView,newPlayerView);

                    fullScreen.setImageDrawable(getResources().getDrawable(R.drawable.exo_controls_fullscreen_exit));
                    flag_fullScreen=false;

                }
                else
                {
                    fullScreen.setImageDrawable(getResources().getDrawable(R.drawable.exo_controls_fullscreen_enter));
                    flag_fullScreen=true;
                }
                // playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);





            }
        });



        middle_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag)
                {

                    // Toast.makeText(getApplicationContext(), "Lock", Toast.LENGTH_LONG).show();
                  //  btnLock_Unlock.setImageDrawable(getResources().getDrawable(R.drawable.hplib_ic_unlock));
                    root.setVisibility(View.INVISIBLE);
                    flag=false;
                }
                else
                {
                    //  Toast.makeText(getApplicationContext(), "UnLock", Toast.LENGTH_LONG).show();
                   // btnLock_Unlock.setImageDrawable(getResources().getDrawable(R.drawable.hplib_ic_lock));
                    root.setVisibility(View.VISIBLE);
                    flag=true;

                }
                return;
            }
        });


//        middle_Layout.setOnTouchListener(new OnSwipeTouchListener(this){
//
//            @Override
//            public void onClick() {
//                super.onClick();
//                if(flag )
//                {
//                    Toast.makeText(getApplicationContext(),"Click= "+flag, Toast.LENGTH_LONG).show();
//
//                    root.setVisibility(View.INVISIBLE);
//                    flag=false;
//                }
//                else
//                {
//
//                    root.setVisibility(View.VISIBLE);
//                    flag=true;
//
//                }
//            }
//        });

        middle_left_layout.setOnTouchListener(new OnSwipeTouchListener(this){
            @Override
            public void onDoubleClick() {
                super.onDoubleClick();
                btn_rewind.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btn_rewind.setVisibility(View.INVISIBLE);
                    }
                },100);
                //  btn_rewind.setVisibility(View.INVISIBLE);
                player.seekTo(player.getCurrentPosition()-10000);

            }
        });

        middle_right_layout.setOnTouchListener(new OnSwipeTouchListener(this){
            @Override
            public void onDoubleClick() {
                super.onDoubleClick();
                btn_fwd.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btn_fwd.setVisibility(View.INVISIBLE);
                    }
                },100);

                player.seekTo(player.getCurrentPosition()+10000);

            }

            @Override
            public void onSwipeUp() {
                super.onSwipeUp();

                //  float c_vol=player.getVolume();
                Log.d("Volume","PrevVolUp= "+currentVolume);

                if(!flag_vol)
                {
                    btnSound.setImageDrawable(getResources().getDrawable(R.drawable.hplib_volume));
                }

                if(currentVolume>=1)
                {
                    currentVolume=1;
                }
                else {
                    currentVolume += 0.1;
                }
                player.setVolume(currentVolume);

                Toast.makeText(getApplicationContext(), "Up VOlume "+currentVolume, Toast.LENGTH_SHORT).show();
                value_volOrBrightness.setImageDrawable(getResources().getDrawable(R.drawable.hplib_volume));
                value_volOrBrightness.setVisibility(View.VISIBLE);


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        value_volOrBrightness.setVisibility(View.INVISIBLE);
                    }
                },1000);

                Log.d("Volume","CurrentVolUp= "+currentVolume);
            }

            @Override
            public void onSwipeDown() {
                super.onSwipeDown();
                Log.d("Volume","PrevVolDown= "+currentVolume);


                if(currentVolume-0.1<=0)
                {
                    currentVolume=0;
                    // Toast.makeText(getApplicationContext(), "Down VOlume "+currentVolume, Toast.LENGTH_SHORT).show();
                    btnSound.setImageDrawable(getResources().getDrawable(R.drawable.hplib_volume_mute));
                }
                else
                    currentVolume-=0.1;
                player.setVolume(currentVolume);

                // Toast.makeText(getApplicationContext(), "Down VOlume "+currentVolume, Toast.LENGTH_SHORT).show();

                if(currentVolume==0)
                    value_volOrBrightness.setImageDrawable(getResources().getDrawable(R.drawable.hplib_volume_mute));
                else
                    value_volOrBrightness.setImageDrawable(getResources().getDrawable(R.drawable.volume_down_white_24x24));

                value_volOrBrightness.setVisibility(View.VISIBLE);


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        value_volOrBrightness.setVisibility(View.INVISIBLE);
                    }
                },1000);

                Log.d("Volume","CurrentVolDown= "+currentVolume);

            }
        });

    }



    Player.EventListener exoPlayEventListener = new Player.EventListener() {
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

            if(playbackState==Player.STATE_BUFFERING)
            {

                Toast.makeText(getApplicationContext(),"Buffering", Toast.LENGTH_LONG).show();

                loader.setVisibility(View.VISIBLE);


            }
            else
            {

                loader.setVisibility(View.INVISIBLE);

            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        initializePlayer1();

    }


    private void initializePlayer1()
    {


        player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
        playerView.setPlayer(player);

        adsLoader.setPlayer(player);


        DataSource.Factory dataSourceFactory =new DefaultHttpDataSourceFactory("exoplayer-codelab");

        MediaSource mediaSource =new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MP4_URI);

        MediaSource adsMediaSource =
                new AdsMediaSource(mediaSource, dataSourceFactory, adsLoader, playerView);
        player.prepare(adsMediaSource);

        player.setPlayWhenReady(true);
        player.seekTo(currentWindow, playbackPosition);

        player.addListener(exoPlayEventListener);

        currentVolume=player.getVolume();

    }


    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer-codelab")).
                createMediaSource(uri);
    }


//    private void initializePlayer() {
//        player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
//        playerView.setPlayer(player);
//
//        adsLoader.setPlayer(player);
//
//        DataSource.Factory dataSourceFactory =new DefaultHttpDataSourceFactory("exoplayer-codelab");
//
//        MediaSource mediaSource =new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(MP4_URI);
//
//        MediaSource adsMediaSource =
//                new AdsMediaSource(mediaSource, dataSourceFactory, adsLoader, playerView);
//        player.prepare(adsMediaSource);
//
//        player.setPlayWhenReady(true);
//        player.seekTo(currentWindow, playbackPosition);
//
//    }


    @Override
    protected void onStop() {
        adsLoader.setPlayer(null);
        playerView.setPlayer(null);
        player.release();
        player = null;

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        adsLoader.release();

        super.onDestroy();
    }
}
