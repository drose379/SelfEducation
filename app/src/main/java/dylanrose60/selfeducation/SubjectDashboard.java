package dylanrose60.selfeducation;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import dylanrose60.selfeducation.DialogFragment.LCreateDialog1;
import dylanrose60.selfeducation.DialogFragment.LCreateDialog2;
import dylanrose60.selfeducation.DialogFragment.LCreateDialog3;
import dylanrose60.selfeducation.DialogFragment.LCreateDialog4;
import dylanrose60.selfeducation.DialogFragment.NewTagDialog;
import dylanrose60.selfeducation.SubjectFragment.PublicSubjectsFragment;

@SuppressLint("NewApi")
public class SubjectDashboard extends ActionBarActivity implements LessonManager.Listener,
        LCreateDialog1.Listener,
        LCreateDialog2.Listener,
        LCreateDialog3.Listener,
        LCreateDialog4.Listener,
        NewTagDialog.Listener
{
        //category is only needed when infalting a public subject dash layout, incase user creates new bookmark
    private String category = null;
    private String subject;
    private int type;
    private String ownerID;
    private Bundle bookmarkInfo;


    private LessonManager manager;
    private FragmentManager fragmentManager = getFragmentManager();
    private OkHttpClient httpClient = new OkHttpClient();
    Handler handler = new Handler();

    static PublicSubjectsFragment publicFrag;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        //Get the title of the subject the user selected or created in the previous activity
        Intent intent = getIntent();
        Bundle subInfo = intent.getBundleExtra("selectedInfo");
        subject = (String) subInfo.get("subName");
        type = (int) subInfo.get("subType");
        //0 == local | 1 == public | 2 == bookmark (if 2, need to get more data about the bookmark

        switch (type) {
            case 0:
                setContentView(R.layout.subject_dashboard_local);
                //GetLocalLessons
                break;
            case 1:
                setContentView(R.layout.subject_dashboard_public);
                ownerID = subInfo.getString("ownerID");
                category = subInfo.getString("category");
                Log.i("publicCategory",subInfo.getString("category"));
                //Get public lessons
                break;
            case 2:
                setContentView(R.layout.subject_dashboard_bookmark);
                ownerID = (String) subInfo.get("ownerID");
                //Bundle bookmarkInfo = getBookmarkInfo(ownerID);
                getBookmarkInfo();
                break;
        }


        //if type is bookmark, get owner ID from bundle and then make request to DB to get bookmark info where ownerID and bookmark name match

        //Need to get bundle from getIntent()
        //Bundle will contain booleans containing the information needed for the correct layout to be chosen
        //Make sure Activity knows which tab being called from, either Local,Public, or bookmark

        super.onCreate(savedInstanceState);


        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        //Set the value of the activity title bar to the title of the subject with: setTitle(title)
        setTitle(subject);

        manager = new LessonManager(this,subject);

        LinearLayout parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Method must be overridden for OnSwipeTouchListener to work... not sure why.
            }
        });
        parentLayout.setOnTouchListener(new OnSwipeTouchListener() {
            @Override
            public boolean onSwipeRight() {
                //this is the method that gets called when physical back key gets pushed on device, works perfectly in this situation too
                onBackPressed();
                return true;
            }
        });
    }

    public String ownerIDJSON(String owner_id) {
        JSONStringer json = new JSONStringer();
        try {
            json.object();
            json.key("owner_id");
            json.value(owner_id);
            json.key("subject");
            json.value(subject);
            json.endObject();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public void getBookmarkInfo() {
        //needs to be changed to use bookmarkUtil

        List<String> keys = new ArrayList<String>();
        List<String> values = new ArrayList<String>();

        keys.add("owner_id");
        keys.add("subject");
        values.add(ownerID);
        values.add(subject);

        String subData = null;

        try {
            subData = CommunicationUtil.toJSONString(keys,values);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),subData);
        Request.Builder builder = new Request.Builder();
        builder.post(body);
        builder.url("http://codeyourweb.net/httpTest/index.php/getBookmarkData");
        Request request = builder.build();
        Call newCall = httpClient.newCall(request);
        newCall.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                String responseString = response.body().string();
                Bundle bookmarkInfo = new Bundle();
                try {
                    JSONArray responseArray = new JSONArray(responseString);
                    JSONObject responseObj = responseArray.getJSONObject(0);


                    bookmarkInfo.putString("lessons_privacy",responseObj.getString("lesson_privacy"));
                    bookmarkInfo.putInt("subscribed", responseObj.getInt("subscribed"));
                    bookmarkInfo.putString("category", responseObj.getString("category"));
                    bookmarkInfo.putInt("bookmarkID", responseObj.getInt("bookmark_id"));
                    SubjectDashboard.this.bookmarkInfo = bookmarkInfo;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
                                manager.setListener(SubjectDashboard.this);
                                newLesson1Test();
                                break;
                            case "Project":
                                //New project code
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
        manager.setObjectives(objectives);
        newLesson3Test(false);
    }

    public void newLesson3Test(Boolean newTag) {
        LCreateDialog3 dialog3 = new LCreateDialog3();
        Bundle newTagBundle = new Bundle();
        newTagBundle.putBoolean("new",newTag);
        dialog3.setArguments(newTagBundle);
        dialog3.show(fragmentManager,"Dialog3");
    }

    @Override
    public void getSelectedTags(List<String> selectedTags) {
        manager.setTags(selectedTags);
        //Need to call to new dialog that asks for default image for the lesson
        //buildLesson();
        LCreateDialog4 dialog4 = new LCreateDialog4();
        dialog4.show(fragmentManager,"dialog4");
    }

    @Override
    public void getDefaultImage(Bitmap image) {
        manager.setImgFile(image);
        buildLesson();
    }

    public void buildLesson() {
        if (bookmarkInfo != null) {
            manager.buildLesson(bookmarkInfo);
        } else if (bookmarkInfo == null) {
            manager.buildLesson();
        }
    }

    @Override
    public void newTag(Boolean newTag) {
        if (newTag) {
            newLesson3Test(true);
        } else {
            newLesson3Test(false);
        }
        //Pass true,so Dialog3 can putExtra a boolean of true of false to decide whether new tag is checked off
    }

    @Override
    public void onSuccess() {
        SnackbarManager.show(Snackbar.with(getApplicationContext()).text("Lesson created successfully"),this);
    }

    public void newBookmarkFromDash(View v) {
        /*
            * Inflate the bookmark dialog with 2 preference options
            * Once user creates lesson, close the current activity with .finish()
            * Show SnackBar with success and green "Go" button
        */

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout dialogLayout = (LinearLayout) inflater.inflate(R.layout.bookmark_prefs,null);

        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.title("Bookmark Preferences");
        builder.customView(dialogLayout,true);
        builder.positiveText("Save");
        builder.negativeText("Cancel");
        builder.positiveColor(getResources().getColor(R.color.ColorSubText));
        builder.negativeColor(Color.RED);
        builder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                String subscribe;
                String publicLessons;

                RadioGroup rGroup1 = (RadioGroup) dialogLayout.findViewById(R.id.question1);
                RadioGroup rGroup2 = (RadioGroup) dialogLayout.findViewById(R.id.question2);

                int selectedID1 = rGroup1.getCheckedRadioButtonId();
                int selectedID2 = rGroup2.getCheckedRadioButtonId();

                RadioButton rButton1 = (RadioButton) dialogLayout.findViewById(selectedID1);
                RadioButton rButton2 = (RadioButton) dialogLayout.findViewById(selectedID2);

                subscribe = (String) rButton1.getText();
                publicLessons = (String) rButton2.getText();

                //create bookmark w values
                Random rand = new Random();
                int bookmarkID = rand.nextInt(1000000);

                List<String> keys = new ArrayList<String>();
                List<String> values = new ArrayList<String>();

                keys.add("subName");
                keys.add("category");
                keys.add("ownerID");
                keys.add("bookmarkID");
                keys.add("subscribe");
                keys.add("publicLessons");

                values.add(subject);
                values.add(category);
                values.add(ownerID);
                values.add(String.valueOf(bookmarkID));
                values.add(subscribe);
                values.add(publicLessons);


                try {
                    String bookmarkInfo = CommunicationUtil.toJSONString(keys,values);
                    Log.i("bookmarkCat",category);
                    createBookmark(bookmarkInfo,subject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onNegative(MaterialDialog dialog) {
                dialog.dismiss();
            }
        });

        MaterialDialog dialog = builder.build();
        dialog.show();

    }

    public static void setPublicSubFrag(PublicSubjectsFragment instance) {
        publicFrag = instance;
    }

    public void createBookmark(String bookmarkInfo,final String bookmarkName) {
        //make request
        RequestBody body = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),bookmarkInfo);
        Request.Builder builder = new Request.Builder();
        builder.post(body);
        builder.url("http://codeyourweb.net/httpTest/index.php/newBookmark");
        Request request = builder.build();
        Call newCall = httpClient.newCall(request);
        newCall.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                bookmarkSuccess(bookmarkName);
                publicFrag.onStart();

                //Need to call finish after a delay, so the snackbar has time to show. 3 seconds.

                ScheduledExecutorService delayCaller = Executors.newSingleThreadScheduledExecutor();

                Runnable delayMethod = new Runnable() {
                    @Override
                    public void run() {
                        SubjectDashboard.this.finish();
                    }
                };

                delayCaller.schedule(delayMethod,1,TimeUnit.SECONDS);

            }
        });
    }

    public void bookmarkSuccess(final String bookmark) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                SnackbarManager.show(Snackbar.with(SubjectDashboard.this)
                        .text("Bookmark Created")
                        .actionLabel("Go")
                        .actionColor(Color.GREEN)
                        .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                        .actionListener(new ActionClickListener() {
                            @Override
                            public void onActionClicked(Snackbar snackbar) {
                                //goToSubject(bookmark);
                                Log.i("goto",bookmark);
                            }
                        }), SubjectDashboard.this);
            }
        });

    }



    public void toLocalLessonList(View view) {
        Intent intent = new Intent(getApplicationContext(),LessonList.class);
        Bundle listParams = new Bundle();
        listParams.putString("subject",subject);
        listParams.putString("type","Local");
        listParams.putString("ownerID",ownerID);
        intent.putExtra("listParams",listParams);
        startActivity(intent);
    }


    public void toBookmarkLessonList(View view) {
        Intent intent = new Intent(getApplicationContext(),LessonList.class);
        Bundle listParams = new Bundle();
        listParams.putString("subject",subject);
        listParams.putString("type","Bookmark");
        listParams.putString("ownerID",ownerID);
        intent.putExtra("listParams",listParams);
        startActivity(intent);
    }

    public void toPublicLessonList(View view) {
        //open lesson list but pass paramas that this is a public sub that user is not subscribed to
        Intent intent = new Intent(getApplicationContext(),LessonList.class);
        Bundle listParams = new Bundle();
        listParams.putString("subject",subject);
        listParams.putString("type","Public");
        listParams.putString("ownerID",ownerID);
        intent.putExtra("listParams",listParams);
        startActivity(intent);
    }

    public void openQuestions(View view) {

    }

}
