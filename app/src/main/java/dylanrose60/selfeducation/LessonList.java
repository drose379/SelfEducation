package dylanrose60.selfeducation;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
import org.json.JSONStringer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LessonList extends ActionBarActivity implements ImageGrabUtil.imageCallback {

    private String subject;
    private String ownerID;
    private String type;

    private List<JSONObject> lessonInfo;

    //Testing
    private Handler handler = new Handler();
    private OkHttpClient httpClient = new OkHttpClient();
    private ImageGrabUtil imageGrab = new ImageGrabUtil(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lesson_list_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        Bundle intentBundle = getIntent().getExtras();
        Bundle listParams = intentBundle.getBundle("listParams");
        type = listParams.getString("type");
        subject = listParams.getString("subject");
        ownerID = listParams.getString("ownerID");
        setTitle(subject.trim() + " | Lessons:");

        //Need to check sub type, and grab lessons accordingly
        if (type .equals("Bookmark")) {
            getBookmarkLessons();
        } else if (type.equals("Local")) {
            getLocalLessons();
        } else {
            getPublicLessons();
        }

    }

    public void getBookmarkLessons() {
        String ownerIDJSON = null;
        List<String> key = new ArrayList<String>();
        List<String> value = new ArrayList<String>();

        key.add("owner_id");
        value.add(ownerID);

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
                final String responseString = response.body().string();
                Log.i("bookmarkLessons",responseString);
                JSONObject respObject;
                try {

                    respObject = new JSONObject(responseString);
                    String bookmarks = respObject.getString(subject);

                    JSONArray currentLessons = new JSONArray(bookmarks);

                    buildList(currentLessons,1);

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
                String responseString = response.body().string();
                Log.i("localLessons",responseString);
                try {
                    JSONArray currentLessons = new JSONArray(responseString);
                    Log.i("locLessonCount",String.valueOf(currentLessons.length()));
                    buildList(currentLessons,0);
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
                String responseString = response.body().string();
                Log.i("publicLessons", responseString);
            }
        });
    }

    public void buildList(JSONArray lessons,int type) throws JSONException {

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

        List<JSONObject> lessonInfo = new ArrayList<JSONObject>();

        /*
            * Need to use AsyncImageGrabber to grab all images for each lesson
            * Add the bitmap to the List, send to adapter with regular info
         */

        for (int i = 0; i<lessons.length();i++) {
            JSONObject currentObj = (JSONObject) lessons.get(i);
            lessonInfo.add(currentObj);
        }
        this.lessonInfo = lessonInfo;
        imageGrab.doInBackground(lessonInfo);
    }

    @Override
    public void onImageCallback(List<LessonPackage> lessonPacks) {
        getLessonObjectives(lessonPacks);
        final ListView lessonListView = (ListView) findViewById(R.id.lessonListView);
        final LessonListAdapter adapter = new LessonListAdapter(this,lessonPacks);

        //need to post back to UI thread
        //Move this to onResponse of getLessonObjectives method
        handler.post(new Runnable() {
            @Override
            public void run() {
                lessonListView.setAdapter(adapter);
            }
        });

    }

    public void getLessonObjectives(List<LessonPackage> lessonPacks) {
        JSONArray jsonNames = new JSONArray();
        for(LessonPackage pack : lessonPacks) {
            String lessonName = pack.getName();
            jsonNames.put(lessonName);
        }

        List<String> key = new ArrayList<String>();
        List<String> value = new ArrayList<String>();

        key.add("lessons");
        value.add(jsonNames.toString());

        String jsonString = null;

        try {
            jsonString = CommunicationUtil.toJSONString(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),jsonString);
        Request.Builder builder = new Request.Builder()
                .post(body)
                .url();
        Request request = builder.build();
        Call call = httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Response response) throws IOException {
                //API script will return arrays of lesson names and chosen objective
                //Match up lesson names to each item already inside of lessonPacks
                //Use lessonPacks .setObjective() method to set new objective
                //add the adapter here instead of in callback above
            }
            @Override
            public void onFailure(Request request,IOException e) {

            }
        });
    }


}
