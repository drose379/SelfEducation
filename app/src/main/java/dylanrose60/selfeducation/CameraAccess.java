package dylanrose60.selfeducation;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
public class CameraAccess extends ActionBarActivity {

    private Camera mainCamera = null;
    private CameraPreview mainPreview = null;
    private File imageFile;

    private Camera.PictureCallback mainCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            imageFile = getMediaFile();
            try {
                FileOutputStream output = new FileOutputStream(imageFile);
                output.write(data);
                output.close();
            } catch (FileNotFoundException e) {
                Log.i("fileNotFound",e.getMessage());
            } catch(IOException e) {
                Log.i("CannotAccessFile",e.getMessage());
            }

            //Need to act on captured image
            //get rid of capture button and change it to a save or discard button
            //save link to image if savd and release camera



            LinearLayout origControls = (LinearLayout) findViewById(R.id.origionalControls);
            LinearLayout afterControl = (LinearLayout) findViewById(R.id.afterPhotoControls);
            LinearLayout camSettings = (LinearLayout) findViewById(R.id.cameraSettings);

            origControls.setVisibility(View.GONE);
            camSettings.setVisibility(View.GONE);
            afterControl.setVisibility(View.VISIBLE);

            Button save = (Button) afterControl.findViewById(R.id.save);
            Button discard = (Button) afterControl.findViewById(R.id.discard);
            discard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainCamera.stopPreview();
                    imageFile.delete();
                    init();
                    //mainCamera.startPreview();
                }
            });

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callback = new Intent();
                    callback.putExtra("imagePath",imageFile.getPath());
                    setResult(1,callback);
                    finish();
                }
            });
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);
        init();
    }

    public void init() {
        if (mainCamera == null) {
            mainCamera = getCamera();

            Camera.Parameters params = mainCamera.getParameters();
            List<String> flashModes = params.getSupportedFlashModes();
            List<String> focus = params.getSupportedFocusModes();
            params.setFlashMode("on");
            params.setFocusMode("auto");
            mainCamera.setParameters(params);
            Log.i("focusModes",focus.toString());
        }
        if (mainPreview == null) {
            mainPreview = new CameraPreview(this,mainCamera);
        }

        mainCamera.setDisplayOrientation(90);


        FrameLayout previewWindow = (FrameLayout) findViewById(R.id.previewContainer);
        previewWindow.removeAllViews();
        previewWindow.addView(mainPreview);

        LinearLayout origControls = (LinearLayout) findViewById(R.id.origionalControls);
        LinearLayout camSettings = (LinearLayout) findViewById(R.id.cameraSettings);
        if (origControls.getVisibility() != View.VISIBLE) {
            LinearLayout afterControls = (LinearLayout) findViewById(R.id.afterPhotoControls);
            afterControls.setVisibility(View.GONE);
            origControls.setVisibility(View.VISIBLE);
            camSettings.setVisibility(View.VISIBLE);
        }

        Button capture = (Button) findViewById(R.id.capture);
        Button cancel = (Button) findViewById(R.id.cancel);

        Typeface sansPro = Typeface.createFromAsset(getAssets(),"sourceSans_reg.ttf");

        capture.setTypeface(sansPro);
        cancel.setTypeface(sansPro);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainCamera.takePicture(null,null,mainCallback);
            }
        });
    }

    public Camera getCamera() {
        Camera cam = null;
        try {
            cam = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cam;
    }

    public File getMediaFile() {
        File mediaFile;

        //create directory instance
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"customCam");

        //make sure directory exists
        if (!dir.exists()) {
            if(!dir.mkdirs()) {
                Log.i("Camera","Directory not created");
            }
        }

        //create file inside direcotry where image will be saved
        Random rand = new Random();
        int randInt = rand.nextInt(10000);
        mediaFile = new File(dir.getPath() + File.separator + "IMG" + String.valueOf(randInt) + ".jpg");

        return mediaFile;
    }

/*
    //Called when user uses back button when in this camera activity
    @Override
    public void finish() {
        Log.i("finished","Finished");
        super.finish();
        mainCamera.release();
    }
*/
    @Override
    public void onPause() {
        super.onPause();
        Log.i("paused", "paused");
        mainCamera.release();
        mainCamera = null;
        mainPreview = null;
    }


    //Called when user comes from home screen back to application activity
    @Override
    public void onRestart() {
        super.onRestart();
        Log.i("restarted", "restarted");
        init();
    }


}
