package dylanrose60.selfeducation;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LessonOptionsLinearLayout extends LinearLayout {

    private Context context;

    public LessonOptionsLinearLayout(Context context) {
        super(context,null);
        this.context = context;
    }
    public LessonOptionsLinearLayout(final Context context,AttributeSet attrs) {
        super(context,attrs);
        this.context = context;
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView optionTitle = (TextView) getChildAt(1);
                String option = (String) optionTitle.getText();
                LessonDashboard lessonDash = (LessonDashboard) context;
                lessonDash.onOptionSelected(option);
            }
        });
    }

}

