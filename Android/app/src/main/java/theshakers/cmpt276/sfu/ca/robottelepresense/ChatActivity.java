package theshakers.cmpt276.sfu.ca.robottelepresense;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.io.File;
import java.util.Date;
import java.util.HashMap;

import theshakers.cmpt276.sfu.ca.robottelepresense.Model.Author;
import theshakers.cmpt276.sfu.ca.robottelepresense.Model.Message;
import theshakers.cmpt276.sfu.ca.robottelepresense.WebServer.ResponseCallback.JsonResponseCallback;
import theshakers.cmpt276.sfu.ca.robottelepresense.WebServer.SendAndReceiveJsonAsyncTask;
import theshakers.cmpt276.sfu.ca.robottelepresense.WebServer.UploadPhotoAsyncTask;

/**
 * Created by baesubin on 2018-10-22.
 */

// ChatActivity creates chat window allows to send commands to Pepper
public class ChatActivity extends AppCompatActivity {
    private final String TAG = "ChatActivity";
    private final int REQUEST_PHOTOS_FROM_GALLERY_CODE = 1112;
    private final int REQUEST_PHOTOS_FOR_SENDING_TO_SERVER = 1113;
    private MessageInput inputView = null;
    private MessagesList messagesList = null;
    private MessagesListAdapter<Message> adapter = null;
    private String[] permissionList = null;
    private String senderId = "User";
    private HashMap<String, String> param = null;
    private HashMap<String, String> files = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        permissionList = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        checkPermission();

        inputView = (MessageInput) findViewById (R.id.input);
        messagesList = (MessagesList) findViewById (R.id.messagesList);

        adapter = new MessagesListAdapter<>(senderId, new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {
                Picasso.get().load(url).into(imageView);
            }
        });

        messagesList.setAdapter(adapter);
        sendAndReceiveJsonFromWebServer("Connect");

        inputView.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                addMsgToAdapter("User", input.toString());
                sendAndReceiveJsonFromWebServer(input.toString());
                return true;
            }
        });

        inputView.setAttachmentsListener(new MessageInput.AttachmentsListener() {
            @Override
            public void onAddAttachments() {
                selectPhotoFromGallery();
            }
        });

        files = new HashMap<String, String>();
        param = new HashMap<String, String>();
        param.put("id", "id");
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissionList, REQUEST_PHOTOS_FOR_SENDING_TO_SERVER);
        } else {
            // Permission has already been granted
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHOTOS_FOR_SENDING_TO_SERVER: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    ActivityCompat.requestPermissions(this, permissionList, REQUEST_PHOTOS_FOR_SENDING_TO_SERVER);
                }
                return;
            }
        }
    }

    private void selectPhotoFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_PHOTOS_FROM_GALLERY_CODE);
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

    private void sendPhotoToServer() {
        new UploadPhotoAsyncTask(this, param, files).execute();
    }

    private void getPicture(Uri imgUri) {
        String imagePath = getRealPathFromURI(imgUri);
        sendPhotoToServer();
        files.put("file", imagePath);
        try {
            addPhotoToAdapter(senderId, new File(imagePath).toURI().toURL().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void addPhotoToAdapter(String id, String url) {
        Log.i(TAG, "url: "+ url);
        Message message;
        Author author = new Author(id, id, "null");
        message = new Message(id, "", author, new Date(), url);
        adapter.addToStart(message, true);
    }

    private void addMsgToAdapter(String id, String inputText) {
        Message message;
        Author author = new Author(id, id, "null");
        message = new Message(id, inputText, author, new Date(), null);
        adapter.addToStart(message, true);
    }

    private void sendAndReceiveJsonFromWebServer(String inputText) {
        SendAndReceiveJsonAsyncTask sendAndReceiveJsonAsyncTask = new SendAndReceiveJsonAsyncTask(this, new JsonResponseCallback() {
            @Override
            public void onResponseReceived(String result) {
                addMsgToAdapter("Pepper", result);
            }
        });
        sendAndReceiveJsonAsyncTask.execute(inputText);
    }
}

