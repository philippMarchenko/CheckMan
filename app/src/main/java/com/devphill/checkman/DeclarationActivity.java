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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;


import com.devphill.checkman.adapter.DeclarationsAdapter;
import com.devphill.checkman.internet.ServerAPI;
import com.devphill.checkman.model.Declarations;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
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

    private String name;

    private PDFView pdfView;

    private Toolbar toolbar;
    private TextView toolbar_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_declaration_show);

        toolbar = (Toolbar) findViewById(R.id.toolbarDeclaration);
     //   toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);


        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
      //  getSupportActionBar().hide();

        pdfView = (PDFView) findViewById(R.id.pdfView);

        name = getIntent().getStringExtra("name");


        File filesDir = getExternalFilesDir("");

        loadPDF(new File(filesDir + "/" + name + ".pdf"));
    }

    private void loadPDF(File file){


                 pdfView.fromFile(file)
                .enableSwipe(true) // allows to block changing pages using swipe
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)

                // allows to draw something on the current page, usually visible in the middle of the screen
                //  .onDraw(onDrawListener)
                // allows to draw something on all pages, separately for every page. Called only for visible pages
                //   .onDrawAll(onDrawListener)
                //  .onLoad(onLoadCompleteListener) // called after document is loaded and starts to be rendered
                  .onPageChange(new OnPageChangeListener() {
                      @Override
                      public void onPageChanged(int page, int pageCount) {

                         // toolbar_title.setText("(" + page + "/" + pageCount + ")" + name);
                          toolbar.setTitle("(" + (page + 1) + "/" + pageCount + ")" + name);
                      }
                  })
                //  .onPageScroll(onPageScrollListener)
                //  .onError(onErrorListener)
                //   .onRender(onRenderListener) // called after document is rendered for the first time
                // called on single tap, return true if handled, false to toggle scroll handle visibility
                //    .onTap(onTapListener)
                //////  .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                ///  .password(null)
                //   .scrollHandle(null)
                ////  .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                // spacing between pages in dp. To define spacing color, set view background
                //
                //    .spacing(0)
                .load();



    }

}
