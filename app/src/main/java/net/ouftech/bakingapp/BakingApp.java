package net.ouftech.bakingapp;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.raizlabs.android.dbflow.config.DatabaseConfig;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import net.ouftech.bakingapp.model.BakingAppDatabase;

import io.fabric.sdk.android.Fabric;

public class BakingApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());

        FlowManager.init(FlowConfig.builder(this)
                .addDatabaseConfig(DatabaseConfig.builder(BakingAppDatabase.class)
                        .databaseName("BakingAppDatabase")
                        .build())
                .build());
    }
}
