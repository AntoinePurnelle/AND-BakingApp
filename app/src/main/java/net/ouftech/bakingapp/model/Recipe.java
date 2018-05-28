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

package net.ouftech.bakingapp.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import net.ouftech.bakingapp.commons.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Table(database = BakingAppDatabase.class)
public class Recipe extends BaseModel implements Parcelable {
    public static final String ID_KEY = "id";
    public static final String NAME_KEY = "name";
    public static final String INGREDIENTS_KEY = "ingredients";
    public static final String STEPS_KEY = "steps";
    public static final String SERVINGS_KEY = "servings";
    public static final String THUMBNAIL_URL_KEY = "image";

    @PrimaryKey
    @SerializedName(ID_KEY)
    public int id;
    @Column
    @SerializedName(NAME_KEY)
    public String name;
    @Column
    @SerializedName(SERVINGS_KEY)
    public int servings;
    @Column
    @SerializedName(THUMBNAIL_URL_KEY)
    public String thumbnailUrl;

    @SerializedName(INGREDIENTS_KEY)
    public List<Ingredient> ingredients;

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "ingredients")
    public List<Ingredient> getIngredients() {
        if (CollectionUtils.isEmpty(ingredients)) {
            ingredients = SQLite.select()
                    .from(Ingredient.class)
                    .where(Ingredient_Table.recipe_id.eq(id))
                    .queryList();
        }
        return ingredients;
    }

    @SerializedName(STEPS_KEY)
    public List<Step> steps;

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "steps")
    public List<Step> getSteps() {
        if (CollectionUtils.isEmpty(steps)) {
            steps = SQLite.select()
                    .from(Step.class)
                    .where(Step_Table.recipe_id.eq(id))
                    .queryList();
        }
        return steps;
    }

    public Recipe() {
    }

    public void initWithChildren() {

        for (Ingredient ingredient : ingredients) {
            ingredient.recipe = this;
        }

        for (Step step : steps) {
            step.recipe = this;
        }
    }

    protected Recipe(Parcel in) {
        id = in.readInt();
        name = in.readString();
        if (in.readByte() == 0x01) {
            ingredients = new ArrayList<>();
            in.readList(ingredients, Ingredient.class.getClassLoader());
        } else {
            ingredients = null;
        }
        if (in.readByte() == 0x01) {
            steps = new ArrayList<>();
            in.readList(steps, Step.class.getClassLoader());
        } else {
            steps = null;
        }
        servings = in.readInt();
        thumbnailUrl = in.readString();
    }

    public String getIngredientsString() {
        if (ingredients.size() == 1)
            return ingredients.get(0).toString();

        return "\u2022 " + TextUtils.join("\n\u2022 ", ingredients);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        if (ingredients == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(ingredients);
        }
        if (steps == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(steps);
        }
        dest.writeInt(servings);
        dest.writeString(thumbnailUrl);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}