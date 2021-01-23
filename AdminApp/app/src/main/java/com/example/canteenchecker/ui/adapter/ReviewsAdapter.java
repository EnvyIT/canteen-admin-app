package com.example.canteenchecker.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.example.canteenchecker.adminapp.R;
import com.example.canteenchecker.common.StringUtils;
import com.example.canteenchecker.core.ReviewData;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.SneakyThrows;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

  private final List<ReviewData> reviewData = new ArrayList<>();

  @NonNull
  @Override
  public ReviewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
    return new ViewHolder(view);
  }

  @SneakyThrows
  @Override
  public void onBindViewHolder(@NonNull ReviewsAdapter.ViewHolder holder, int position) {
    holder.updateView(reviewData.get(position));
  }

  @Override
  public int getItemCount() {
    return reviewData.size();
  }

  public void remove(int position) {
    reviewData.remove(position);
    notifyItemRemoved(position);
  }

  public void display(Collection<ReviewData> newReviewData) {
    reviewData.clear();
    if (newReviewData != null) {
      reviewData.addAll(newReviewData);
    }
    notifyDataSetChanged();
  }

  public ReviewData get(int position) {
    return reviewData.get(position);
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    private TextView textViewUserName;
    private TextView textViewDateTime;
    private TextView textViewRating;
    private RatingBar ratingBarAverageRating;
    private TextView textRemark;

    public RelativeLayout viewBackground;
    public ConstraintLayout viewForeground;

    ViewHolder(@NonNull View itemView) {
      super(itemView);
      textViewUserName = itemView.findViewById(R.id.textViewUserName);
      textViewDateTime = itemView.findViewById(R.id.textViewDateTime);
      textViewRating = itemView.findViewById(R.id.textViewRating);
      ratingBarAverageRating = itemView.findViewById(R.id.ratingBarAverageRating);
      textRemark = itemView.findViewById(R.id.textRemark);
      viewBackground = itemView.findViewById(R.id.viewBackground);
      viewForeground = itemView.findViewById(R.id.viewForeground);
    }

    void updateView(final ReviewData reviewData) throws ParseException {
      textViewUserName.setText(reviewData.getCreator());
      textViewDateTime.setText(StringUtils.toFormattedDate(reviewData.getCreationDate()));
      textViewRating.setText(String.valueOf(reviewData.getRating()));
      ratingBarAverageRating.setRating(reviewData.getRating());
      textRemark.setText(reviewData.getRemark());
    }
  }

}
