package org.randoomz.learnit.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.squareup.sqlbrite.SqlBrite;

import org.randoomz.learnit.MyApp;
import org.randoomz.learnit.R;
import org.randoomz.learnit.database.Language;
import org.randoomz.learnit.database.Vocabulary;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivityFragment extends Fragment implements View.OnClickListener, ListVocabularyAdapter.VocabularySwiped, MainActivity.LanguageSelectedListener {

  static MainActivityFragment newInstance() {
    return new MainActivityFragment();
  }

  public static final String CURRENT_VOCABULARY = "current_voc";

  @Inject SqlBrite db;

  @InjectView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbar;
  @InjectView(R.id.toolbar) Toolbar toolbar;
  @InjectView(R.id.recycler_view) RecyclerView rvVocabularies;
  @InjectView(R.id.fab_add_vocabulary) FloatingActionButton fabAddVocabulary;

  private Subscription subscribe;
  private ListVocabularyAdapter adapter;
  private LinearLayoutManager layoutManager;
  private Language current;

  @Override public void onAttach(Context context) {
    super.onAttach(context);

    MyApp.objectGraph(context).inject(this);
    adapter = new ListVocabularyAdapter(this);
    layoutManager = new LinearLayoutManager(context);
    ((MainActivity) getActivity()).setLanguageSelectedListener(this);
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_main, container, false);
    ButterKnife.inject(this, view);
    return view;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    final AppCompatActivity activity = (AppCompatActivity) getActivity();
    activity.setSupportActionBar(toolbar);

    final ActionBar ab = activity.getSupportActionBar();
    if (ab != null) {
      ab.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
      ab.setDisplayHomeAsUpEnabled(true);
    }

    rvVocabularies.setHasFixedSize(true);
    rvVocabularies.setLayoutManager(layoutManager);
    rvVocabularies.setAdapter(adapter);
    fabAddVocabulary.setOnClickListener(this);
  }

  @Override
  public void onResume() {
    super.onResume();
    if (current != null) {
      onLanguageSelected(current);
    }
  }

  @Override public void onLanguageSelected(Language current) {
    this.current = current;
    collapsingToolbar.setTitle(current.language());
    subscribe = db.createQuery(Vocabulary.TABLE, Vocabulary.ALL_QUERY + " WHERE " + Vocabulary.LANG_TRANSLATED + " = ?", current.language())
        .map(Vocabulary.MAP)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(adapter);
  }

  @Override
  public void onPause() {
    super.onPause();
    subscribe.unsubscribe();
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (current != null) {
      outState.putParcelable(CURRENT_VOCABULARY, current);
    }
  }

  @Override public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    super.onViewStateRestored(savedInstanceState);
    if (savedInstanceState != null && savedInstanceState.containsKey(CURRENT_VOCABULARY)) {
      current = savedInstanceState.getParcelable(CURRENT_VOCABULARY);
    }
  }

  @Override public void onClick(View v) {
    if (current == null) {
      Snackbar.make(getView(), R.string.error_language_not_selected, Snackbar.LENGTH_SHORT).show();
      return;
    }
    final LayoutInflater layoutInflater = getActivity().getLayoutInflater();
    final View inflate = layoutInflater.inflate(R.layout.dialog_add_vocabulary, null);
    final EditText etOriginal = (EditText) inflate.findViewById(R.id.et_original);
    final EditText etTranslated = (EditText) inflate.findViewById(R.id.et_translated);
    new AlertDialog.Builder(getActivity())
        .setTitle(R.string.new_vocabulary)
        .setView(inflate)
        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            db.insert(Vocabulary.TABLE, new Vocabulary.Builder()
                .langOriginal("Not used for now.")
                .original(etOriginal.getText().toString().trim())
                .langTranslated(current.language())
                .translated(etTranslated.getText().toString().trim())
                .build());
          }
        })
        .setNegativeButton(R.string.cancel, null)
        .show();
  }

  @Override public void onVocabularySwiped(Vocabulary vocabulary) {
    db.delete(Vocabulary.TABLE, Vocabulary.ID + " = " + vocabulary.id());
  }
}
