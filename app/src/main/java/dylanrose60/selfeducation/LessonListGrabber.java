package dylanrose60.selfeducation;

import android.content.Context;
import android.os.Handler;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LessonListGrabber {

    public interface Listener {
        void onLessonListGrabbed(List<Lesson> list);
    }

    private Listener listener;

    private OkHttpClient client = new OkHttpClient();
    private MediaType mediaType = MediaType.parse("application/json");

    private String subject;
    private List<Lesson> endList;

    //Testing
    private Handler handler;
    private Context context;


    public LessonListGrabber(String subject,Handler handler,Context context) {
        this.subject = subject;
        this.handler = handler;
        this.context = context;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void getData() {
        RequestBody body = RequestBody.create(mediaType,subject);
        Request.Builder builder = new Request.Builder();
        builder.post(body);
        builder.url("http://codeyourweb.net/httpTest/index.php/getLessons");
        Request request = builder.build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(final Response response) throws IOException {
                buildList(response.body().string());
            }
        });
    }


    public void buildList(String json) {
        List<Lesson> list = new ArrayList();
        try {
            //JSONArray array = new JSONArray(response);
            JSONArray array = new JSONArray(json);
            for(int i = 0;i<array.length();i++) {
                JSONObject object = array.getJSONObject(i);
                String lessonName = object.getString("lesson_name");
                int lessonPriority = object.getInt("priority");
                list.add(new Lesson(lessonName,lessonPriority));
            }
        } catch (JSONException e) {

        }
        giveListToListener(list);
    }


    public void giveListToListener(final List<Lesson> list) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onLessonListGrabbed(list);
            }
        });
    }


}
