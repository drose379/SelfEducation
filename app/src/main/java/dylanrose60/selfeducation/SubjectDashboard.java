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

    /*
    public void newLesson1() {
        manager = new LessonManager(subject);
        final EditText edit = new EditText(this);

        new MaterialDialog.Builder(this)
                .title("Name Your Lesson")
                .customView(edit, true)
                .positiveText("Next")
                .positiveColor(Color.parseColor("#01579b"))
                .negativeText("Cancel")
                .negativeColor(Color.RED)
                .autoDismiss(false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        String lesson = edit.getText().toString();
                        if (edit.length() == 0) {
                            edit.setError("Please enter a valid name");
                        } else {
                            dialog.dismiss();
                            manager.setLessonName(lesson);
                            newLesson2(manager);
                        }
                    }
                })
                .show();
    }
    */


    /*
    public void newLesson2(final LessonManager manager) {

        EditText editText1 = new EditText(this);

        final LinearLayout dialogLayout = (LinearLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.new_lesson_layout, null);

        dialogLayout.addView(editText1);

        new MaterialDialog.Builder(this)
                .title("Lesson Objectives")
                .customView(dialogLayout, true)
                .positiveText("Next")
                .positiveColor(getResources().getColor(R.color.ColorSubText))
                .negativeText("Cancel")
                .negativeColor(Color.RED)
                .neutralText("New")
                .neutralColor(getResources().getColor(R.color.ColorPrimary))
                .autoDismiss(false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onNeutral(MaterialDialog dialog) {
                        EditText editText = new EditText(SubjectDashboard.this);
                        dialogLayout.addView(editText);
                    }

                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                        List<String> objectives = getObjectiveValues(dialogLayout);
                        manager.setObjectives(objectives);
                        newLesson3(manager,false);
                    }
                })
                .show();
                }

*/

/*

    public void newLesson3(final LessonManager manager,boolean newTag) {
        manager.setListener(this);

        LinearLayout dialogLayout = new LinearLayout(this);
        LinearLayout.LayoutParams dialogParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setLayoutParams(dialogParams);

        ProgressWheel spinner = new ProgressWheel(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(80,80);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        spinner.setLayoutParams(params);
        spinner.setBarColor(getResources().getColor(R.color.ColorPrimary));
        spinner.spin();
        dialogLayout.addView(spinner);
        new MaterialDialog.Builder(this)
                .title("Tag your new lesson")
                .customView(dialogLayout, true)
                .positiveText("Create")
                .positiveColor(getResources().getColor(R.color.ColorSubText))
                .negativeText("Cancel")
                .negativeColor(Color.RED)
                .neutralText("Add Tag")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        List<String> selectedTags = getSelectedTags((LinearLayout) dialog.getCustomView());
                        manager.setTags(selectedTags);
                        manager.buildLesson();
                    }

                    @Override
                    public void onNeutral(MaterialDialog dialog) {
                        final EditText editText = new EditText(SubjectDashboard.this);
                        new MaterialDialog.Builder(SubjectDashboard.this)
                                .title("New Tag")
                                .customView(editText, true)
                                .positiveText("Add")
                                .negativeText("Cancel")
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        TagDataHandler tagHandler = new TagDataHandler(subject);
                                        tagHandler.addTag(editText.getText().toString());
                                        newLesson3(manager,true);
                                    }

                                    @Override
                                    public void onNegative(MaterialDialog dialog) {
                                        newLesson3(manager,false);
                                    }
                                })
                                .show();
                    }
                })
                .show();
        manager.setDialog(dialogLayout);
        if (newTag){manager.getTags(true);} else {manager.getTags(false);}
    }

*/




    @Override
    public void onSuccess() {
        SnackbarManager.show(Snackbar.with(getApplicationContext()).text("Lesson created successfully"),this);
    }



    public void toLessonList(View view) {
        Intent intent = new Intent(getApplicationContext(),LessonList.class);
        intent.putExtra("subject",subject);
        startActivity(intent);
    }

}
