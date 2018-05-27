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

import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

@Table(database = BakingAppDatabase.class)
public class Ingredient implements Parcelable {
    public static final String QUANTITY_KEY = "quantity";
    public static final String MEASURE_KEY = "measure";
    public static final String NAME_KEY = "ingredient";


    @PrimaryKey(autoincrement = true)
    int id;
    @Column
    @SerializedName(NAME_KEY)
    public String name;
    @Column
    @SerializedName(QUANTITY_KEY)
    public float quantity;
    @Column
    @SerializedName(MEASURE_KEY)
    public String measure;

    @ForeignKey(stubbedRelationship = true)
    public Recipe recipe;

    public Ingredient() {
    }

    protected Ingredient(Parcel in) {
        name = in.readString();
        quantity = in.readFloat();
        measure = in.readString();
    }

    @Override
    public String toString() {
        return String.format("%s: %s %s", name, quantity, measure);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeFloat(quantity);
        dest.writeString(measure);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Ingredient> CREATOR = new Parcelable.Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };
}