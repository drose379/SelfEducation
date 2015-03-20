package dylanrose60.selfeducation;

import android.annotation.SuppressLint;

import android.app.FragmentManager;
import android.graphics.Color;

import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.squareup.okhttp.OkHttpClient;

import dylanrose60.selfeducation.DialogFragment.NewSubjectDialog;
import dylanrose60.selfeducation.DialogFragment.NewTagDialog;
import dylanrose60.selfeducation.SubjectFragment.MySubjectsFragment;
import dylanrose60.selfeducation.tabs.SlidingTabLayout;

@SuppressLint("NewApi")
public class MainActivity extends ActionBarActivity implements
        NewSubjectDialog.Listener {

    //Tabs
    private ViewPager viewPager;
    private SlidingTabLayout tabLayout;

    //Fragments
    static MySubjectsFragment mySubsFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_listview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        setTitle("Select A Subject");

        //Tabs
        ViewPagerAdapter tabAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(tabAdapter);
        viewPager.setOffscreenPageLimit(3);
        tabLayout = (SlidingTabLayout) findViewById(R.id.tabLayout);
        tabLayout.setViewPager(viewPager);

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
        switch(item.getItemId()) {
            case R.id.addSubject :
                //newSubject();
                FragmentManager fragmentManager = getFragmentManager();
                NewSubjectDialog newSubject = new NewSubjectDialog();
                newSubject.show(fragmentManager,"NewSubject");
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }

    }

    public static void setFrag0(MySubjectsFragment frag) {
        mySubsFrag = frag;
    }

    @Override
    public void subjectCreated() {
        SnackbarManager.show(Snackbar.with(getApplicationContext()).text("Created Successfully"),this);
        if (mySubsFrag != null) {
            mySubsFrag.onStart();
        }
    }

}