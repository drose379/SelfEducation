package dylanrose60.selfeducation;

import android.content.Context;
import android.os.Handler;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONStringer;

import java.io.IOException;

public class SubjectManager {

    private Handler handler = new Handler();
    private Listener listener;
    private String subject;
    private String privacy;
    private int serialID;

    private OkHttpClient client = new OkHttpClient();
    private MediaType mediaType = MediaType.parse("application/json;charset=utf-8");

    public interface Listener {
        void deletedCallback();
    }


    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void deleteSubject() {
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody rBody = RequestBody.create(mediaType,subject);

        Request.Builder rBuilder = new Request.Builder();
        rBuilder.post(rBody);
        rBuilder.url("http://codeyourweb.net/httpTest/index.php/deleteSubject");
        Request request = rBuilder.build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
        @Override
        public void onFailure(Request request, IOException e) {

        }

        @Override
        public void onResponse(Response response) throws IOException {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.deletedCallback();
                }
            });
        }
        });
    }




}
