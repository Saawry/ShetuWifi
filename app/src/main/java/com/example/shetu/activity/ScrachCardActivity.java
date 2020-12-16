package com.example.shetu.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.cooltechworks.views.ScratchImageView;
import com.cooltechworks.views.ScratchTextView;
import com.example.shetu.R;

public class ScrachCardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrach_card);

        ScratchImageView scratchImageView = findViewById(R.id.sample_image);

        //isRevealed() - tells whether the text/image has been revealed.
        scratchImageView.setRevealListener(new ScratchImageView.IRevealListener() {
            @Override
            public void onRevealed(ScratchImageView tv) {
                // on reveal
            }

            @Override
            public void onRevealPercentChangedListener(ScratchImageView siv, float percent) {
                // on image percent reveal
            }
        });


        ScratchTextView scratchTextView = findViewById(R.id.simple_text);
        //ScratchTextView scratchTextView = new ScratchTextView(this);

        scratchTextView.setRevealListener(new ScratchTextView.IRevealListener() {
            @Override
            public void onRevealed(ScratchTextView tv) {
                //on reveal
            }


            @Override
            public void onRevealPercentChangedListener(ScratchTextView stv, float percent) {
                // on text percent reveal
            }
        });
    }
}