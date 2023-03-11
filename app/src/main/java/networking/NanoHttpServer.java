package networking;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.example.local_file_share.FileChooserFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import fi.iki.elonen.NanoHTTPD;

public class NanoHttpServer extends  NanoHTTPD {

    //Intent m_Intent;
    private FileChooserFragment m_FileChooserFragment;
    public NanoHttpServer(int port, FileChooserFragment fragment)  throws IOException {
        super(port);
        super.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
   //     m_Intent = intent;
        Log.d("NANOHTTPD Running", "NanoHttpServer: "+String.valueOf(port));
        m_FileChooserFragment = fragment;
    }


    @Override
    public Response serve(IHTTPSession session) {
       Log.d("IntentURI", "serve: "+m_FileChooserFragment.FileUri());
        String msg = "<html><body><h1>No File Chosen</h1>\n";

        if(m_FileChooserFragment.FileUri() != null)
        {
            try {
                InputStream fs = m_FileChooserFragment.getContext().getContentResolver().openInputStream(m_FileChooserFragment.FileUri());
                MimeTypeMap mime =  MimeTypeMap.getSingleton();
                String extension = m_FileChooserFragment.FileUri().getPath().substring(m_FileChooserFragment.FileUri().getPath().lastIndexOf("."));
                String mimeType = mime.getMimeTypeFromExtension(extension);
                return newChunkedResponse(Response.Status.OK, mimeType, fs);
            } catch (Exception e) {
                Log.d("nanoHTTPD execpetion", e.getMessage());
            }

        }

        return newFixedLengthResponse(msg + "</body></html>\n");
    }




}
