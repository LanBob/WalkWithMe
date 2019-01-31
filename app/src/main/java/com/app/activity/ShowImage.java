package com.app.activity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;


import com.app.R;

import java.io.File;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ShowImage extends AppCompatActivity {
    private PhotoView photoView;
    private ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_image);
        imageButton = (ImageButton) findViewById(R.id.show_image_download);

        Intent intent = getIntent();
        String url = intent.getStringExtra("path");

        photoView = (PhotoView) findViewById(R.id.show_iamge);
        PhotoViewAttacher mAttacher;
        mAttacher = new PhotoViewAttacher(photoView);
        File file = null;
        if (url != null) {
            file = new File(url);
            if (file.exists()) {
                Bitmap bm = BitmapFactory.decodeFile(url);
                photoView.setImageBitmap(bm);
            }
        }
        mAttacher.update();
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ShowImage.this,"download",Toast.LENGTH_SHORT).show();
            }
        });

        /**
         * PhotoView mPhotoView;
         * PhotoViewAttacher mAttacher;
         *
         * mAttacher = new PhotoViewAttacher(mPhotoView);
         * mPhotoView.setImageBitmap(mBitmap);
         * mAttacher.update();
         *
         */
    }

}
