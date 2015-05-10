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
import java.util.HashMap;
import java.util.List;


public class LessonGrabUtil {

    public interface imageCallback {
        public void onImageCallback(List<LessonPackage> lessonPackages);
    }

    private Context ctxt;
    private imageCallback listener;
    //private List<Bitmap> lessonImages = new ArrayList<Bitmap>();

    private List<LessonPackage> lessonPackages = new ArrayList<LessonPackage>();

    private byte[] iBytes = null;
    private OkHttpClient httpClient = new OkHttpClient();

    public LessonGrabUtil(Context context) {
        this.ctxt = context;
        this.listener = (imageCallback) ctxt;
    }


    public void getLocalImages(List<JSONObject> lessonInfo) {
        //use url to grab image from sever, use php to grab it and recieve a base64 string.
        //base64->byte[]->bitmap

        List<List<String>> realUrl = new ArrayList<List<String>>();


        for (JSONObject currentObj : lessonInfo) {
            try {
                String imageURL = currentObj.getString("imageURL");
                String lessonName = currentObj.getString("lesson_name");

                if (imageURL.contains("stock")) {
                    int id = ctxt.getResources().getIdentifier(imageURL,"drawable",ctxt.getPackageName());

                    Drawable d = ctxt.getResources().getDrawable(id);

                    Bitmap bitmap = ((BitmapDrawable)d).getBitmap();

                    lessonPackages.add(new LessonPackage(lessonName,bitmap));

                } else {

                    /*
                        * need to save reg URLS in a List
                        * Create a JSONArray out of them
                        * Convert JSONArray to string
                        * Pass that to get base64 strings for each image given by URL
                        * in onResponse, loop over each base64 string, create a Bitmap out of it, then add it to List<bitmap> lessonImages
                        * Give that list to the callback to LessonList
                    */

                    List<String> currentInfo = new ArrayList<String>();

                    currentInfo.add(lessonName);
                    currentInfo.add(imageURL);

                    realUrl.add(currentInfo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        getRemoteImages(realUrl);
    }

    public void getBookmarkImages(List<String> lessons) {

    }

    public void getRemoteImages(final List<List<String>> urls) {

        Log.i("urls",urls.toString());

        /*
            * Need to create some logic to match the returned bitmap up with its lesson name passed in by _urls_ parameter to this method
                * Need to loop over response and match returned url to a url in _urls_ , then match name to bitmap
         */

        JSONArray urlJSON = new JSONArray();

        for (int i=0;i<urls.size();i++) {
            List<String> currentLesson = urls.get(i);
            urlJSON.put(currentLesson.get(1));
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

                try {
                    JSONArray base64Images = new JSONArray(resString);
                    Log.i("jsonA",base64Images.toString());
                    base64toBitmap(base64Images, urls);

                } catch (JSONException e) {
                    //e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {

            }
        });
    }

    public void base64toBitmap(JSONArray base64Images,List<List<String>> lessonData) throws JSONException {

        /*
            * Need to loop over JSONArray to get JSONObjects
             * For each JSONObject, look in lesson data to match the returned URL to a url in the array
             * Get the lesson name, add to hashmap
         */

        for(int i=0;i<base64Images.length();i++) {
            JSONObject currentObj = (JSONObject) base64Images.get(i);

            String imageURL = currentObj.getString("url");

            byte[] imageBytes = Base64.decode(currentObj.getString("base64"),Base64.DEFAULT);

            Bitmap currentBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(currentBitmap,500,250,false);

            //Need to loop over lessonData array and see which url is equal to imageURL (defined above)
            //When there is a match in URL, grab the name from lessonData and add to the the HashMap<LessonName,Bitmap>

            for(List<String> oneLesson : lessonData) {
                if (oneLesson.contains(imageURL)) {
                    //add bitmap to hashmap with lesson name as key
                    String lessonName = oneLesson.get(0);
                    lessonPackages.add(new LessonPackage(lessonName, scaledBitmap));
                    Log.i("lessonPlaced",lessonName + " was matched");
                }
            }

            //lessonImages.add(scaledBitmap);
        }

        listener.onImageCallback(lessonPackages);
    }


}
