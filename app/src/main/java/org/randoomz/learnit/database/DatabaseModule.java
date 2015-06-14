package org.randoomz.learnit.database;

import android.app.Application;
import android.database.sqlite.SQLiteOpenHelper;

import com.squareup.sqlbrite.SqlBrite;

import org.randoomz.learnit.BuildConfig;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

/**
 * Created by gerard on 11/06/2015.
 */
@Module(complete = false, library = true)
public class DatabaseModule {
  @Provides @Singleton SQLiteOpenHelper provideOpenHelper(Application application) {
    return new DatabaseOpenHelper(application);
  }

  @Provides @Singleton SqlBrite provideSqlBrite(SQLiteOpenHelper openHelper) {
    SqlBrite db = SqlBrite.create(openHelper);

    if (BuildConfig.DEBUG) {
      db.setLogger(new SqlBrite.Logger() {
        @Override public void log(String message) {
          Timber.tag("Database").v(message);
        }
      });
      db.setLoggingEnabled(true);
    }

    return db;
  }
}
