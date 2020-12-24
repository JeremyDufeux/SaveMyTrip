package com.openclassrooms.savemytrip.todolist;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.os.Environment.getExternalStoragePublicDirectory;
import static com.openclassrooms.savemytrip.utils.StorageUtils.saveFile;

public class TodoListActivity extends BaseActivity implements ItemAdapter.Listener {
    private static final int RC_STORAGE_READ_PERMS = 200;
    private static final int IMAGE_PICK_CODE = 1000;
    public static final String AUTHORITY = "com.openclassrooms.savemytrip.fileprovider";

    // For data
    private ItemViewModel itemViewModel;
    private ItemAdapter itemAdapter;
    private static int USER_ID = 1;
    private String pictureUri = null;

    // FOR DESIGN
    @BindView(R.id.todo_list_activity_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.todo_list_activity_spinner) Spinner spinner;
    @BindView(R.id.todo_list_activity_edit_text) EditText editText;
    @BindView(R.id.todo_list_activity_load_picture) ImageView loadImageView;
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

    @Override
    public void onClickItem(int position) {
        updateItem(itemAdapter.getItem(position));
    }

    @AfterPermissionGranted(RC_STORAGE_READ_PERMS)
    private void loadPicture(){
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(this, getString(R.string.title_permission), RC_STORAGE_READ_PERMS, Manifest.permission.READ_EXTERNAL_STORAGE);
            return;
        }

        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {

            Uri uri = data.getData();
            String fileName = new File(uri.toString()).getName()+".jpg";
            String folder = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()+File.separator+"SaveMyTrip";
            pictureUri = folder+File.separator+fileName;
            saveFile(this, fileName, uri, folder, fileName);
            Glide.with(this).load(pictureUri).apply(RequestOptions.circleCropTransform()).into(loadImageView);
        }
    }

    private void sharePicture(Item item) { // TODO
        File file = new File(item.getPictureUri());
        Log.d("Debug", "sharePicture: file.getAbsolutePath() : " + file.getAbsolutePath());
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, item.getPictureUri());
        shareIntent.setType("image/jpg");
        startActivity(Intent.createChooser(shareIntent, "Share Image"));
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
            item.setPictureUri(pictureUri);
            pictureUri = null;
        }

        loadImageView.setImageResource(R.drawable.picture);
        editText.setText("");
        itemViewModel.createItem(item);
    }

    private void updateItem(Item item){
        this.itemViewModel.updateItemSelection(item.getId(), !item.isSelected());
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
