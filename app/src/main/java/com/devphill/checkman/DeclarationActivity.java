package com.devphill.checkman;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.RecyclerView;
import android.util.Log;


import com.devphill.checkman.adapter.DeclarationsAdapter;
import com.devphill.checkman.internet.ServerAPI;
import com.devphill.checkman.model.Declarations;

import com.necistudio.vigerpdf.VigerPDF;
import com.necistudio.vigerpdf.adapter.VigerAdapter;
import com.necistudio.vigerpdf.manage.OnResultListener;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadManager;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.RetryPolicy;
import com.thin.downloadmanager.ThinDownloadManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;

public class DeclarationActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private ServerAPI serverAPI;

    private List<Declarations.Item> declarationsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private DeclarationsAdapter declarationsAdapter;

    private String LOG_TAG = "DeclarationActivityTag";

    MyDownloadDownloadStatusListenerV1 myDownloadStatusListener = new MyDownloadDownloadStatusListenerV1();
    private ThinDownloadManager downloadManager;

    int downloadId1;

    private String linkPDF,name;

    ViewPager viewPager;
    File filesDir;
    private ArrayList<Bitmap> itemData;
    private VigerAdapter adapter;
    private VigerPDF vigerPDF;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_declaration_show);

       // pdfView = (PDFView) findViewById(R.id.pdfview);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        itemData = new ArrayList<>();
        adapter = new VigerAdapter(getApplicationContext(), itemData);
        viewPager.setAdapter(adapter);

        linkPDF = getIntent().getStringExtra("linkPDF");
        name = getIntent().getStringExtra("name");

        Log.i(LOG_TAG, "load file  " + linkPDF);


        downloadManager = new ThinDownloadManager(4);
        RetryPolicy retryPolicy = new DefaultRetryPolicy();

        filesDir = getExternalFilesDir("");

        Uri downloadUri = Uri.parse(linkPDF);
        Uri destinationUri = Uri.parse(filesDir + "/" + name + ".pdf");
        final DownloadRequest downloadRequest1 = new DownloadRequest(downloadUri)
                .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.LOW)
                .setRetryPolicy(retryPolicy)
                .setDownloadContext("Download1")
                .setStatusListener(myDownloadStatusListener);


        if (downloadManager.query(downloadId1) == DownloadManager.STATUS_NOT_FOUND) {
            downloadId1 = downloadManager.add(downloadRequest1);

        }
    }

    private void loadPDF(File file){

        itemData.clear();
        adapter.notifyDataSetChanged();
        vigerPDF.cancle();

      /*  pdfView.fromFile(file)
               // .fromAsset(pdfName)
              //  .pages(0, 2, 1, 3, 3, 3)
                .defaultPage(1)
                .showMinimap(false)
                .enableSwipe(true)
                //.onDraw(onDrawListener)
                //.onLoad(onLoadCompleteListener)
                //  .onPageChange(onPageChangeListener)
                .load();*/

        new VigerPDF(getBaseContext()).initFromFile(file,new OnResultListener() {
            @Override
            public void resultData(Bitmap bitmap) {
                itemData.add(bitmap);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void progressData(int progress) {

                Log.e("data", "" + progress);
            }

            @Override
            public void failed(Throwable throwable) {

            }

        });

    }

    private String getNetworkType(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            return activeNetwork.getTypeName();
        }
        return null;
    }

    class MyDownloadDownloadStatusListenerV1 implements DownloadStatusListenerV1 {

        @Override
        public void onDownloadComplete(DownloadRequest request) {
            final int id = request.getDownloadId();
            if (id == downloadId1) {
              //  mProgress1Txt.setText(request.getDownloadContext() + " id: "+id+" Completed");
                loadPDF(new File(filesDir + "/" + name + ".pdf"));

                Log.i(LOG_TAG, "onDownloadComplete " );
            }
        }

        @Override
        public void onDownloadFailed(DownloadRequest request, int errorCode, String errorMessage) {
            final int id = request.getDownloadId();
            if (id == downloadId1) {
               // mProgress1Txt.setText("Download1 id: "+id+" Failed: ErrorCode "+errorCode+", "+errorMessage);
               // mProgress1.setProgress(0);
                Log.i(LOG_TAG, "onDownloadFailed "   + errorMessage);
            }
        }

        @Override
        public void onProgress(DownloadRequest request, long totalBytes, long downloadedBytes, int progress) {
            int id = request.getDownloadId();

            System.out.println("######## onProgress ###### "+id+" : "+totalBytes+" : "+downloadedBytes+" : "+progress);
            if (id == downloadId1) {
            //    mProgress1Txt.setText("Download1 id: " + id + ", " + progress + "%" + "  " + getBytesDownloaded(progress, totalBytes));
             //  mProgress1.setProgress(progress);
                Log.i(LOG_TAG, "onProgress " +id+" : "+totalBytes+" : "+downloadedBytes+" : "+progress);

            }
        }
    }

    private String getBytesDownloaded(int progress, long totalBytes) {
        //Greater than 1 MB
        long bytesCompleted = (progress * totalBytes)/100;
        if (totalBytes >= 1000000) {
            return (""+(String.format("%.1f", (float)bytesCompleted/1000000))+ "/"+ ( String.format("%.1f", (float)totalBytes/1000000)) + "MB");
        } if (totalBytes >= 1000) {
            return (""+(String.format("%.1f", (float)bytesCompleted/1000))+ "/"+ ( String.format("%.1f", (float)totalBytes/1000)) + "Kb");

        } else {
            return ( ""+bytesCompleted+"/"+totalBytes );
        }
    }

}
