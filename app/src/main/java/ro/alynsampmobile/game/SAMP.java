package ro.alynsampmobile.game;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Toast;

import com.joom.paranoid.Obfuscate;
import com.rockstargames.gtasa.GTASA;

import java.io.File;
import java.util.Objects;

import ro.alynsampmobile.game.ui.UI;
import ro.alynsampmobile.launcher.utils.SignatureChecker;

@Obfuscate
public class SAMP extends GTASA {
    private static final String TAG = "SAMP";

    public UI ui;

    private native void initializeSAMP(UI ui, String gameDir, boolean isOffline);

    private native boolean multiTouchEvent4Ex(int action, int count, int x0, int y0, int x1, int y1, int x2, int y2, int x3, int y3);

    private void loadLibraries() {
        Log.i(TAG, "**** Loading libraries");

        // check if arch is arm32 and load ImmEmulatorJ
        String abi = Build.CPU_ABI;
        boolean is_64 = abi.equals("arm64-v8a");
        Log.i(TAG, "ABI: " + abi);

        if (!is_64) {
            System.loadLibrary("ImmEmulatorJ");
        }

        System.loadLibrary("GTASA");
        System.loadLibrary("Alyn_SAMPMOBILE");

/*
        // new version
        if (getSharedPreferences("samp_settings", Context.MODE_PRIVATE).getInt("game_version", 0) == 0) {
            System.loadLibrary("GTASA");
            System.loadLibrary("Alyn_SAMPMOBILE");
        }
        // old version
        else if (getSharedPreferences("samp_settings", Context.MODE_PRIVATE).getInt("game_version", 0) == 1 && !is_64) {
            System.loadLibrary("GTASA");
            System.loadLibrary("Alyn_SAMPMOBILE");

            if (getSharedPreferences("samp_settings", Context.MODE_PRIVATE).getBoolean("cleo_scripts", false)) {
                try {
                    System.loadLibrary("CLEO");
                } catch (ExceptionInInitializerError | UnsatisfiedLinkError e) {
                    Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                }
            }

            if (getSharedPreferences("samp_settings", Context.MODE_PRIVATE).getBoolean("aml_scripts", false)) {
                try {
                    System.loadLibrary("AML");
                } catch (ExceptionInInitializerError | UnsatisfiedLinkError e) {
                    Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                }
            }

            if (getSharedPreferences("samp_settings", Context.MODE_PRIVATE).getBoolean("monet_scripts", false)) {
                try {
                    System.loadLibrary("monetloader");
                } catch (ExceptionInInitializerError | UnsatisfiedLinkError e) {
                    Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                }
            }
        }
        */
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "**** onCreate");

        if (!Objects.equals(getIntent().getStringExtra("extra_check"), "alynsampmobile1337")) {
            Log.e(TAG, "Not joined from launcher!");
            finish();
            return;
        }

        loadLibraries();

        super.onCreate(savedInstanceState);
        Toast.makeText(this, "SA-MP Mobile Started", Toast.LENGTH_LONG).show();

        // delete MINFO.BIN and CINFO.BIN if they exist
        File minfo = new File(getExternalFilesDir(null), "MINFO.BIN");
        File cinfo = new File(getExternalFilesDir(null), "CINFO.BIN");

        if (minfo.exists()) {
            Log.i(TAG, "Deleting MINFO.BIN");
            minfo.delete();
        }

        if (cinfo.exists()) {
            Log.i(TAG, "Deleting CINFO.BIN");
            cinfo.delete();
        }

        ui = new UI(this);
        ui.initializeUI();

        // check signature
        if (!SignatureChecker.isSignatureValid(this, getPackageName())) {
            Log.e("SplashActivity", "No remake idiot!");
            finish();
            return;
        } else {
            Log.i("SplashActivity", "Using original client!");
        }

        String gameDir = getExternalFilesDir(null).toString().concat("/");
        Log.i(TAG, "Game directory: " + gameDir);

        try {
            initializeSAMP(ui, gameDir, getSharedPreferences("samp_settings", Context.MODE_PRIVATE).getBoolean("offline_mode", false));
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }

        // fullscreen
        if (getSharedPreferences("samp_settings", Context.MODE_PRIVATE).getBoolean("fullscreen", false)) {
            if (Build.VERSION.SDK_INT >= 30) {
                getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
            } else if (Build.VERSION.SDK_INT >= 28) {
                getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (ui != null) {
            ui.onBackPressed();
        }
        super.onBackPressed();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (ui != null) {
            ui.onTouchEvent(event);
        }

        int x1 = 0, y1 = 0, x2 = 0, y2 = 0, x3 = 0, y3 = 0, x4 = 0, y4 = 0;
        int numEvents = event.getPointerCount();

        for (int i = 0; i < numEvents; i++) {
            int pointerId = event.getPointerId(i);

            if (pointerId == 0) {
                x1 = (int) event.getX(i);
                y1 = (int) event.getY(i);
            } else if (pointerId == 1) {
                x2 = (int) event.getX(i);
                y2 = (int) event.getY(i);
            } else if (pointerId == 2) {
                x3 = (int) event.getX(i);
                y3 = (int) event.getY(i);
            } else if (pointerId == 3) {
                x4 = (int) event.getX(i);
                y4 = (int) event.getY(i);
            }
        }

        int pointerId = event.getPointerId(event.getActionIndex());
        int action = event.getActionMasked();

        try {
            multiTouchEvent4Ex(action, pointerId, x1, y1, x2, y2, x3, y3, x4, y4);
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
        return super.onTouchEvent(event);
    }

/*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getSharedPreferences("samp_settings", Context.MODE_PRIVATE).getInt("game_version", 0) == 0) {
            if (ui != null) {
                ui.onTouchEvent(event);
            }

            int x1 = 0, y1 = 0, x2 = 0, y2 = 0, x3 = 0, y3 = 0, x4 = 0, y4 = 0;
            int numEvents = event.getPointerCount();

            for (int i = 0; i < numEvents; i++) {
                int pointerId = event.getPointerId(i);

                if (pointerId == 0) {
                    x1 = (int) event.getX(i);
                    y1 = (int) event.getY(i);
                } else if (pointerId == 1) {
                    x2 = (int) event.getX(i);
                    y2 = (int) event.getY(i);
                } else if (pointerId == 2) {
                    x3 = (int) event.getX(i);
                    y3 = (int) event.getY(i);
                } else if (pointerId == 3) {
                    x4 = (int) event.getX(i);
                    y4 = (int) event.getY(i);
                }
            }

            int pointerId = event.getPointerId(event.getActionIndex());
            int action = event.getActionMasked();

            try {
                multiTouchEvent4Ex(action, pointerId, x1, y1, x2, y2, x3, y3, x4, y4);
            } catch (UnsatisfiedLinkError e) {
                e.printStackTrace();
            }
            return super.onTouchEvent(event);
        }
        // arz version
        else {
            int pointerCount = event.getPointerCount();
            int i = 0;
            int i2 = 0;
            int i3 = 0;
            int i4 = 0;
            int i5 = 0;
            int i6 = 0;
            int i7 = 0;
            int i8 = 0;
            for (int i9 = 0; i9 < pointerCount; i9++) {
                int pointerId = event.getPointerId(i9);
                if (pointerId == 0) {
                    i = (int) event.getX(i9);
                    i2 = (int) event.getY(i9);
                } else if (pointerId == 1) {
                    i3 = (int) event.getX(i9);
                    i4 = (int) event.getY(i9);
                } else if (pointerId == 2) {
                    i5 = (int) event.getX(i9);
                    i6 = (int) event.getY(i9);
                } else if (pointerId == 3) {
                    i7 = (int) event.getX(i9);
                    i8 = (int) event.getY(i9);
                }
            }
            multiTouchEvent4(event.getActionMasked(), event.getPointerId(event.getActionIndex()), i, i2, i3, i4, i5, i6, i7, i8, event);
            return true;
        }
    }
*/

    @Override
    public void onStart() {
        Log.i(TAG, "**** onStart");
        super.onStart();
    }

    @Override
    public void onRestart() {
        Log.i(TAG, "**** onRestart");
        super.onRestart();
    }

    @Override
    public void onResume() {
        Log.i(TAG, "**** onResume");
        super.onResume();

        if (ui != null) {
            ui.onResumeEvent();
        }
    }

    @Override
    public void onPause() {
        Log.i(TAG, "**** onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "**** onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "**** onDestroy");
        super.onDestroy();
    }
}
