package dylanrose60.selfeducation;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.melnykov.fab.FloatingActionButton;

public class LessonDashboard extends ActionBarActivity {

    private String subjectName;
    private String lessonName;

    private FloatingActionButton actionButton;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        //need to grab toolbar and set title to lesson name also save subject and category for later use
        //grab from intent

        setContentView(R.layout.lesson_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        Bundle lessonInfo = getIntent().getBundleExtra("lessonInfo");
        lessonName = lessonInfo.getString("lesson");
        subjectName = lessonInfo.getString("subject");

        actionButton = (FloatingActionButton) findViewById(R.id.fab);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inflateLessonMenu();
            }
        });

        setTitle(lessonName);

    }

    public void inflateLessonMenu() {
        //actionButton.setImageDrawable(getResources().getDrawable(R.drawable.plus_2));
        Animation rotate = AnimationUtils.loadAnimation(this,R.anim.rotate_45);
        actionButton.setRotation(45);
    }

}
