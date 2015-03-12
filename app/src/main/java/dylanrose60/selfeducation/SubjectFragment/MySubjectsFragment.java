package dylanrose60.selfeducation.SubjectFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dylanrose60.selfeducation.CustomAdapter;
import dylanrose60.selfeducation.DBHelper;
import dylanrose60.selfeducation.MainActivity;
import dylanrose60.selfeducation.R;
import dylanrose60.selfeducation.Subject;
import dylanrose60.selfeducation.SubjectDashboard;
import dylanrose60.selfeducation.SubjectManager;

public class MySubjectsFragment extends Fragment {

    private OkHttpClient httpClient = new OkHttpClient();
    private Handler handler = new Handler();
    private String ownerID;
    private List<Subject> mySubjects;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MainActivity.setFrag0(this);
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.my_subjects_frag,root,false);
        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        getLocalSubjects();
    }

    public void getLocalSubjects() {
        DBHelper dbClient = new DBHelper(getActivity());
        SQLiteDatabase db = dbClient.getReadableDatabase();
        String query = "SELECT ID FROM owner_id";
        Cursor cursor = db.rawQuery(query,null);
        if (cursor.moveToFirst()) {
            ownerID = cursor.getString(cursor.getColumnIndex("ID"));
            getSubjects();
        }
         else {
            showWelcomeText();
         }
    }

    public void showWelcomeText() {
        ViewGroup layout = (ViewGroup) getView();

        ProgressWheel spinner = (ProgressWheel) layout.findViewById(R.id.spinnerAnimation);
        TextView welcomeText1 = (TextView) layout.findViewById(R.id.welcomeText1);
        TextView welcomeText2 = (TextView) layout.findViewById(R.id.welcomeText2);

        spinner.setVisibility(View.GONE);
        welcomeText1.setVisibility(View.VISIBLE);
        welcomeText2.setVisibility(View.VISIBLE);
    }

    public String toJSONString(String ownerID) {
        JSONStringer json = new JSONStringer();
        try {
            json.object();
            json.key("owner_id");
            json.value(ownerID);
            json.endObject();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }



    public void getSubjects() {
        String json = toJSONString(ownerID);
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),json);
        Request.Builder builder = new Request.Builder();
        builder.post(body);
        builder.url("http://codeyourweb.net/httpTest/index.php/getLocalSubjectData"); //add url
        Request request = builder.build();
        Call newCall = httpClient.newCall(request);
        newCall.enqueue(new Callback() {
            @Override
            public void onResponse(Response response) throws IOException {
                String responseString = response.body().string();
                final ProgressWheel spinAnimation = (ProgressWheel) getView().findViewById(R.id.spinnerAnimation);
                final TextView welcomeText1 = (TextView) getView().findViewById(R.id.welcomeText1);
                final TextView welcomeText2 = (TextView) getView().findViewById(R.id.welcomeText2);
                final LinearLayout logoText = (LinearLayout) getView().findViewById(R.id.logoLayout);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        spinAnimation.setVisibility(View.GONE);
                        logoText.setVisibility(View.GONE);
                        welcomeText1.setVisibility(View.GONE);
                        welcomeText2.setVisibility(View.GONE);
                    }
                });
                try {
                    List<Subject> subjectList = toArray(responseString);
                    buildList(subjectList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Request request,IOException exception) {

            }
        });
    }


    public List<Subject> toArray(String jsonString) throws JSONException {
        JSONArray json = new JSONArray(jsonString);
        List<Subject> list = new ArrayList<Subject>();
        for(int i = 0;i<json.length();i++) {
            JSONObject jObject = json.getJSONObject(i);
            list.add(new Subject(jObject.getString("name"),jObject.getString("start_date"),jObject.getInt("lesson_count")));
        }
        mySubjects = list;
        return list;
    }

    public void buildList(final List array) {
        ViewGroup layout = (ViewGroup) getView();

      //Build listview

        final ListView listView = (ListView) layout.findViewById(R.id.subjectList);
        registerForContextMenu(listView);
        final ArrayAdapter<Subject> adapter = new CustomAdapter(getActivity(),R.layout.subject_card_view,array);
        handler.post(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(adapter);
                final List<Subject> list = array;
                //registerForContextMenu(listView);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView parent,View view,int position,long id) {
                        Subject selectedSubject = list.get(position);
                        String subjectName = selectedSubject.getSubjectName();
                        Intent newAct = new Intent(getActivity(),SubjectDashboard.class);
                        newAct.putExtra("subjectName",subjectName);
                        startActivity(newAct);
                    }
                });
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu,View v,ContextMenu.ContextMenuInfo info) {
        super.onCreateContextMenu(menu,v,info);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.subject_list_menu,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Subject selectedSubject = mySubjects.get(info.position);
        String subjectName = selectedSubject.getSubjectName();
        switch(item.getItemId()) {
            case R.id.delete :
                deleteConfirm(subjectName);
                return true;
            default:
                return false;
        }
    }

    public void deleteConfirm(final String subject) {
        SnackbarManager.show(Snackbar.with(getActivity())
                .text("Are you sure?")
                .actionLabel("Confirm")
                .actionColor(Color.RED)
                .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                .actionListener(new ActionClickListener() {
                    @Override
                    public void onActionClicked(Snackbar snackbar) {
                        snackbar.dismiss();
                        deleteSubject(subject);
                    }
                }), getActivity());
    }

    public void deleteSubject(String subject) {
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody rBody = RequestBody.create(mediaType,subject);

        Request.Builder rBuilder = new Request.Builder();
        rBuilder.post(rBody);
        rBuilder.url("http://codeyourweb.net/httpTest/index.php/deleteSubject");
        Request request = rBuilder.build();
        Call call = httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                deletedCallback();
            }
        });
    }

    public void deletedCallback() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                SnackbarManager.show(Snackbar.with(getActivity()).text("Deleted Successfully"),getActivity());
            }
        });
        onStart();
    }

}
