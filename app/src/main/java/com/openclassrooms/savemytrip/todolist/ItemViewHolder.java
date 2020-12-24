package com.openclassrooms.savemytrip.todolist;

import android.graphics.Paint;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.openclassrooms.savemytrip.models.Item;
import com.openclassrooms.savemytrip.R;

import java.io.File;
import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    @BindView(R.id.activity_todo_list_item_text)
    TextView mTextView;
    @BindView(R.id.activity_todo_list_item_image)
    ImageView mImageView;
    @BindView(R.id.activity_todo_list_item_picture)
    ImageView mPictureView;
    @BindView(R.id.activity_todo_list_item_remove)
    ImageButton mImageButton;
    @BindView(R.id.activity_todo_list_item_share)
    ImageButton mShareButton;
    @BindView(R.id.activity_todo_list_item)
    View mItemView;

    private WeakReference<ItemAdapter.Listener> callbackRef;

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void updateWithItem(Item item, ItemAdapter.Listener callback, RequestManager glide){
        callbackRef = new WeakReference<ItemAdapter.Listener>(callback);
        mTextView.setText(item.getText());
        mImageButton.setOnClickListener(this);
        mShareButton.setOnClickListener(this);
        mItemView.setOnClickListener(v -> {
            if(callback!=null) callback.onClickItem(getAdapterPosition());
        });
        switch (item.getCategory()){
            case 0: // To visit
                mImageView.setImageResource(R.drawable.ic_room_black_24px);
                break;
            case 1: // Ideas
                mImageView.setImageResource(R.drawable.ic_lightbulb_outline_black_24px);
                break;
            case 2: // Restaurants
                mImageView.setImageResource(R.drawable.ic_local_cafe_black_24px);
                break;
        }

        if(item.getPictureUri()!=null) {
            mPictureView.setVisibility(View.VISIBLE);
            mShareButton.setVisibility(View.VISIBLE);
            Uri uri = Uri.parse(item.getPictureUri());
            //mPictureView.setImageURI(uri);
            glide.load(new File(item.getPictureUri())).apply(RequestOptions.circleCropTransform()).into(mPictureView);
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
        switch (v.getId()){
            case R.id.activity_todo_list_item_remove:
                if(callback!=null) callback.onClickDeleteButton(getAdapterPosition());
                break;
            case R.id.activity_todo_list_item_share:
                if(callback!=null) callback.onClickShareButton(getAdapterPosition());
                break;
        }
    }
}
