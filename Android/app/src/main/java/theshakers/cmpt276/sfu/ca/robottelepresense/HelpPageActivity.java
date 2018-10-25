package theshakers.cmpt276.sfu.ca.robottelepresense;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by baesubin on 2018-10-24.
 */

//Help page activity givess information how to connect to Pepper and how to use it
public class HelpPageActivity extends Activity {
    private final String TAG = "HelpPageActivity";
    private Button goBackBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        goBackBtn = (Button) findViewById(R.id.mainMenu);
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
