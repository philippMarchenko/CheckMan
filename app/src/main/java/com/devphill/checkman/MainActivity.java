package com.devphill.checkman;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.devphill.checkman.adapter.DeclarationsAdapter;
import com.devphill.checkman.internet.ServerAPI;
import com.devphill.checkman.model.Declarations;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private ServerAPI serverAPI;

    private List<Declarations.Item> declarationsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private DeclarationsAdapter declarationsAdapter;

    private String LOG_TAG = "MainActivityTag";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_declarations);
        declarationsAdapter = new DeclarationsAdapter(getBaseContext(),this,declarationsList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getBaseContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(declarationsAdapter);

        initRetrofit ();
        getDeclarationsList ();

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

    private String getNetworkType(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            return activeNetwork.getTypeName();
        }
        return null;
    }
}
