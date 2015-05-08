package dylanrose60.selfeducation;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

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


public class ImageGrabUtil {

    public interface imageCallback {
        public void onImageCallback(List<Bitmap> lessonImages);
    }

    private Context ctxt;
    private imageCallback listener;
    private List<Bitmap> lessonImages = new ArrayList<Bitmap>();
    private byte[] iBytes = null;
    private OkHttpClient httpClient = new OkHttpClient();

    public ImageGrabUtil(Context context) {
        this.ctxt = context;
        this.listener = (imageCallback) ctxt;
    }


    public void doInBackground(List<JSONObject> lessonInfo) {
        //use url to grab image from sever, use php to grab it and recieve a base64 string.
        //base64->byte[]->bitmap

        List<String> realUrl = new ArrayList<String>();


        for (JSONObject currentObj : lessonInfo) {
            try {
                String imageURL = currentObj.getString("imageURL");
                if (imageURL.contains("stock")) {
                    int id = ctxt.getResources().getIdentifier(imageURL,"drawable",ctxt.getPackageName());
                    Drawable d = ctxt.getResources().getDrawable(id);
                    Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
                    lessonImages.add(bitmap);
                } else {
                    /*
                        * need to save reg URLS in a List
                        * Create a JSONArray out of them
                        * Convert JSONArray to string
                        * Pass that to get base64 strings for each image given by URL
                        * in onResponse, loop over each base64 string, create a Bitmap out of it, then add it to List<bitmap> lessonImages
                        * Give that list to the callback to LessonList
                    */
                    realUrl.add(imageURL);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        getRemoteImages(realUrl);
    }

    public void getRemoteImages(List<String> urls) {
        JSONArray urlJSON = new JSONArray();
        for (int i=0;i<urls.size();i++) {
            urlJSON.put(urls.get(i));
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),urlJSON.toString());
        Request.Builder builder = new Request.Builder();
        builder.post(body);
        builder.url("http://codeyourweb.net/httpTest/index.php/getImageFromURL");
        Request request = builder.build();
        Call call = httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Response response) throws IOException {
                //get images from each base64 string, add them to lessonImages (List<bitmap>
                //callback to LessonList using its static callback method
                String resString = response.body().string();
                Log.i("serverResp",resString);
                try {
                    JSONArray base64Images = new JSONArray(resString);

                    base64toBitmap(base64Images);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {

            }
        });
    }

    public void base64toBitmap(JSONArray base64Images) throws JSONException {
        for(int i=0;i<base64Images.length();i++) {
            byte[] imageBytes = Base64.decode((String)base64Images.get(i),Base64.DEFAULT);
            if (imageBytes == null) {
                Log.i("currentBytes","This byte[] is null!");
            } else {
                Log.i("currentBytes",(String)base64Images.get(i));
            }
            Bitmap currentBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(currentBitmap,500,250,false);

            lessonImages.add(scaledBitmap);
        }
        listener.onImageCallback(lessonImages);
    }


}
