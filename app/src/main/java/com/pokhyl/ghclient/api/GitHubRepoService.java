package com.pokhyl.ghclient.api;


import com.pokhyl.ghclient.model.SearchRepoResult;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GitHubRepoService {

    @GET("search/repositories?&sort=stars&order=desc")
    Flowable<SearchRepoResult> searchRepos(@Query("q") String query);
}

