package theshakers.cmpt276.sfu.ca.robottelepresense;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.Date;

import theshakers.cmpt276.sfu.ca.robottelepresense.Model.Author;
import theshakers.cmpt276.sfu.ca.robottelepresense.Model.Message;
import theshakers.cmpt276.sfu.ca.robottelepresense.R;
import theshakers.cmpt276.sfu.ca.robottelepresense.Server.Client;
import theshakers.cmpt276.sfu.ca.robottelepresense.Server.ServerResponseCallback;

/**
 * Created by baesubin on 2018-10-22.
 */

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

        sendAndReceiveMsgFromServer("Connect");

        inputView.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                addMsgToAdapter("User", input.toString());
                sendAndReceiveMsgFromServer(input.toString());
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

    private void sendAndReceiveMsgFromServer(String inputText) {
        Client client =new Client(new ServerResponseCallback() {
            @Override
            public void onResponseReceived(String str) {
                addMsgToAdapter("Pepper", str);
            }
        });
        client.execute(inputText);
    }
}

