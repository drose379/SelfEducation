package dylanrose60.selfeducation;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

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


/*

    public void viewController() {
        ListView listElement = (ListView) findViewById(R.id.subjectList);
        listElement.setVisibility(View.GONE);
    }

    public void getSubjects() {
        final ListView listElement = (ListView) findViewById(R.id.subjectList);
        final ProgressWheel spinAnimation = (ProgressWheel) findViewById(R.id.spinAnimation1);
        final LinearLayout logoText = (LinearLayout) findViewById(R.id.logoLayout);

        Request.Builder builder = new Request.Builder();
        builder.url("http://codeyourweb.net/httpTest/index.php/getSubjects");
        Request readyRequest = builder.build();
        Call call = client.newCall(readyRequest);
        call.enqueue(new Callback() {

            @Override
            public void onResponse(final Response response) throws IOException {
                listElement.post(new Runnable() {
                    @Override
                    public void run() {
                        spinAnimation.setVisibility(View.GONE);
                        logoText.setVisibility(View.GONE);
                        listElement.setVisibility(View.VISIBLE);
                    }
                });
                String responseString = response.body().string();
                try {
                    List subjectList = toArray(responseString);
                    buildList(subjectList);
                } catch (JSONException e) {
                    //Handle errors
                }
            }
            @Override
            public void onFailure(Request request,IOException e) {

            }
        });
    }

    public List<Subject> toArray(String jsonString) throws JSONException {
        JSONArray json = new JSONArray(jsonString); //Need to pass in the real JSON String to here
        List<Subject> list = new ArrayList<Subject>();
        for(int i = 0;i<json.length();i++) {
            JSONObject jObject = json.getJSONObject(i);
            list.add(new Subject(jObject.getString("name"),jObject.getString("start_date"),jObject.getInt("lesson_count")));
        }
        return list;
    }

    public void buildList(final List array) {
        View listViewLayout = findViewById(R.id.subject_home);
        final ListView listView = (ListView) findViewById(R.id.subjectList);
        final ArrayAdapter<Subject> adapter = new CustomAdapter(getApplicationContext(),R.layout.subject_card_view,array);
        listViewLayout.post(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(adapter);
                list = array;
                //registerForContextMenu(listView);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView parent,View view,int position,long id) {
                        Subject selectedSubject = list.get(position);
                        String subjectName = selectedSubject.getSubjectName();
                        Intent newAct = new Intent(getApplicationContext(),SubjectDashboard.class);
                        newAct.putExtra("subjectName",subjectName);
                        startActivity(newAct);
                    }
                });
            }
        });

    }
*/
    @Override
    public void getSubjectInfo(Bundle info) {
        String subjectName = (String) info.get("subject");
        String privacy = (String) info.get("privacy");
        subjectManager.setListener(this);
        subjectManager.setSubject(subjectName);
        subjectManager.setPrivacy(privacy);
        subjectManager.create();
        getDemoInfo();
    }

    public void getDemoInfo() {
        DBHelper dbClient = new DBHelper(this);
        SQLiteDatabase db = dbClient.getReadableDatabase();
        String select = "SELECT ID FROM subject_info WHERE subject = ?";
        Cursor cursor = db.rawQuery(select,new String[] {"testing12"});
        if (cursor.moveToFirst()) {
            String result = cursor.getString(cursor.getColumnIndex("ID"));
            //result is the result
        }
    }

    @Override
    public void callBack() {
        SnackbarManager.show(Snackbar.with(getApplicationContext()).text("Created Successfully"),this);
        //getSubjects();
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
