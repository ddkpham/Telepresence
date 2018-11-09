package theshakers.cmpt276.sfu.ca.robottelepresense;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import me.relex.circleindicator.CircleIndicator;
import theshakers.cmpt276.sfu.ca.robottelepresense.Utility.MenuCardFragment;
import theshakers.cmpt276.sfu.ca.robottelepresense.Utility.MainActivityPagerAdapter;

// Main activity contains three buttons: Connect button, Photo button, and Help Page button
public class MenuActivity extends AppCompatActivity implements MenuCardFragment.OnActionListener {
    private final String TAG = "MenuActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
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
                startActivity(intent);
                break;
            case MainActivityPagerAdapter.ID_HELP_PAGE:
                intent = new Intent(this, HelpPageActivity.class);
                startActivity(intent);
                break;

        }
    }
}
