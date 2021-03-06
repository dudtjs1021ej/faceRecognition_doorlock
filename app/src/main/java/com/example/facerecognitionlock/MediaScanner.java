package com.example.facerecognitionlock;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.text.TextUtils;

public class MediaScanner {
    private Context mContext;
    private static volatile MediaScanner mMediaInstance = null;
    private MediaScannerConnection mMediaScanner;

    private String mFilePath;

    public static MediaScanner getInstance( Context context ) {
        if( null == context )
            return null;

        if( null == mMediaInstance )
            mMediaInstance = new MediaScanner( context );
        return mMediaInstance;
    }

    public static void releaseInstance() {
        if ( null != mMediaInstance ) {
            mMediaInstance = null;
        }
    }


    private MediaScanner(Context context) {
        mContext = context;
        mFilePath = "";
        MediaScannerConnection.MediaScannerConnectionClient mediaScanClient;
        mediaScanClient = new MediaScannerConnection.MediaScannerConnectionClient(){
            @Override
            public void onScanCompleted(String s, Uri uri) {
                System.out.println("::::MediaScan Success::::");

                mMediaScanner.disconnect();
            }

            @Override public void onMediaScannerConnected() {
                mMediaScanner.scanFile(mFilePath, null);
            }
        };
        mMediaScanner = new MediaScannerConnection(mContext, mediaScanClient);
    }

    public void mediaScanning(final String path) {

        if( TextUtils.isEmpty(path) )
            return;
        mFilePath = path;

        if( !mMediaScanner.isConnected() )
            mMediaScanner.connect();
    }

}
