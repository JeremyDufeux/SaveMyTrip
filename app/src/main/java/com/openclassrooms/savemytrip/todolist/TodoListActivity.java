package com.openclassrooms.savemytrip.todolist;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.openclassrooms.savemytrip.R;
import com.openclassrooms.savemytrip.base.BaseActivity;
import com.openclassrooms.savemytrip.injections.Injection;
import com.openclassrooms.savemytrip.injections.ViewModelFactory;
import com.openclassrooms.savemytrip.models.Item;
import com.openclassrooms.savemytrip.models.User;
import com.openclassrooms.savemytrip.utils.ItemClickSupport;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class TodoListActivity extends BaseActivity implements ItemAdapter.Listener {

    // For data
    private ItemViewModel itemViewModel;
    private ItemAdapter itemAdapter;
    private static int USER_ID = 1;

    // FOR DESIGN
    @BindView(R.id.todo_list_activity_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.todo_list_activity_spinner) Spinner spinner;
    @BindView(R.id.todo_list_activity_edit_text) EditText editText;
    @BindView(R.id.todo_list_activity_header_profile_image) ImageView profileImage;
    @BindView(R.id.todo_list_activity_header_profile_text) TextView profileText;

    @Override
    public int getLayoutContentViewID() { return R.layout.activity_todo_list; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configureToolbar();
        this.configureSpinner();
        this.configureRecyclerView();
        this.configureViewModel();
        this.getCurrentUser(USER_ID);
        this.getItems(USER_ID);
    }

    // -------------------
    // ACTIONS
    // -------------------

    @OnClick(R.id.todo_list_activity_button_add)
    public void onClickAddButton() {
        this.createItem();
    }

    @Override
    public void onClickDeleteButton(int position) {
        this.deleteItem(this.itemAdapter.getItem(position));
    }

    // -------------------
    // Data
    // -------------------

    private void configureViewModel(){
        ViewModelFactory mViewModelFactory = Injection.provideViewModelFactory(this);
        this.itemViewModel = ViewModelProviders.of(this, mViewModelFactory).get(ItemViewModel.class);
        this.itemViewModel.init(USER_ID);
    }

    private void getCurrentUser(int userId){
        this.itemViewModel.getUser(userId).observe(this, this::updateHeader);
    }

    private void getItems(int userId){
        this.itemViewModel.getItems(userId).observe(this, this::updateItemList);
    }

    private void createItem(){
        Item item = new Item(editText.getText().toString(), spinner.getSelectedItemPosition(), USER_ID);
        editText.setText("");
        itemViewModel.createItem(item);
    }

    private void updateItem(Item item){
        item.setSelected(!item.isSelected());
        this.itemViewModel.updateItem(item);
    }

    private void deleteItem(Item item){
        this.itemViewModel.deleteItem(item.getId());
    }

    // -------------------
    // UI
    // -------------------

    private void configureRecyclerView(){
        itemAdapter = new ItemAdapter(this);
        recyclerView.setAdapter(itemAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemClickSupport.addTo(recyclerView, R.layout.activity_todo_list_item)
                .setOnItemClickListener((recyclerView, position, v)
                        -> updateItem(itemAdapter.getItem(position)));
    }

    private void updateHeader(User user){
        profileText.setText(user.getUserName());
        Glide.with(this).load(user.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(profileImage);
    }

    private void updateItemList(List<Item> items){
        itemAdapter.updateData(items);
    }

    private void configureSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

}
