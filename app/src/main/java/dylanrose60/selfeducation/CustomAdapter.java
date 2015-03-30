package dylanrose60.selfeducation;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends ArrayAdapter {

    protected List<Subject> list = null;

    public CustomAdapter(Context context,int layout, List<Subject> array) {
        super(context,layout,array);
        list = array;
    }
/*
    @Override
    public View getView(int position,View recycledView,ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = recycledView;
        if (v == null) {
            v = inflater.inflate(R.layout.subject_card_view,parent,false);
        }

        TextView mainText = (TextView) v.findViewById(R.id.mainText);
        TextView subText = (TextView) v.findViewById(R.id.subText);
        TextView countText = (TextView) v.findViewById(R.id.countText);

        Subject currentSubject = list.get(position);
        mainText.setText(currentSubject.getSubjectName());
        subText.setText(currentSubject.getStartDate());
        int lessonCount = currentSubject.getLessonCount();
        if (lessonCount == 0) {
            countText.setText(lessonCount + " Lessons");
        } else if (lessonCount == 1) {
            countText.setText(lessonCount + " Lesson");
        } else {
            countText.setText(lessonCount + " Lessons");
        }

        return v;
    }


*/


}
