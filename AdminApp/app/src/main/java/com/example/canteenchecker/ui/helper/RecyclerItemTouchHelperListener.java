package com.example.canteenchecker.ui.helper;

import androidx.recyclerview.widget.RecyclerView;

public interface RecyclerItemTouchHelperListener {

  void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);

}
