package com.pokhyl.ghclient;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.pokhyl.ghclient.api.GitHubRepoService;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private Retrofit retrofit;
    private RepoAdapter repoAdapter;
    private ProgressBar progressBar;
    private EditText searchText;
    private RecyclerView recyclerView;
    private Disposable disposable;
    private GitHubRepoService gitHubRepoService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initViews();
        initRetrofit();
    }

    @NonNull
    private void initRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        gitHubRepoService = retrofit.create(GitHubRepoService.class);
    }

    public void initViews() {
        progressBar = (ProgressBar) findViewById(R.id.search_progress);
        searchText = (EditText) findViewById(R.id.search_repo_text);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.repo_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        repoAdapter = new RepoAdapter();
        recyclerView.setAdapter(repoAdapter);
    }

    private void performSearch(String query) {
        if (disposable != null) {
            disposable.dispose();
        }
        disposable = gitHubRepoService.searchRepos(query)
                .delay(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(repos -> {
                            Log.d("TEST1", "onNext");
                            repoAdapter.setRepoList(repos.items);
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        },
                        throwable -> Log.d("TEST1", throwable.toString()),
                        () -> {},
                        subscription -> {
                            subscription.request(1);
                            Log.d("TEST1", "onSubscribe");
                            progressBar.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        });
    }


}
