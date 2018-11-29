package theshakers.cmpt276.sfu.ca.robottelepresense;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

import me.relex.circleindicator.CircleIndicator;
import theshakers.cmpt276.sfu.ca.robottelepresense.Utility.MenuCardFragment;
import theshakers.cmpt276.sfu.ca.robottelepresense.Utility.MainActivityPagerAdapter;

// Menu activity allows user to choose Chat Mode, Game Mode, Help Page
public class MenuActivity extends AppCompatActivity implements MenuCardFragment.OnActionListener {
    private final String TAG = "MenuActivity";
    private Context context = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_menu);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new MainActivityPagerAdapter(this, getSupportFragmentManager()));
        pager.setPageMargin((int) getResources().getDimension(R.dimen.card_padding) / 4);
        pager.setOffscreenPageLimit(3);

        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(pager);

    }
    @Override
    public void onAction(int id) {
        Intent intent = null;
        switch (id) {
            case MainActivityPagerAdapter.ID_CHAT:
                intent = new Intent(this, ChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                break;
            case MainActivityPagerAdapter.ID_HELP_PAGE:
                intent = new Intent(this, HelpPageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                break;
            case MainActivityPagerAdapter.ID_GAME:
                intent = new Intent(this, RequestGameActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();

        }
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
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MenuActivity.this);
        dialogBuilder.setMessage(context.getString(R.string.do_you_really_want_to_logout));
        dialogBuilder.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPreferences = context.getSharedPreferences("userdetails", context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear().commit();
                Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
