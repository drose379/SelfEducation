package dylanrose60.selfeducation;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joooonho.SelectableRoundedImageView;

import java.util.List;

public class LessonListAdapter extends BaseAdapter {
    private Activity context;
    private LayoutInflater inflater;
    private List<LessonPackage> lessonPacks;

    public LessonListAdapter(Context context,List<LessonPackage> lessonPacks) {
        inflater = LayoutInflater.from(context);
        this.context = (Activity) context;
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
            v = inflater.inflate(R.layout.layout_tile,parent,false);
        }


        LessonPackage currentLesson = lessonPacks.get(position);

        String lessonName = currentLesson.getName();


        String[] splitLesson = lessonName.split("\\s");

        int largestLength = 0;
        for(String lesson: splitLesson) {
            int length = lesson.length();
            if (length > largestLength) {
                largestLength = length;
            }
        }

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        float density = metrics.density;
        int dp = (int) Math.ceil(parent.getWidth()/density);

        int tileFullWidthDP = dp/2;
        int tileFullWidthPX = parent.getWidth()/2;

        int viewWidth = 0;

        /*
            * Use Math.floor to evaluate which percentile to set the textview width to
            * int percentile = Math.floor(longestStringLength/2);
            * use switch {} on the percentile to view all possabilities (maybe use booleans)
            * Set textView width accordingly
         */

        TextView test = (TextView) v.findViewById(R.id.testText);
        test.setLayoutParams(new LinearLayout.LayoutParams(viewWidth,LinearLayout.LayoutParams.WRAP_CONTENT));
        test.setText(lessonName);

        return v;
    }

}
