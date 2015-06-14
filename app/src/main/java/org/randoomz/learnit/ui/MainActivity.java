package org.randoomz.learnit.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.EditText;

import com.squareup.sqlbrite.SqlBrite;

import org.randoomz.learnit.MyApp;
import org.randoomz.learnit.R;
import org.randoomz.learnit.database.Language;
import org.randoomz.learnit.database.Vocabulary;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import de.psdev.licensesdialog.LicensesDialog;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {
  public static final String CURRENT_LANGUAGE = "current_pos";

  private List<Language> languages = Collections.emptyList();
  private int currentPosition = 0;

  private LanguageSelectedListener listener;
  private Subscription subscribe;

  private DrawerLayout drawerLayout;
  private NavigationView navigationView;

  @Inject SqlBrite db;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    MyApp.objectGraph(this).inject(this);
    setContentView(R.layout.activity_main);

    drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

    if (savedInstanceState == null) {
      MainActivityFragment fragment = MainActivityFragment.newInstance();
      getSupportFragmentManager().beginTransaction()
          .add(R.id.content, fragment)
          .commit();
      listener = fragment;
    }

    navigationView = (NavigationView) findViewById(R.id.nav_view);
    if (navigationView != null) {
      setupDrawerContent(navigationView);
    }
  }

  @Override protected void onResume() {
    super.onResume();
    subscribe = db.createQuery(Language.TABLE, Language.ALL_QUERY)
        .map(Language.MAP)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<List<Language>>() {
          @Override public void call(List<Language> languages) {
            MainActivity.this.languages = languages;
            final MenuItem item = navigationView.getMenu().findItem(R.id.languages);
            final SubMenu subMenuLanguages = item.getSubMenu();
            subMenuLanguages.clear();
            for (int i = 0; i < languages.size(); i++) {
              subMenuLanguages.add(Menu.NONE, i, i, languages.get(i).language());
            }
            // TODO https://code.google.com/p/android/issues/detail?id=176300
            item.setTitle(item.getTitle());
            if (!languages.isEmpty()) {
              listener.onLanguageSelected(languages.get(currentPosition));
              subMenuLanguages.getItem(currentPosition).setChecked(true);
            }
          }
        });
  }

  @Override protected void onPause() {
    super.onPause();
    subscribe.unsubscribe();
  }

  @Override public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
    super.onSaveInstanceState(outState, outPersistentState);
    outState.putInt(CURRENT_LANGUAGE, currentPosition);
  }

  @Override protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    currentPosition = savedInstanceState.getParcelable(CURRENT_LANGUAGE);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.activity_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        drawerLayout.openDrawer(GravityCompat.START);
        return true;
      case R.id.remove:
        if (currentPosition < 0) {
          return false;
        }
        final String language = languages.get(currentPosition).language();
        db.delete(Vocabulary.TABLE, Vocabulary.LANG_TRANSLATED + " = ?", language);
        db.delete(Language.TABLE, Language.LANGUAGE + " = ?", language);
        currentPosition = 0;
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void setupDrawerContent(NavigationView navigationView) {
    navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
          case R.id.new_language:
            final LayoutInflater layoutInflater = getLayoutInflater();
            final View inflate = layoutInflater.inflate(R.layout.dialog_new_language, null);
            final EditText etLanguage = (EditText) inflate.findViewById(R.id.et_language);
            new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.new_language)
                .setView(inflate)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                  @Override public void onClick(DialogInterface dialog, int which) {
                    currentPosition = languages.size();
                    db.insert(Language.TABLE, new Language.Builder()
                        .language(etLanguage.getText().toString().trim())
                        .build());
                  }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
            break;
          case R.id.license:
            new LicensesDialog.Builder(MainActivity.this)
                .setNotices(R.raw.notices)
                .build()
                .show();
            break;
          default:
            if (listener != null) {
              MainActivity.this.currentPosition = menuItem.getItemId();
              final Language current = MainActivity.this.languages.get(MainActivity.this.currentPosition);
              listener.onLanguageSelected(current);
            }
            menuItem.setChecked(true);
        }
        drawerLayout.closeDrawers();
        return true;
      }
    });
  }

  interface LanguageSelectedListener {
    void onLanguageSelected(Language current);
  }
}
