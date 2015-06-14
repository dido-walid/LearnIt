package org.randoomz.learnit.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.randoomz.learnit.BuildConfig;

/**
 * Created by gerard on 11/06/2015.
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {
  private static final int VERSION = 1;

  private static final String CREATE_LANGUAGE = ""
      + "CREATE TABLE " + Language.TABLE + " ("
      + Language.ID + " INTEGER NOT NULL PRIMARY KEY,"
      + Language.LANGUAGE + " TEXT NOT NULL"
      + ")";

  private static final String CREATE_VOCABULARY = ""
      + "CREATE TABLE " + Vocabulary.TABLE + " ("
      + Vocabulary.ID + " INTEGER NOT NULL PRIMARY KEY,"
      + Vocabulary.ORIGINAL + " TEXT NOT NULL,"
      + Vocabulary.TRANSLATED + " TEXT NOT NULL,"
      + Vocabulary.LANG_ORIGINAL + " TEXT NOT NULL,"
      + Vocabulary.LANG_TRANSLATED + " TEXT NOT NULL"
      + ")";

  public DatabaseOpenHelper(Context context) {
    super(context, "zds.db", null, VERSION);
  }

  @Override public void onCreate(SQLiteDatabase db) {
    db.execSQL(CREATE_LANGUAGE);
    db.execSQL(CREATE_VOCABULARY);

    if (BuildConfig.DEBUG) {
      db.insert(Language.TABLE, null, new Language.Builder()
          .id(1)
          .language("English")
          .build());

      db.insert(Language.TABLE, null, new Language.Builder()
          .id(2)
          .language("Spanish")
          .build());

      db.insert(Vocabulary.TABLE, null, new Vocabulary.Builder()
          .id(1)
          .original("Hello")
          .translated("Bonjour")
          .langOriginal("English")
          .langTranslated("French")
          .build());

      db.insert(Vocabulary.TABLE, null, new Vocabulary.Builder()
          .id(2)
          .original("Fourbe")
          .translated("Deceitful")
          .langOriginal("French")
          .langTranslated("English")
          .build());

      db.insert(Vocabulary.TABLE, null, new Vocabulary.Builder()
          .id(3)
          .original("Français")
          .translated("francés")
          .langOriginal("French")
          .langTranslated("Spanish")
          .build());
    }
  }

  @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
  }
}
