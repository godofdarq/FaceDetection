package com.example.facedetection;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;
import com.zolad.zoominimageview.ZoomInImageView;

public class MainActivity extends AppCompatActivity {
    Button button1, button2;
    ZoomInImageView zoomInImageViewo;
    private static final int REQUEST_CODE1 = 101;
    private static final int REQUEST_CODE2 = 102;
    private static Bitmap bitmap = null;
    private static Bitmap tempBitmap;
    private static Paint paint;
    private static Canvas canvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        zoomInImageViewo = findViewById(R.id.imageView);
    }

    public void selectImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(intent, REQUEST_CODE1);

    }

    public void processCapture(View view) {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE1) {
            zoomInImageViewo.setImageURI(data.getData());
            paint = new Paint();

            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(4);
            paint.setStyle(Paint.Style.STROKE);
            BitmapDrawable bitmapDrawable = (BitmapDrawable) zoomInImageViewo.getDrawable();
            bitmap = bitmapDrawable.getBitmap();


            tempBitmap = bitmap.copy(bitmap.getConfig(), true);

            canvas = new Canvas(tempBitmap);
            processImage();
        }
        if (requestCode == REQUEST_CODE2) {
            Bundle bundle = data.getExtras();
            bitmap = (Bitmap) bundle.get("data");
            zoomInImageViewo.setImageBitmap(bitmap);
            paint = new Paint();

            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(4);
            paint.setStyle(Paint.Style.STROKE);


            tempBitmap = bitmap.copy(bitmap.getConfig(), true);

            canvas = new Canvas(tempBitmap);
        }
    }

    private void processImage() {
        FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.FAST_MODE)
                .build();

        if (faceDetector.isOperational()) {
            Toast.makeText(getApplicationContext(), "Not Functional", Toast.LENGTH_LONG).show();
           return;
        }
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faceSparseArray = faceDetector.detect(frame);

        for (int i = 0; i < faceSparseArray.size(); i++) {
            Face face = faceSparseArray.valueAt(i);
            float x1, y1, x2, y2;

            x1 = face.getPosition().x;
            y1 = face.getPosition().y;

            x2 = x1 + face.getWidth();
            y2 = y1 + face.getHeight();


            RectF rectF = new RectF(x1, y1, x2, y2);
            paint.setColor(Color.WHITE);
            canvas.drawRoundRect(rectF, 2, 2, paint);


            for (Landmark landmark : face.getLandmarks()) {
                int cx, cy;
                cx = (int) landmark.getPosition().x;
                cy = (int) landmark.getPosition().y;


                paint.setColor(Color.GREEN);
                if (faceSparseArray.size() > 1) {
                    canvas.drawCircle(cx, cy, faceSparseArray.size() / 2, paint);
                } else {
                    canvas.drawCircle(cx, cy, faceSparseArray.size() + 5, paint);
                }
            }


        }


        zoomInImageViewo.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));

        int face_detected = faceSparseArray.size();

        Toast.makeText(getApplicationContext(), "No. of faces detected : "+face_detected+ " ", Toast.LENGTH_SHORT).show();
    }
}