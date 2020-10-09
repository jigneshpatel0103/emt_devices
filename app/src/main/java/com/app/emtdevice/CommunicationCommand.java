package com.app.emtdevice;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.app.emtdevice.activity.MainActivity;

import static com.app.emtdevice.activity.MainActivity.imageRetriveDialog;

public class CommunicationCommand {
    static CommunicationTask communicationTask;

    public CommunicationCommand() {
        communicationTask = new CommunicationTask();
        communicationTask.execute();
    }

    public byte loadBMPImage() {
        communicationTask.sendCommandToPi(Constants.LOAD_BMP_IMAGE);
        return communicationTask.readCommandFromPi();
    }

    public byte processBMPImage() {
        communicationTask.sendCommandToPi(Constants.PROCESS_BMP_IMAGE);
        return communicationTask.readCommandFromPi();
    }

    public int getApplicationState() {
        communicationTask.sendCommandToPi(Constants.GET_APP_STATE);
        byte cmd = communicationTask.readCommandFromPi();
        if (cmd == Constants.SUCCESS) {
            communicationTask.sendCommandToPi(Constants.NEXT_ACTION);
        } else {
        }
        int piAppState = communicationTask.readIntDataFromPi();
        communicationTask.sendCommandToPi((byte) Constants.SUCCESS);

        return piAppState;
    }

    public int getCurrentLine() {
        communicationTask.sendCommandToPi(Constants.GET_CURRENT_LINE);
        byte cmd = communicationTask.readCommandFromPi();
        if (cmd == Constants.SUCCESS) {
            communicationTask.sendCommandToPi(Constants.NEXT_ACTION);
        }
        int currentLine = communicationTask.readIntDataFromPi();
        communicationTask.sendCommandToPi(Constants.SUCCESS);

        return currentLine;
    }

    public int getPeakGearValue() {
        communicationTask.sendCommandToPi(Constants.GET_PEAK_GEAR_VALUE);
        byte cmd = communicationTask.readCommandFromPi();
        if (cmd == Constants.SUCCESS) {
            communicationTask.sendCommandToPi(Constants.NEXT_ACTION);
        }
        int peakGearValue = communicationTask.readIntDataFromPi();
        communicationTask.sendCommandToPi(Constants.SUCCESS);

        return peakGearValue;
    }

    public Bitmap getBmpImage() {
        Bitmap bmpData = null;

        communicationTask.sendCommandToPi(Constants.GET_BMP_IMAGE);
        byte cmd = communicationTask.readCommandFromPi();
        if (cmd == Constants.SUCCESS) {
            communicationTask.sendCommandToPi(Constants.NEXT_ACTION);
        }
        int bmpImageSize = communicationTask.readIntDataFromPi();
        if (bmpImageSize > 0) {
            Log.e("BMP", bmpImageSize + "");
            communicationTask.sendCommandToPi(Constants.SUCCESS);
            bmpData = communicationTask.receiveImageFromPi(bmpImageSize);
            communicationTask.sendCommandToPi(Constants.SUCCESS);
            imageRetriveDialog.dismiss();
        } else {
            Log.e("BMP", "Invalid image size");
            imageRetriveDialog.dismiss();
        }
        return bmpData;
    }

    public byte sendBmpImage(byte[] data, int bmpImageSize) {
        byte response = 00;
        communicationTask.sendCommandToPi(Constants.SET_BMP_IMAGE);
        byte cmd = communicationTask.readCommandFromPi();
        if (cmd == Constants.SUCCESS) {
            communicationTask.sendIntDataToPi(bmpImageSize);
            cmd = communicationTask.readCommandFromPi();
            if (cmd == Constants.SUCCESS) {
                communicationTask.sendImageToPi(data, bmpImageSize);
                cmd = communicationTask.readCommandFromPi();
                Log.d("IMAGE", cmd + "");
                if (cmd == Constants.SUCCESS) {
                    Log.d("IMAGE", "SENT");
                    communicationTask.sendCommandToPi(Constants.NEXT_ACTION);
                    cmd = communicationTask.readCommandFromPi();

                    if (cmd == Constants.SUCCESS) {

                        response = cmd;
                    }
                }
            }
        }

        return response;
    }

    public String getPiLog() {
        String file = "";
        communicationTask.sendCommandToPi(Constants.GET_PI_LOG);
        byte cmd = communicationTask.readCommandFromPi();
        Log.d("Read from pi:-", cmd + "");
        if (cmd == Constants.SUCCESS) {
            communicationTask.sendCommandToPi(Constants.NEXT_ACTION);

            int fileSize = communicationTask.readIntDataFromPi();
            if (fileSize > 0) {
                Log.d("Log file size", fileSize + "");
                communicationTask.sendCommandToPi(Constants.SUCCESS);
                file = communicationTask.receiveFileFromPi(fileSize);
                communicationTask.sendCommandToPi(Constants.SUCCESS);
            } else {
                Log.d("Log file ", "Invalid file size");
            }
            MainActivity.isPaused = false;
        }
        return file;
    }


    public boolean setCurrentLine(int line) {
        boolean isSuccess = false;
        communicationTask.sendCommandToPi(Constants.SET_CURRENT_LINE);
        byte cmd = communicationTask.readCommandFromPi();
        if (cmd == Constants.SUCCESS) {
            communicationTask.sendIntDataToPi(line);
            byte cmdRead = communicationTask.readCommandFromPi();
            if (cmdRead == Constants.SUCCESS) {
                isSuccess = true;
            }
        }
        return isSuccess;
    }

    public boolean setPeakGearValue(int gearValue) {
        boolean isSuccess = false;
        communicationTask.sendCommandToPi(Constants.SET_PEAK_GEAR_VALUE);
        byte cmd = communicationTask.readCommandFromPi();
        if (cmd == Constants.SUCCESS) {
            communicationTask.sendIntDataToPi(gearValue);
            byte cmdRead = communicationTask.readCommandFromPi();
            if (cmdRead == Constants.SUCCESS) {
                isSuccess = true;
            }
        }
        return isSuccess;
    }

    public boolean resetMeter() {
        boolean isSuccess = false;
        communicationTask.sendCommandToPi(Constants.METER_RESET);
        byte cmd = communicationTask.readCommandFromPi();
        if (cmd == Constants.SUCCESS) {
                isSuccess = true;
        }
        return isSuccess;
    }



    public int getMaxLineNumber() {
        int maxLine;
        communicationTask.sendCommandToPi(Constants.GET_MAX_LINE_NUMBER);
        byte cmd = communicationTask.readCommandFromPi();
        if (cmd == Constants.SUCCESS) {
            communicationTask.sendCommandToPi(Constants.NEXT_ACTION);
        } else {
            Log.d("GET MAX LINE", "FAIL" + cmd);
            return -1;
        }
        maxLine = communicationTask.readIntDataFromPi();
        communicationTask.sendCommandToPi(Constants.SUCCESS);

        return maxLine;
    }

    public boolean setTimeOut(int enabled) {
        boolean isSuccess = false;
        communicationTask.sendCommandToPi(Constants.SET_TIME_OUT);
        byte cmd = communicationTask.readCommandFromPi();
        if (cmd == Constants.SUCCESS) {
            communicationTask.sendIntDataToPi(enabled);
            byte cmdRead = communicationTask.readCommandFromPi();
            if (cmdRead == Constants.SUCCESS) {
                Log.e("SET TIME OUT", "READING INT DATA  ");

                isSuccess = true;
            }
        }
        return isSuccess;
    }

    public int getTimeOut() {
        communicationTask.sendCommandToPi(Constants.GET_TIME_OUT);
        byte cmd = communicationTask.readCommandFromPi();
        if (cmd == Constants.SUCCESS) {
            communicationTask.sendCommandToPi(Constants.NEXT_ACTION);
        } else {
        }
        int getTimeOut = communicationTask.readIntDataFromPi();
        communicationTask.sendCommandToPi(Constants.SUCCESS);

        Log.e("GET TIME OUT", "READING INT DATA : " + getTimeOut);
        return getTimeOut;
    }

    public byte repeat() {
        communicationTask.sendCommandToPi(Constants.REPEAT_DATA);
        return communicationTask.readCommandFromPi();
    }

    public boolean setEqupmentFirstStatus(int enabled) {
        boolean isSuccess = false;
        communicationTask.sendCommandToPi(Constants.SET_EQUPMENT_FIRST_STATUS);
        byte cmd = communicationTask.readCommandFromPi();
        if (cmd == Constants.SUCCESS) {
            communicationTask.sendIntDataToPi(enabled);
            byte cmdRead = communicationTask.readCommandFromPi();
            if (cmdRead == Constants.SUCCESS) {
                Log.e("SET TIME OUT", "READING INT DATA  ");

                isSuccess = true;
            }
        }
        return isSuccess;
    }
    public boolean setImageSetting (int imageSetting) {
        boolean isSuccess = false;
        communicationTask.sendCommandToPi(Constants.SET_INVERT_INPUT_IMAGE);
        byte cmd = communicationTask.readCommandFromPi();
        if (cmd == Constants.SUCCESS) {
            communicationTask.sendIntDataToPi(imageSetting);
            byte cmdRead = communicationTask.readCommandFromPi();
            if (cmdRead == Constants.SUCCESS) {
                Log.e("SET IMAGE SETTING", "READING INT DATA  ");

                isSuccess = true;
            }
        }
        return isSuccess;
    }

    public boolean setForceMode (int forceMode) {
        boolean isSuccess = false;
        communicationTask.sendCommandToPi(Constants.SET_FORCE_BROAD_MODE);
        byte cmd = communicationTask.readCommandFromPi();
        if (cmd == Constants.SUCCESS) {
            communicationTask.sendIntDataToPi(forceMode);
            byte cmdRead = communicationTask.readCommandFromPi();
            if (cmdRead == Constants.SUCCESS) {
                Log.e("SET FORCE MODE", "READING INT DATA  ");

                isSuccess = true;
            }
        }
        return isSuccess;
    }

    public int getEqupmentFirstStatus() {
        communicationTask.sendCommandToPi(Constants.GET_EQUPMENT_FIRST_STATUS);
        byte cmd = communicationTask.readCommandFromPi();
        if (cmd == Constants.SUCCESS) {
            communicationTask.sendCommandToPi(Constants.NEXT_ACTION);
        } else {
        }
        int getStatus = communicationTask.readIntDataFromPi();
        communicationTask.sendCommandToPi(Constants.SUCCESS);

        Log.e("EQUPMENT FIRSTS SATUS", "READING INT DATA : " + getStatus);
        return getStatus;
    }

    public int getImageSetting() {
        communicationTask.sendCommandToPi(Constants.GET_INVERT_INPUT_IMAGE);
        byte cmd = communicationTask.readCommandFromPi();
        if (cmd == Constants.SUCCESS) {
            communicationTask.sendCommandToPi(Constants.NEXT_ACTION);
        } else {
        }
        int getStatus = communicationTask.readIntDataFromPi();
        communicationTask.sendCommandToPi(Constants.SUCCESS);

        Log.e("IMAGE SETTIMG SATUS", "READING INT DATA : " + getStatus);
        return getStatus;
    }

    public int getForceMode() {
        communicationTask.sendCommandToPi(Constants.GET_FORCE_BROAD_MODE);
        byte cmd = communicationTask.readCommandFromPi();
        if (cmd == Constants.SUCCESS) {
            communicationTask.sendCommandToPi(Constants.NEXT_ACTION);
        } else {
        }
        int getStatus = communicationTask.readIntDataFromPi();
        communicationTask.sendCommandToPi(Constants.SUCCESS);

        Log.e("FORCE MODE SATUS", "READING INT DATA : " + getStatus);
        return getStatus;
    }

    public int getNo_Of_Equepment_Per_Line() {
        communicationTask.sendCommandToPi(Constants.GET_NO_OF_EQUPMENT_PER_LINE);
        byte cmd = communicationTask.readCommandFromPi();
        if (cmd == Constants.SUCCESS) {
            communicationTask.sendCommandToPi(Constants.NEXT_ACTION);
        } else {
            Log.d("EQUPMENT FIRSTS TATUS", "FAIL" + cmd);
        }
        int maxLine = communicationTask.readIntDataFromPi();
        communicationTask.sendCommandToPi(Constants.SUCCESS);

        return maxLine;
    }

    public boolean setNo_Of_Equepment_Per_Line(int noOfEquipment) {
        boolean isSuccess = false;
        communicationTask.sendCommandToPi(Constants.SET_NO_OF_EQUPMENT_PER_LINE);
        byte cmd = communicationTask.readCommandFromPi();
        if (cmd == Constants.SUCCESS) {
            communicationTask.sendIntDataToPi(noOfEquipment);
            byte cmdRead = communicationTask.readCommandFromPi();
            if (cmdRead == Constants.SUCCESS) {
                Log.e("NO_OF_EQUPMENT_PER_LINE", "READING INT DATA  ");

                isSuccess = true;
            }
        }
        return isSuccess;
    }

    public void resetDeviceApplication() {
        communicationTask.sendCommandToPi(Constants.RESET_DEVICE_APPLICATION);
        Log.e("Reset :-", "Device Application");
        closeSocket();

    }

    public void settingCommand(int timeOut, int setEquFirstStatus, int setNoOfEquPerLine, int gearValue, int resetmeter, int imageSetting, int forceMode ) {
        setTimeOut(timeOut);
        setEqupmentFirstStatus(setEquFirstStatus);
        setNo_Of_Equepment_Per_Line(setNoOfEquPerLine);
	setPeakGearValue(gearValue);
	setImageSetting(imageSetting);
	setForceMode(forceMode);
	if(resetmeter == 1)
		resetMeter();
        resetDeviceApplication();
    }

    public void settingCustomerCommand(int timeOut, int setEquFirstStatus, int imageSetting, int resetmeter, int gearValue, int forceMode) {
        setTimeOut(timeOut);
        setEqupmentFirstStatus(setEquFirstStatus);
        setImageSetting(imageSetting);
	setPeakGearValue(gearValue);
	setForceMode(forceMode);
	if(resetmeter == 1)
		resetMeter();
        resetDeviceApplication();
    }

    public String getCheckSum() {
        communicationTask.sendCommandToPi(Constants.GET_MD5_SUM);
        String checksum = "";
        byte cmd = communicationTask.readCommandFromPi();
        if (cmd == Constants.SUCCESS) {
            communicationTask.sendCommandToPi(Constants.NEXT_ACTION);
            checksum = communicationTask.getCheckSum();

            communicationTask.sendCommandToPi(Constants.SUCCESS);
        } else {

        }

        return checksum;
    }

    public void closeSocket() {
        Log.e("socket", "Close");
        communicationTask.closeSocket();
    }
}
