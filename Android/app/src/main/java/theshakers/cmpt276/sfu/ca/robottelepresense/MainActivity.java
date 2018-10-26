package theshakers.cmpt276.sfu.ca.robottelepresense;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

//Main activity contains two buttons: Connect button and Help Page button
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "MainActivity";
    private Button connectBtn = null;
    private Button helpPageBtn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectBtn = (Button)findViewById(R.id.connectBtn);
        connectBtn.setOnClickListener(this);

        helpPageBtn = (Button)findViewById(R.id.helpPageBtn);
        helpPageBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.connectBtn:
                intent = new Intent(this, ChatActivity.class);
                startActivity(intent);
                break;
            case R.id.helpPageBtn:
                intent = new  Intent (this, HelpPageActivity.class);
                startActivity(intent);
                break;
        }
    }
}
