package dylanrose60.selfeducation.SubjectFragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class MySubjectsFragment extends Fragment {

    private List<String> localSerialIDs;
    private OkHttpClient httpClient = new OkHttpClient();
    private Handler handler = new Handler();


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
        localSerialIDs = new ArrayList<String>();
        DBHelper dbClient = new DBHelper(getActivity());
        SQLiteDatabase db = dbClient.getReadableDatabase();
        String query = "SELECT * FROM subject_info";
        Cursor cursor = db.rawQuery(query,null);
        if (cursor.moveToFirst()) {
            int rowCount = cursor.getCount();
            for (int i = 0;i < rowCount;i++) {
                String serialID = cursor.getString(cursor.getColumnIndex("ID"));
                localSerialIDs.add(serialID);
                cursor.moveToNext();
            }
            getRemoteSubjects();
        } else {
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

    public String toJSONString() {
        JSONArray json = new JSONArray(localSerialIDs);
        return json.toString();
    }



    public void getRemoteSubjects() {
        String json = toJSONString();
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
        JSONArray json = new JSONArray(jsonString); //Need to pass in the real JSON String to here
        List<Subject> list = new ArrayList<Subject>();
        for(int i = 0;i<json.length();i++) {
            JSONObject jObject = json.getJSONObject(i);
            list.add(new Subject(jObject.getString("name"),jObject.getString("start_date"),jObject.getInt("lesson_count")));
        }
        return list;
    }

    public void buildList(final List array) {
        ViewGroup layout = (ViewGroup) getView();

      //Build listview

        final ListView listView = (ListView) layout.findViewById(R.id.subjectList);
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

}
