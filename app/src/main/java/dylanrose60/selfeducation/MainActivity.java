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
import dylanrose60.selfeducation.SubjectFragment.MySubjectsFragment;
import dylanrose60.selfeducation.tabs.SlidingTabLayout;

/*
    * Main activity has a welcome screen with 2 main uses
    * Button: Create new subject
    * Button: View my subjects
 */

@SuppressLint("NewApi")
public class MainActivity extends ActionBarActivity implements
        SubjectManager.Listener,
        NewSubjectDialog.Listener  {

    protected OkHttpClient client = new OkHttpClient();
    private List<Subject> list;
    private SubjectManager subjectManager = new SubjectManager();

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



        //UNDER CONSTRUCTION
        //ListView list = (ListView) findViewById(R.id.subjectList);
        //registerForContextMenu(list);
        //viewController();
        //getSubjects();
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
    public void getSubjectInfo(Bundle info) {
        String subjectName = info.getString("subject");
        String privacy = info.getString("privacy");
        int serialID = info.getInt("serialID");
        subjectManager.setListener(this);
        subjectManager.setSubject(subjectName);
        subjectManager.setPrivacy(privacy);
        subjectManager.setSerialID(serialID);
        subjectManager.create();
    }

    @Override
    public void callBack() {
        SnackbarManager.show(Snackbar.with(getApplicationContext()).text("Created Successfully"),this);
        if (mySubsFrag != null) {
            mySubsFrag.getLocalSubjects();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu,View v,ContextMenu.ContextMenuInfo info) {
        super.onCreateContextMenu(menu,v,info);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.subject_list_menu,menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Subject selectedSubject = list.get(info.position);
        String subjectName = selectedSubject.getSubjectName();
        switch(item.getItemId()) {
            case R.id.delete :
                deleteConfirm(subjectName);
                return true;
            default:
                return false;
        }
    }

    public void deleteConfirm(final String subject) {
        SnackbarManager.show(Snackbar.with(getApplicationContext())
        .text("Are you sure?")
        .actionLabel("Confirm")
        .actionColor(Color.RED)
        .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
        .actionListener(new ActionClickListener() {
            @Override
            public void onActionClicked(Snackbar snackbar) {
                snackbar.dismiss();
                deleteSubject(subject);
            }
        }),this);
    }

    public void deleteSubject(String subject) {
        SubjectManager manager = new SubjectManager();
        manager.setListener(this);
        manager.setSubject(subject);
        manager.deleteSubject();
    }

    @Override
    public void deletedCallback() {
        SnackbarManager.show(Snackbar.with(getApplicationContext()).text("Deleted Successfully"),this);
        //getSubjects();
    }






}
