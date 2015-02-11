package dylanrose60.selfeducation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class CustomLessonAdapter extends ArrayAdapter {

    protected List<Lesson> list = null;

    public CustomLessonAdapter(Context context,int layout, List<Lesson> array) {
        super(context,layout,array);
        list = array;
    }

    public View getView(int position,View recycledView,ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = recycledView;
        if (v == null) {
            v = inflater.inflate(R.layout.lesson_list_card,parent,false);
        }

        TextView mainText = (TextView) v.findViewById(R.id.mainText);
        TextView subText = (TextView) v.findViewById(R.id.subText);

        ImageView dotTop = (ImageView) v.findViewById(R.id.dotTop);
        ImageView dotMid = (ImageView) v.findViewById(R.id.dotMid);
        ImageView dotLow = (ImageView) v.findViewById(R.id.dotBottom);

        CardView card = (CardView) v.findViewById(R.id.lessonCard);
        card.setCardBackgroundColor(Color.parseColor("#e74c3c"));

        //Change the HEX colors to @colors defined in the color.xml file
        //Use Light Blue color pallete for priority colors
        Lesson currentLesson = list.get(position);
        mainText.setText(currentLesson.getLessonName());
        switch (currentLesson.getPriority()) {
            case 1 :
                card.setCardBackgroundColor(Color.parseColor("#c8e6c9"));
                dotTop.setImageResource(R.drawable.dot_empty);
                dotMid.setImageResource(R.drawable.dot_empty);
                dotLow.setImageResource(R.drawable.dot_full);
                break;
            case 2 :
                card.setCardBackgroundColor(Color.parseColor("#a5d6a7"));
                dotTop.setImageResource(R.drawable.dot_empty);
                dotMid.setImageResource(R.drawable.dot_full);
                dotLow.setImageResource(R.drawable.dot_full);
                break;
            case 3 :
                card.setCardBackgroundColor(Color.parseColor("#81c784"));
                dotTop.setImageResource(R.drawable.dot_full);
                dotMid.setImageResource(R.drawable.dot_full);
                dotLow.setImageResource(R.drawable.dot_full);
                break;
        }

        //subText.setText(currentLesson.); Method that gets Tasks and Completed tasks, does calculation and gets percent and creates string.

        return v;
    }

}
