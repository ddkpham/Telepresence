package theshakers.cmpt276.sfu.ca.robottelepresense;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import theshakers.cmpt276.sfu.ca.robottelepresense.Utility.CustomExpandableListAdapter;
import theshakers.cmpt276.sfu.ca.robottelepresense.Utility.ExpandableListDataPump;

/**
 * Created by baesubin on 2018-10-24.
 */

// HelpPageActivity gives information how to connect to Pepper and how to use it
public class HelpPageActivity extends AppCompatActivity {
    private final String TAG = "HelpPageActivity";
    private Context context  = null;
    private Button goBackBtn = null;
    private ExpandableListView expandableListView = null;
    private ExpandableListAdapter expandableListAdapter = null;
    private List<String> expandableListTitle = null;
    private HashMap<String, List<String>> expandableListDetail = null;
    private ExpandableListDataPump expandableListDataPump = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        context = this;

        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        goBackBtn = (Button) findViewById(R.id.mainMenu);

        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        expandableListDataPump = new ExpandableListDataPump(this);
        expandableListDetail = expandableListDataPump.getData();
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            showDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(HelpPageActivity.this);
        dialogBuilder.setMessage(context.getString(R.string.do_you_want_to_go_back));
        dialogBuilder.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(HelpPageActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });

        dialogBuilder.setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
}
