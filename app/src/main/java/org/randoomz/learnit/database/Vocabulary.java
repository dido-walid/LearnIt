package org.randoomz.learnit.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcelable;

import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import auto.parcel.AutoParcel;
import rx.functions.Func1;

/**
 * Created by gerard on 11/06/2015.
 */
@AutoParcel
public abstract class Vocabulary implements Parcelable {
  public static final String TABLE = "vocabulary";

  public static final String ID = "_id";
  public static final String ORIGINAL = "original";
  public static final String TRANSLATED = "translated";
  public static final String LANG_ORIGINAL = "lang_original";
  public static final String LANG_TRANSLATED = "lang_translated";

  public static String ALL_QUERY = ""
      + "SELECT *"
      + " FROM " + TABLE;

  public abstract long id();

  public abstract String original();

  public abstract String translated();

  public abstract String langOriginal();

  public abstract String langTranslated();

  public static Func1<SqlBrite.Query, List<Vocabulary>> MAP = new Func1<SqlBrite.Query, List<Vocabulary>>() {
    @Override public List<Vocabulary> call(SqlBrite.Query query) {
      Cursor cursor = query.run();
      try {
        List<Vocabulary> values = new ArrayList<>(cursor.getCount());

        while (cursor.moveToNext()) {
          values.add(new AutoParcel_Vocabulary(
              DbUtils.getLong(cursor, ID),
              DbUtils.getString(cursor, ORIGINAL),
              DbUtils.getString(cursor, TRANSLATED),
              DbUtils.getString(cursor, LANG_ORIGINAL),
              DbUtils.getString(cursor, LANG_TRANSLATED)
          ));
        }
        return values;
      } finally {
        cursor.close();
      }
    }
  };

  public static final Func1<SqlBrite.Query, List<Vocabulary>> MAP_DRAWER = new Func1<SqlBrite.Query, List<Vocabulary>>() {
    @Override public List<Vocabulary> call(SqlBrite.Query query) {
      Cursor cursor = query.run();
      try {
        List<Vocabulary> values = new ArrayList<>(cursor.getCount());

        while (cursor.moveToNext()) {
          values.add(new AutoParcel_Vocabulary(
              0,
              "",
              "",
              "",
              DbUtils.getString(cursor, LANG_TRANSLATED)
          ));
        }
        return values;
      } finally {
        cursor.close();
      }
    }
  };

  public static final class Builder {
    private final ContentValues values = new ContentValues();

    public Builder id(long id) {
      values.put(ID, id);
      return this;
    }

    public Builder original(String original) {
      values.put(ORIGINAL, original);
      return this;
    }

    public Builder translated(String translated) {
      values.put(TRANSLATED, translated);
      return this;
    }

    public Builder langOriginal(String langOriginal) {
      values.put(LANG_ORIGINAL, langOriginal);
      return this;
    }

    public Builder langTranslated(String langTranslated) {
      values.put(LANG_TRANSLATED, langTranslated);
      return this;
    }

    public ContentValues build() {
      return values;
    }
  }
}
