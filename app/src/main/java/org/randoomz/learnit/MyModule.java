package org.randoomz.learnit;

import android.app.Application;

import org.randoomz.learnit.database.DatabaseModule;
import org.randoomz.learnit.ui.UiModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by gerard on 11/06/2015.
 */
@dagger.Module(
    includes = {
        DatabaseModule.class,
        UiModule.class,
    }
)
public class MyModule {
  private final Application application;

  MyModule(Application application) {
    this.application = application;
  }

  @Provides @Singleton Application provideApplication() {
    return application;
  }
}
