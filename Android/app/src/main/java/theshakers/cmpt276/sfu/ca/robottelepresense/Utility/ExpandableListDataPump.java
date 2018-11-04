package theshakers.cmpt276.sfu.ca.robottelepresense.Utility;

import android.content.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import theshakers.cmpt276.sfu.ca.robottelepresense.R;

/**
 * Created by baesubin on 2018-10-25.
 */

// This class is for data will be showed Help page through ExpandableList
public class ExpandableListDataPump {
    private Context context = null;

    public ExpandableListDataPump(Context context) {
        this.context = context;
    }

    public HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> simpleConversation = new ArrayList<String>();
        String conversation_str[] = context.getResources().getStringArray(R.array.simple_conversation);
        for(String i: conversation_str)
            simpleConversation.add(i);

        List<String> status = new ArrayList<String>();
        String status_str[] = context.getResources().getStringArray(R.array.status);
        for(String i: status_str)
            status.add(i);

        List<String> action = new ArrayList<String>();
        String action_str[] = context.getResources().getStringArray(R.array.action);
        for(String i: action_str)
            action.add(i);

        List<String> sensor = new ArrayList<String>();
        String sensor_str[] = context.getResources().getStringArray(R.array.sensor);
        for(String i: sensor_str)
            sensor.add(i);

        expandableListDetail.put("Simple Conversation", simpleConversation);
        expandableListDetail.put("Status", status);
        expandableListDetail.put("Action", action);
        expandableListDetail.put("Sensor", sensor);

        return expandableListDetail;
    }
}


