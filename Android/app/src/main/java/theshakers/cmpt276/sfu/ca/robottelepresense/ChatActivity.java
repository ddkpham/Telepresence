package theshakers.cmpt276.sfu.ca.robottelepresense;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.Date;

import theshakers.cmpt276.sfu.ca.robottelepresense.Model.Author;
import theshakers.cmpt276.sfu.ca.robottelepresense.Model.Message;
import theshakers.cmpt276.sfu.ca.robottelepresense.SocketServer_Unused.SocketServerClient;
import theshakers.cmpt276.sfu.ca.robottelepresense.SocketServer_Unused.SocketServerResponseCallback;
import theshakers.cmpt276.sfu.ca.robottelepresense.WebServer.JsonResponseCallback;
import theshakers.cmpt276.sfu.ca.robottelepresense.WebServer.SendAndReceiveJsonAsyncTask;

/**
 * Created by baesubin on 2018-10-22.
 */

//chat activity creates chat window allows to send commands to Pepper
public class ChatActivity extends AppCompatActivity {
    private final String TAG = "ChatActivity";
    private MessageInput inputView = null;
    private MessagesList messagesList = null;
    private MessagesListAdapter<Message> adapter = null;
    private String senderId = "User";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        inputView = (MessageInput) findViewById (R.id.input);
        messagesList = (MessagesList) findViewById (R.id.messagesList);

        adapter = new MessagesListAdapter<>(senderId, null);
        messagesList.setAdapter(adapter);

        //sendAndReceiveMsgFromSocketServer("Connect");
        sendAndReceiveJsonFromWebserver("Connect");

        inputView.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                addMsgToAdapter("User", input.toString());
                sendAndReceiveJsonFromWebserver(input.toString());
                //sendAndReceiveMsgFromSocketServer(input.toString());
                return true;
            }
        });
    }

    private void addMsgToAdapter(String id, String inputText) {
        Message message;
        Author author = new Author(id, id, "null");
        message = new Message(id, inputText, author, new Date());
        adapter.addToStart(message, true);
    }

    private void sendAndReceiveJsonFromWebserver(String inputText) {
        SendAndReceiveJsonAsyncTask sendAndReceiveJsonAsyncTask = new SendAndReceiveJsonAsyncTask(this, new JsonResponseCallback() {
            @Override
            public void onResponseReceived(String result) {
                addMsgToAdapter("Pepper", result);
            }
        });
        sendAndReceiveJsonAsyncTask.execute(inputText);
    }

    /*
    private void sendAndReceiveMsgFromSocketServer(String inputText) {
        SocketServerClient socketServerClient =new SocketServerClient(this, new SocketServerResponseCallback() {
            @Override
            public void onResponseReceived(String str) {
                addMsgToAdapter("Pepper", str);
            }
        });
        socketServerClient.execute(inputText);
    }*/
}

