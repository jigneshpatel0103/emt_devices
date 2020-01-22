package com.app.emtdevice;

import android.graphics.Bitmap;
import android.os.AsyncTask;

public class CommunicationTask extends AsyncTask<String, String, TCPClient> {
    TCPClient tcpClient;

    @Override
    protected TCPClient doInBackground(String... message) {
        tcpClient = new TCPClient();
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }


    public void connectToSocket() {
        tcpClient = new TCPClient();
    }

    public void closeSocket() {
        tcpClient.stopClient();
    }

    public void sendCommandToPi(byte cmd) {
        if (tcpClient != null)
            tcpClient.sendCommandToPi(cmd);
    }

    public byte readCommandFromPi() {
        byte result = 0;
        if (tcpClient != null)
            result = tcpClient.readCommandFromPi();
        return result;
    }

    public void sendIntDataToPi(int data) {
        if (tcpClient != null)
            tcpClient.sendIntDataToPi(data);
    }

    public int readIntDataFromPi() {
        int result = 0;
        if (tcpClient != null)
            result = tcpClient.readIntDataFromPi();
        return result;
    }

    public Bitmap receiveImageFromPi(int bmpImageSize) {
        Bitmap bitmap = null;
        if (tcpClient != null)
            bitmap = tcpClient.receiveImageFromPi(bmpImageSize);
        return bitmap;
    }

    public String receiveFileFromPi(int filesize) {
        String filedata = null;
        if (tcpClient != null)
            filedata = tcpClient.receiveFileFromPi(filesize);
        return filedata;
    }

    public void sendImageToPi(byte[] data, int bmpImageSize) {
        if (tcpClient != null)
            tcpClient.sendImageToPi(data, bmpImageSize);
    }

    public String getCheckSum() {
        String checksum = "";
        if (tcpClient != null) {
            checksum = tcpClient.getMD5CheckSum();
        }
        return checksum;
    }

}
