package dylanrose60.selfeducation;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.joooonho.SelectableRoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class LessonListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<LessonPackage> lessonPacks;

    public LessonListAdapter(Context context,List<LessonPackage> lessonPacks) {
        inflater = LayoutInflater.from(context);
        this.lessonPacks = lessonPacks;
        Log.i("lessonSize",String.valueOf(lessonPacks.size()));

        /*
            * Loop over each JSONObject in lessons
            * Get imageURL for the object
            * Create AsyncImageGrab outside of the for loop
            * inside the for loop, use the imageURL to pass to the async.execute()
            *
         */

    }

    @Override
    public int getCount() {
        return lessonPacks.size();
    }

    @Override
    public LessonPackage getItem(int position) {
        return lessonPacks.get(position);
    }

    @Override
    public long getItemId(int position) {return position;}

    @Override
    public View getView(int position,View recycledView,ViewGroup parent) {



        /*
            * To get item at position
                *use the position passed to getView to grab the item with getItem(position)
                * Use all of the JSONObjects info to grab rest of info and add it to card

        */

        View v = recycledView;

        if (v == null) {
            v = inflater.inflate(R.layout.lesson_card,parent,false);
        }


        //Use currentInfo to grab URL, send URL to method that makes a request for the image base64 string, decode it into image Bitmap, return the Bitmap

        //Do not execute more code until the Bitmap has been returned.

        //Use asyncTask

        //Base64->byteArray->Bitmap



        SelectableRoundedImageView headImage = (SelectableRoundedImageView) v.findViewById(R.id.headImage);
        TextView titleText = (TextView) v.findViewById(R.id.headText);
        TextView objectiveText = (TextView) v.findViewById(R.id.objectiveText);
        headImage.setCornerRadiiDP(3,3,0,0);

        LessonPackage currentLesson = lessonPacks.get(position);
        Bitmap bitmap = currentLesson.getImage();
        String lessonName = currentLesson.getName();
        String objective = currentLesson.getObjective();

        titleText.setText(lessonName);
        objectiveText.setText(objective);
        headImage.setImageBitmap(bitmap);

        return v;
    }

}
