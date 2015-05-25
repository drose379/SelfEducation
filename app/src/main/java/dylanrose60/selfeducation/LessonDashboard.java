package dylanrose60.selfeducation;

import android.annotation.SuppressLint;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class LessonDashboard extends ActionBarActivity implements SlidingUpPanelLayout.PanelSlideListener {

    /*
        * Add guide icon to dashboard by default, if no other items, show no items added to lesson yet dialot
        * Add guide functionality
     */

    private String subjectName;
    private String lessonName;


    //Need to set guideCaret to a private property to gain access to it in SlidingLayout interface
    //Need to set FAB to private prop to gain access to it when it is clicked
    private FloatingActionButton actionButton;
    private ImageView guideCaret;

    //Material Dialog needs to be property so it can be accessed from onOptionSelected method
    private MaterialDialog dialog;


    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        //need to grab toolbar and set title to lesson name also save subject and category for later use
        //grab from intent

        setContentView(R.layout.lesson_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        guideCaret = (ImageView) findViewById(R.id.downCaret);
        SlidingUpPanelLayout slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.lessonDashMain);
        slidingLayout.setPanelSlideListener(this);

        Animation slideDown = AnimationUtils.loadAnimation(this,R.anim.slide_up);
        Animation fadeOut = AnimationUtils.loadAnimation(this,R.anim.fade_out);
        AnimationSet set = new AnimationSet(false);
        set.addAnimation(slideDown);
        set.addAnimation(fadeOut);

        guideCaret.setRotation(180f);
        guideCaret.setAnimation(set);

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

    @Override
    public void onPanelSlide(View panel,float slideOffset) {

    }
    @Override
    public void onPanelCollapsed(View panel) {
        Animation slideDown = AnimationUtils.loadAnimation(this,R.anim.slide_up);
        Animation fadeOut = AnimationUtils.loadAnimation(this,R.anim.fade_out);
        AnimationSet set = new AnimationSet(false);
        set.addAnimation(slideDown);
        set.addAnimation(fadeOut);

        guideCaret.setRotation(180f);
        guideCaret.setAnimation(set);
    }
    @Override
    public void onPanelExpanded(View panel) {
        AnimationSet animSet = new AnimationSet(false);
        Animation slideDown = AnimationUtils.loadAnimation(this,R.anim.slide_down);
        Animation fadeOut = AnimationUtils.loadAnimation(this,R.anim.fade_out);
        animSet.addAnimation(slideDown);
        animSet.addAnimation(fadeOut);

        guideCaret.setAnimation(animSet);
        guideCaret.setRotation(0);
    }
    @Override
    public void onPanelAnchored(View panel) {

    }
    @Override
    public void onPanelHidden(View panel) {

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


        dialog = dBuilder.build();
        dialog.show();
    }

    public void onOptionSelected(String option) {
        switch (option) {
            case "Photo Album" :
                dialog.dismiss();
                //start new album activity
                //slide in

                /*
                    * Needs to be changed to display a Fragment instead of activity
                    * Remove activity for NewAlbum from manifest
                    * Remove toolbar from newAlbum view
                    * Look into sliding out activities current view and sliding in fragment view
                 */
                /*
                Intent intent = new Intent(getApplicationContext(),NewAlbum.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
                */


                //Need to have container for fragment in R.layout.lesson_dashboard, add fragment to container
                //slide out any other containers (Linear or Relative) (for test set to gone)

                FragmentManager fragManager = getSupportFragmentManager();
                FragmentTransaction fragTrans = fragManager.beginTransaction();

                NewAlbum newAlbum = new NewAlbum();
                fragTrans.add(R.id.fragmentContainer, newAlbum, "newAlbum");
                fragTrans.addToBackStack("newAlbum");

                Animation animation1 = AnimationUtils.loadAnimation(this,R.anim.slide_in);
                Animation animation2 = AnimationUtils.loadAnimation(this,R.anim.slide_out);

                LinearLayout fragContainer = (LinearLayout) findViewById(R.id.fragmentContainer);
                TextView testText = (TextView) findViewById(R.id.replace);

                testText.setAnimation(animation2);
                fragContainer.setAnimation(animation1);

                fragTrans.commit();

                break;
            case "Video" :

                break;
        }
    }

}
