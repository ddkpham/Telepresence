package theshakers.cmpt276.sfu.ca.robottelepresense;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import theshakers.cmpt276.sfu.ca.robottelepresense.CloudServer.ResponseCallback.StringResponseCallback;
import theshakers.cmpt276.sfu.ca.robottelepresense.CloudServer.requestUserAndAuthAsyncTask;

/**
 * Created by baesubin on 2018-11-08.
 */

// This Activity is for showing all authorized peppers which current user can use
public class PepperListActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "PepperListActivity";
    private Context context = null;

    private ArrayList<String> authorizedPepperStrArr = null;
    private ArrayList<String> requestedPepperStrArr = null;
    private ListView authorizedPepperListView = null;
    private ArrayAdapter authorizedPepperAdapter = null;
    private ListView requestedPepperListView = null;
    private ArrayAdapter requestedPepperAdapter = null;

    private Button addPepperBtn = null;
    private String selected_pepper_id =  null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pepper_list);
        context = this;

        authorizedPepperStrArr = new ArrayList<String>();
        requestedPepperStrArr = new ArrayList<String>();

        SharedPreferences sharedPreferences = context.getSharedPreferences("userdetails", context.MODE_PRIVATE);
        try {
            JSONArray authorizedPepperJsonArray = new JSONArray(sharedPreferences.getString("pepper_list", "[]"));
            for (int i = 0; i < authorizedPepperJsonArray.length(); i++) {
                Log.i(TAG, authorizedPepperJsonArray.getString(i));
                authorizedPepperStrArr.add(authorizedPepperJsonArray.getString(i));
            }
            JSONArray requestedPepperJsonArray = new JSONArray(sharedPreferences.getString("request_list", "[]"));
            for (int i = 0; i < requestedPepperJsonArray.length(); i++) {
                Log.i(TAG, requestedPepperJsonArray.getString(i));
                requestedPepperStrArr.add(requestedPepperJsonArray.getString(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        authorizedPepperListView = (ListView) findViewById(R.id.authorized_pepper_list);
        authorizedPepperAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, authorizedPepperStrArr) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
                return tv;

            }
        };

        authorizedPepperListView.setAdapter(authorizedPepperAdapter);
        authorizedPepperListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                selected_pepper_id = (String) parent.getItemAtPosition(position);
                Log.i(TAG, "selected_pepper_id: "+selected_pepper_id);

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(PepperListActivity.this);
                dialogBuilder.setMessage(context.getString(R.string.what_action_do_you_want_to_take));
                dialogBuilder.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startMenuActivity();
                    }
                });

                dialogBuilder.setNegativeButton("DeAuth", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendDeAuthRequest();
                    }
                });

                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                return true;
            }
        });

        requestedPepperListView = (ListView) findViewById(R.id.requested_pepper_list);
        requestedPepperAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, requestedPepperStrArr) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
                return tv;

            }
        };

        authorizedPepperListView.setAdapter(authorizedPepperAdapter);
        requestedPepperListView.setAdapter(requestedPepperAdapter);

        addPepperBtn = (Button)findViewById(R.id.addBtn);
        addPepperBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addBtn:
                requestPepperAuth();
                break;
        }
    }

    private void sendDeAuthRequest() {
        JSONObject jsonData = new JSONObject();
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences("userdetails", context.MODE_PRIVATE);
            jsonData.put("pep_id", selected_pepper_id);
            Log.i(TAG, "pep_id: " +selected_pepper_id);
            jsonData.put("username", sharedPreferences.getString("username", ""));
            jsonData.put("ASK", sharedPreferences.getString("ASK", ""));
            jsonData.put("PSK", sharedPreferences.getString("PSK", ""));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestUserAndAuthAsyncTask requestUserAndAuthAsyncTask = new requestUserAndAuthAsyncTask(this, "deAuth", new StringResponseCallback() {
            @Override
            public void onResponseReceived(String result) {
                if(result=="OK"){
                    Toast.makeText(getApplicationContext(), context.getString(R.string.request_sent), Toast.LENGTH_SHORT).show();

                    for(int i=0; i<authorizedPepperStrArr.size(); i++)
                    {
                        if(authorizedPepperStrArr.get(i) == selected_pepper_id){
                            authorizedPepperStrArr.remove(i);
                            break;
                        }
                    }

                    authorizedPepperAdapter.notifyDataSetChanged();
                    selected_pepper_id = "";
                } else {
                    Toast.makeText(getApplicationContext(), context.getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
                }
            }
        });
        requestUserAndAuthAsyncTask.execute(jsonData);
    }

    private void startMenuActivity() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("userdetails", context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("selected_pepper_id", selected_pepper_id);
        Log.i(TAG,"selected_pepper_id: "+selected_pepper_id);
        edit.apply();

        Intent intent  = new Intent(this, MenuActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void requestPepperAuth() {
        Intent intent  = new Intent(this, RequestAuthoActivity.class);
        startActivity(intent);
        this.finish();
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
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(PepperListActivity.this);
        dialogBuilder.setMessage(context.getString(R.string.do_you_really_want_to_logout));
        dialogBuilder.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("userdetails", context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear().commit();
                Intent intent = new Intent(PepperListActivity.this, LoginActivity.class);
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
