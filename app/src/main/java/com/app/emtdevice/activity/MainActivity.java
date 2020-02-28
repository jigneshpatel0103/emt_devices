package com.app.emtdevice.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.app.emtdevice.ApplicationClass;
import com.app.emtdevice.CommunicationCommand;
import com.app.emtdevice.Constants;
import com.app.emtdevice.R;
import com.app.emtdevice.util.Staticdata;
import com.app.emtdevice.util.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;

import ir.mahdi.mzip.zip.ZipArchive;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mImageViewDesign, imageViewRefreshWifi, imageViewRefresDevice;
    Bitmap imageBm;
    private Intent intent;
    public static ProgressBar mProgressbarSocketLoader, progressBarImage;
    private Button btnLoadBMP, btnProcess, btnMinus, btnPlus, btnRepeat, buttonSetLineNumber;
    private static final int PERMISSIONS_REQUEST_STORAGE = 999;
    private int piAppState;
    public static CommunicationCommand communicationCommand;
    public static ConstraintLayout constraintLayoutUi, constraintLayoutWifiNotConnected, constraintLayoutDeviceNotConnected;
    private EditText editTextLineNumber;
    private TextView mTextViewLineNumber;
    private ScrollView scrollviewMain;
    private Handler handlerCommon;
    public static int androidAppState;
    private int maxLine = 1;
    private boolean isPausedCurrentLine, isImageEnable = true, isConnected = false;
    public static boolean isPaused;
    private RoundCornerProgressBar progressBar;
    public static ProgressDialog progressDialog, imageRetriveDialog;
    static final File dir = new File(Constants.IMAGE_FILEPATH);
    static final String[] EXTENSIONS = new String[]{"bmp"};// and other formats you need
    private String filename;
    private Runnable mRunnableGetLineNumber = null;
    final Handler mHandlerGetLineNumber = new Handler();
    private Runnable mRunnableReconnect = null;
    final Handler mHandlerReconnect = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        initComponents();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_STORAGE);
            } else {
                setupUi();
            }
        } else {
            setupUi();
        }


    }

    private void setupUi() {
        Staticdata.isProgressShow(MainActivity.this);
        //progressDismiss("Unable to setup ui");
        Handler sockethandler = new Handler();
        sockethandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifi.isWifiEnabled()) {
                    if (isSocketAlive()) {
                        initsocket();
                    } else {
                        constraintLayoutDeviceNotConnected.setVisibility(View.VISIBLE);
                        constraintLayoutUi.setVisibility(View.GONE);
                        constraintLayoutWifiNotConnected.setVisibility(View.GONE);
                        Staticdata.isProgressCancle();
                    }
                } else {
                    constraintLayoutUi.setVisibility(View.GONE);
                    constraintLayoutWifiNotConnected.setVisibility(View.VISIBLE);
                    constraintLayoutDeviceNotConnected.setVisibility(View.GONE);
                    Staticdata.isProgressCancle();
                }
            }
        }, 1000);
    }

    private void initComponents() {
        progressBarImage = findViewById(R.id.progressBarImage);
        mProgressbarSocketLoader = findViewById(R.id.progressbarSocketLoader);
        constraintLayoutDeviceNotConnected = findViewById(R.id.constraintLayoutDeviceNotConnected);
        constraintLayoutWifiNotConnected = findViewById(R.id.constraintLayoutWifiNotConnected);
        imageViewRefreshWifi = findViewById(R.id.imageViewRefreshWifi);
        imageViewRefresDevice = findViewById(R.id.imageViewRefresDevice);
        constraintLayoutUi = findViewById(R.id.constraintLayoutUi);
        mImageViewDesign = findViewById(R.id.imageViewDesign);
        btnLoadBMP = findViewById(R.id.btnLoadBMP);
        btnProcess = findViewById(R.id.btnProcess);
        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);
        btnRepeat = findViewById(R.id.btnRepeat);
        buttonSetLineNumber = findViewById(R.id.buttonSetLineNumber);
        editTextLineNumber = findViewById(R.id.editTextLineNumber);
        mTextViewLineNumber = findViewById(R.id.textViewLineNumber);
        progressBar = findViewById(R.id.progressBar);
        scrollviewMain = findViewById(R.id.scrollviewMain);

        mImageViewDesign.setOnClickListener(this);
        btnLoadBMP.setOnClickListener(this);
        btnProcess.setOnClickListener(this);
        btnMinus.setOnClickListener(this);
        btnPlus.setOnClickListener(this);
        btnRepeat.setOnClickListener(this);
        buttonSetLineNumber.setOnClickListener(this);
        imageViewRefreshWifi.setOnClickListener(this);
        imageViewRefresDevice.setOnClickListener(this);

        imageButton(false);
        operationalButton(false);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewDesign:
                if (isImageEnable) {
                    communicationCommand.closeSocket();
                    selectImage();
                } else {
                    alertImageSelect();
                }
                break;

            case R.id.btnLoadBMP:
                Staticdata.isProgressShow(MainActivity.this);
                progressDismiss("Failed to load BMP Image.");
                communicationCommand = new CommunicationCommand();
                handlerCommon = new Handler();
                handlerCommon.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        byte ret = communicationCommand.loadBMPImage();
                        if (ret == Constants.SUCCESS) {
                            Log.d("PI", "Image Loaded");
                            androidAppState = Constants.ANDROID_APP_LOAD_IMAGE_STATE;
                        } else {
                            androidAppState = Constants.ANDROID_APP_DEFAULT_STATE;
                            isImageEnable = true;
                            imageButton(false);
                            operationalButton(false);
                            mImageViewDesign.setImageDrawable(getResources().getDrawable(R.drawable.no_image));
                            Snackbar.make(scrollviewMain, "Image not supported.", Snackbar.LENGTH_LONG).show();
                        }
                        communicationCommand.closeSocket();
                        Staticdata.isProgressCancle();
                        btnLoadBMP.setEnabled(false);
                        btnProcess.setEnabled(true);
                    }
                }, 1000);

                break;
            case R.id.btnProcess:
                Staticdata.isProgressShow(MainActivity.this);
                progressDismiss("Failed to process image");
                communicationCommand = new CommunicationCommand();
                handlerCommon = new Handler();
                handlerCommon.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        byte processBMPImage = communicationCommand.processBMPImage();
                        Log.e("image process", processBMPImage + "");
                        if (processBMPImage == Constants.SUCCESS) {
                            androidAppState = Constants.ANDROID_APP_PROCESS_IMAGE_STATE;
                            imageButton(false);
                            operationalButton(true);
                            handlerCommon = new Handler();
                            handlerCommon.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Staticdata.isProgressCancle();
                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_up, R.anim.no_animation);
                                }
                            }, 5000);

                        } else {
                            androidAppState = Constants.ANDROID_APP_DEFAULT_STATE;
                            isImageEnable = true;
                            btnLoadBMP.setEnabled(false);
                            mImageViewDesign.setImageDrawable(getResources().getDrawable(R.drawable.no_image));
                            Snackbar.make(scrollviewMain, "Image design is not valid or not supported.", Snackbar.LENGTH_LONG).show();
                        }
                    }
                }, 1000);
                break;
            case R.id.btnMinus:
                stopRepeatingGetLineNumber();
                Staticdata.isProgressShow(MainActivity.this);
                progressDismiss("Failed to set line number");
                handlerCommon = new Handler();
                handlerCommon.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int currentLIne = Integer.parseInt(mTextViewLineNumber.getText().toString());
                        if (currentLIne == 1) {
                            currentLIne = maxLine;
                        } else {
                            currentLIne--;
                            communicationCommand.setCurrentLine(currentLIne);

                        }
                        communicationCommand.closeSocket();
                        communicationCommand = new CommunicationCommand();

                        handlerCommon = new Handler();
                        handlerCommon.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startRepeatingGetLineNumber();
                                Staticdata.isProgressCancle();
                            }
                        }, 1000);
                    }
                }, 1000);
                break;
            case R.id.btnPlus:
                stopRepeatingGetLineNumber();
                Staticdata.isProgressShow(MainActivity.this);
                progressDismiss("Failed to set line number");

                handlerCommon = new Handler();
                handlerCommon.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        int currentLInePlus = Integer.parseInt(mTextViewLineNumber.getText().toString());
                        if (currentLInePlus == maxLine) {
                            currentLInePlus = 1;
                        } else {
                            currentLInePlus++;
                        }
                        communicationCommand.setCurrentLine(currentLInePlus);
                        communicationCommand.closeSocket();
                        communicationCommand = new CommunicationCommand();

                        handlerCommon = new Handler();
                        handlerCommon.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startRepeatingGetLineNumber();
                                Staticdata.isProgressCancle();
                            }
                        }, 1000);

                    }
                }, 1000);
                break;
            case R.id.btnRepeat:
                stopRepeatingGetLineNumber();
                Staticdata.isProgressShow(MainActivity.this);
                progressDismiss("Failed to repeat line number");
                handlerCommon = new Handler();
                handlerCommon.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        communicationCommand.repeat();
                        communicationCommand.closeSocket();
                        communicationCommand = new CommunicationCommand();

                        handlerCommon = new Handler();
                        handlerCommon.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startRepeatingGetLineNumber();
                                Staticdata.isProgressCancle();
                            }
                        }, 1000);
                    }
                }, 1000);
                break;
            case R.id.buttonSetLineNumber:
                Staticdata.isProgressShow(MainActivity.this);
                progressDismiss("Failed to set line number");
                communicationCommand = new CommunicationCommand();
                handlerCommon = new Handler();
                handlerCommon.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (!editTextLineNumber.getText().toString().equals("")) {
                            int inputLine = Integer.parseInt(editTextLineNumber.getText().toString().trim());
                            if (inputLine >= 1 && inputLine <= maxLine) {
                                communicationCommand.setCurrentLine(inputLine);
                                editTextLineNumber.setText("");
                            } else {
                                Snackbar.make(scrollviewMain, "Valid range : 1-" + maxLine, Snackbar.LENGTH_LONG).show();
                            }
                        }
                        communicationCommand.closeSocket();
                        communicationCommand = new CommunicationCommand();

                        handlerCommon = new Handler();
                        handlerCommon.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startRepeatingGetLineNumber();
                                Staticdata.isProgressCancle();
                            }
                        }, 1000);
                        Staticdata.isProgressCancle();
                    }
                }, 1000);
                break;
            case R.id.imageViewRefreshWifi:
                mProgressbarSocketLoader.setVisibility(View.VISIBLE);
                WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifi.isWifiEnabled()) {
                    if (isSocketAlive()) {
                        initsocket();
                    } else {
                        constraintLayoutDeviceNotConnected.setVisibility(View.VISIBLE);
                        constraintLayoutUi.setVisibility(View.GONE);
                        constraintLayoutWifiNotConnected.setVisibility(View.GONE);
                    }
                } else {
                    constraintLayoutUi.setVisibility(View.GONE);
                    constraintLayoutDeviceNotConnected.setVisibility(View.GONE);
                    constraintLayoutWifiNotConnected.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.imageViewRefresDevice:
                mProgressbarSocketLoader.setVisibility(View.VISIBLE);
                if (isSocketAlive()) {
                    initsocket();
                } else {
                    constraintLayoutDeviceNotConnected.setVisibility(View.VISIBLE);
                    constraintLayoutUi.setVisibility(View.GONE);
                    constraintLayoutWifiNotConnected.setVisibility(View.GONE);
                }
                break;
        }
    }

    private void initsocket() {
        constraintLayoutUi.setVisibility(View.VISIBLE);
        constraintLayoutWifiNotConnected.setVisibility(View.GONE);
        constraintLayoutDeviceNotConnected.setVisibility(View.GONE);

        communicationCommand = new CommunicationCommand();
        handlerCommon = new Handler();
        handlerCommon.postDelayed(new Runnable() {
            @Override
            public void run() {
                piAppState = communicationCommand.getApplicationState();
                Log.e("pistate", piAppState + "");
                if (piAppState == Constants.APP_PROCESS_IMAGE_STATE) {
                    getImageFromPi();
                }
                Staticdata.isProgressCancle();
            }
        }, 100);

    }


    private void getImageFromPi() {
        progressBarImage.setVisibility(View.VISIBLE);
        String checkSum = null;
        String checkSumLocal;
        try {
            checkSum = URLEncoder.encode(communicationCommand.getCheckSum(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        checkSumLocal = ApplicationClass.preference.getChecksum();
        Log.e("local", checkSumLocal);
        Log.e("checksum", checkSum);
        if (checkSumLocal.equals(checkSum)) {
            filename = getFiles();
            Log.e("filename", filename + "--");
            if (!filename.isEmpty()) {

                unZip();
                repeatGetLineNumber();
                startRepeatingGetLineNumber();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        File image = new File(Constants.IMAGE_FILEPATH, filename);
                        if (image.exists()) {
                            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
                            Bitmap converetdImage = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 0.8), (int) (bitmap.getHeight() * 0.8), true);


                            RequestOptions requestOptions = new RequestOptions();
                            requestOptions.placeholder(R.drawable.no_image);
                            requestOptions.error(R.drawable.preview_not_supported);

                            Glide.with(MainActivity.this)
                                    .setDefaultRequestOptions(requestOptions)
                                    .load(converetdImage)
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            progressBarImage.setVisibility(View.GONE);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            progressBarImage.setVisibility(View.GONE);
                                            return false;
                                        }
                                    })
                                    .into(mImageViewDesign);
                            operationalButton(true);
                        }

                    }
                }, 3000);
            } else {
                Handler handlerConnection = new Handler();
                handlerConnection.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        deleteFiles();
                        downloadImage();
                    }
                }, 1000);

            }

        } else {
            deleteFiles();
            downloadImage();
        }
        androidAppState = Constants.ANDROID_APP_PROCESS_IMAGE_STATE;

    }

    private int getCurrentLine() {
        int currentLine = communicationCommand.getCurrentLine();
        maxLine = communicationCommand.getMaxLineNumber();
        if (maxLine != 0) {
            progressBar.setMax(maxLine);
        }
        mTextViewLineNumber.setText(String.valueOf(currentLine));
        progressBar.setProgress(currentLine);
        isImageEnable = false;
        return maxLine;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPaused = false;

    }


    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;
    }

    /*
     * method for set visibility image button
     * */
    private void imageButton(boolean visible) {
        btnProcess.setEnabled(visible);
        btnLoadBMP.setEnabled(visible);
    }

    /*
     * method for set visibility operational button
     * */
    private void operationalButton(boolean visible) {
        btnMinus.setEnabled(visible);
        btnPlus.setEnabled(visible);
        btnRepeat.setEnabled(visible);
        buttonSetLineNumber.setEnabled(visible);
        editTextLineNumber.setEnabled(visible);
        progressBar.setProgress(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menuPiLog) {
            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifi.isWifiEnabled()) {
                if (isSocketAlive()) {
                    stopRepeatingGetLineNumber();
                    communicationCommand.getPiLog();
                    startActivity(new Intent(MainActivity.this, PiLogActivity.class));
                }
            }

        } else if (item.getItemId() == R.id.menuAndroidLog) {
            stopRepeatingGetLineNumber();
            Constants.piLog = androidLog();
            startActivity(new Intent(MainActivity.this, PiLogActivity.class));
        } else if (item.getItemId() == R.id.menuSeting) {
            stopRepeatingGetLineNumber();
            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifi.isWifiEnabled()) {
                if (isSocketAlive()) {
                    startActivity(new Intent(MainActivity.this, SettingActivity.class));
                    communicationCommand.closeSocket();
                    finish();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void repeatGetLineNumber() {
        mRunnableGetLineNumber = new Runnable() {
            @Override
            public void run() {
                getCurrentLine();
                // Log.e("Get", "Line number");
                mHandlerGetLineNumber.postDelayed(mRunnableGetLineNumber, Constants.INTERVALGETLINE);
            }
        };
    }

    void startRepeatingGetLineNumber() {
        mRunnableGetLineNumber.run();
    }

    void stopRepeatingGetLineNumber() {
        mHandlerGetLineNumber.removeCallbacks(mRunnableGetLineNumber);
    }

    private void repeatReconnect() {
        mRunnableReconnect = new Runnable() {
            @Override
            public void run() {
                if (isSocketAlive()) {
                    stopRepeatingReconnect();
                    communicationCommand = new CommunicationCommand();
                    handlerCommon = new Handler();
                    repeatGetLineNumber();
                    startRepeatingReconnect();

                } else {
                    constraintLayoutDeviceNotConnected.setVisibility(View.VISIBLE);
                    constraintLayoutUi.setVisibility(View.GONE);
                    constraintLayoutWifiNotConnected.setVisibility(View.GONE);
                }
                mHandlerReconnect.postDelayed(mRunnableReconnect, Constants.INTERVALRECONNECT);
            }
        };
    }

    void startRepeatingReconnect() {
        mRunnableReconnect.run();
    }

    void stopRepeatingReconnect() {
        mHandlerReconnect.removeCallbacks(mRunnableReconnect);
    }

    private void selectImage() {
        imageBm = null;
        intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/bmp");
        startActivityForResult(intent, 7);
        stopRepeatingGetLineNumber();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 7:
                if (resultCode == RESULT_OK) {
                    operationalButton(false);
                    Uri path = data.getData();
                    if (data != null) {
                        try {
                            imageBm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String realPath = Staticdata.getRealPathFromUri(MainActivity.this, path);
                        Log.d("REAL PATH", " " + realPath);
                        deleteFiles();
                        archive(realPath);
                        btnProcess.setEnabled(false);
                        communicationCommand = new CommunicationCommand();
                        stopRepeatingGetLineNumber();
                        try {
                            FileInputStream fis = new FileInputStream(new File(Constants.IMAGE_FILEPATH + Constants.IMAGE_ZIPNAME));
                            //FileInputStream fis = new FileInputStream(new File(realPath));
                            final int bmpImageSize = fis.available();
                            final byte[] bytesArray = new byte[bmpImageSize];
                            fis.read(bytesArray);

                            progressDialog = new ProgressDialog(this);
                            progressDialog.setMessage("Image uploading....");
                            progressDialog.setCancelable(false);
                            progressDialog.setProgress(0);
                            progressDialog.setMax(bmpImageSize);
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.show();

                            Bitmap converetdImage = Bitmap.createScaledBitmap(imageBm, (int) (imageBm.getWidth() * 0.8), (int) (imageBm.getHeight() * 0.8), true);
                            mImageViewDesign.setImageBitmap(converetdImage);

                          /*  Glide.with(MainActivity.this)
                                    .load(converetdImage)
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            progressBarImage.setVisibility(View.GONE);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            progressBarImage.setVisibility(View.GONE);
                                            return false;
                                        }
                                    })
                                    .into(mImageViewDesign);*/
                            byte response = communicationCommand.sendBmpImage(bytesArray, bmpImageSize);
                            Log.e("response", response + "");
                            if (response == Constants.SUCCESS) {
                                btnLoadBMP.setEnabled(true);
                                String checkSum = null;
                                try {
                                    checkSum = URLEncoder.encode(communicationCommand.getCheckSum(), "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                Log.e("checkSum store ", checkSum);

                                ApplicationClass.preference.storeChecksum(checkSum);
                                androidAppState = Constants.ANDROID_APP_GOT_IMAGE_STATE;
                            } else {
                                Log.e("image", "upload faild");
                                btnLoadBMP.setEnabled(false);
                                mImageViewDesign.setImageDrawable(getResources().getDrawable(R.drawable.no_image));
                                isImageEnable = true;
                                progressDialog.dismiss();
                            }
                            communicationCommand.closeSocket();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    protected int sizeOf(Bitmap data) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            return data.getRowBytes() * data.getHeight();
        } else {
            return data.getByteCount();
        }
    }

    private void downloadImage() {
        imageRetriveDialog = new ProgressDialog(this);
        imageRetriveDialog.setMessage("Getting current image from device....");
        imageRetriveDialog.setCancelable(false);
        imageRetriveDialog.setProgress(0);
        imageRetriveDialog.setMax(100);
        imageRetriveDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        imageRetriveDialog.show();

        new ImageRetriveAsync().execute();
    }

    private void deleteFiles() {
        File index = new File(Constants.IMAGE_FILEPATH);
        String[] entries = index.list();
        if (entries != null) {
            for (String s : entries) {
                File currentFile = new File(index.getPath(), s);
                currentFile.delete();
            }
        }
    }

    private void alertImageSelect() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
        } else {
            builder = new AlertDialog.Builder(MainActivity.this);
        }
        builder.setTitle("Image in Process")
                .setMessage("Image is already in processing do you want to select new image?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        selectImage();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private String androidLog() {
        String androidLogs = "";
        Process logcat;
        final StringBuilder log = new StringBuilder();
        try {
            logcat = Runtime.getRuntime().exec(new String[]{"logcat", "-d"});
            BufferedReader br = new BufferedReader(new InputStreamReader(logcat.getInputStream()), 4 * 1024);
            String line;
            String separator = System.getProperty("line.separator");
            while ((line = br.readLine()) != null) {
                log.append(line);
                log.append(separator);
            }

            androidLogs = String.valueOf(log);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return androidLogs;
    }

    /**
     * Crunchify's isAlive Utility
     *
     * @param hostName
     * @param port
     * @return boolean - true/false
     */
    public static boolean isSocketAlive() {
        mProgressbarSocketLoader.setVisibility(View.VISIBLE);
        boolean isAlive = false;
        // Creates a socket address from a hostname and a port number
        SocketAddress socketAddress = new InetSocketAddress(Constants.SERVER_IP, Constants.SERVER_PORT);
        Socket socket = new Socket();

        // Timeout required - it's in milliseconds
        int timeout = 1000;

        Log.e("hostName: " + Constants.SERVER_IP, ", port: " + Constants.SERVER_PORT);
        try {
            socket.connect(socketAddress, timeout);
            socket.close();
            isAlive = true;
        } catch (SocketTimeoutException exception) {
            isAlive = false;
            Log.e("SocketTimeoutException ", exception.getMessage());
        } catch (IOException exception) {
            isAlive = false;
            Log.e("Unable to connect to", exception.getMessage());

        }

        mProgressbarSocketLoader.setVisibility(View.GONE);
        return isAlive;
    }

    class ImageRetriveAsync extends AsyncTask<Void, Bitmap, Bitmap> {

        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected Bitmap doInBackground(Void... arg0) {
            androidAppState = Constants.ANDROID_CONTINUE;
            Bitmap bmp = communicationCommand.getBmpImage();
            return bmp;
        }

        protected void onProgressUpdate(Bitmap... a) {
            super.onProgressUpdate(a);
        }

        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageButton(false);
            maxLine = communicationCommand.getMaxLineNumber();
            String checksum = "";
            try {
                checksum = URLEncoder.encode(communicationCommand.getCheckSum(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ApplicationClass.preference.storeChecksum(checksum);

            repeatGetLineNumber();
            startRepeatingGetLineNumber();
            isImageEnable = false;
            unZip();
            filename = getFiles();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    File image = new File(Constants.IMAGE_FILEPATH, filename);
                    Bitmap converetdImage = null;
                    if (image.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
                        if (sizeOf(bitmap) > 100000) {
                            converetdImage = Utility.getResizedBitmap(bitmap, 500);
                        } else {
                            converetdImage = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 0.8), (int) (bitmap.getHeight() * 0.8), true);

                        }
                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.placeholder(R.drawable.no_image);
                        requestOptions.error(R.drawable.preview_not_supported);

                        Glide.with(MainActivity.this)
                                .setDefaultRequestOptions(requestOptions)
                                .load(converetdImage)
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        progressBarImage.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        progressBarImage.setVisibility(View.GONE);
                                        operationalButton(true);
                                        return false;
                                    }
                                }).into(mImageViewDesign);


                        operationalButton(true);
                        Staticdata.isProgressCancle();

                    }
                }
            }, 3000);
        }

    }


    private void archive(String path) {
        ZipArchive zipArchive = new ZipArchive();
        File folder = new File(Constants.IMAGE_FILEPATH);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
            zipArchive.zip(path, Constants.IMAGE_FILEPATH + Constants.IMAGE_ZIPNAME, "");
        }
    }

    private void unZip() {
        ZipArchive zipArchive = new ZipArchive();
        zipArchive.unzip(Constants.IMAGE_FILEPATH + Constants.IMAGE_ZIPNAME, Constants.IMAGE_FILEPATH, "");
    }

    static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

        @Override
        public boolean accept(final File dir, final String name) {
            for (final String ext : EXTENSIONS) {
                if (name.endsWith("." + ext)) {
                    return (true);
                }
            }
            return (false);
        }
    };

    private String getFiles() {
        String fileName = "";
        if (dir.isDirectory()) { // make sure it's a directory
            for (final File f : dir.listFiles(IMAGE_FILTER)) {
                fileName = f.getName();
                System.out.println("image: " + f.getName());
                System.out.println(" size  : " + f.length());
            }
        }
        return fileName;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_STORAGE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.e("----->", "Permission Granted");
            setupUi();
        } else {
            // Permission Denied
            Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
            finish();

        }
    }

    public void progressDismiss(final String msg) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Staticdata.isProgressCancle();
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }, 25000);
    }
}
