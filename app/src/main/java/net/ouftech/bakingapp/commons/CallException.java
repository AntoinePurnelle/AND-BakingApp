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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by antoine.purnelle@ouftech.net on 13-03-18.
 */

public class CallException extends Exception {

    @NonNull
    private String getLotTag() {
        return "CallException";
    }

    private int code;
    private String message;
    private String url;
    @Nullable
    private String errorBodyString;

    public CallException(int code, String message, @Nullable ResponseBody errorBody, Call call) {
        this.code = code;
        this.message = message;

        try {
            if (errorBody != null)
                errorBodyString = errorBody.string();
        } catch (IOException e) {
            Logger.w(getLotTag(), String.format("Cannot build errorBodyString based on errorBody %s", errorBody), e, false);
        }

        url = call.request().url().toString();
    }

    @Override
    public String toString() {
        return "\n CallException{" +
                "\n  code= " + code +
                ",\n  message= '" + message + '\'' +
                ",\n  for url= '" + url + '\'' +
                ",\n  errorBodyString= " + errorBodyString +
                "\n }\n";
    }
}
