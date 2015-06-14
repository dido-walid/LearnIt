package org.randoomz.learnit.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;

import org.randoomz.learnit.R;
import org.randoomz.learnit.database.Vocabulary;

import java.util.Collections;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by gerard on 11/06/2015.
 */
public class ListVocabularyAdapter extends RecyclerView.Adapter<ListVocabularyAdapter.ViewHolder>
    implements Action1<List<Vocabulary>> {
  private final VocabularySwiped listener;
  private List<Vocabulary> vocabularies = Collections.emptyList();

  public ListVocabularyAdapter(VocabularySwiped listener) {
    this.listener = listener;
  }

  @Override public void call(List<Vocabulary> vocabularies) {
    this.vocabularies = vocabularies;
    notifyDataSetChanged();
  }

  @Override public ListVocabularyAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_vocabulary, viewGroup, false);
    return new ViewHolder(inflate);
  }

  @Override public void onBindViewHolder(ListVocabularyAdapter.ViewHolder viewHolder, final int i) {
    final Vocabulary vocabulary = vocabularies.get(i);
    viewHolder.tvOriginal.setText(vocabulary.original());
    viewHolder.tvTranslated.setText(vocabulary.translated());
    viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
    viewHolder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
      @Override public void onStartOpen(SwipeLayout swipeLayout) {
      }

      @Override public void onOpen(SwipeLayout swipeLayout) {
        listener.onVocabularySwiped(vocabulary);
      }

      @Override public void onStartClose(SwipeLayout swipeLayout) {
      }

      @Override public void onClose(SwipeLayout swipeLayout) {
      }

      @Override public void onUpdate(SwipeLayout swipeLayout, int i, int i1) {
      }

      @Override public void onHandRelease(SwipeLayout swipeLayout, float v, float v1) {
      }
    });
  }

  @Override public int getItemCount() {
    return vocabularies.size();
  }

  public final class ViewHolder extends RecyclerView.ViewHolder {
    SwipeLayout swipeLayout;
    TextView tvOriginal;
    TextView tvTranslated;

    public ViewHolder(View itemView) {
      super(itemView);
      swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
      tvOriginal = (TextView) itemView.findViewById(R.id.tv_original);
      tvTranslated = (TextView) itemView.findViewById(R.id.tv_translated);
    }
  }

  public interface VocabularySwiped {
    void onVocabularySwiped(Vocabulary vocabulary);
  }
}
