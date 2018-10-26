package theshakers.cmpt276.sfu.ca.robottelepresense;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import theshakers.cmpt276.sfu.ca.robottelepresense.UI.CustomExpandableListAdapter;
import theshakers.cmpt276.sfu.ca.robottelepresense.UI.ExpandableListDataPump;

/**
 * Created by baesubin on 2018-10-24.
 */

//Help page activity gives information how to connect to Pepper and how to use it
public class HelpPageActivity extends AppCompatActivity {
    private final String TAG = "HelpPageActivity";
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
}
