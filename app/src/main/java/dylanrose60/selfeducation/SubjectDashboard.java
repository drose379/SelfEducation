package dylanrose60.selfeducation;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import java.util.List;

import dylanrose60.selfeducation.DialogFragment.LCreateDialog1;
import dylanrose60.selfeducation.DialogFragment.LCreateDialog2;
import dylanrose60.selfeducation.DialogFragment.LCreateDialog3;
import dylanrose60.selfeducation.DialogFragment.NewTagDialog;

@SuppressLint("NewApi")
public class SubjectDashboard extends ActionBarActivity implements LessonManager.Listener,
        LCreateDialog1.Listener,
        LCreateDialog2.Listener,
        LCreateDialog3.Listener,
        NewTagDialog.Listener
{

    private String subject;
    private LessonManager manager;
    private FragmentManager fragmentManager = getFragmentManager();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        //Get the title of the subject the user selected or created in the previous activity
        Intent intent = getIntent();
        subject = intent.getStringExtra("subjectName");
        //Set the value of the activity title bar to the title of the subject with: setTitle(title)
        setTitle(subject);

        manager = new LessonManager(subject);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mInflater = new MenuInflater(getApplicationContext());
        mInflater.inflate(R.menu.subject_dashboard_menu,menu);
        return true;
    }


    public void createNew(View view) {
        final String[] items = {"Lesson","Project"};
        new MaterialDialog.Builder(this)
                .title("Create new...")
                .items(items)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View v, int selected, CharSequence text) {
                        String itemSelected = items[selected];
                        switch (itemSelected) {
                            case "Lesson":
                                //newLesson1();
                                manager.setListener(SubjectDashboard.this);
                                newLesson1Test();
                                break;
                            case "Project":
                            break;
                        }
                    }
                })
                .show();
    }

    public void openTagManager(View view) {
        Intent intent = new Intent(getApplicationContext(),TagManager.class);
        intent.putExtra("subject",subject);
        startActivity(intent);
    }

    public void newLesson1Test() {
        LCreateDialog1 dialog1 = new LCreateDialog1();
        dialog1.show(fragmentManager,"Dialog1");
    }

    @Override
    public void getNewLesson(String lesson) {
        manager.setLessonName(lesson);
        newLesson2Test();
    }

    public void newLesson2Test() {
        LCreateDialog2 dialog2 = new LCreateDialog2();
        dialog2.show(fragmentManager,"Dialog2");
    }

    @Override
    public void getObjectives(List<String> objectives) {
        manager.setObjectives(objectives);
        newLesson3Test(false);
    }

    public void newLesson3Test(Boolean newTag) {
        LCreateDialog3 dialog3 = new LCreateDialog3();
        Bundle newTagBundle = new Bundle();
        newTagBundle.putBoolean("new",newTag);
        dialog3.setArguments(newTagBundle);
        dialog3.show(fragmentManager,"Dialog3");
    }

    @Override
    public void getSelectedTags(List<String> selectedTags) {
        manager.setTags(selectedTags);
        buildLesson();
    }

    public void buildLesson() {
        manager.buildLesson();
    }

    @Override
    public void newTag(Boolean newTag) {
        newLesson3Test(true);
        //Pass true,so Dialog3 can putExtra a boolean of true of false to decide whether new tag is checked off
    }

    @Override
    public void onSuccess() {
        SnackbarManager.show(Snackbar.with(getApplicationContext()).text("Lesson created successfully"),this);
    }



    public void toLessonList(View view) {
        Intent intent = new Intent(getApplicationContext(),LessonList.class);
        intent.putExtra("subject",subject);
        startActivity(intent);
    }

    public void openQuestions(View view) {

    }

}
