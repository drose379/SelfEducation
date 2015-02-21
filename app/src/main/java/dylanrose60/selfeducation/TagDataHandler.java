package dylanrose60.selfeducation;

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

public class TagDataHandler {

    public interface Listener {
        public void tagAdded();
    }

    private String subject;
    private Listener listener;

    OkHttpClient client = new OkHttpClient();
    Handler handler = new Handler();

    public TagDataHandler(String subject) {
        this.subject = subject;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }


    public void addTag(final String tag) {
        MediaType mediaType = MediaType.parse("application/json");
        String info = toJson(tag);
        Request.Builder builder = new Request.Builder();
        RequestBody body = RequestBody.create(mediaType,info);
        builder.post(body);
        builder.url("http://codeyourweb.net/httpTest/index.php/newTag");
        Request request = builder.build();
        Call newCall = client.newCall(request);
        newCall.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (listener !=null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.tagAdded();
                        }
                    });
                }
            }
        });

    }

    public String toJson(String tag) {
        JSONStringer stringer = new JSONStringer();
        try {
            stringer.object();
            stringer.key("tag");
            stringer.value(tag);
            stringer.key("subject");
            stringer.value(subject);
            stringer.endObject();
            return stringer.toString();
        } catch (JSONException e)  {
            throw new RuntimeException(e);
        }

    }
}
