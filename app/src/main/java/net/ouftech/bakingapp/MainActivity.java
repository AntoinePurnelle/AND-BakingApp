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
import android.os.Bundle;
import android.database.SQLException;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Select;

import net.ouftech.bakingapp.IdlingResource.SimpleIdlingResource;
import net.ouftech.bakingapp.commons.BaseActivity;
import net.ouftech.bakingapp.commons.CallException;
import net.ouftech.bakingapp.commons.CollectionUtils;
import net.ouftech.bakingapp.commons.Logger;
import net.ouftech.bakingapp.commons.NetworkUtils;
import net.ouftech.bakingapp.model.BakingAppDatabase;
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

    @Nullable
    private SimpleIdlingResource idlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIdlingResource();
        idlingResource.setIdleState(false);
        loadRecipes();

        recipesRV.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.recycler_view_span_count));
        recipesRV.setLayoutManager(layoutManager);
    }

    private void loadRecipes() {
        if (!isRunning())
            return;

        setProgressBarVisibility(View.VISIBLE);

        recipes = new Select()
                .from(Recipe.class)
                .queryList();

        if (CollectionUtils.isEmpty(recipes))
            loadRecipesFromNetwork();
        else
            displayRecipes();
    }

    private void loadRecipesFromNetwork() {
        NetworkUtils.getRecipes(this, new Callback<List<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {
                recipes = response.body();

                if (recipes == null) {
                    ResponseBody errorBody = response.errorBody();
                    CallException callException = new CallException(response.code(), response.message(), errorBody, call);

                    if (errorBody == null)
                        Logger.e(getLogTag(), "Error while executing getRecipes call", callException);
                    else
                        Logger.e(getLogTag(), String.format("Error while executing getRecipes call. ErrorBody = %s", errorBody), callException);

                    showErrorMessage();
                } else {
                    if (CollectionUtils.isEmpty(recipes)) {
                        Logger.e(getLogTag(), "Error while executing getRecipes call. Returned list is empty", new CallException(response.code(), response.message(), null, call));
                        showErrorMessage();
                    }
                }

                saveRecipes();
                displayRecipes();
            }

            @Override
            public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
                Logger.e(getLogTag(), "Error while executing getRecipes call", t);
                setProgressBarVisibility(View.GONE);
                showErrorMessage();
            }
        });
    }

    private void saveRecipes() {
        FlowManager.getDatabase(BakingAppDatabase.class).executeTransaction(databaseWrapper -> {
            for (Recipe recipe : recipes) {
                recipe.initWithChildren();
                if (!recipe.save())
                    Logger.e(getLogTag(), new SQLException(String.format("Error while inserting %s", recipe)));
                else
                    Logger.d(getLogTag(), String.format("Saved %s", recipe));
            }
        });
    }

    private void displayRecipes() {
        setProgressBarVisibility(View.GONE);

        recipesAdapter = new RecipesAdapter(recipes, this::onRecipeClick);
        recipesRV.setAdapter(recipesAdapter);
        idlingResource.setIdleState(true);
    }

    protected void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(MainActivity.this, StepListActivity.class);
        intent.putExtra(StepListActivity.RECIPE_EXTRA_ID, recipe);
        MainActivity.this.startActivity(intent);
    }

    private void setProgressBarVisibility(final int visibility) {
        if (isRunning() && loadingIndicator != null)
            runOnUiThread(() -> loadingIndicator.setVisibility(visibility));
    }

    private void showErrorMessage() {
        if (errorMessageDisplay != null)
            errorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @NonNull
    @Override
    protected String getLogTag() {
        return "MainActivity";
    }

    @Override
    @LayoutRes
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (idlingResource == null) {
            idlingResource = new SimpleIdlingResource();
        }
        return idlingResource;
    }

}
