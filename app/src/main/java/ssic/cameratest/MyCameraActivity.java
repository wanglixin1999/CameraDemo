package ssic.cameratest;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by wanglixin on 2017/1/12.
 */
public class MyCameraActivity extends Activity implements android.hardware.Camera.PictureCallback {

    private CameraPreview mPreview;
    private Camera mCamera;
    private Button takePictureButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        initUI();

        openCamera();
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        releaseCameraAndPreview();
//    }

    public void initUI() {
        mPreview = new CameraPreview(this);
        FrameLayout vPreview = (FrameLayout) findViewById(R.id.camera_preview);
        vPreview.addView(mPreview);

        takePictureButton = (Button) findViewById(R.id.camera_take_photo);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
    }

    public void openCamera() {
        int cameraId = CameraUtil.findBackFacingCamera();
        safeCameraOpen(cameraId);
        mPreview.setCamera(mCamera);
    }

    /** * 重置相机 */
    private void resetCamera() {
        mCamera.startPreview();
        mPreview.setCamera(mCamera);
    }

    /** * 释放相机和预览 */
    private void releaseCameraAndPreview() {
        mPreview.setCamera(null);
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    /** * 获取Camera，并加入开启检测 * * @param id 相机id * @return */
    private boolean safeCameraOpen(int id) {
        boolean qOpened = false;
        try {
            releaseCameraAndPreview();
            mCamera = Camera.open(id);
            qOpened = (mCamera != null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return qOpened;
    }

    public void takePicture() {
        //触发一个异步的图片捕获回调
        mCamera.takePicture(null, null, this);
    }

    @Override
    public void onPictureTaken(final byte[] data, Camera camera) {
        Log.d(getClass().getName(),"onPictureTaken");
        File pictureFile = new File("test.jpg"); //自行创建一个file文件用于保存照片
        OutputStream output = null;
        try {
            output = new FileOutputStream(pictureFile);
            output.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //保存成功后的处理
        resetCamera();
    }
}
