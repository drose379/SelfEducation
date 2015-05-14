package dylanrose60.selfeducation;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONStringer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LessonManager {

    public interface Listener {
        //public void getAllTags(LessonManager manager,String stringArray,boolean newTag);
        public void onSuccess();
    }

    private Context context;

    private String subject;
    private String lessonName;
    private List<String> objectives = new ArrayList<>();
    private List<String> tags = new ArrayList<>();

    private boolean isStockImage;
    private String stockName;

    //private File imageFile;
    private Bitmap imageBitmap;
    private String imageSavedURL;
    private MaterialDialog loadingDialog;

    private OkHttpClient client = new OkHttpClient();
    private MediaType mediaType = MediaType.parse("application/json;charset=utf-8");

    private Listener listener;

    private Handler handler = new Handler();

    public LessonManager(Context context,String subject) {
        this.context = context;
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
        tags.clear();
        int tagsSize = selectedTags.size();
        for (int i = 0;i < tagsSize; i++) {
            String currentTag = selectedTags.get(i);
            tags.add(currentTag);
        }
        Log.i("lessonTags",tags.toString());
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }


    public void buildLesson() {
        showLoadingDialog();
        String finalJSON = completeJSONBuilder();
        RequestBody body = RequestBody.create(mediaType,finalJSON);
        Request.Builder builder = new Request.Builder();
        builder.post(body);
        builder.url("http://codeyourweb.net/httpTest/index.php/newLesson");
        Request request = builder.build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                loadingDialog.dismiss();
                Log.i("newLesson", "Lesson added");
            }
        });
    }

    public void buildLesson(final Bundle bookmarkInfo) {
        showLoadingDialog();
        String lessonData = bookmarkJSONBuilder(bookmarkInfo);
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
                loadingDialog.dismiss();
                /*
                handler.post(new Runnable() {
                    public void run() {
                        listener.onSuccess();
                    }
                });
                */
            }
        });
    }


    public void showLoadingDialog() {
        Activity activity = (Activity) context;

        MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(context);

        LinearLayout layout = new LinearLayout(activity);

        LinearLayout.LayoutParams dialogParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(dialogParams);

        ProgressWheel loadWheel = new ProgressWheel(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(80,80);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        loadWheel.setLayoutParams(params);
        loadWheel.setBarColor(activity.getResources().getColor(R.color.ColorPrimary));
        loadWheel.spin();

        layout.addView(loadWheel);

        dialogBuilder.customView(layout,true);
        dialogBuilder.title("One Moment");
        dialogBuilder.autoDismiss(false);

        loadingDialog = dialogBuilder.build();
        loadingDialog.show();
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
            builder.value(tagsJSON);
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
