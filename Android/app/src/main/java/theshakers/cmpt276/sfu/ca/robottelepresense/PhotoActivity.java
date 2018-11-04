package theshakers.cmpt276.sfu.ca.robottelepresense;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;
import java.util.HashMap;

import theshakers.cmpt276.sfu.ca.robottelepresense.WebServer.UploadPhotoAsyncTask;

// PhotoActivity allows you to select photo from your gallery then send it to pepper
public class PhotoActivity extends AppCompatActivity implements View.OnClickListener{
    private final String TAG = "PhotoActivity";
    private final int REQUEST_PHOTOS_FROM_GALLERY_CODE = 1112;
    private final int REQUEST_PHOTOS_FOR_PICTURES = 1113;
    private Button selectBtn = null;
    private Button sendBtn = null;
    private ImageView imageView = null;
    private String[] permissionList = null;
    private HashMap<String, String> param = null;
    private HashMap<String, String> files = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        permissionList = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        checkPermission();

        selectBtn = (Button)findViewById(R.id.selectBtn);
        sendBtn = (Button)findViewById(R.id.sendBtn);
        imageView = (ImageView) findViewById(R.id.imageView);

        selectBtn.setOnClickListener(this);
        sendBtn.setOnClickListener(this);

        param = new HashMap<String, String>();
        param.put("id", "id");
        files = new HashMap<String, String>();
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.selectBtn:
                selectGallery();
                break;
            case R.id.sendBtn:
                sendPhoto();
                break;
        }
    }

    private void sendPhoto() {
        new UploadPhotoAsyncTask(this, param, files).execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // when user chose picture if not RESULT_CANCEL
            switch (requestCode) {
                case REQUEST_PHOTOS_FROM_GALLERY_CODE:
                    getPicture(data.getData());
                    break;
                default:
                    break;
            }
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissionList, REQUEST_PHOTOS_FOR_PICTURES);
        } else {
            // Permission has already been granted
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHOTOS_FOR_PICTURES: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    ActivityCompat.requestPermissions(this, permissionList, REQUEST_PHOTOS_FOR_PICTURES);
                }
                return;
            }
        }
    }

    private void selectGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_PHOTOS_FROM_GALLERY_CODE);
    }

    private void getPicture(Uri imgUri) {
        String imagePath = getRealPathFromURI(imgUri);
        files.put("file", imagePath);
        Log.i(TAG, "getPath(): " + imagePath);

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = exifOrientationToDegrees(exifOrientation);

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        imageView.setImageBitmap(rotate(bitmap, exifDegree));
    }

    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private String getRealPathFromURI(Uri contentUri) {
        int column_index=0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }
        return cursor.getString(column_index);
    }

    private Bitmap rotate(Bitmap src, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }
}
