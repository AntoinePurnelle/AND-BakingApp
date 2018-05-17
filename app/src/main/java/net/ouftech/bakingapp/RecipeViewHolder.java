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

package  net.ouftech.bakingapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import net.ouftech.bakingapp.commons.CollectionUtils;
import net.ouftech.bakingapp.model.Recipe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.recipe_item_title_tv)
    protected TextView titleTV;
    @BindView(R.id.steps_tv)
    protected TextView stepsTV;
    @BindView(R.id.servings_tv)
    protected TextView servingsTV;

    private Recipe recipe;

    public RecipeViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(Recipe recipe, RecipesAdapter.OnItemClickListener listener) {
        titleTV.setText(recipe.name);
        stepsTV.setText(itemView.getContext().getResources().getQuantityString(R.plurals.steps, CollectionUtils.getSize(recipe.steps), CollectionUtils.getSize(recipe.steps)));
        servingsTV.setText(String.valueOf(recipe.servings));

        if (listener != null)
            itemView.setOnClickListener(v -> listener.onItemClick(recipe));
    }
}
