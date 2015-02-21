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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class SubjectDashboard extends ActionBarActivity implements LessonManager.Listener,TagDataHandler.Listener {

    private String subject;
    private LessonManager manager;

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

    public void openTagManager(View view) {
        Intent intent = new Intent(getApplicationContext(),TagManager.class);
        intent.putExtra("subject",subject);
        startActivity(intent);
    }

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
                        newLesson3(manager);
                    }
                })
                .show();
                }

    public List<String> getObjectiveValues(ViewGroup view) {
        ViewGroup viewGroup = view;

        List<String> objectives = new ArrayList<>();

        int objectiveCount = viewGroup.getChildCount();

        for (int i = 0; i < objectiveCount;i++) {
            EditText currentEditText = (EditText) viewGroup.getChildAt(i);
            String objective = currentEditText.getText().toString();
            objectives.add(i,objective);
        }
        return objectives;
    }

    public void newLesson3(LessonManager manager) {
        manager.setListener(this);
        manager.getTags();
        //Create MaterialDialog and layout with a progress loader
        //Once response from DB comes in (getAllTags()), get rid of the spinner animation and add the check boxes to the Dialog
        //Use a setter and getter in the LessonManager to enable access to the Layout in getAllTags. Also have a getter and setter for spinner
            //manager.setLayout(layoutName)
            //manager.setSpinner(spinnerName)
            //Spinner spin = manager.getSpinner();
            //spin.GONE;
            //LinearLayout layout = manager.getLayout()
            //layout.addView(checkbox)
    }

    @Override
    public void getAllTags(LessonManager manager,String stringArray) {
        try {
            JSONArray array = new JSONArray(stringArray);
            String[] tagArray = new String[array.length()];

            for (int i = 0;i < array.length();i++) {
                String tag = array.getJSONObject(i).getString("tag_name");
                tagArray[i] = tag;
            }
            buildTagDialog(manager,tagArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void buildTagDialog(final LessonManager manager,String[] tags) {
        final LinearLayout dialogLayout = (LinearLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.new_lesson_layout, null);
        for (String tag : tags) {
            CheckBox checkbox = new CheckBox(this);
            checkbox.setText(tag);
            dialogLayout.addView(checkbox);
        }
        new MaterialDialog.Builder(this)
                .title("Tag your new lesson")
                .customView(dialogLayout, true)
                .positiveText("Create")
                .positiveColor(getResources().getColor(R.color.ColorSubText))
                .negativeText("Cancel")
                .negativeColor(Color.RED)
                .neutralText("New Tag")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        List<String> selectedTags = getSelectedTags(dialogLayout);
                        manager.setTags(selectedTags);
                        manager.buildLesson();
                    }
                    @Override
                    public void onNeutral(MaterialDialog masterDialog) {
                        final EditText editText = new EditText(SubjectDashboard.this);
                        new MaterialDialog.Builder(SubjectDashboard.this)
                                .title("Add Tag")
                                .customView(editText,true)
                                .positiveText("Insert")
                                .negativeText("Back")
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog subDialog) {
                                        TagDataHandler tagHandler = new TagDataHandler(subject);
                                        tagHandler.setListener(SubjectDashboard.this);
                                        tagHandler.addTag(editText.getText().toString());
                                    }
                                    @Override
                                    public void onNegative(MaterialDialog dialog) {
                                        manager.getTags();
                                    }
                                })
                                .show();

                    }
                })
                .show();
    }

    @Override
    public void tagAdded() {
        manager.getTags();
    }


    public List<String> getSelectedTags(LinearLayout layout) {
        List<String> selectedTags = new ArrayList<>();
        for (int i = 0;i < layout.getChildCount();i++) {
            CheckBox currentBox = (CheckBox) layout.getChildAt(i);
            if (currentBox.isChecked()) {
                String boxValue = currentBox.getText().toString();
                selectedTags.add(boxValue);
            }
        }
        return selectedTags;
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

}
