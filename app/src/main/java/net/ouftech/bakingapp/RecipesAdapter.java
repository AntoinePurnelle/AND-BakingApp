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

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.ouftech.bakingapp.commons.CollectionUtils;
import net.ouftech.bakingapp.model.Recipe;

import java.util.List;

public class RecipesAdapter extends android.support.v7.widget.RecyclerView.Adapter<RecipeViewHolder> {

    private List<Recipe> recipes;
    private OnItemClickListener onItemClickListener;

    public RecipesAdapter(List<Recipe> recipes, OnItemClickListener onItemClickListener) {
        this.recipes = recipes;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_list_item, parent, false);
        return new RecipeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        if (CollectionUtils.contains(recipes, position))
            holder.bind(recipes.get(position), onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return CollectionUtils.getSize(recipes);
    }

    public interface OnItemClickListener {
        void onItemClick(Recipe recipe);
    }
}
