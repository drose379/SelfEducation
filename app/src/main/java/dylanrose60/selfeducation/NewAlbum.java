package dylanrose60.selfeducation;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.provider.MediaStore.Files.FileColumns;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.util.Random;


public class NewAlbum extends Fragment {

    private ViewGroup parentLayout;
    private Context context;

    private String ownerID;

    private String albumName;
    private String albumDesc;
    private File imageFile;

    private OkHttpClient httpClient = new OkHttpClient();


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = (Context) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstance) {
        super.onCreateView(inflater, container, savedInstance);

        Bundle ownerInfo = getArguments();
        ownerID = ownerInfo.getString("ownerID");


        View view = inflater.inflate(R.layout.new_album,container,false);

        setDefaultImageListener(view);
        setConfirmListener(view);
        this.parentLayout = container;
        return view;
    }

    public void setConfirmListener(View view) {
        //Button click listener
        final FloatingActionButton confButton = (FloatingActionButton) view.findViewById(R.id.confButton);
        confButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                /*
                    * Make sure all fields are filled
                    * Grab all data, including the default image url (must be uploaded first)
                    * Create new album
                    * set request code to 0 to decipher difference in onActivityResult
                 */

                albumName = null;
                albumDesc = null;

                EditText albumNameArea = (EditText) getView().findViewById(R.id.albumName);
                EditText albumDescArea = (EditText) getView().findViewById(R.id.albumDesc);

                if (albumNameArea.length() > 1) {
                    albumName = albumNameArea.getText().toString();
                } else {
                    //set errror or use error anim
                }
                if (albumDescArea.length() > 1) {
                    albumDesc = albumDescArea.getText().toString();
                } else {
                    //set error or error anim
                }

                //check for title and desc also

                if (albumName != null &&albumDesc != null && imageFile != null) {
                    Log.i("allFull", "AllFull");
                    /*
                        * once both requests are finished, (onResponse from the second request) close this fragment and reload the lesson dashboard
                        * Build a multipart-form and send to php script that generates a random name for the image and saves the default photo in directory
                        * Image name on directory will be returned on onResponse(Response).
                        * Use returned name to save all photo album info in photo_albums table in DB
                     */

                    MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpeg");

                    RequestBody body = new MultipartBuilder()
                            .type(MultipartBuilder.FORM)
                            .addFormDataPart("photo",imageFile.getName(),RequestBody.create(MEDIA_TYPE_JPG,imageFile))
                            .build();

                    Request.Builder rBuilder = new Request.Builder();
                    rBuilder.url("http://104.236.15.47/selfEducate/index.php/albumDefInsert");
                    rBuilder.post(body);

                    Call newCall = httpClient.newCall(rBuilder.build());
                    newCall.enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {

                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            //grab image location on server from return content
                            //call method to insert photo album entry in db, pass image location
                            //.finish() this fragment and create a snackbar, make sure dashboard is refreshed in case this is first of type (photo album)
                        }
                    });

                }
            }
        });
    }

    public void setDefaultImageListener(View view) {
        //Default image click listener
        ImageView defaultImage = (ImageView) view.findViewById(R.id.defaultImage);
        defaultImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                    * Bring up option to choose from gallery or take photo with camera (Material Dialog)
                    * Set photo taken to imageview src
                    * Save data
                */
                MaterialDialog.Builder dBuilder = new MaterialDialog.Builder(context);
                dBuilder.title("Select From");
                dBuilder.items(new String[]{"Camera", "Storage"});
                dBuilder.itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence itemSelected) {
                        switch (String.valueOf(itemSelected)) {
                            case "Camera":
                                Intent startCamera = new Intent(getActivity(), CameraAccess.class);
                                startActivityForResult(startCamera, 1);
                                break;

                            case "Storage":
                                Intent startGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(startGallery, 2);
                                break;
                        }
                    }
                });
                MaterialDialog dialog = dBuilder.build();
                dialog.show();
            }
        });

    }

    //grab default image path from camera activity
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        switch (requestCode) {
            case 1:
                String imagePath = data.getStringExtra("imagePath");
                imageFile = new File(imagePath);

                try {
                    ExifInterface jpegInterface = new ExifInterface(imagePath);
                    jpegInterface.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(ExifInterface.ORIENTATION_ROTATE_90));
                    jpegInterface.saveAttributes();
                    setImagePreview(true);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case 2:
                Uri imageUri = data.getData();

                String[] external = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().getContentResolver().query(imageUri,external,null,null,null);
                cursor.moveToFirst();
                int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                String realPath = cursor.getString(column_index);

                imageFile =  new File(realPath);
                try {
                    setImagePreview(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void setImagePreview(boolean custom) throws IOException {
        ImageView defaultImage = (ImageView) getView().findViewById(R.id.defaultImage);
        ExifInterface imageParams = new ExifInterface(imageFile.getPath());

        ViewGroup.LayoutParams params = defaultImage.getLayoutParams();
        params.width = 350;
        params.height= 350;
        defaultImage.setLayoutParams(params);
        if (custom) {
            if (imageParams.getAttribute(ExifInterface.TAG_ORIENTATION).equals(String.valueOf(ExifInterface.ORIENTATION_ROTATE_90))) {
                defaultImage.setRotation(90f);
                defaultImage.setImageBitmap(BitmapFactory.decodeFile(imageFile.getPath()));
            }
        } else {
            defaultImage.setImageBitmap(BitmapFactory.decodeFile(imageFile.getPath()));
        }


    }


    @Override
    public void onDetach() {
        Log.i("detach","Detached");
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();

        Window currentWindow = getActivity().getWindow();
        WindowManager.LayoutParams windowParams = (WindowManager.LayoutParams) currentWindow.getAttributes();
        windowParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN;
        currentWindow.setAttributes(windowParams);


    }

}
