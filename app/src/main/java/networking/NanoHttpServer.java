package networking;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.OpenableColumns;
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
                Cursor returnCursor = m_FileChooserFragment.getContext().getContentResolver().query(m_FileChooserFragment.FileUri(), null, null, null, null);
                int sizeIndx = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                returnCursor.moveToFirst();
                long size = returnCursor.getLong(sizeIndx);
            //    File file = new File(m_FileChooserFragment.FileUri());
                MimeTypeMap mime =  MimeTypeMap.getSingleton();
                String extension = m_FileChooserFragment.FileUri().getPath().substring(m_FileChooserFragment.FileUri().getPath().lastIndexOf("."));
                String fileName = m_FileChooserFragment.FileUri().getPath().substring(m_FileChooserFragment.FileUri().getPath().lastIndexOf("/"));
                fileName = fileName.replace(' ','_');
                extension = extension.replace(".","");
                String mimeType = mime.getMimeTypeFromExtension(extension);
                if(mimeType == null) mimeType = "application/octet-stream";
                Log.d("MIMETYPE", mimeType);
                Log.d("extension", extension);
                Log.d("fs.avail", String.valueOf(fs.available()));
                Log.d("fs size", String.valueOf(size));

                if(session.getHeaders().get("range") != null)
                {
                    return getPartialResponse(fs,mimeType,session.getHeaders().get("range"),size,fileName);
                } else {
                    return getFullResponse(fs,mimeType,size,fileName);
                }


               // return newFixedLengthResponse(Response.Status.OK,mimeType,fs,fs.available());
            } catch (Exception e) {
                Log.d("nanoHTTPD execpetion", e.getMessage());
                throw new RuntimeException(e);
            }

        }

        return newFixedLengthResponse(msg + "</body></html>\n");
    }

    // handle non range requests
    private  Response getFullResponse(InputStream fs, String mimeType, long size, String fileName)
    {

        try {
            Response response = newFixedLengthResponse(Response.Status.OK,mimeType,fs,size);
            response.addHeader("Content-Length", size + "");
            response.addHeader("Content-Range", "bytes " + 0 + "-" + size + "/" + size);
            response.addHeader("Content-Type", mimeType);
            response.addHeader("Content-Disposition", "attachment; filename=\""+fileName+"\"");
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    // handle range request
    private  Response getPartialResponse(InputStream fs, String mimeType, String rangeHeader, long size, String fileName)
    {
        try
        {
            String rangeValue = rangeHeader.trim().substring("bytes=".length());
            long fileLength = size;
            long start, end;
            if (rangeValue.startsWith("-")) {
                end = fileLength - 1;
                start = fileLength - 1
                        - Long.parseLong(rangeValue.substring("-".length()));
            } else {
                String[] range = rangeValue.split("-");
                start = Long.parseLong(range[0]);
                end = range.length > 1 ? Long.parseLong(range[1])
                        : fileLength - 1;
            }
            if (end > fileLength - 1) {
                end = fileLength - 1;
            }
            if (start <= end) {
                long contentLength = end - start + 1;
                //noinspection ResultOfMethodCallIgnored
                fs.skip(start);
                Response response = newChunkedResponse(Response.Status.PARTIAL_CONTENT, mimeType, fs);
                response.addHeader("Content-Length", contentLength + "");
                response.addHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
                response.addHeader("Content-Type", mimeType);
                response.addHeader("Content-Disposition", "attachment; filename=\""+fileName+"\"");
                return response;
            } else {
                return newFixedLengthResponse(Response.Status.RANGE_NOT_SATISFIABLE, mimeType, rangeHeader);
            }
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }

    }




}
