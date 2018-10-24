package theshakers.cmpt276.sfu.ca.robottelepresense;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button connectBtn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectBtn = (Button)findViewById(R.id.connectBtn);
        connectBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //new ServerConnection().execute();
    }
}
