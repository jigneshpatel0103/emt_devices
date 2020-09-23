package com.app.emtdevice.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;

import com.app.emtdevice.CommunicationCommand;
import com.app.emtdevice.Constants;
import com.app.emtdevice.R;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

import static com.app.emtdevice.activity.MainActivity.androidAppState;
import static com.app.emtdevice.activity.MainActivity.communicationCommand;

public class SettingActivity extends AppCompatActivity {
    private ConstraintLayout mConstraintLayoutSettingUin;
    private ScrollView mScrollviewSetting;
    private LinearLayout mLinearLayoutLogin;
    private EditText mEdittextUsername, mEdittextPassword, mEdittextPeakGear;
    private Button mButtonLogin, mButtonSave;
    private Switch switchTImeout, switchEquipmentFirstTIme, resetMeter, narrowSwap, invertBMP;
    private Spinner spinnerEquipmentFirstTime;
    private Spinner spinnerEquipmentPerLine;
    private String[] arrayNoOfEquipment = {"5 X 4", "5 x 5", "5 X 6", "5 X 7", "5 X 8", "5 X 16", "5 X 32", "10 X 4", "10 x 5", "10 x 6", "10 X 7", "10 X 8", "10 x 16"};
    //private String[] dataSequence = {"D0:D1:..:D6:D7", "D7:D6:..:D1:D0", "D1:D0:..:D7:D6", "D6:D7:..:D0:D1"};
    private String[] dataSequence = {"Left Front Sequence", "Right Front Sequence", "Left Shuffle Sequence", "Right Shuffle Sequence"};
    public static CommunicationCommand communicationCommand;
    ProgressDialog dialog;
    ArrayAdapter equipmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        android.support.v7.app.ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initializeComponents();
        equipmentAdapter = new ArrayAdapter(this,
                android.R.layout.simple_expandable_list_item_1, arrayNoOfEquipment);
        spinnerEquipmentPerLine.setAdapter(equipmentAdapter);

	    ArrayAdapter sequenceAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, dataSequence);
        spinnerEquipmentFirstTime.setAdapter(sequenceAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SettingActivity.this, MainActivity.class));
        finish();
    }

    private void initializeComponents() {
        spinnerEquipmentPerLine = findViewById(R.id.spinnerEquipmentPerLine);
	switchEquipmentFirstTIme = findViewById(R.id.switchEquipmentFirstTIme);
        spinnerEquipmentFirstTime = findViewById(R.id.spinnerEquipmentFirstTime);
        mButtonSave = findViewById(R.id.buttonSave);
        switchTImeout = findViewById(R.id.switchTImeout);
        resetMeter = findViewById(R.id.resetMeter);
        narrowSwap = findViewById(R.id.narrowOddEven);
        invertBMP = findViewById(R.id.invertBMP);
        mLinearLayoutLogin = findViewById(R.id.linearLayoutLogin);
        mConstraintLayoutSettingUin = findViewById(R.id.constraintLayoutSettingUi);
        mEdittextUsername = findViewById(R.id.edittextUsername);
        mEdittextPassword = findViewById(R.id.edittextPassword);
        mEdittextPeakGear = findViewById(R.id.PeakGearValue);
        mButtonLogin = findViewById(R.id.buttonLogin);
        mScrollviewSetting = findViewById(R.id.scrollviewSetting);

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((mEdittextUsername.getText().toString().trim().equals("User@emt") && mEdittextPassword.getText().toString().trim().equals("emt")) ||
		     (mEdittextUsername.getText().toString().trim().equals("electromechtechno") && mEdittextPassword.getText().toString().trim().equals("ra9kumari8abhai7240"))) {
                    if (isSocketAlive()) {
                        communicationCommand = new CommunicationCommand();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setData();
				if(mEdittextUsername.getText().toString().trim().equals("User@emt")) {
					spinnerEquipmentPerLine.setEnabled(false);
					resetMeter.setEnabled(false);
					mEdittextPeakGear.setEnabled(false);
				}
                    		mLinearLayoutLogin.setVisibility(View.GONE);
                    		mConstraintLayoutSettingUin.setVisibility(View.VISIBLE);
                            }
                        }, 500);
                    } else {
                        Snackbar.make(mScrollviewSetting, "Not connected to device.", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(mScrollviewSetting, "Invalid username or password.", Snackbar.LENGTH_LONG).show();
                }
            }
        });


        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSocketAlive()) {
                    communicationCommand = new CommunicationCommand();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog = new ProgressDialog(SettingActivity.this);
                            dialog.setMessage("Saving Data, please wait...");
                            dialog.show();
                            int setTime, setEquStatus, setEquPerLine = 0, gear_value, meterReset, imageSetting = 0;
                            if (switchTImeout.isChecked()) {
                                setTime = Constants.ENABLED_EQUIPMENT_TIME_OUT;
                            } else {
                                setTime = Constants.DISABLE_EQUIPMENT_TIME_OUT;
                            }
			    if(resetMeter.isChecked())
			    	meterReset = 1;
			    else
			    	meterReset = 0;

				if(narrowSwap.isChecked()) {
					imageSetting = 0;
				} else {
					imageSetting = 1;
				}
				if(invertBMP.isChecked()) {
					imageSetting += 2;
				}

				setEquStatus = spinnerEquipmentFirstTime.getSelectedItemPosition() + 1;
			    if (switchEquipmentFirstTIme.isChecked())
				setEquStatus += 4;
			    setEquPerLine = spinnerEquipmentPerLine.getSelectedItemPosition() + 1;

			    gear_value = Integer.parseInt( mEdittextPeakGear.getText().toString() );

			    if(mEdittextUsername.getText().toString().trim().equals("electromechtechno"))
				communicationCommand.settingCommand(setTime, setEquStatus, setEquPerLine, gear_value, meterReset, imageSetting);
			    else
				communicationCommand.settingCustomerCommand(setTime, setEquStatus, imageSetting, meterReset);

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (dialog.isShowing()) {
                                        dialog.dismiss();
                                        startActivity(new Intent(SettingActivity.this, MainActivity.class));
                                        finish();
                                        /*Intent mStartActivity = new Intent(SettingActivity.this, MainActivity.class);
                                        int mPendingIntentId = 123456;
                                        PendingIntent mPendingIntent = PendingIntent.getActivity(SettingActivity.this, mPendingIntentId, mStartActivity,
                                                PendingIntent.FLAG_CANCEL_CURRENT);
                                        AlarmManager mgr = (AlarmManager) SettingActivity.this.getSystemService(Context.ALARM_SERVICE);
                                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, mPendingIntent);
                                        System.exit(0);*/
                                    }
                                }
                            }, 5000);
                        }
                    }, 500);
                } else {
                    Snackbar.make(mScrollviewSetting, "Not connected to device.", Snackbar.LENGTH_LONG).show();
                }
            }

        });
    }

    private void setData() {
        int isTimeout = communicationCommand.getTimeOut();
        if (isTimeout == Constants.ENABLED_EQUIPMENT_TIME_OUT) {
            switchTImeout.setChecked(true);
        } else if (isTimeout == Constants.DISABLE_EQUIPMENT_TIME_OUT) {
            switchTImeout.setChecked(false);
        }

	resetMeter.setChecked(false);
        int isEquipment = communicationCommand.getEqupmentFirstStatus();
	if(isEquipment > 4) {
		switchEquipmentFirstTIme.setChecked(true);
		isEquipment -= 4;
	} else
		switchEquipmentFirstTIme.setChecked(false);
        spinnerEquipmentFirstTime.setSelection(isEquipment - 1);
        int noOfEquipment = communicationCommand.getNo_Of_Equepment_Per_Line();
	spinnerEquipmentPerLine.setSelection(noOfEquipment - 1);
	int gear_value = communicationCommand.getPeakGearValue();
	mEdittextPeakGear.setText(String.valueOf(gear_value));
	int imageSetting = communicationCommand.getImageSetting();
	if (imageSetting > 1) {
	    invertBMP.setChecked(true);
	    imageSetting -= 2;
    } else {
	    invertBMP.setChecked(false);
    }
	if(imageSetting > 0) {
	    narrowSwap.setChecked(false);
    } else {
	    narrowSwap.setChecked(true);
    }

        communicationCommand.closeSocket();
    }

    /**
     * Crunchify's isAlive Utility
     *
     * @param hostName
     * @param port
     * @return boolean - true/false
     */
    public boolean isSocketAlive() {
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

        return isAlive;
    }

}
