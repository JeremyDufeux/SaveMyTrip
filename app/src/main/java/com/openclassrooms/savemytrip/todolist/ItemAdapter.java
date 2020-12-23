package com.openclassrooms.savemytrip.todolist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.AsyncListDiffer;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.openclassrooms.savemytrip.models.Item;
import com.openclassrooms.savemytrip.R;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    public interface Listener {
        void onClickDeleteButton(int position);
        void onClickShareButton(int position);
    }
    private final Listener callback;
    private final RequestManager glide;

    AsyncListDiffer<Item> differ;

    public ItemAdapter(Listener callback, RequestManager glide) {
        this.callback = callback;
        this.glide = glide;
        differ = new AsyncListDiffer<>(this, new DifferCallback());
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_todo_list_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
        itemViewHolder.updateWithItem(differ.getCurrentList().get(i), callback, glide);
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    public Item getItem(int position){
        return differ.getCurrentList().get(position);
    }

    public void updateData(List<Item> items){
        differ.submitList(items);
    }

    public static class DifferCallback extends DiffUtil.ItemCallback<Item> {
        public boolean areItemsTheSame(Item oldItem, Item newItem) {
            return oldItem.getId() == newItem.getId();
        }

        public boolean areContentsTheSame(Item oldItem, @NonNull Item newItem) {
            return oldItem.getText().equals(newItem.getText());
        }
    }

}
