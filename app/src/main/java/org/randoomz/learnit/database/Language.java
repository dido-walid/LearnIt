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
public abstract class Language implements Parcelable {
  public static final String TABLE = "language";

  public static final String ID = "_id";
  public static final String LANGUAGE = "language";

  public static String ALL_QUERY = ""
      + "SELECT *"
      + " FROM " + TABLE;

  public abstract long id();

  public abstract String language();

  public static Func1<SqlBrite.Query, List<Language>> MAP = new Func1<SqlBrite.Query, List<Language>>() {
    @Override public List<Language> call(SqlBrite.Query query) {
      Cursor cursor = query.run();
      try {
        List<Language> values = new ArrayList<>(cursor.getCount());

        while (cursor.moveToNext()) {
          values.add(new AutoParcel_Language(
              DbUtils.getLong(cursor, ID),
              DbUtils.getString(cursor, LANGUAGE)
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

    public Builder language(String language) {
      values.put(LANGUAGE, language);
      return this;
    }

    public ContentValues build() {
      return values;
    }
  }
}
