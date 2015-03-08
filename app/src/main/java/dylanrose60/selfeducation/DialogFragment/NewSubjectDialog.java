package dylanrose60.selfeducation.DialogFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Random;

import dylanrose60.selfeducation.DBHelper;
import dylanrose60.selfeducation.R;

@SuppressLint("NewApi")
public class NewSubjectDialog extends DialogFragment {

    public interface Listener {
        public void getSubjectInfo(Bundle info);
    }

    private String privacy;
    private Listener listener;
    //Need to make a static getInstance method for getting instance of DBHelper
    private DBHelper dbClient;
    private int randID;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (Listener) activity;
        dbClient = new DBHelper(activity);
        //Create rand ID, assign to randID
        Random randGenerator = new Random();
        randID = randGenerator.nextInt(1000000);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        LinearLayout dialogLayout = (LinearLayout) inflater.inflate(R.layout.new_subject_dialog, null);

        final EditText editText1 = new EditText(getActivity());

        RadioGroup radioGroup = new RadioGroup(getActivity());
        radioGroup.setOrientation(LinearLayout.HORIZONTAL);
        radioGroup.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView midText = new TextView(getActivity());
        midText.setText("Choose Privacy");
        midText.setGravity(Gravity.CENTER_HORIZONTAL);
        midText.setPadding(0,5,0,5);

        final RadioButton publicButton = new RadioButton(getActivity());
        publicButton.setText("Public");
        radioGroup.addView(publicButton);
        final RadioButton privateButton = new RadioButton(getActivity());
        privateButton.setText("Private");
        radioGroup.addView(privateButton);

        dialogLayout.addView(editText1);
        dialogLayout.addView(midText);
        dialogLayout.addView(radioGroup);

        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .title("New Subject")
                .customView(dialogLayout, true)
                .positiveText("Create")
                .positiveColor(getResources().getColor(R.color.ColorSubText))
                .negativeText("Cancel")
                .negativeColor(Color.RED)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        String subjectName = editText1.getText().toString();
                        Bundle subjectInfo = new Bundle();

                        if (privateButton.isChecked()) {
                            privacy = "PRIVATE";
                        } else {
                            privacy = "PUBLIC";
                        }

                        subjectInfo.putString("subject",subjectName);
                        subjectInfo.putString("privacy",privacy);
                        subjectInfo.putInt("serialID",randID);
                        addToLocal(subjectInfo);
                        listener.getSubjectInfo(subjectInfo);
                    }
                });
        MaterialDialog dialog = builder.build();
        return dialog;
    }


    public void addToLocal(Bundle subjectInfo) {
        String subject = subjectInfo.getString("subject");
        SQLiteDatabase localDB = dbClient.getWritableDatabase();
        String insertQuery = "INSERT INTO subject_info (ID,subject) VALUES ('"+randID+"','"+subject+"')";
        localDB.execSQL(insertQuery);
    }

}
