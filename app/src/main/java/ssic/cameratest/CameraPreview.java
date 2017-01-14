package ssic.cameratest;

import android.app.Activity;
//import android.graphics.Camera;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/**
 * Created by wanglixin on 2017/1/12.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "TAG";

    /** * 控制相机方向 */
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private SurfaceHolder mHolder;
    private Camera mCamera;

    //持有Activity引用，为了获取屏幕方向，改成内部类会比较好
    private Activity mActivity;

    public CameraPreview(Activity activity) {
        super(activity);
        mActivity = activity;
        mHolder = getHolder();
        mHolder.addCallback(this);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            //API 11及以后废弃，需要时自动配置
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
    }

    /** * 刷新相机 */
    private void refreshCamera() {
        if (mCamera != null) {
            requestLayout();
            //获取当前手机屏幕方向
            int rotation = mActivity.getWindowManager()
                    .getDefaultDisplay().getRotation();
            //调整相机方向
            mCamera.setDisplayOrientation(
                    ORIENTATIONS.get(rotation));
            // 设置相机参数
            mCamera.setParameters(settingParameters());
        }
    }

//    public void takePicture() {
//        //触发一个异步的图片捕获回调
//        mCamera.takePicture(null, null, this);
//    }

    /** * 配置相机参数 * @return */
    private Camera.Parameters settingParameters() {
        // 获取相机参数
        Camera.Parameters params = mCamera.getParameters();
        List<String> focusModes = params.getSupportedFocusModes();
        //设置持续的对焦模式
        if (focusModes.contains(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        //设置闪光灯自动开启
        if (focusModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
            params.setFocusMode(Camera.Parameters.FLASH_MODE_AUTO);
        }
        return params;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            if(mCamera != null) {
                //surface创建，设置预览SurfaceHolder
                mCamera.setPreviewDisplay(holder);
                //开启预览
                mCamera.startPreview();
            }
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        refreshCamera();
        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
}
