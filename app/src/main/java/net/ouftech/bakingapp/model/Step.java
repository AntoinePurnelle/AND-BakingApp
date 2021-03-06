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
public class Step implements Parcelable {
    public static final String ID_KEY = "id";
    public static final String SHORT_DESCRIPTION_KEY = "shortDescription";
    public static final String DESCRIPTION_KEY = "description";
    public static final String VIDEO_URL_KEY = "videoURL";
    public static final String THUMBNAIL_URL_KEY = "thumbnailURL";


    @PrimaryKey(autoincrement = true)
    int _id;
    @SerializedName(ID_KEY)
    @Column
    public int stepId;
    @Column
    @SerializedName(SHORT_DESCRIPTION_KEY)
    public String shortDescription;
    @Column
    @SerializedName(DESCRIPTION_KEY)
    public String description;
    @Column
    @SerializedName(VIDEO_URL_KEY)
    public String videoUrl;
    @Column
    @SerializedName(THUMBNAIL_URL_KEY)
    public String thumbnailUrl;

    @ForeignKey(stubbedRelationship = true)
    public Recipe recipe;

    public Step() {
    }

    protected Step(Parcel in) {
        stepId = in.readInt();
        shortDescription = in.readString();
        description = in.readString();
        videoUrl = in.readString();
        thumbnailUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(stepId);
        dest.writeString(shortDescription);
        dest.writeString(description);
        dest.writeString(videoUrl);
        dest.writeString(thumbnailUrl);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Step> CREATOR = new Parcelable.Creator<Step>() {
        @Override
        public Step createFromParcel(Parcel in) {
            return new Step(in);
        }

        @Override
        public Step[] newArray(int size) {
            return new Step[size];
        }
    };
}