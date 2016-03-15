package barqsoft.footballscores;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import barqsoft.footballscores.api.DatabaseProvider;
import barqsoft.footballscores.api.FixtureManager;

import barqsoft.footballscores.model.Season;
import barqsoft.footballscores.service.SoccerService;
import barqsoft.footballscores.service.TeamService;
import database.DaoHelper;

public class MainActivity extends AppCompatActivity
{
    private static final String LOG_TAG =MainActivity.class.getSimpleName();
    private static final String RECEIVER = "RECEIVER";

    private ProgressDialog mProgressDialog;
    private FixtureManager mFixtureManager;
    private barqsoft.footballscores.api.Provider mProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG, "Reached MainActivity onCreate");

        //Check if all teams are downloaded
        startServiceTeam();

        //gets Fixture Manager
        mFixtureManager = ((MainApplication) getApplication()).getFixtureManager();
        //gets provider
        mProvider = ((MainApplication) getApplication()).getProvider();

        if (mProvider instanceof DatabaseProvider) {
            Toast.makeText(this, getString(R.string.internet_is_not_available), Toast.LENGTH_SHORT).show();
        }

        //Prepares tabs
        String[] datesTitle = getTitlesForTabBar();

        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);

       
        //Setup view pager
        ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        setupViewPager(mViewPager, getDatesYYYYMMDD());

        tabs.setupWithViewPager(mViewPager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabs.removeAllTabs();

        for (String date : datesTitle) {
            TabLayout.Tab tab = tabs.newTab();
            tabs.addTab(tab.setText(date));
        }

        //select tab Today. Remember that it uses 3 days before and 3 days next.
        TabLayout.Tab tab = tabs.getTabAt(3);
        if (tab != null) {
            tab.select();
        }

        //prepares for a progress bar dialog
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setMessage(getString(R.string.update_teams));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setProgress(0);
        mProgressDialog.setMax(50);

        DaoHelper daoHelper = new DaoHelper(this);

        if (daoHelper.getLastItemId() == 0) {
            mProgressDialog.show();
        } else {
            mProgressDialog.hide();
        }

        //creates alarm to clean database every week
        createAlarmToCleanDatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about)
        {
            Intent start_about = new Intent(this,AboutActivity.class);
            startActivity(start_about);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }
    @Override
    protected void onResume() {
        super.onResume();

        MessageReceiver messageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(TeamService.NOTIFICATION_GET_TEAMS);
        filter.addAction(TeamService.NOTIFICATION_SPINNER_STATUS);
        filter.addAction(SoccerService.NOTIFICATION_CLEAN_DATABASE);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, filter);
    }

    private void startServiceTeam() {
        Intent intent = new Intent(this, TeamService.class);
        intent.setAction(TeamService.ACTION_GET_TEAMS);
        intent.putExtra(RECEIVER, new DownReceiver(new Handler()));
        startService(intent);
    }
    private class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(TeamService.NOTIFICATION_GET_TEAMS)) {
                mProgressDialog.hide();
            }

            if (intent.getAction().equals(TeamService.NOTIFICATION_SPINNER_STATUS)) {
                String status = intent.getStringExtra(TeamService.SPINNER_STATUS);

                if (status.equals(TeamService.SPINNER_NOT_ACTIVE)) {
                    mProgressDialog.hide();

                    ArrayList<Season> seasons = mFixtureManager.getSeasons();
                    if (seasons != null && seasons.isEmpty()) {
                        fetchFixtures();
                    }
                }
            }

            if (intent.getAction().equals(SoccerService.NOTIFICATION_CLEAN_DATABASE)) {
                String msg = intent.getStringExtra(SoccerService.PARAM_CLEAN_DATABASE);
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                startServiceTeam();
            }
        }
    }

    private void fetchFixtures() {
        //asks for fixtures
        ArrayList<Season> seasons = mFixtureManager.getSeasons();
        if (seasons != null && seasons.isEmpty()) {
            mFixtureManager.obtainSeasons(mProvider);
        }
    }

    private void setupViewPager(ViewPager viewPager, String[] dates) {

        final int NUM_PAGES = 7;
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager(), mFixtureManager, dates);

        //init fragments
        for (int i = 0; i < NUM_PAGES; i++) {
            adapter.getItem(i);
        }

        viewPager.setAdapter(adapter);
    }

    private String[] getTitlesForTabBar() {

        Date[] previousDates = getLastThreeDays();
        Date[] nextDates = getNextThreeDays();

        int size = previousDates.length + nextDates.length + 1;

        String[] datesWithFormat = new String[size];

        SimpleDateFormat format = new SimpleDateFormat(getString(R.string.format_EEE_dd_MMM), Locale.getDefault());

        for (int i = 0; i < 3; i++) {
            datesWithFormat[i] = format.format(previousDates[i]);
        }

        datesWithFormat[2] = getString(R.string.yesterday);
        datesWithFormat[3] = getString(R.string.today);
        datesWithFormat[4] = getString(R.string.tomorrow);

        for (int i = 1; i < 3; i++) {
            datesWithFormat[i + 4] = format.format(nextDates[i]);
        }

        return datesWithFormat;
    }

    private String[] getDatesYYYYMMDD() {
        Date[] previousDates = getLastThreeDays();
        Date[] nextDates = getNextThreeDays();

        int size = previousDates.length + nextDates.length + 1;

        String[] datesWithFormat = new String[size];
        SimpleDateFormat format = new SimpleDateFormat(getString(R.string.format_yyyy_MM_dd), Locale.getDefault());

        for (int i = 0; i < 3; i++) {
            datesWithFormat[i] = format.format(previousDates[i]);
        }

        datesWithFormat[3] = format.format(new Date());

        for (int i = 1; i < 3; i++) {
            datesWithFormat[i + 3] = format.format(nextDates[i]);
        }

        return datesWithFormat;
    }

    private Date[] getNextThreeDays() {

        Date[] dates = new Date[3];

        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(new Date());

        for (int i = 0; i < dates.length; i++) {
            cal.add(Calendar.DATE, +1);
            dates[i] = cal.getTime();
        }

        return dates;
    }

    private Date[] getLastThreeDays() {

        Date[] dates = new Date[3];
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(new Date());

        for (int i = 2; i >= 0; i--) {
            cal.add(Calendar.DATE, -1);
            dates[i] = cal.getTime();
        }

        return dates;
    }
    private class DownReceiver extends ResultReceiver {

        public DownReceiver(Handler handler) {
            super(handler);
        }

        @Override
        public void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == TeamService.NEW_PROGRESS) {
                int progress = resultData.getInt(TeamService.PROGRESS);
                mProgressDialog.setProgress(progress);
            }
        }
    }

    private class SectionsPagerAdapter extends PagerAdapter {

        String[] mDates = new String[7];
        FixtureManager mFixtureManager;
        FragmentManager fragmentManager;
        SparseArray<Fragment> mFragments;

        public SectionsPagerAdapter(FragmentManager fm, FixtureManager fixtureManager, String[] dates) {
            fragmentManager = fm;
            mFixtureManager = fixtureManager;
            mDates = dates;
            mFragments = new SparseArray<>(7);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            FragmentTransaction trans = fragmentManager.beginTransaction();
            trans.remove(mFragments.get(position));
            trans.commit();
            mFragments.put(position, null);
        }

        @Override
        public Fragment instantiateItem(ViewGroup container, int position) {
            Fragment fragment = getItem(position);
            FragmentTransaction trans = fragmentManager.beginTransaction();
            trans.add(container.getId(), fragment, "fragment:" + position);
            trans.commit();
            return fragment;
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object fragment) {
            return ((Fragment) fragment).getView() == view;
        }

        public Fragment getItem(int position) {
            if (mFragments.get(position) == null) {
                FixtureFragment fixtureFragment = new FixtureFragment();
                fixtureFragment.setDate(mDates[position]);
                fixtureFragment.setFixtureFragment(mFixtureManager);
                mFragments.put(position, fixtureFragment);
            }
            return mFragments.get(position);
        }
    }

    /**
     * Creates an alarm which will be trigger a intent in order to clean the database. This will be
     * done after each week.
     */
    private void createAlarmToCleanDatabase() {
        Intent intent = new Intent(this, SoccerService.class);
        intent.setAction(SoccerService.ACTION_CLEAN_DATABASE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), alarmManager.INTERVAL_DAY * 8, pendingIntent);
    }

}
