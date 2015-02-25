package dylanrose60.selfeducation;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.FragmentManager;
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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
@SuppressLint("NewApi")
public class SubjectDashboard extends ActionBarActivity implements LessonManager.Listener,LCreateDialog1.Listener,LCreateDialog2.Listener {

    private String subject;
    private LessonManager manager = new LessonManager(subject);
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
        //assign objectives to manager with manager.setObjectives()
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

    @Override
    public void getAllTags(LessonManager manager,String stringArray,boolean newTag) {
        View animation = manager.getDialogLayout().getChildAt(0);
        animation.setVisibility(View.GONE);
        try {
            JSONArray array = new JSONArray(stringArray);
            String[] tagArray = new String[array.length()];

            for (int i = 0;i < array.length();i++) {
                String tag = array.getJSONObject(i).getString("tag_name");
                tagArray[i] = tag;
            }
            if (newTag) {
                buildTagDialog(manager, tagArray, true);
            } else {
                buildTagDialog(manager,tagArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void buildTagDialog(LessonManager manager,String[] tagArray) {
        LinearLayout dialogLayout = manager.getDialogLayout();
        for (String tag : tagArray) {
            CheckBox checkbox = new CheckBox(this);
            checkbox.setText(tag);
            dialogLayout.addView(checkbox);
        }
    }

    public void buildTagDialog(LessonManager manager, String[] tagArray,boolean checkNew) {
        LinearLayout dialogLayout = manager.getDialogLayout();
        int tagCount = tagArray.length;

        for (int i = 0; i < tagCount - 1; i++) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(tagArray[i]);
            dialogLayout.addView(checkBox);
        }
        CheckBox lastBox = new CheckBox(this);
        lastBox.setText(tagArray[tagCount-1]);
        lastBox.setChecked(true);
        dialogLayout.addView(lastBox);
    }

    public List<String> getSelectedTags(LinearLayout layout) {
        List<String> selectedTags = new ArrayList<>();
        for (int i = 1;i < layout.getChildCount();i++) {
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
