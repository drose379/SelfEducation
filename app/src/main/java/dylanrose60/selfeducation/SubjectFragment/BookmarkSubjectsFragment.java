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
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
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
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import dylanrose60.selfeducation.Category;
import dylanrose60.selfeducation.CommunicationUtil;
import dylanrose60.selfeducation.CustomAdapter;
import dylanrose60.selfeducation.DBHelper;
import dylanrose60.selfeducation.ExpListAdapter;
import dylanrose60.selfeducation.FragmentUtility;
import dylanrose60.selfeducation.R;
import dylanrose60.selfeducation.Subject;
import dylanrose60.selfeducation.SubjectDashboard;


public class BookmarkSubjectsFragment extends Fragment {

    private String owner_id;

    private Handler handler = new Handler();

    private OkHttpClient client = new OkHttpClient();
    private FragmentUtility fragUtil = new FragmentUtility();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstance) {
        View view = inflater.inflate(R.layout.bookmark_subjects_frag, root, false);
        return view;
    }

    public void onStart() {
        super.onStart();
        PublicSubjectsFragment.setBookmarkFrag(this);
        getOwnerId();
        getBookmarks();
    }

    public void getOwnerId() {
        DBHelper dbClient = new DBHelper(getActivity());
        SQLiteDatabase db = dbClient.getReadableDatabase();
        String query = "SELECT ID FROM owner_id";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            owner_id = cursor.getString(cursor.getColumnIndex("ID"));
        }
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


    public void getBookmarks() {
        String json = null;

        List<String> key = new ArrayList<String>();
        List<String> value = new ArrayList<String>();

        key.add("owner_id");
        value.add(owner_id);

        try {
            json = CommunicationUtil.toJSONString(key,value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), json);
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
                    JSONObject responseObject = new JSONObject(responseString);

                    String categories = responseObject.getString("categories");
                    String subjects = responseObject.getString("bookmarks");


                    List<String> catList = fragUtil.catToList(categories);
                    List<Subject> subjectsList = fragUtil.subToList(subjects,true);
                    HashMap<String, List<String>> map = fragUtil.mapData(catList,subjectsList,true);

                    List<Category> fullCatList = fragUtil.fullCatBuilder(categories);

                    List<String> finalCatList = fragUtil.trimCategories(catList,map);

                    buildList(map, finalCatList, fullCatList);
                    Log.i("finalCats",finalCatList.toString());
                    Log.i("mapped",map.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void buildList(final HashMap<String, List<String>> map, final List<String> categories, final List<Category> catFullInfo) {

        final ExpandableListView expList = (ExpandableListView) getView().findViewById(R.id.bookmarkSubjectList);
        final ExpListAdapter adapter = new ExpListAdapter(getActivity(), map, categories, catFullInfo);
        handler.post(new Runnable() {
            @Override
            public void run() {
                expList.setAdapter(adapter);

                if (map.size() > 0) {
                    LinearLayout logoLayout = (LinearLayout) getView().findViewById(R.id.logoLayout);
                    TextView notice = (TextView) getView().findViewById(R.id.bookmarkNotice);
                    TextView directions = (TextView) getView().findViewById(R.id.bookmarkDirections);

                    logoLayout.setVisibility(View.GONE);
                    notice.setVisibility(View.GONE);
                    directions.setVisibility(View.GONE);
                }

                expList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView view, View v, int group, int child, long id) {
                        List<String> groupList = map.get(categories.get(group));
                        String subject = groupList.get(child);

                        Intent newAct = new Intent(getActivity(), SubjectDashboard.class);
                        Bundle selectedInfo = new Bundle();
                        selectedInfo.putString("subName",subject);
                        selectedInfo.putInt("subType",2);
                        selectedInfo.putString("ownerID",owner_id);
                        newAct.putExtra("selectedInfo", selectedInfo);
                        startActivity(newAct);

                        return true;
                    }
                });
            }
        });

    }


}

