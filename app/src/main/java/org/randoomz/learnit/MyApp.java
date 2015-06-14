package org.randoomz.learnit;

import android.app.Application;
import android.content.Context;

import dagger.ObjectGraph;
import timber.log.Timber;

/**
 * Created by gerard on 11/06/2015.
 */
public class MyApp extends Application {
  private ObjectGraph objectGraph;

  @Override public void onCreate() {
    super.onCreate();

    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    }

    objectGraph = ObjectGraph.create(new MyModule(this));
  }

  public static ObjectGraph objectGraph(Context context) {
    return ((MyApp) context.getApplicationContext()).objectGraph;
  }
}
