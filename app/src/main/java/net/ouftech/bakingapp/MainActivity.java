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

package net.ouftech.bakingapp;

import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.ouftech.bakingapp.commons.BaseActivity;
import net.ouftech.bakingapp.commons.CallException;
import net.ouftech.bakingapp.commons.CollectionUtils;
import net.ouftech.bakingapp.commons.Logger;
import net.ouftech.bakingapp.commons.NetworkUtils;
import net.ouftech.bakingapp.model.Recipe;

import java.util.List;

import butterknife.BindView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {

    @BindView(R.id.pb_loading_indicator)
    protected ProgressBar loadingIndicator;
    @BindView(R.id.tv_error_message_display)
    protected TextView errorMessageDisplay;
    @BindView(R.id.recipes_rv)
    protected RecyclerView recipesRV;

    private List<Recipe> recipes;
    private RecipesAdapter recipesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadRecipes();

        recipesRV.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.recycler_view_span_count));
        recipesRV.setLayoutManager(layoutManager);
    }

    private void loadRecipes() {
        if (!isRunning())
            return;

        setProgressBarVisibility(View.VISIBLE);

        NetworkUtils.getRecipes(this, new Callback<List<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {
                recipes = response.body();

                if (recipes == null) {
                    ResponseBody errorBody = response.errorBody();
                    CallException callException = new CallException(response.code(), response.message(), errorBody, call);

                    if (errorBody == null)
                        Logger.e(getLotTag(), "Error while executing getRecipes call", callException);
                    else
                        Logger.e(getLotTag(), String.format("Error while executing getRecipes call. ErrorBody = %s", errorBody), callException);

                    showErrorMessage();
                } else {
                    if (CollectionUtils.isEmpty(recipes)) {
                        Logger.e(getLotTag(), "Error while executing getRecipes call. Returned list is empty", new CallException(response.code(), response.message(), null, call));
                        showErrorMessage();
                    }
                }

                setProgressBarVisibility(View.GONE);

                recipesAdapter = new RecipesAdapter(recipes, recipe -> {
                    Intent intent = new Intent(MainActivity.this, StepListActivity.class);
                    MainActivity.this.startActivity(intent);
                });
                recipesRV.setAdapter(recipesAdapter);
            }

            @Override
            public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
                Logger.e(getLotTag(), "Error while executing getRecipes call", t);
                setProgressBarVisibility(View.GONE);
                showErrorMessage();
            }
        });
    }

    private void setProgressBarVisibility(final int visibility) {
        if (isRunning() && loadingIndicator != null)
            runOnUiThread(() -> loadingIndicator.setVisibility(visibility));
    }

    private void showErrorMessage() {
        errorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @NonNull
    @Override
    protected String getLotTag() {
        return "MainActivity";
    }

    @Override
    @LayoutRes
    protected int getLayoutId() {
        return R.layout.activity_main;
    }
}
