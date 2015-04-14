package dylanrose60.selfeducation;

import android.annotation.SuppressLint;

import android.app.FragmentManager;
import android.graphics.Color;

import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import java.io.IOException;
import java.util.List;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONStringer;

import dylanrose60.selfeducation.DialogFragment.NewCategoryDialog;
import dylanrose60.selfeducation.DialogFragment.NewSubjectDialog;
import dylanrose60.selfeducation.DialogFragment.NewTagDialog;
import dylanrose60.selfeducation.DialogFragment.SubjectCategoryDialog;
import dylanrose60.selfeducation.SubjectFragment.MySubjectsFragment;
import dylanrose60.selfeducation.tabs.SlidingTabLayout;

@SuppressLint("NewApi")
public class MainActivity extends ActionBarActivity implements
        NewSubjectDialog.Listener,
        SubjectCategoryDialog.Listener,
        NewCategoryDialog.Listener {

    //Tabs
    private ViewPager viewPager;
    private SlidingTabLayout tabLayout;

    //Fragments
    static MySubjectsFragment mySubsFrag;

    //http
    private OkHttpClient httpClient = new OkHttpClient();

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
                //Inflate a menu that asks: Category OR Subject. Inflate respective dialog after selecton
                final String[] options = {"Category","Subject"};
                new MaterialDialog.Builder(this)
                        .title("Create New...")
                        .items(options)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog materialDialog, View view, int selected, CharSequence charSequence) {
                                String selection = options[selected];
                                if (selection.equals("Subject")) {
                                    FragmentManager fragmentManager = getFragmentManager();
                                    SubjectCategoryDialog categoryDialog = new SubjectCategoryDialog();
                                    categoryDialog.show(fragmentManager,"categoryDialog");
                                } else if (selection.equals("Category")) {
                                    FragmentManager fragmentManager = getFragmentManager();
                                    NewCategoryDialog newCategory = new NewCategoryDialog();
                                    newCategory.show(fragmentManager, "NewCategory");
                                }

                            }
                        })
                        .build().show();
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }

    }

    public static void setFrag0(MySubjectsFragment frag) {
        mySubsFrag = frag;
    }


    //If user adds new category from subject category selection dialog
    @Override
    public void newCategory() {
        //inflate subject category selected
        FragmentManager fragmentManager = getFragmentManager();
        //Call new category dialog, pass boolean telling it wether to create listener and callback when done
        NewCategoryDialog newCatDialog = new NewCategoryDialog();
        Bundle callback = new Bundle();
        callback.putBoolean("callback",true);
        newCatDialog.setArguments(callback);
        newCatDialog.show(fragmentManager,"newCat");
    }

    @Override
    public void subCategoryComplete(Bundle subjectInfo) {
        FragmentManager fragmentManager = getFragmentManager();
        NewSubjectDialog newSubject = new NewSubjectDialog();
        newSubject.setArguments(subjectInfo);
        newSubject.show(fragmentManager, "NewSubject");

    }

    @Override
    public void newSubComplete(Bundle subjectInfo) {
        String subJSON = subBundleJSON(subjectInfo);
        createSubject(subJSON);
    }

    @Override
    public void newCategoryAdded() {
        FragmentManager fragmentManager = getFragmentManager();
        SubjectCategoryDialog categoryDialog = new SubjectCategoryDialog();
        categoryDialog.show(fragmentManager,"categoryDialog");
    }


    public String subBundleJSON(Bundle subjectInfo) {

        String subjectName = subjectInfo.getString("subject");
        String privacy = subjectInfo.getString("privacy");
        String category = subjectInfo.getString("category");
        String ownerId = subjectInfo.getString("owner_id");

        try {
            JSONStringer json = new JSONStringer();
            json.object();
            json.key("subject");
            json.value(subjectName);
            json.key("privacy");
            json.value(privacy);
            json.key("category");
            json.value(category);
            json.key("owner_id");
            json.value(ownerId);
            json.endObject();
            return json.toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void createSubject(String json) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),json);
        Request.Builder builder = new Request.Builder();
        builder.post(body);
        builder.url("http://codeyourweb.net/httpTest/index.php/newSubject");
        Request request = builder.build();
        Call newCall = httpClient.newCall(request);
        newCall.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                mySubsFrag.onStart();
            }
        });
    }

}