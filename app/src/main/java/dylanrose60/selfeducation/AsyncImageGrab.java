package dylanrose60.selfeducation;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AsyncImageGrab extends AsyncTask<List<JSONObject>,Void,String> {

    private Context ctxt;
    private List<Bitmap> lessonImages = new ArrayList<Bitmap>();

    public AsyncImageGrab(Context context) {
        this.ctxt = context;
    }


    @Override
    public void onPreExecute() {

    }

    @Override
    public String doInBackground(List<JSONObject>... lessonInfo) {
        //use url to grab image from sever, use php to grab it and recieve a base64 string.
        //base64->byte[]->bitmap
        //Loop over all strings passed
        //To get imageURL use : imageURL[0]

        List<JSONObject> lessons = lessonInfo[0];

        for (JSONObject currentObj : lessons) {
            try {
                String imageURL = currentObj.getString("imageURL");
                Bitmap currentImage = getCurrentImage(imageURL);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return "hey";
    }

    @Override
    public void onPostExecute(String s) {
        //Call LessonListAdpater Callback method and give it the Bitmap
        //LessonList.asyncCallback(bitmaps);
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
        }
        return bitmap;
    }

}
