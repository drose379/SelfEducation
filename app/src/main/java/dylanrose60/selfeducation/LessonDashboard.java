package dylanrose60.selfeducation;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.melnykov.fab.FloatingActionButton;

public class LessonDashboard extends ActionBarActivity {

    /*
        * Add guide icon to dashboard by default, if no other items, show no items added to lesson yet dialot
        * Add guide functionality
     */

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

        ImageView downCaret = (ImageView) findViewById(R.id.downCaret);

        Animation slideDown = AnimationUtils.loadAnimation(this,R.anim.slide_down);
        Animation fadeOut = AnimationUtils.loadAnimation(this,R.anim.fade_out);
        AnimationSet set = new AnimationSet(false);
        set.addAnimation(slideDown);
        set.addAnimation(fadeOut);

        downCaret.setAnimation(set);

        /*
            * Create TextView subclass so no more need to set typeface for each view
         */

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
        final String[] options = {"Photo Album","Video"};

        actionButton.setRotation(45);
        MaterialDialog.Builder dBuilder = new MaterialDialog.Builder(this);
        dBuilder.title("Add New");
        dBuilder.customView(R.layout.new_lesson_items, true);
        dBuilder.dismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                actionButton.setRotation(0);
            }
        });


        MaterialDialog dialog = dBuilder.build();
        dialog.show();

        final View dialogView = dialog.getCustomView();

        dialogView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedID = v.getId();
                View vi = dialogView.findViewById(selectedID);
                //check items in view, need to determine what this view is, could be the linearlayout that image and text are in
            }
        });


    }

}
