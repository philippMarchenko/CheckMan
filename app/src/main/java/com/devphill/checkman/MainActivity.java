package com.devphill.checkman;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.devphill.checkman.adapter.DeclarationsAdapter;
import com.devphill.checkman.internet.ServerAPI;
import com.devphill.checkman.model.Declarations;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadManager;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.RetryPolicy;
import com.thin.downloadmanager.ThinDownloadManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements DeclarationsAdapter.IDeclarationsAdapterListener{

    private Retrofit retrofit;
    private ServerAPI serverAPI;

    private List<Declarations.Item> declarationsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private DeclarationsAdapter declarationsAdapter;

    MyDownloadDownloadStatusListenerV1 myDownloadStatusListener = new MyDownloadDownloadStatusListenerV1();
    private ThinDownloadManager downloadManager;
    File filesDir;
    int downloadId1;

    private ProgressBar progressBar;

    private String LOG_TAG = "MainActivityTag";
    private String name = "";

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        //   toolbar_title = (TextView) toolbar.findViewById(R.id.toolbar_title);


        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_declarations);
        declarationsAdapter = new DeclarationsAdapter(getBaseContext(),this,declarationsList,this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getBaseContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(declarationsAdapter);

        progressBar = (ProgressBar) findViewById(R.id.progress);
        progressBar.setVisibility(View.INVISIBLE);

        initRetrofit ();
        getDeclarationsList ();

        Log.i(LOG_TAG, "onCreate");

    }
    private void initRetrofit (){

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(ServerAPI.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        serverAPI = retrofit.create(ServerAPI.class);
    }

    private void getDeclarationsList (){

        Log.i(LOG_TAG, "getAllNewsList ");

        String netType = getNetworkType(getBaseContext());
        if(netType == null){
            Toast.makeText(getBaseContext(), "Подключение к сети отсутствует!", Toast.LENGTH_LONG).show();

        }
        else {
            try {

                serverAPI.getDeclarationsList("Бовбас").enqueue(new Callback<Declarations>() {
                    @Override
                    public void onResponse(Call<Declarations> call, Response<Declarations> response) {

                        Declarations declarations = response.body();

                        try {
                            declarationsList.addAll(declarations.getItems());
                            declarationsAdapter.notifyDataSetChanged();
                        }
                        catch(Exception e){

                            Toast.makeText(getBaseContext(), "Нет новостей на сервере!", Toast.LENGTH_LONG).show();

                        }

                    }

                    @Override
                    public void onFailure(Call<Declarations> call, Throwable t) {

                        Log.i(LOG_TAG, "onFailure. Ошибка REST запроса getListNews " + t.toString());
                    }
                });
            } catch (Exception e) {

                Log.i(LOG_TAG, "Ошибка REST запроса к серверу  getListNews " + e.getMessage());
            }
        }
        }

    private void downloadFile(String url,String name){

        downloadManager = new ThinDownloadManager(4);
        RetryPolicy retryPolicy = new DefaultRetryPolicy();

        filesDir = getExternalFilesDir("");

        Uri downloadUri = Uri.parse(url);
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

    class MyDownloadDownloadStatusListenerV1 implements DownloadStatusListenerV1 {

        @Override
        public void onDownloadComplete(DownloadRequest request) {
            final int id = request.getDownloadId();
            if (id == downloadId1) {

                // Log.i(LOG_TAG, "onDownloadComplete file " + filesDir + "/" + name + ".pdf");
                //  mProgress1Txt.setText(request.getDownloadContext() + " id: "+id+" Completed");
                //    loadPDF(new File(filesDir + "/" + name + ".pdf"));

                Intent intent = new Intent(getBaseContext(), DeclarationActivity.class);
                intent.putExtra("name",name);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getBaseContext().startActivity(intent);
            }
        }

        @Override
        public void onDownloadFailed(DownloadRequest request, int errorCode, String errorMessage) {
            final int id = request.getDownloadId();
            if (id == downloadId1) {
                // mProgress1Txt.setText("Download1 id: "+id+" Failed: ErrorCode "+errorCode+", "+errorMessage);
                // mProgress1.setProgress(0);
                //  Log.i(LOG_TAG, "onDownloadFailed "   + errorMessage);
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

    private String getNetworkType(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            return activeNetwork.getTypeName();
        }
        return null;
    }

    @Override
    public void onClick(int position) {

        name =  declarationsList.get(position).getLastname() + " " + declarationsList.get(position).getFirstname();
        downloadFile(declarationsList.get(position).getLinkPDF(),name);

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);

    }

    public void onResume() {
        super.onResume();


        progressBar.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);

        Log.i(LOG_TAG, "MainFragment onResume");


    }

    @Override
    public void onPause() {
        super.onPause();

        Log.i(LOG_TAG, "MainFragment onPause");



    }
}
