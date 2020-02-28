package com.app.emtdevice;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.app.emtdevice.activity.MainActivity;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import static com.app.emtdevice.Constants.INTERVALGETLINE;
import static com.app.emtdevice.Constants.INTERVAL_TIMEOUT;
import static com.app.emtdevice.activity.MainActivity.progressDialog;


public class TCPClient {

    public static final String TAG = TCPClient.class.getSimpleName();
    //public static final String SERVER_IP = "192.168.1.5"; //server IP address

    private DataInputStream readFromSocket = null;
    private DataOutputStream sendToSocket = null;
    Socket socket;


    public TCPClient() {
        try {
            InetAddress serverAddr = InetAddress.getByName(Constants.SERVER_IP);
            socket = new Socket(serverAddr, Constants.SERVER_PORT);
            socket.setSoTimeout(Constants.SOCKET_TIMEOUT);


            sendToSocket = new DataOutputStream(socket.getOutputStream());
            readFromSocket = new DataInputStream(socket.getInputStream());
        } catch (SocketTimeoutException exception) {
            MainActivity.androidAppState = Constants.ANDROID_APP_DISCONECT;
            INTERVALGETLINE = INTERVAL_TIMEOUT;
            Log.e("SocketTimeoutException ", exception.getMessage());
        } catch (IOException e) {
            MainActivity.androidAppState = Constants.ANDROID_APP_DISCONECT;
            INTERVALGETLINE = INTERVAL_TIMEOUT;
            e.printStackTrace();
        }
    }

    /**
     * Close the connection and release the members
     */
    public void stopClient() {
        if (sendToSocket != null) {
            try {
                sendToSocket.flush();
                sendToSocket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        readFromSocket = null;
        sendToSocket = null;
    }

    public void sendCommandToPi(byte cmd) {
        if (sendToSocket != null) {
            try {
                sendToSocket.write(cmd);
                sendToSocket.flush();
            } catch (IOException i) {
                System.out.println(i);
            }
        }
    }

    public byte readCommandFromPi() {
        byte cmd = 0;
        if (readFromSocket != null) {
            try {
                cmd = readFromSocket.readByte();
            } catch (IOException i) {
                MainActivity.androidAppState = Constants.ANDROID_APP_DISCONECT;
                INTERVALGETLINE = INTERVAL_TIMEOUT;
                System.out.println(i);
            }
        }
        return cmd;
    }

    public int readIntDataFromPi() {
        int data = 0;
        if (readFromSocket != null) {
            try {
                data = readFromSocket.readInt();
            } catch (IOException i) {
                MainActivity.androidAppState = Constants.ANDROID_APP_DISCONECT;
                INTERVALGETLINE = INTERVAL_TIMEOUT;

                System.out.println(i);
            }
        }
        return data;
    }

    public void sendIntDataToPi(int data) {
        if (sendToSocket != null) {
            try {
                sendToSocket.writeInt(data);
                sendToSocket.flush();
            } catch (IOException i) {
                System.out.println(i);
            }
        }
    }

    public Bitmap receiveImageFromPi(int bmpFileSize) {
        byte[] data = new byte[bmpFileSize];
        Bitmap bitmap = null;
        if (readFromSocket != null) {
            try {
                readFromSocket.readFully(data, 0, bmpFileSize);
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                File folder = new File(Constants.IMAGE_FILEPATH);
                boolean success = true;
                if (!folder.exists()) {
                    success = folder.mkdirs();
                }
                if (success) {
                    FileOutputStream fos = new FileOutputStream(Constants.IMAGE_FILEPATH + Constants.IMAGE_ZIPNAME);
                    fos.write(data);
                    fos.close();
                }


            } catch (IOException e) {
                Log.e("exception", "Time out");
                MainActivity.androidAppState = Constants.ANDROID_APP_DISCONECT;
                INTERVALGETLINE = INTERVAL_TIMEOUT;

                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public String receiveFileFromPi(int filesize) {
        byte[] data = new byte[filesize];
        String fileString = null;
        if (readFromSocket != null) {

            try {
                FileOutputStream fos = new FileOutputStream("/sdcard/log.txt");
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                readFromSocket.readFully(data, 0, filesize);
                fos.write(data);
                String str = new String(data, "UTF-8");
                fileString = str;
                Constants.piLog = str;
                bos.close();

            } catch (IOException e) {
                MainActivity.androidAppState = Constants.ANDROID_APP_DISCONECT;
                INTERVALGETLINE = INTERVAL_TIMEOUT;

                e.printStackTrace();
            }

        }

        return fileString;
    }

    public void sendImageToPi(byte[] data, int bmpFileSize) {
        if (sendToSocket != null) {
            try {
                //sendToSocket.write(data, 0, bmpFileSize);
                int remain = bmpFileSize;
                int off = 0;
                int progressValue = 0;
                while (remain > 0) {

                    if (remain >= 1048576) {
                        sendToSocket.write(data, off, 1048576);
                        progressValue += 1048576;
                    } else {
                        sendToSocket.write(data, off, remain);
                        progressValue += remain;

                    }
                    Log.e("send byte:-", remain + "");
                    off = off + (1048576);
                    remain = remain - 1048576;
                    progressDialog.setProgress(progressValue);
                }
                progressDialog.dismiss();

                sendToSocket.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    public String getMD5CheckSum() {
        byte[] data = new byte[33];
        String fileString = null;

        if (readFromSocket != null) {
            try {
                readFromSocket.readFully(data, 0, 33);
                fileString = new String(data, "UTF-8");
            } catch (IOException e) {
                MainActivity.androidAppState = Constants.ANDROID_APP_DISCONECT;
                INTERVALGETLINE = INTERVAL_TIMEOUT;
                e.printStackTrace();
            }

        }
        return fileString;
    }
}
