package com.openclassrooms.savemytrip.todolist;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.openclassrooms.savemytrip.utils.StorageUtils.getFileFromStorage;
import static com.openclassrooms.savemytrip.utils.StorageUtils.saveFile;

public class TodoListActivity extends BaseActivity implements ItemAdapter.Listener {
    private static final int RC_STORAGE_READ_PERMS = 200;
    public static final String AUTHORITY = "com.openclassrooms.savemytrip.fileprovider";
    private static final int IMAGE_PICK_CODE = 1000;
    private static final String FOLDER = "item_pictures";

    // For data
    private ItemViewModel itemViewModel;
    private ItemAdapter itemAdapter;
    private static int USER_ID = 1;

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            getImageFromIntent(data);
        }
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
        createItem();
    }

    @OnClick(R.id.todo_list_activity_load_picture)
    public void onClickLoadPicture(){
        loadPicture(this);
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

        itemViewModel.insertItem(this, editText.getText().toString(), spinner.getSelectedItemPosition(), USER_ID, (String)loadImageView.getTag());

        loadImageView.setTag(null);
        loadImageView.setImageResource(R.drawable.picture);
        editText.setText("");
    }

    private void updateItem(Item item){
        this.itemViewModel.updateItemSelection(item.getId(), !item.isSelected());
    }

    private void deleteItem(Item item){
        this.itemViewModel.deleteItem(this, item);
    }

    private void getImageFromIntent(Intent data){
        Uri uri = data.getData();
        updateImageLoader(uri.toString());
    }


    @AfterPermissionGranted(RC_STORAGE_READ_PERMS)
    public void loadPicture(Context context){
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(this, context.getString(R.string.title_permission), RC_STORAGE_READ_PERMS, Manifest.permission.READ_EXTERNAL_STORAGE);
            return;
        }

        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    private void sharePicture(Item item) {
        Uri uri = Uri.parse(item.getPictureUri());
        String fileName = new File(uri.toString()).getName();
        File dest = getFilesDir();

        File image = getFileFromStorage(dest,this, fileName, FOLDER);
        Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), AUTHORITY, image);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_STREAM,  contentUri);
        shareIntent.setType("image/jpg");
        startActivity(Intent.createChooser(shareIntent, "Share Image"));
    }

    // -------------------
    // UI
    // -------------------

    private void configureRecyclerView(){
        itemAdapter = new ItemAdapter(this, Glide.with(this));
        recyclerView.setAdapter(itemAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void updateImageLoader(String pictureUri){
        Glide.with(this).load(pictureUri).apply(RequestOptions.circleCropTransform()).into(loadImageView);
        loadImageView.setTag(pictureUri);
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
