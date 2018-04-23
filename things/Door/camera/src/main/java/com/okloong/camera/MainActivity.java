package com.okloong.camera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <p>
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class MainActivity extends Activity {

    private CameraHelper mCamera;

    /**
     * A {@link Handler} for running Camera tasks in the background.
     */
    private Handler mCameraHandler;

    /**
     * An additional thread for running Camera tasks that shouldn't block the UI.
     */
    private HandlerThread mCameraThread;

    Bitmap thumbnail;
    ImageView image;

        Handler mHandler = new Handler(Looper.getMainLooper());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Creates new handlers and associated threads for camera and networking operations.
        mCameraThread = new HandlerThread("CameraBackground");
        mCameraThread.start();

        // Camera code is complicated, so we've shoved it all in this closet class for you.
        mCamera = CameraHelper.getInstance();
        mCamera.initializeCamera(this, mCameraHandler, mOnImageAvailableListener);


        image = (ImageView) findViewById(R.id.photoResultView);
        // Doorbell rang!
        Log.d("MainActivity", "button pressed");
        mCamera.takePicture();

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            // Doorbell rang!
            Log.d("Nain", "button pressed");
            mCamera.takePicture();
            return true;
        }
//        mCamera.takePicture();
//        return true;
        return super.onKeyUp(keyCode, event);
    }


    @Override
    protected void onPause() {
        super.onPause();
        // Doorbell rang!
        Log.d("MainActivity", "button pressed");
        mCamera.takePicture();
    }

    /**
     * Listener for new camera images.
     */
    private ImageReader.OnImageAvailableListener mOnImageAvailableListener =
            new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = reader.acquireLatestImage();
                    // get image bytes
                    ByteBuffer imageBuf = image.getPlanes()[0].getBuffer();
                    final byte[] imageBytes = new byte[imageBuf.remaining()];
                    imageBuf.get(imageBytes);
                    image.close();

                    onPictureTaken(imageBytes);
                }
            };


    /**
     * Handle image processing in Firebase and Cloud Vision.
     */
    private void onPictureTaken(final byte[] imageBytes) {
        Log.d("MainActivity", "onPictureTaken");
        if (imageBytes != null) {

            String imageStr = Base64.encodeToString(imageBytes, Base64.NO_WRAP | Base64.URL_SAFE);

            thumbnail =  BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    image.setImageBitmap(thumbnail);
                }
            });



            Log.d("MainActivity", "sending image to cloud vision");


        }
    }
}
