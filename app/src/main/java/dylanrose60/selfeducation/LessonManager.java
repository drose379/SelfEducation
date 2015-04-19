package dylanrose60.selfeducation;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONStringer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LessonManager {

    public interface Listener {
        //public void getAllTags(LessonManager manager,String stringArray,boolean newTag);
        public void onSuccess();
    }

    private String subject;
    private String lessonName;
    private List<String> objectives = new ArrayList<>();
    private List<String> tags = new ArrayList<>();
    private String imgUri;
    private LinearLayout dialogLayout;

    private OkHttpClient client = new OkHttpClient();
    private MediaType mediaType = MediaType.parse("application/json;charset=utf-8");

    private Listener listener;

    private Handler handler = new Handler();

    public LessonManager(String subject) {
        this.subject = subject;
    }

    public void setLessonName(String lesson) {
        lessonName = lesson;
    }

    public void setObjectives(List<String> objectiveList) {
        int listSize = objectiveList.size();
        for (int i = 0;i < listSize;i++) {
            String currentObjective = objectiveList.get(i);
            objectives.add(i,currentObjective);
        }
    }

    public void setTags(List<String> selectedTags) {
        int tagsSize = selectedTags.size();
        for (int i = 0;i < tagsSize; i++) {
            String currentTag = selectedTags.get(i);
            tags.add(currentTag);
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setImgUri(String uri) {
        this.imgUri = uri;
    }

    //For getting subject tags from DB

/*
    public void getTags(final boolean newTag){
        Request.Builder builder = new Request.Builder();
        builder.url("http://codeyourweb.net/httpTest/index.php/getTags");
        Request ready = builder.build();
        Call call = client.newCall(ready);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String responseBody = response.body().string();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.getAllTags(LessonManager.this,responseBody,newTag);
                    }
                });

            }
        });
    }
*/
    //Methods for creating new lesson

    public void buildLesson() {
        String lessonData = completeJSONBuilder();
        Log.i("localLessonData",lessonData);
        RequestBody body = RequestBody.create(mediaType,lessonData);
        Request.Builder rBuilder = new Request.Builder();
        rBuilder.post(body);
        rBuilder.url("http://codeyourweb.net/httpTest/index.php/newLesson");
        Request request = rBuilder.build();
        Call newCall = client.newCall(request);
        newCall.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                handler.post(new Runnable() {
                    public void run() {
                        listener.onSuccess();
                    }
                });
            }
        });
    }

    public void buildLesson(Bundle bookmarkInfo) {
        //Customize this to build lesson to bookmark prefs.
        String lessonData = bookmarkJSONBuilder(bookmarkInfo);
        Log.i("bookmarkLesson","Bookmark Lesson Method Called");

        RequestBody body = RequestBody.create(mediaType,lessonData);
        Request.Builder rBuilder = new Request.Builder();
        rBuilder.post(body);
        rBuilder.url("http://codeyourweb.net/httpTest/index.php/newBookmarkLesson");
        Request request = rBuilder.build();
        Call newCall = client.newCall(request);
        newCall.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                handler.post(new Runnable() {
                    public void run() {
                        listener.onSuccess();
                    }
                });
            }
        });

    }

    public JSONArray getTagsJSON() {
        JSONArray tagsJSON = new JSONArray();
        try {
            for (int i = 0;i < tags.size(); i++) {
                tagsJSON.put(i,tags.get(i));
            }
            return tagsJSON;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONArray getObjectivesJSON() {
        JSONArray objectivesJSON = new JSONArray();
        try {
            for (int i = 0; i < objectives.size(); i++) {
                objectivesJSON.put(i,objectives.get(i));
            }
            return objectivesJSON;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public String completeJSONBuilder() {

        JSONArray tagsJSON = getTagsJSON();
        JSONArray objectivesJSON = getObjectivesJSON();
        JSONStringer builder = new JSONStringer();

        try {
            builder.object();
            builder.key("subject_name");
            builder.value(subject);
            builder.key("lesson_name");
            builder.value(lessonName);
            builder.key("objectives");
            builder.value(objectivesJSON);
            builder.key("tags");
            builder.value(tagsJSON); //Remove toArray()
            builder.key("imgUri");
            builder.value(imgUri);
            builder.endObject();
            return builder.toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public String bookmarkJSONBuilder(Bundle bookmarkData) {
        JSONArray tagsJSON = getTagsJSON();
        JSONArray objectivesJSON = getObjectivesJSON();
        JSONStringer builder = new JSONStringer();

        try {
            builder.object();
            builder.key("subject_name");
            builder.value(subject);
            builder.key("lesson_name");
            builder.value(lessonName);
            builder.key("objectives");
            builder.value(objectivesJSON);
            builder.key("tags");
            builder.value(tagsJSON);
            builder.key("lesson_privacy");
            builder.value(bookmarkData.getString("lessons_privacy"));
            builder.key("subscribed");
            builder.value(bookmarkData.getInt("subscribed"));
            builder.key("bookmarkID");
            builder.value(bookmarkData.getInt("bookmarkID"));
            builder.endObject();
            return builder.toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }



}
