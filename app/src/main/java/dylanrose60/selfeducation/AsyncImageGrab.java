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

    private Context ctxt;
    private List<Bitmap> lessonImages = new ArrayList<Bitmap>();
    private byte[] iBytes = null;
    private OkHttpClient httpClient = new OkHttpClient();

    public ImageGrabUtil(Context context) {
        this.ctxt = context;
    }


    public String doInBackground(List<JSONObject> lessonInfo) {
        //use url to grab image from sever, use php to grab it and recieve a base64 string.
        //base64->byte[]->bitmap
        //Loop over all strings passed
        //To get imageURL use : imageURL[0]


        for (JSONObject currentObj : lessonInfo) {
            try {
                String imageURL = currentObj.getString("imageURL");
                Bitmap currentImage = getCurrentImage(imageURL);
                lessonImages.add(currentImage);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return "hey";
    }

    public Bitmap getCurrentImage(String url) {
        //If url contains the word "stock" then just pull correct bitmap from drawables and return that

        /*
            * To get drawable as bitmap, use Resources getIdentifier
            * use ImageURL as drawableName
            * type is "drawable"
            * getPackageName as package
            * int id = getResources().getIdentifier(imageURL,drawable,package)
            * Drawable d = getResources().getDrawable(id);
            * NEED TO GO FROM DRAWABLE TO  BITMAP AND RETURN THE BITMAP
         */

        //Else, make a request to server with the URL and get the base64 string back,
        //base64->byte[]->Bitmap

        Bitmap bitmap = null;

        if (url.contains("stock")) {
            int id = ctxt.getResources().getIdentifier(url,"drawable",ctxt.getPackageName());
            Drawable d = ctxt.getResources().getDrawable(id);
            //cast Drawable to BitmapDrawable and call getBitmap from it, to get bitmap
            bitmap = ((BitmapDrawable)d).getBitmap();
        } else {
            //Make http request with URL to grab bitmap
            //assign to bitmap
            byte[] bteArray = imageRequest(url);
            bitmap = BitmapFactory.decodeByteArray(bteArray,0,bteArray.length);
        }
        return bitmap;
    }

    public byte[] imageRequest(String url) {

        List<String> key = new ArrayList<String>();
        List<String> value = new ArrayList<String>();

        key.add("url");
        value.add(url);

        String jsonBody = null;

        try {
            jsonBody = CommunicationUtil.toJSONString(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),jsonBody);
        Request.Builder builder = new Request.Builder();
        builder.url("http://codeyourweb.net/httpTest/index.php/getImageFromURL");
        builder.post(body);
        Request request = builder.build();
        Call call = httpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Response response) throws IOException {
                iBytes = Base64.decode(response.body().string(),Base64.DEFAULT);
            }

            @Override
            public void onFailure(Request request, IOException e) {

            }
        });
        throw new RuntimeException();
    }

}
