package dylanrose60.selfeducation.DialogFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
        public void dialog1Complete(Bundle subjectInfo);
    }

    private String privacy;
    private Listener listener;
    //Need to make a static getInstance method for getting instance of DBHelper
    private DBHelper dbClient;
    //private OkHttpClient client = new OkHttpClient();
    //private Handler handler = new Handler();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (Listener) activity;
        dbClient = new DBHelper(activity);
        //Create rand ID, assign to randID
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
                .autoDismiss(false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        String subjectName = editText1.getText().toString();
                        if (subjectName.length() > 0) {
                            dialog.dismiss();
                            Bundle subjectInfo = new Bundle();

                            if (privateButton.isChecked()) {
                                privacy = "PRIVATE";
                            } else {
                                privacy = "PUBLIC";
                            }

                            subjectInfo.putString("subject", subjectName);
                            subjectInfo.putString("privacy", privacy);
                            getOwnerID(subjectInfo);
                            //addToRemote(subjectInfo);
                        } else {
                            editText1.setError("Please enter a subject");
                        }

                    }
                });
        MaterialDialog dialog = builder.build();
        return dialog;
    }


    public void getOwnerID(Bundle subjectInfo) {
        //Get readable DB, check if owner ID is present, if it is, get the ID and add to bundle, if its not, getWritable and create one
        //DBHelper dbClient = new DBHelper(getActivity());
        SQLiteDatabase readableDB = dbClient.getReadableDatabase();
        String getOwnerId = "SELECT ID from owner_id";
        Cursor response = readableDB.rawQuery(getOwnerId,null);
        if (response.moveToFirst()) {
            String ownerId = response.getString(response.getColumnIndex("ID"));
            subjectInfo.putString("owner_id",ownerId);
            listener.dialog1Complete(subjectInfo);
            //addToRemote(subjectInfo);
        } else {
            Random rand = new Random();
            int randID = rand.nextInt(1000000);
            SQLiteDatabase writableDB = dbClient.getWritableDatabase();
            String insertID = "INSERT INTO owner_id (ID) VALUES ('"+randID+"')";
            writableDB.execSQL(insertID);
            getOwnerID(subjectInfo);
        }
    }

    /*

    public void addToRemote(Bundle subjectInfo) {
        String json = toJson(subjectInfo);
        request(json);
    }

    public String toJson(Bundle subjectInfo) {
        String subject = subjectInfo.getString("subject");
        String privacy = subjectInfo.getString("privacy");
        String owner_id = subjectInfo.getString("owner_id");

        JSONStringer stringer = new JSONStringer();
        try {
            stringer.object();
            stringer.key("subjectName");
            stringer.value(subject);
            stringer.key("privacy");
            stringer.value(privacy);
            stringer.key("owner_id");
            stringer.value(owner_id);
            stringer.endObject();
            return stringer.toString();
        } catch (JSONException e) {
            throw new RuntimeException();
        }
    }

    public void request(String stringBody) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),stringBody);
        Request.Builder builder = new Request.Builder();
        builder.post(body);
        builder.url("http://codeyourweb.net/httpTest/index.php/newSubject");
        Request request = builder.build();
        Call newCall = client.newCall(request);
        newCall.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.subjectCreated();
                    }
                });
            }
        });
    }

    */

}
