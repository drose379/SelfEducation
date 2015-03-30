package dylanrose60.selfeducation.SubjectFragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.Random;

import dylanrose60.selfeducation.CustomAdapter;
import dylanrose60.selfeducation.DBHelper;
import dylanrose60.selfeducation.R;
import dylanrose60.selfeducation.Subject;
import dylanrose60.selfeducation.SubjectDashboard;


public class BookmarkSubjectsFragment extends Fragment {

    private String owner_id;
    private Handler handler = new Handler();
    private OkHttpClient client = new OkHttpClient();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup root,Bundle savedInstance) {
        View view = inflater.inflate(R.layout.bookmark_subjects_frag,root,false);
        return view;
    }

    public void onStart() {
        super.onStart();
        PublicSubjectsFragment.setBookmarkFrag(this);
        getOwnerId();
        //getBookmarks();
    }

    public void getOwnerId() {
        DBHelper dbClient = new DBHelper(getActivity());
        SQLiteDatabase db = dbClient.getReadableDatabase();
        String query = "SELECT ID FROM owner_id";
        Cursor cursor = db.rawQuery(query,null);
        if (cursor.moveToFirst()) {
            owner_id = cursor.getString(cursor.getColumnIndex("ID"));
        }
    }
/*
    public void getBookmarks() {
        String json = ownerIDJSON();
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),json);
        Request.Builder builder = new Request.Builder();
        builder.url("http://codeyourweb.net/httpTest/index.php/getBookmarks");
        builder.post(body);
        Request request = builder.build();
        Call newCall = client.newCall(request);
        newCall.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                String responseString = response.body().string();
                try {
                    List<Subject> bookmarks = toArray(responseString);
                    if (bookmarks.size() > 0) {
                        final LinearLayout logoText = (LinearLayout) getView().findViewById(R.id.logoLayout);
                        final TextView noticeText = (TextView) getView().findViewById(R.id.bookmarkNotice);
                        final TextView directionsText = (TextView) getView().findViewById(R.id.bookmarkDirections);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                logoText.setVisibility(View.GONE);
                                noticeText.setVisibility(View.GONE);
                                directionsText.setVisibility(View.GONE);
                            }
                        });

                    }
                    //buildList(bookmarks);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public String ownerIDJSON() {
        JSONStringer json = new JSONStringer();
        try {
            json.object();
            json.key("owner_id");
            json.value(owner_id);
            json.endObject();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
/*
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

        final ListView listView = (ListView) layout.findViewById(R.id.bookmarkSubjectList);
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

    */

}
