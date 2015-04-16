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

    private String thisSubBookmarks;

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
            //get local lessons
        } else {
            //get public lessons
        }

    }

    public String ownerIDJSON() {

        JSONStringer json = new JSONStringer();
        try {
            json.object();
            json.key("owner_id");
            json.value(ownerID);
            json.endObject();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public void getBookmarkLessons() {
        String ownerIdJSON = ownerIDJSON();
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),ownerIdJSON);
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
                    //Pull lessons as a string from the objject, if not work, use JSONArray and pull JSONObject from that.
                    thisSubBookmarks = respObject.getString(subject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //for debug
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LessonList.this,thisSubBookmarks,Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }





}
