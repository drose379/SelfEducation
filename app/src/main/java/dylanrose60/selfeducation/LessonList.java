package dylanrose60.selfeducation;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class LessonList extends ActionBarActivity implements LessonListGrabber.Listener {

    private String subject;

    //Testing
    public Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lesson_list_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        Bundle intentBundle = getIntent().getExtras();
        Bundle listParams = intentBundle.getBundle("listParams");
        Log.i("listParam", listParams.getString("type"));

        /*
        subject = getIntent().getStringExtra("subject");
        setTitle(subject + "| Lessons:");
        buildView();
        */
    }

    public void buildView() {
        LessonListGrabber grabber = new LessonListGrabber(subject,handler,getApplicationContext());
        grabber.setListener(this);
        grabber.getData();
    }

    @Override
    public void onLessonListGrabbed(List list) {
        buildList(list);
    }

    public void buildList(List<Lesson> list) {
        ListView listView = (ListView) findViewById(R.id.lessonListView);

        ArrayAdapter<Lesson> adapter = new CustomLessonAdapter(getApplicationContext(),R.layout.lesson_list_card,list);

        listView.setAdapter(adapter);

    }



}
