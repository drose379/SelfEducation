package dylanrose60.selfeducation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONStringer;

import java.io.IOException;

public class NewLesson {

    private String Subject;
    private Context context;
    private Handler handler;
    private Boolean error = false;

    private OkHttpClient client = new OkHttpClient();
    private MediaType mediaType = MediaType.parse("application/json");

    private int prioritySelection;

    public void run(String subject,Context appContext,Handler handler) {
        Subject = subject;
        context = appContext;
        this.handler = handler;
        lessonDialog();
    }


    public void lessonDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        final EditText editText = new EditText(context);
        //final RadioGroup checkboxes = findViewById(R.id.radioGroup);
        //Add radioGroup to dialogBuilder with setView();

        RadioButton rbLow = new RadioButton(context);
        RadioButton rbNeutral = new RadioButton(context);
        RadioButton rbHigh = new RadioButton(context);


        //Look into using a for() loop to dynamically add the buttons to the group
        final RadioGroup rbGroup = new RadioGroup(context);
        rbGroup.addView(rbLow);
        rbGroup.addView(rbNeutral);
        rbGroup.addView(rbHigh);
        rbGroup.setOrientation(LinearLayout.HORIZONTAL);
        rbGroup.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);

        rbLow.setText("Low");
        rbLow.setId(R.id.low);
        rbNeutral.setText("Neutral");
        rbNeutral.setId(R.id.neutral);
        rbHigh.setText("High");
        rbHigh.setId(R.id.high);



        TextView priorityText = new TextView(context);
        priorityText.setText("Select Priority");
        priorityText.setPadding(2,2,2,2);
        priorityText.setGravity(Gravity.CENTER_HORIZONTAL);


        LinearLayout alertLayout = new LinearLayout(context);
        alertLayout.setOrientation(LinearLayout.VERTICAL);
        alertLayout.addView(editText);
        alertLayout.addView(priorityText);
        alertLayout.addView(rbGroup);

        dialogBuilder.setTitle("Create New Lesson");
        if (error) {
            editText.setError("Enter a valid name");
        }
        dialogBuilder.setView(alertLayout);
        dialogBuilder.setIcon(R.drawable.rename_32);
        dialogBuilder.setPositiveButton("Save",new DialogInterface.OnClickListener() {
            AlertDialog completeDialog = dialogBuilder.create();
            @Override
            public void onClick(DialogInterface dialog, int selected) {
                String newLesson = editText.getText().toString();
                int textLength = editText.length();
                int priorityId = rbGroup.getCheckedRadioButtonId();
                int priority = getPriorityNum(priorityId);

                if (textLength == 0) {
                    error = true;
                    completeDialog.dismiss();
                    lessonDialog();
                } else {
                    AlertDialog loader = showLoader();
                    createLesson(newLesson,priority,loader);
                    error = false;
                }
            }
        });
        dialogBuilder.show();
    }

    public AlertDialog showLoader() {
        AlertDialog.Builder load = new AlertDialog.Builder(context);
        ProgressBar spin = new ProgressBar(context);
        load.setView(spin);
        load.setMessage("Please Wait");
        AlertDialog readyDialog = load.create();
        readyDialog.show();
        return readyDialog;
    }

    public int getPriorityNum(int id) {
        int priorityNum = 0;
        switch (id) {
            case R.id.high :
                priorityNum = 3;
                break;
            case R.id.neutral :
                priorityNum = 2;
                break;
            case R.id.low :
                priorityNum = 1;
                break;
        }
        return priorityNum;
    }


    public void createLesson(String lesson,int priority,final AlertDialog loader) {
        String jsonReady = buildJson(lesson,priority);
        Request.Builder builder = new Request.Builder();
        RequestBody body = RequestBody.create(mediaType,jsonReady);
        builder.post(body);
        builder.url("http://codeyourweb.net/httpTest/index.php/newLesson");
        Request request = builder.build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                loader.dismiss();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,"Lesson Created",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    public String buildJson(String lesson,int priority) {
        try {
            JSONStringer builder = new JSONStringer();
            builder.object();
            builder.key("subject");
            builder.value(Subject);
            builder.key("lesson");
            builder.value(lesson);
            builder.key("priority");
            builder.value(priority);
            builder.endObject();
            String jsonString = builder.toString();
            return jsonString;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
