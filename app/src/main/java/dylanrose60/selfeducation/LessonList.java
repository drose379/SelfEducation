package dylanrose60.selfeducation;

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
import java.util.List;

public class LessonList extends ActionBarActivity{

    private String subject;
    private String ownerID;
    private String type;

    //Testing
    private Handler handler = new Handler();
    private OkHttpClient httpClient = new OkHttpClient();

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

        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),ownerIDJSON);
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

                    JSONArray bookmarkLessons = new JSONArray(bookmarks);

                    buildList(bookmarkLessons,1);

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
                    JSONArray allLocalLessons = new JSONArray(responseString);
                    Log.i("locLessonCount",String.valueOf(allLocalLessons.length()));
                    buildList(allLocalLessons,0);
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
                Log.i("publicLessons",responseString);
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
        */

        ListView lessonListView = (ListView) findViewById(R.id.lessonListView);

    }

}
