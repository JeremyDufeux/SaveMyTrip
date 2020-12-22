package com.openclassrooms.savemytrip.todolist;

import android.content.Context;
import android.support.annotation.NonNull;
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
    private RequestManager glide;

    private List<Item> items;

    public ItemAdapter(Listener callback, RequestManager glide) {
        this.callback = callback;
        this.glide = glide;
        items = new ArrayList<>();
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
        itemViewHolder.updateWithItem(items.get(i), callback, glide);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public Item getItem(int position){
        return items.get(position);
    }

    public void updateData(List<Item> items){
        this.items = items;
        notifyDataSetChanged();
    }
}
