package com.example.canteenchecker.ui.helper;

import android.graphics.Canvas;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.example.canteenchecker.ui.adapter.ReviewsAdapter;

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

  private RecyclerItemTouchHelperListener listener;

  public RecyclerItemTouchHelper(int dragDirs, int swipeDirs, RecyclerItemTouchHelperListener listener) {
    super(dragDirs, swipeDirs);
    this.listener = listener;
  }

  @Override
  public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull ViewHolder viewHolder, @NonNull ViewHolder target) {
    return true;
  }

  @Override
  public void onSwiped(@NonNull ViewHolder viewHolder, int direction) {
    listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
  }

  @Override
  public void onSelectedChanged(@Nullable ViewHolder viewHolder, int actionState) {
    if (viewHolder != null) {
      final View foregroundView = ((ReviewsAdapter.ViewHolder) viewHolder).viewForeground;
      getDefaultUIUtil().onSelected(foregroundView);
    }
  }

  @Override
  public void onChildDrawOver(Canvas canvas, RecyclerView recyclerView,
      RecyclerView.ViewHolder viewHolder, float dX, float dY,
      int actionState, boolean isCurrentlyActive) {
    final View foregroundView = ((ReviewsAdapter.ViewHolder) viewHolder).viewForeground;
    getDefaultUIUtil().onDrawOver(canvas, recyclerView, foregroundView, dX, dY,
        actionState, isCurrentlyActive);
  }

  @Override
  public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    final View foregroundView = ((ReviewsAdapter.ViewHolder) viewHolder).viewForeground;
    getDefaultUIUtil().clearView(foregroundView);
  }

  @Override
  public void onChildDraw(Canvas canvas, RecyclerView recyclerView,
      RecyclerView.ViewHolder viewHolder, float dX, float dY,
      int actionState, boolean isCurrentlyActive) {
    final View foregroundView = ((ReviewsAdapter.ViewHolder) viewHolder).viewForeground;

    getDefaultUIUtil().onDraw(canvas, recyclerView, foregroundView, dX, dY,
        actionState, isCurrentlyActive);
  }

}
