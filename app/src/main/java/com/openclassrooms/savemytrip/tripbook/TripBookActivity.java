package com.openclassrooms.savemytrip.tripbook;


import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.openclassrooms.savemytrip.R;
import com.openclassrooms.savemytrip.base.BaseActivity;
import com.openclassrooms.savemytrip.utils.StorageUtils;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class TripBookActivity extends BaseActivity {

    //FOR DESIGN
    @BindView(R.id.trip_book_activity_external_choice) LinearLayout linearLayoutExternalChoice;
    @BindView(R.id.trip_book_activity_internal_choice) LinearLayout linearLayoutInternalChoice;
    @BindView(R.id.trip_book_activity_radio_external) RadioButton radioButtonExternalChoice;
    @BindView(R.id.trip_book_activity_radio_public) RadioButton radioButtonExternalPublicChoice;
    @BindView(R.id.trip_book_activity_radio_volatile) RadioButton radioButtonInternalVolatileChoice;
    @BindView(R.id.trip_book_activity_edit_text) EditText editText;

    // File purposes
    private static final String FILE_NAME = "tripBook.txt";
    private static final String FOLDER_NAME = "booktrip";

    private static final int RC_STORAGE_WRITE_PERMS = 100;

    @Override
    public int getLayoutContentViewID() { return R.layout.activity_trip_book; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureToolbar();
        readFromStorage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                /*TODO*/
                return true;
            case R.id.action_save:
                save();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // --------------------
    // ACTIONS
    // --------------------

    @OnCheckedChanged({R.id.trip_book_activity_radio_internal, R.id.trip_book_activity_radio_external,
                       R.id.trip_book_activity_radio_private, R.id.trip_book_activity_radio_public,
                       R.id.trip_book_activity_radio_normal, R.id.trip_book_activity_radio_volatile})
    public void onClickRadioButton(CompoundButton button, boolean isChecked){
        Log.d("Debug", "onClickRadioButton");
        if (isChecked) {
            switch (button.getId()) {
                case R.id.trip_book_activity_radio_internal:
                    linearLayoutExternalChoice.setVisibility(View.GONE);
                    linearLayoutInternalChoice.setVisibility(View.VISIBLE);
                    break;
                case R.id.trip_book_activity_radio_external:
                    linearLayoutExternalChoice.setVisibility(View.VISIBLE);
                    linearLayoutInternalChoice.setVisibility(View.GONE);
                    break;
            }
        }
        readFromStorage();
    }

    private void save(){
        if(radioButtonExternalChoice.isChecked()){
            writeOnExternalStorage();
        } else {
            writeOnInternalStorage();
        }
    }

    // --------------------
    // Utils - Storage
    // --------------------

    @AfterPermissionGranted(RC_STORAGE_WRITE_PERMS)
    private void readFromStorage(){
        Log.d("Debug", "readFromStorage");
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(this, getString(R.string.title_permission), RC_STORAGE_WRITE_PERMS, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return;
        }
        if (radioButtonExternalChoice.isChecked()){
            if(StorageUtils.isExternalStorageReadable()){
                // External
                if(radioButtonExternalPublicChoice.isChecked()){
                    // Public
                    editText.setText(StorageUtils.getTextFromStorage(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), this, FILE_NAME, FOLDER_NAME));
                } else{
                    editText.setText(StorageUtils.getTextFromStorage(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), this, FILE_NAME, FOLDER_NAME));
                }
            }
        } else {
            if(radioButtonInternalVolatileChoice.isChecked()){
                editText.setText(StorageUtils.getTextFromStorage(getCacheDir(), this, FILE_NAME, FOLDER_NAME));
            } else {
                editText.setText(StorageUtils.getTextFromStorage(getFilesDir(), this, FILE_NAME, FOLDER_NAME));
            }
        }
    }

    private void writeOnExternalStorage(){
        Log.d("Debug", "writeOnExternalStorage");
        if(StorageUtils.isExternalStorageWritable()){
            if(radioButtonExternalPublicChoice.isChecked()){
                StorageUtils.setTextInStorage(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), this, FILE_NAME, FOLDER_NAME, editText.getText().toString());
            } else {
                StorageUtils.setTextInStorage(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), this, FILE_NAME, FOLDER_NAME, editText.getText().toString());
            }
        } else {
            Toast.makeText(this, getString(R.string.external_storage_impossible_create_file), Toast.LENGTH_SHORT).show();
        }
    }


    private void writeOnInternalStorage() {
        if(radioButtonInternalVolatileChoice.isChecked()){
            StorageUtils.setTextInStorage(getCacheDir(), this, FILE_NAME, FOLDER_NAME, editText.getText().toString());
        } else {
            StorageUtils.setTextInStorage(getFilesDir(), this, FILE_NAME, FOLDER_NAME, editText.getText().toString());
        }
    }
}
