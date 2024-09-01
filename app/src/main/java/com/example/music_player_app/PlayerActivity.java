package com.example.music_player_app;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import java.io.*;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chibde.visualizer.BarVisualizer;

import java.io.File;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class PlayerActivity extends AppCompatActivity
{

    Button btnPlay , btnNext , btnPrev , btnFR ,btnFF;
    TextView txtsname , txtsstart , txtddtop;
    SeekBar seekmusic;
    BarVisualizer visualizer;
    Thread updateSeekbar;

    String sname;
    public static final String EXTRA_NAME = "song_name";
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mySongs;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        btnPlay = findViewById(R.id.play_Button);
        btnNext = findViewById(R.id.next_Button);
        btnPrev = findViewById(R.id.previews_Button);
        btnFR = findViewById(R.id.btn_fast_next);
        btnFF = findViewById(R.id.btn_fast_prev);
        txtsname = findViewById(R.id.txt_sn);
        txtsstart = findViewById(R.id.txt_sStart);
        txtddtop = findViewById(R.id.txt_sStop);
        seekmusic = findViewById(R.id.seekbar);
        visualizer = findViewById(R.id.visualizer);
        imageView = findViewById(R.id.image_view);

        if(mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent i = getIntent();

        Bundle bundle  = i.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");
        String songname = i.getStringExtra("songName");
        position = bundle.getInt("pos" , 0);

        txtsname.setSelected(true);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        sname = mySongs.get(position).getName();
        txtsname.setText(sname);

        mediaPlayer = MediaPlayer.create(this , uri);
        mediaPlayer.start();



        //زياده ال Seekbar طول ما الاغنيه شغاله هو يزيد
         updateSeekbar = new Thread()
         {
             @Override
             public void run()
             {
                 int totalDuration = mediaPlayer.getDuration();
                 int currentPosition ;

                 try {
                     currentPosition = mediaPlayer.getCurrentPosition();
                 }
                 catch (final Exception e)
                 {
                     e.printStackTrace();
                     if (e instanceof IllegalStateException) { // bypass IllegalStateException
                         // You can again call the method and make a counter for deadlock situation or implement your own code according to your situation
                         boolean checkAgain = true;
                         int counter = 0;
                         for(int i = 0; i < 2; i++){
                             if (checkAgain) {
                                 mediaPlayer.reset();
                                 if(mediaPlayer != null & mediaPlayer.isPlaying()) {
                                     currentPosition = mediaPlayer.getCurrentPosition();
                                 } else {
                                     currentPosition = 0;
                                 }
                                 if(currentPosition > 0) {
                                     checkAgain = false;
                                     counter++;
                                 }
                             } else {
                                 if(counter == 0){
                                     throw e;
                                 }
                             }
                         }


                     }
                 }
             }
         };


        seekmusic.setMax(mediaPlayer.getDuration());
        updateSeekbar.start();
        seekmusic.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary) , PorterDuff.Mode.MULTIPLY);
        seekmusic.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary) , PorterDuff.Mode.SRC_IN);


        seekmusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });





        String endTime = createTime(mediaPlayer.getDuration());
        txtddtop.setText(endTime);

        final Handler handler = new Handler();

        final int delay = 1000;

        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                String currentTime = createTime(mediaPlayer.getCurrentPosition());
                txtsstart.setText(currentTime);
                handler.postDelayed(this , delay);

            }
        } , delay);



        btnPlay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(mediaPlayer.isPlaying())
                {
                    btnPlay.setBackgroundResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                }
                else
                {
                    btnPlay.setBackgroundResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                }
            }
        });


        //next song automatic
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer)
            {
                btnNext.performClick();
            }
        });



        btnNext.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position+1)%mySongs.size());
                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext() , u);
                sname = mySongs.get(position).getName();
                txtsname.setText(sname);
                mediaPlayer.start();
                btnPlay.setBackgroundResource(R.drawable.ic_pause);
                startAnimation(imageView);

            }
        });


        btnPrev.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position-1)<0)? (mySongs.size()-1) : (position-1);
                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext() , u);
                sname = mySongs.get(position).getName();
                txtsname.setText(sname);
                mediaPlayer.start();
                btnPlay.setBackgroundResource(R.drawable.ic_pause);
                startAnimation(imageView);

            }
        });



        btnFR.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(mediaPlayer.isPlaying())
                {
                   mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);

                }
            }
        });


        btnFF.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);

                }
            }
        });


    }


    public void startAnimation(View view)
    {
        ObjectAnimator animator =  ObjectAnimator.ofFloat(imageView , "rotation" , 0f , 360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }


    public  String createTime(int duration)
    {
      String time = "";

      int min = duration/1000/60;

      int sec = duration/1000%60;

      time+=min+":";

      if(sec<10)
      {
          time+="0";
      }

      time+=sec;

      return time;

    }


}