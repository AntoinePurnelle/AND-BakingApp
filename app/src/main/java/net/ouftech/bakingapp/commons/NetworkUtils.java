/*
 * Copyright 2018 Antoine PURNELLE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ouftech.bakingapp.commons;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.ouftech.bakingapp.model.Recipe;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

/**
 * Created by antoine.purnelle@ouftech.net on 27-02-18.
 */

public class NetworkUtils {

    @NonNull
    protected static String getLotTag() {
        return "NetworkUtils";
    }

    public interface Client {
        @GET("topher/2017/May/59121517_baking/baking.json")
        Call<List<Recipe>> getRecipes();
    }

    private static Client client;

    private static Client getClient() {
        if (client == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl("https://d17h27t6h515a5.cloudfront.net/")
                    .addConverterFactory(
                            GsonConverterFactory.create()
                    );

            Retrofit retrofit = builder.client(httpClient.build()).build();

            client = retrofit.create(Client.class);
        }

        return client;
    }

    public static void getRecipes(@Nullable Context context, @NonNull Callback<List<Recipe>> callback) {
        if (context == null) {
            Logger.w(getLotTag(), "Cannot build URL", new NullPointerException("Context is null"), false);
            return;
        }

        Call<List<Recipe>> call = getClient().getRecipes();
        call.enqueue(callback);
    }

}
