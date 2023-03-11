package com.example.local_file_share;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;

import java.io.File;

import networking.NanoHttpServer;
import networking.NetworkingUtils;
import networking.udpServer;

public class MainActivity extends AppCompatActivity {

    private static int FILE_PORT = 1234;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
//if you added fragment via layout xml
        FileChooserFragment fragment = (FileChooserFragment) fm.findFragmentById(R.id.fragmentContainerView3);



        NetworkThread networkThread = new NetworkThread(fragment);
        networkThread.start();

        TextView serverIP = (TextView)findViewById(R.id.ServerIP);
        String serverStr = "http://"+NetworkingUtils.getIPAddress(true)+":"+String.valueOf(FILE_PORT);
        serverIP.setText(serverStr);

        Log.d("My IP Adderss", NetworkingUtils.getIPAddress(true));

        // create the QR image
        Utils utils = new Utils();
        utils.m_Context = this;
        ImageView qrView = (ImageView) findViewById(R.id.QRView);
        try {
            Bitmap qrImg = utils.TextToImageEncode(serverStr);
            qrView.setImageBitmap(qrImg);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }


//        udpServer server = new udpServer(1234);
//        server.start();
//
//        Log.d("START", "onCreate: ");
    }

    class NetworkThread extends Thread
    {
        FileChooserFragment m_FileChooserFragment;
        NetworkThread(FileChooserFragment fileChooserFragment)
        {
            m_FileChooserFragment = fileChooserFragment;

        }
        public void run()
        {
            try {
                NanoHttpServer server = new NanoHttpServer(FILE_PORT, m_FileChooserFragment);


            } catch (Exception e)
            {
                Log.d("Network thread exception", e.getMessage());
            }

        }
    }
}