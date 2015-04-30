package dylanrose60.selfeducation.DialogFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import android.net.Uri;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import dylanrose60.selfeducation.R;

@SuppressLint("NewApi")
public class LCreateDialog4 extends DialogFragment {

    private Listener listener;

    private LinearLayout dialogLayout;
    private OkHttpClient httpClient = new OkHttpClient();

    //private File imgFile;
    private Bitmap imgBitmap;

    public interface Listener {
        public void getDefaultImage(Bitmap imgBitmap);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = (Listener) activity;
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        dialogLayout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.img_picker,null);
        Button imgPick = (Button) dialogLayout.findViewById(R.id.imgPickButton);
        imgPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inflateImgPicker = new Intent();
                inflateImgPicker.setType("image/*");
                inflateImgPicker.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(inflateImgPicker,"Select image"),1);
            }
        });
    }

    public void onActivityResult(int requestCode,int resultCode,Intent intent) {
        ImageView testImg = (ImageView) dialogLayout.findViewById(R.id.testImg);
        testImg.setVisibility(View.VISIBLE);

        //Get the Local URI path for the image on the device
        Uri imgUri = intent.getData();

        /*
        //Just grabs a string (_data) and puts it into a String[] to be passed into ContentResolver query() method
        String[] fileStream = {MediaStore.Images.Media.DATA};
        //Get all data for the given path
        Cursor cursor = getActivity().getContentResolver().query(imgUri,fileStream,null,null,null);
        //make sure cursor is on first item
        cursor.moveToFirst();

        //Get the column number for _data in the cursor. _data holds the path

        //int columnIndex = cursor.getColumnIndex(fileStream[0]);
        int columnIndex = cursor.getColumnIndex("_data");
        //get the path of the image using the columnIndex from the cursor and _data column
        String filePath = cursor.getString(columnIndex);
        //Create a new file from the filePath
        File file = new File(filePath);

        imgFile = file;
        Log.i("imageFileSize",String.valueOf(imgFile.length()));

        //Need to pass this file to a method that uploads file to server. Php script needs to save path to image on server in lesson table in database

        Bitmap finalImage = BitmapFactory.decodeFile(filePath);

        */

        try {
            InputStream input = getActivity().getContentResolver().openInputStream(imgUri);
            imgBitmap = BitmapFactory.decodeStream(input);
            testImg.setImageBitmap(imgBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //testImg.setImageBitmap(finalImage);



    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity())
                .title("Set default image")
                .customView(dialogLayout,true)
                .positiveText("Finish")
                .negativeText("Cancel")
                .positiveColor(getResources().getColor(R.color.ColorSubText))
                .negativeColor(Color.RED)
                .neutralText("Stock Photo")
                .neutralColor(getActivity().getResources().getColor(R.color.ColorBlack))
                .autoDismiss(false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        //Make sure imgUriString is not null, if it is, do not move on
                        //if it is NOT null, send to listener which will create the lesson
                        dialog.dismiss();
                        if (imgBitmap != null) {
                            listener.getDefaultImage(imgBitmap);
                        } else {
                            //Do not dismiss, ask user to choose a photo (offer stock photos)
                        }
                    }

                    @Override
                    public void onNeutral(MaterialDialog dialog) {
                        Log.i("neutralPressed","Neutral Pressed");

                        /*
                            * Add progresswheel to img_picker XML layout and set its visibility to Visible when neutral is pressed
                            * Set visibility to GONE when all info is returned
                         */

                        ProgressWheel wheel = (ProgressWheel) dialogLayout.findViewById(R.id.progSpinner);
                        wheel.setVisibility(View.VISIBLE);

                        /*Need to create a listview with all stock photos inside, once user selects photo, buildLesson()
                            * Get all Bitmaps in a list List<Bitmap>
                            * Create ListView
                            * Create custom ListView adapter that adds each image to view
                            * Create onClickListener for images that gives link to selected image
                        */
                        Request.Builder builder = new Request.Builder();
                        builder.url("http://codeyourweb.net/httpTest/index.php/getStockImages");
                        Request request = builder.build();
                        Call newCall = httpClient.newCall(request);
                        newCall.enqueue(new Callback() {
                            @Override
                            public void onFailure(Request request, IOException e) {
                                Log.i("stockImagesInfo","Failure");
                            }

                            @Override
                            public void onResponse(Response response) throws IOException {

                                Log.i("stockImagesInfo", "Called");
                                String responseString = response.body().string();
                                Log.i("stockImagesInfo",String.valueOf(responseString.length()));

                                try {

                                    /*
                                        * Ship application with all stock images pre-loaded, put them in their own dir and put them in a ListView

                                    Loop over i for the amount of stock images there are,
                                    Inside the loop, use I to grab the current drawable
                                    Add current drawable to List<Drawable>
                                    Use that list in the adapter to make a ListView of images to choose from

                                    Drawable test = getActivity().getResources()
                                            .getDrawable(getResources().getIdentifier("stock_"+i,"drawable",getActivity().getPackageName()));
                                    */
                                    JSONArray masterResp = new JSONArray(responseString);
                                    Log.i("stockImagesInfo", String.valueOf(masterResp.length()));
                                    dialogLayout.findViewById(R.id.progSpinner).setVisibility(View.GONE);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                });

        MaterialDialog dialog = builder.build();
        return dialog;
    }

}