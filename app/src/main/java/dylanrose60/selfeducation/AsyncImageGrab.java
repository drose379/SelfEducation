package dylanrose60.selfeducation;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class AsyncImageGrab extends AsyncTask<String,Void,Bitmap> {


    @Override
    public void onPreExecute() {

    }

    @Override
    public Bitmap doInBackground(String... imageURL) {
        //use url to grab image from sever, use php to grab it and recieve a base64 string.
        //base64->byte[]->bitmap
        //To get imageURL use : imageURL[0]

    }

    @Override
    public void onPostExecute(Bitmap s) {
        //Call LessonListAdpater Callback method and give it the Bitmap
    }

}
