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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import net.ouftech.bakingapp.commons.BaseActivity;
import net.ouftech.bakingapp.commons.CollectionUtils;
import net.ouftech.bakingapp.model.Recipe;
import net.ouftech.bakingapp.model.Step;

import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.State;

/**
 * An activity representing a list of Steps. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link StepDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class StepListActivity extends BaseActivity {

    public static final String RECIPE_EXTRA_ID = "recipe";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.step_list)
    RecyclerView stepListRV;
    @Nullable @BindView(R.id.step_detail_container)
    FrameLayout stepDetailContainer;
    @BindView(R.id.frameLayout)
    FrameLayout frameLayout;

    @State
    protected boolean mTwoPane;
    @State
    protected Recipe recipe;

    @NonNull
    @Override
    protected String getLotTag() {
        return "StepListActivity";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_step_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (stepDetailContainer != null) {
            mTwoPane = true;
        }

        recipe = getIntent().getParcelableExtra(RECIPE_EXTRA_ID);

        stepListRV.setAdapter(new SimpleItemRecyclerViewAdapter(this, recipe, mTwoPane));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private static final int INGREDIENTS_VIEW_TYPE = 0;
        private static final int STEP_VIEW_TYPE = 1;

        private final StepListActivity mParentActivity;
        private final Recipe recipe;
        private final boolean mTwoPane;

        SimpleItemRecyclerViewAdapter(StepListActivity parent,
                                      Recipe recipe,
                                      boolean twoPane) {
            this.recipe = recipe;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == INGREDIENTS_VIEW_TYPE)
                return new IngredientsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.step_list_ingredients, parent, false));
            else
                return new StepViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.step_list_content, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            holder.bind(recipe, position);
        }

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? INGREDIENTS_VIEW_TYPE : STEP_VIEW_TYPE;
        }

        @Override
        public int getItemCount() {
            if (recipe == null)
                return 0;

            return CollectionUtils.getSize(recipe.steps) + 1;
        }

        abstract class ViewHolder extends RecyclerView.ViewHolder {
            protected View rootView;

            ViewHolder(View view) {
                super(view);
                rootView = view;
                ButterKnife.bind(this, view);
            }

            protected abstract void bind(Recipe recipe, int position);
        }

        class StepViewHolder extends ViewHolder {
            @BindView(R.id.step_number_tv)
            TextView stepNumberTV;
            @BindView(R.id.step_name_tv)
            TextView stepNameTV;

            StepViewHolder(View view) {
                super(view);
            }

            @Override
            protected void bind(Recipe recipe, int position) {
                Step step = recipe.steps.get(position-1);
                stepNumberTV.setText(String.valueOf(step.stepId));
                stepNameTV.setText(step.shortDescription);

                rootView.setOnClickListener(v -> {
                    if (mTwoPane) {
                        StepDetailFragment fragment = StepDetailFragmentBuilder.newStepDetailFragment(
                                recipe,
                                position-1);

                        mParentActivity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.step_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = rootView.getContext();
                        Intent intent = new Intent(context, StepDetailActivity.class);
                        intent.putExtra(StepDetailFragment.ARG_RECIPE, recipe);
                        intent.putExtra(StepDetailFragment.ARG_STEP_NUMBER, position-1);

                        context.startActivity(intent);
                    }
                });
            }
        }

        class IngredientsViewHolder extends ViewHolder {
            @BindView(R.id.ingredients_tv)
            TextView ingredientsTV;

            IngredientsViewHolder(View view) {
                super(view);
            }

            @Override
            protected void bind(Recipe recipe, int position) {
                ingredientsTV.setText(recipe.getIngredientsString());
            }
        }
    }
}
