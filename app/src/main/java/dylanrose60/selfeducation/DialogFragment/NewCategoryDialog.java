package dylanrose60.selfeducation.DialogFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
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

import dylanrose60.selfeducation.R;

@SuppressLint("NewApi")
public class NewCategoryDialog extends DialogFragment {

    //Need to create a listener interface
    //Make sure to pass false for hasArgs when calling regularly from main.
    //When user clicks positive button, check if boolean sendToListener is true, if it is, use the listener.
    //Only assign the listener to the activity if boolean hasArgs is true

    public interface Listener {
        public void newCategoryAdded();
    }

    private OkHttpClient httpClient = new OkHttpClient();
    private Handler handler = new Handler();

    private boolean runCallback = false;
    public Listener listener = null;

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle callbackBool = getArguments();
        if (callbackBool != null) {
            Boolean callback = callbackBool.getBoolean("callback");
            runCallback = true;
            listener = (Listener) activity;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout dialogLayout = (LinearLayout) inflater.inflate(R.layout.new_category_layout,null);

        final EditText catNameArea = (EditText) dialogLayout.findViewById(R.id.catNameArea);
        final EditText catDescArea = (EditText) dialogLayout.findViewById(R.id.catDescArea);


        MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(getActivity())
                .title("New Category")
                .customView(dialogLayout,true)
                .positiveText("Add")
                .positiveColor(getResources().getColor(R.color.ColorSubText))
                .negativeText("Cancel")
                .negativeColor(Color.RED)
                .autoDismiss(false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        String catName = catNameArea.getText().toString();
                        String catDesc = catDescArea.getText().toString();

                        if (catName.length() < 2) {
                            catNameArea.setError("Please provide a valid category name");
                        }
                        if (catDesc.length() < 8) {
                            catDescArea.setError("Must be at least 8 characters");
                        }
                        dialog.dismiss();
                        addCategory(catName,catDesc);
                    }
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                });
        MaterialDialog dialog = dialogBuilder.build();
        return dialog;
    }

    public void addCategory(String name,String description) {
        String json = categoryJSON(name,description);
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),json);
        Request.Builder rBuilder = new Request.Builder();
        rBuilder.url("http://codeyourweb.net/httpTest/index.php/newCategory");
        rBuilder.post(body);
        Request request = rBuilder.build();
        Call newCall = httpClient.newCall(request);
        newCall.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (runCallback) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.newCategoryAdded();
                        }
                    });
                }
            }
        });
    }

    public String categoryJSON(String name,String description) {
        JSONStringer json = new JSONStringer();
        try {
            json.object();
            json.key("catName");
            json.value(name);
            json.key("catDesc");
            json.value(description);
            json.endObject();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return json.toString();
    }

}

