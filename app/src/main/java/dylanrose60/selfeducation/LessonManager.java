package dylanrose60.selfeducation;

import android.os.Handler;
import android.widget.EditText;
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
import java.util.List;

public class LessonManager {

    public interface Listener {
        public void getArray(LessonManager manager,String stringArray);
    }

    private String subject;
    private String lessonName;
    private String[] objectives;
    private List<String> tags;

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

    public void setObjectives(String[] objectives) {
        this.objectives = objectives;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setTags(List<String> selectedTags) {
        //Copy values from passed in string to field of this class
    }

    //For getting subject tags from DB

    public void getTags(){
        Request.Builder builder = new Request.Builder();

        String JSONSubject = buildJson();
        RequestBody body = RequestBody.create(mediaType,JSONSubject);

        builder.post(body);
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
                        listener.getArray(LessonManager.this,responseBody);
                    }
                });

            }
        });
    }


    public String buildJson() {
        JSONStringer builder = new JSONStringer();
        try {
            builder.object();
            builder.key("subject");
            builder.value(subject);
            builder.endObject();
            return builder.toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }






}
