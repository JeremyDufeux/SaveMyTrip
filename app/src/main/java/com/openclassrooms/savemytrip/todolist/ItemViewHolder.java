package com.openclassrooms.savemytrip.todolist;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.openclassrooms.savemytrip.Models.Item;
import com.openclassrooms.savemytrip.R;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    @BindView(R.id.activity_todo_list_item_text)
    TextView mTextView;
    @BindView(R.id.activity_todo_list_item_image)
    ImageView mImageView;
    @BindView(R.id.activity_todo_list_item_remove)
    ImageButton mImageButton;

    private WeakReference<ItemAdapter.Listener> callbackRef;

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void updateWithItem(Item item, ItemAdapter.Listener callback){
        callbackRef = new WeakReference<ItemAdapter.Listener>(callback);
        mTextView.setText(item.getText());
        mImageButton.setOnClickListener(this);
        switch (item.getCategory()){
            case 0: // To visit
                mImageView.setBackgroundResource(R.drawable.ic_room_black_24px);
                break;
            case 1: // Ideas
                mImageView.setBackgroundResource(R.drawable.ic_lightbulb_outline_black_24px);
                break;
            case 2: // Restaurants
                mImageView.setBackgroundResource(R.drawable.ic_local_cafe_black_24px);
                break;
        }
        if(item.isSelected()){
            mTextView.setPaintFlags(mTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            mTextView.setPaintFlags(mTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    @Override
    public void onClick(View v) {
        ItemAdapter.Listener callback = callbackRef.get();
        if(callback!=null) callback.onClickDeleteButton(getAdapterPosition());
    }
}
