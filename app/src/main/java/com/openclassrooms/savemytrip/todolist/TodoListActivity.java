package com.openclassrooms.savemytrip.todolist;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.openclassrooms.savemytrip.utils.StorageUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class TodoListActivity extends BaseActivity implements ItemAdapter.Listener {
    private static final int RC_STORAGE_READ_PERMS = 200;

    // For data
    private ItemViewModel itemViewModel;
    private ItemAdapter itemAdapter;
    private static int USER_ID = 1;
    private String pictureUri = null;

    // FOR DESIGN
    @BindView(R.id.todo_list_activity_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.todo_list_activity_spinner) Spinner spinner;
    @BindView(R.id.todo_list_activity_edit_text) EditText editText;
    @BindView(R.id.todo_list_activity_load_picture) ImageView loadImage;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            pictureUri = data.getData().toString();
            Glide.with(this).load(pictureUri).apply(RequestOptions.circleCropTransform()).into(loadImage);
        }
    }

    // -------------------
    // ACTIONS
    // -------------------

    @OnClick(R.id.todo_list_activity_button_add)
    public void onClickAddButton() {
        this.createItem();
    }

    @OnClick(R.id.todo_list_activity_load_picture)
    public void onClickLoadPicture(){
        this.loadPicture();
    }

    @Override
    public void onClickDeleteButton(int position) {
        this.deleteItem(this.itemAdapter.getItem(position));
    }

    @Override
    public void onClickShareButton(int position) {
        sharePicture(this.itemAdapter.getItem(position));
    }

    @AfterPermissionGranted(RC_STORAGE_READ_PERMS)
    private void loadPicture(){
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(this, getString(R.string.title_permission), RC_STORAGE_READ_PERMS, Manifest.permission.READ_EXTERNAL_STORAGE);
            return;
        }

        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);
    }

    private void sharePicture(Item item) { // TODO
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("image/*");

        File fileToShare = new File(item.getPictureUri());
        sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(fileToShare));
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.trip_book_share)));
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
        if(pictureUri!=null) {
            item.setPictureUri(pictureUri.toString());
            pictureUri = null;
        }
        loadImage.setImageResource(R.drawable.picture);
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
        itemAdapter = new ItemAdapter(this, Glide.with(this));
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
