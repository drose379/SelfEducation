package dylanrose60.selfeducation;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.json.JSONArray;

public class LessonListAdapter extends ArrayAdapter<JSONArray> {

    private JSONArray lessons;
    private Context context;

    public LessonListAdapter(Context context,JSONArray lessons,int layout) {
        super(context,layout);
        this.context = context;
        this.lessons = lessons;
    }
/*
    @Override
    public View getView(int position,View recycledView,ViewGroup parent) {


            * To get item at position
                *use the position passed to getView to grab the item with getItem(position)
                * Use all of the JSONObjects info to grab rest of info and add it to card


    }
*/
}
