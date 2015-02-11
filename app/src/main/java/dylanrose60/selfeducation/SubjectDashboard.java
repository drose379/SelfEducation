package dylanrose60.selfeducation;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;

public class SubjectDashboard extends ActionBarActivity {

    private String subject;

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
                                newLesson1();
                                break;
                        }
                    }
                })
                .show();
    }

    public void newLesson1() {
        final LessonManager manager = new LessonManager(subject);
        final EditText edit = new EditText(this);
        /*NewLesson lessonBuilder = new NewLesson();
        lessonBuilder.run(subject,this,handler);
        */
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
                .neutralText("New Objective")
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
                        String[] objectives = getObjectiveValues(dialogLayout);
                        manager.setObjectives(objectives);
                        Toast.makeText(getApplicationContext(),manager.getLessonName(),Toast.LENGTH_LONG).show();
                        //newLesson3
                    }
                })
                .show();
                }

    public String[] getObjectiveValues(ViewGroup view) {
        ViewGroup viewGroup = view;
        String[] objectiveArray = new String[view.getChildCount()];

        int objectiveCount = viewGroup.getChildCount();

        for (int i = 0; i < objectiveCount;i++) {
            EditText currentEditText = (EditText) viewGroup.getChildAt(i);
            String objective = currentEditText.getText().toString();
            objectiveArray[i] = objective;
        }
        return objectiveArray;
    }

    public void newLesson3(LessonManager manager) {
        //get priority
    }



    public void toLessonList(View view) {
        Intent intent = new Intent(getApplicationContext(),LessonList.class);
        intent.putExtra("subject",subject);
        startActivity(intent);
    }

}
