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

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import net.ouftech.bakingapp.commons.CollectionUtils;
import net.ouftech.bakingapp.commons.Logger;
import net.ouftech.bakingapp.model.Recipe;
import net.ouftech.bakingapp.model.Step;

import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.recipe_item_title_tv)
    protected TextView titleTV;
    @BindView(R.id.steps_tv)
    protected TextView stepsTV;
    @BindView(R.id.servings_tv)
    protected TextView servingsTV;
    @Nullable
    @BindView(R.id.recipe_image_view)
    AppCompatImageView imageView;

    private RequestManager requestManager;

    public RecipeViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        this.requestManager = Glide.with(itemView.getContext());
    }

    public void bind(Recipe recipe, RecipesAdapter.OnItemClickListener listener) {
        titleTV.setText(recipe.name);
        stepsTV.setText(itemView.getContext().getResources().getQuantityString(R.plurals.steps, CollectionUtils.getSize(recipe.steps), CollectionUtils.getSize(recipe.steps)));
        servingsTV.setText(String.valueOf(recipe.servings));

        if (listener != null)
            itemView.setOnClickListener(v -> listener.onItemClick(recipe));

        initialiseImageView(recipe);
    }

    private void initialiseImageView(Recipe recipe) {

        if (imageView == null)
            return;

        if (!TextUtils.isEmpty(recipe.thumbnailUrl)) {
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setVisibility(View.VISIBLE);
            requestManager
                    .load(recipe.thumbnailUrl)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable>
                                target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(imageView);
        }
    }

    private String getLogTag() {
        return "RecipeViewHolder";
    }
}
