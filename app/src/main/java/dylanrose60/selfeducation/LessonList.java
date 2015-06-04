package dylanrose60.selfeducation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.etsy.android.grid.StaggeredGridView;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LessonList extends ActionBarActivity {

    private String subject;
    private String ownerID;
    private String type;


    //Testing
    private Handler handler = new Handler();
    private OkHttpClient httpClient = new OkHttpClient();

    private MaterialDialog loadingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lesson_list_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        showLoadingDialog();
        Bundle intentBundle = getIntent().getExtras();
        Bundle listParams = intentBundle.getBundle("listParams");
        type = listParams.getString("type");
        subject = listParams.getString("subject");
        ownerID = listParams.getString("ownerID");
        setTitle(subject.trim() + " | Lessons:");

        //Need to check sub type, and grab lessons accordingly
        if (type .equals("Bookmark")) {
            //Bookmark lessons API has to grab both lesson_name and imageURL
            getBookmarkLessons();
        } else if (type.equals("Local")) {
            getLocalLessons();
        } else {
            getPublicLessons();
        }

    }

    public void showLoadingDialog() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.title("One Moment please");
        builder.customView(R.layout.lesson_loader,true);
        loadingDialog = builder.build();
        loadingDialog.show();
    }

    public void dismissLoadingDialog() {
        loadingDialog.dismiss();
    }

    public void getBookmarkLessons() {
        String ownerIDJSON = null;
        List<String> key = new ArrayList<String>();
        List<String> value = new ArrayList<String>();

        key.add("owner_id");
        key.add("subject");
        value.add(ownerID);
        value.add(subject);

        try {
            ownerIDJSON = CommunicationUtil.toJSONString(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), ownerIDJSON);
        Request.Builder builder = new Request.Builder();
        builder.post(body);
        builder.url("http://codeyourweb.net/httpTest/index.php/getBookmarkLessons");
        Request request = builder.build();
        Call newCall = httpClient.newCall(request);
        newCall.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                dismissLoadingDialog();
                String responseString = response.body().string();
                Log.i("bookmarkLesson",responseString);
                try {
                    JSONObject master = new JSONObject(responseString);
                    JSONArray lessons = master.getJSONArray("lessons");
                    JSONArray tags = master.getJSONArray("tags");
                    buildList(lessons,tags,2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getLocalLessons() {
        String infoJSON = null;

        List<String> key = new ArrayList<String>();
        List<String> value = new ArrayList<String>();

        key.add("subject");
        value.add(subject);

        try {
            infoJSON = CommunicationUtil.toJSONString(key,value);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),infoJSON);
        Request.Builder builder = new Request.Builder();
        builder.post(body);
        builder.url("http://codeyourweb.net/httpTest/index.php/getLocalLessons");
        Request request = builder.build();
        Call newCall = httpClient.newCall(request);
        newCall.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                dismissLoadingDialog();
                String responseString = response.body().string();
                Log.i("localLessons", responseString);
                try {
                    JSONObject master = new JSONObject(responseString);
                    JSONArray currentLessons = master.getJSONArray("lessonInfo");
                    JSONArray tags = master.getJSONArray("tags");
                    buildList(currentLessons, tags, 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getPublicLessons() {
        String infoJSON = null;

        List<String> key = new ArrayList<String>();
        List<String> value = new ArrayList<String>();

        key.add("subject");
        value.add(subject);

        try {
            infoJSON = CommunicationUtil.toJSONString(key,value);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),infoJSON);
        Request.Builder builder = new Request.Builder();
        builder.post(body);
        builder.url("http://codeyourweb.net/httpTest/index.php/getPublicLessons");
        Request request = builder.build();
        Call newCall = httpClient.newCall(request);
        newCall.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                dismissLoadingDialog();
                String responseString = response.body().string();
                Log.i("publicLessons", responseString);
                try {
                    JSONObject master = new JSONObject(responseString);
                    JSONArray currentLessons = master.getJSONArray("lessonInfo");
                    JSONArray tags = master.getJSONArray("tags");
                    buildList(currentLessons, tags, 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void buildList(JSONArray lessons,JSONArray tags,int type) throws JSONException {

        /*
            * Type key:
            * 0 : local
            * 1 : bookmark
            * 2: public
            *
            * To get inside JSONArray use JSONObject
            * EXAMPLE: JSONObject innerArray = (JSONObject) lessons.get(0);
         */

        /*
            * Create listview in lesson_list_view.xml
            * Create card layout for each lesson
            * Create Adapter that extends ArrayAdapter, gets passed the JSONArray and the type, works according to type (names of cols to be pulled from Array)
            * Need to create List<JSONObject> instead of passing JSONArray to adapter
        */

        if (lessons.length() < 1) {
            buildLayout(null,true);
        } else {
            List<LessonPackage> lessonInfo = new ArrayList<LessonPackage>();

            for (int i = 0; i < lessons.length(); i++) {
                String currentLesson = (String) lessons.get(i);

                List<String> currentTags = new ArrayList<String>();

                //Make sure lesson names from both objects match, then create the LessonPackage

                for (int t = 0; t < tags.length(); t++) {
                    JSONObject currentTagObj = tags.getJSONObject(t);
                    String tagLesson = currentTagObj.getString("lesson");
                    if (currentLesson.equals(tagLesson)) {
                        currentTags.add(currentTagObj.getString("tag"));
                    }
                }

                lessonInfo.add(new LessonPackage(currentLesson, currentTags));
            }
            buildLayout(lessonInfo, false);
        }
    }



    public void buildLayout(final List<LessonPackage> lessonPacks,boolean isEmpty) {
        if (isEmpty) {
            final StaggeredGridView lessonGrid = (StaggeredGridView) findViewById(R.id.lessonGrid);
            final TextView notFound = (TextView) findViewById(R.id.notFound);
            final ImageView icon = (ImageView) findViewById(R.id.notFoundIcon);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    lessonGrid.setVisibility(View.GONE);
                    notFound.setVisibility(View.VISIBLE);
                    icon.setVisibility(View.VISIBLE);
                }
            });

        } else {
            final StaggeredGridView lessonGrid = (StaggeredGridView) findViewById(R.id.lessonGrid);
            lessonGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Grab item from lessonPacks using position, open lesson dash
                    Log.i("lessonClick",lessonPacks.get(position).getName());
                    //Start lesson dashboard activity
                    Intent intent = new Intent(getApplicationContext(),LessonDashboard.class);
                    Bundle lessonInfo = new Bundle();
                    lessonInfo.putString("subject",subject);
                    lessonInfo.putString("lesson",lessonPacks.get(position).getName());
                    lessonInfo.putString("ownerID",ownerID);
                    intent.putExtra("lessonInfo",lessonInfo);
                    startActivity(intent);

                }
            });

            final LessonListAdapter adapter = new LessonListAdapter(this, lessonPacks);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    lessonGrid.setAdapter(adapter);
                }
            });
        }

    }


}
