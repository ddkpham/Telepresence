package theshakers.cmpt276.sfu.ca.robottelepresense.Utility;

import android.content.Context;
import android.support.v4.app.FragmentStatePagerAdapter;

import theshakers.cmpt276.sfu.ca.robottelepresense.R;

/**
 * Created by baesubin on 2018-11-04.
 */

// This class is Pager Adapter For MainActivity
public class MainActivityPagerAdapter extends FragmentStatePagerAdapter {
    private final String TAG = "MainActivityPagerAdapter";
    public static final int ID_CHAT = 0;
    public static final int ID_HELP_PAGE = 1;
    private Context context;

    public MainActivityPagerAdapter(Context context, android.support.v4.app.FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public android.support.v4.app.Fragment getItem (int position) {
        String title = null;
        String description = null;
        switch (position) {
            case ID_CHAT:
                title = context.getString(R.string.title_chat);
                description = context.getString(R.string.description_chat);
                break;
            case ID_HELP_PAGE:
                title = context.getString(R.string.title_help);
                description = context.getString(R.string.description_help);
                break;
        }
        return MenuCardFragment.newInstance(position, title, description);
    }

    @Override
    public int getCount() {
        return 2;
    }
}
