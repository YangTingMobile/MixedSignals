package com.fredrick.mixedsignals;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.fredrick.mixedsignals.database.DatabaseAdapter;
import com.fredrick.mixedsignals.model.BlockItem;
import com.fredrick.mixedsignals.model.ContactItem;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;
import com.hitomi.cmlibrary.OnMenuStatusChangeListener;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.lsv_block_users)
    ListView lsvUsers;
    @BindView(R.id.sv_main_search)
    SearchView schUsers;
    @BindView(R.id.iv_main_logo)
    ImageView ivLogo;
    @BindView(R.id.iv_main_search)
    ImageView ivSearch;
    @BindView(R.id.iv_main_close)
    ImageView ivClose;
    @BindView(R.id.main_circle_menu)
    CircleMenu menuAdd;
    @BindView(R.id.main_loading)
    RotateLoading ivLoading;

    public static MainActivity _instance;
    private static final int RequestPermissionCode  = 1 ;
    public static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 101;
    public static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 102;
    public static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 103;
    public static final int MY_PERMISSIONS_REQUEST_READ_SMS = 104;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_SMS = 105;
    protected DatabaseAdapter database_adapter;
    public String BLOCK_USERS_TABLE = "block_users";
    public ArrayList<BlockItem> blockParam = new ArrayList<BlockItem>();
    public BlockAdapter m_block_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        database_adapter = new DatabaseAdapter(this);
        _instance = this;
        onPermissionGo();
        onPermissionSo();
        onPermissionReceive();

        menuAdd.setMainMenu(Color.parseColor("#BE1E2D"), R.mipmap.ic_add, R.mipmap.ic_cancel)
                .addSubMenu(Color.parseColor("#258CFF"), R.mipmap.ic_hand_write)
                .addSubMenu(Color.parseColor("#30A400"), R.mipmap.ic_contact)
                .setOnMenuSelectedListener(new OnMenuSelectedListener() {

                    @Override
                    public void onMenuSelected(int index) {
                        if (index == 0) {
                            new Handler().postDelayed(new Runnable(){
                                public void run(){
                                    Intent manualIntent = new Intent(MainActivity.this, ManualActivity.class);
                                    startActivity(manualIntent);
                                }
                            }, 1000);
                        } else {
                            onPermissionGoto();
                        }
                    }
                }).setOnMenuStatusChangeListener(new OnMenuStatusChangeListener() {

            @Override
            public void onMenuOpened() {}

            @Override
            public void onMenuClosed() {}

        });

        schUsers.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

        schUsers.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchQuery) {
                m_block_adapter.filter(searchQuery.toString().trim());
                lsvUsers.invalidate();
                return true;
            }
        });

        registerForContextMenu(lsvUsers);
        lsvUsers.setOnItemClickListener(new DataItemClickListener());
    }

    @Override
    protected void onResume() {
        super.onResume();
        GetBlockers();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select Actions");
        menu.add(0, v.getId(), 0, "Edit entry");
        menu.add(0, v.getId(), 0, "Delete entry");
        menu.add(0, v.getId(), 0, "Detail entry");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        if (item.getTitle() == "Edit entry") {
            EditBlockers(info.position);
        } else if (item.getTitle() == "Delete entry") {
            DeleteBlockers(info.position);
        } else if (item.getTitle() == "Detail entry") {
            DetailBlocker(info.position);
        } else {
            return false;
        }
        return true;
    }

    @OnClick(R.id.iv_main_search)
    public void onSearchButtonClicked(View v) {
        ivSearch.setVisibility(View.GONE);
        ivLogo.setVisibility(View.INVISIBLE);
        schUsers.setVisibility(View.VISIBLE);
        ivClose.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.iv_main_close)
    public void onCloseButtonClicked(View v) {
        ivSearch.setVisibility(View.VISIBLE);
        ivLogo.setVisibility(View.VISIBLE);
        schUsers.setVisibility(View.INVISIBLE);
        ivClose.setVisibility(View.GONE);
    }

    @OnClick(R.id.iv_add_user)
    public void onAddButtonClicked(View v) {
        onAdd();
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        menuAdd.openMenu();
        return super.onMenuOpened(featureId, menu);
    }

    public void onPermissionGoto() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CONTACTS)) {
            new Handler().postDelayed(new Runnable(){
                public void run(){
                    Intent manualIntent = new Intent(MainActivity.this, ContactActivity.class);
                    startActivity(manualIntent);
                }
            }, 1000);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, RequestPermissionCode);
        }
    }

    public void onPermissionGo() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                // MY_PERMISSIONS_REQUEST_READ_PHONE_STATE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public void onPermissionSo() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
                // MY_PERMISSIONS_REQUEST_READ_PHONE_STATE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public void onPermissionReceive() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
                // MY_PERMISSIONS_REQUEST_READ_PHONE_STATE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, MY_PERMISSIONS_REQUEST_READ_SMS);
                // MY_PERMISSIONS_REQUEST_READ_PHONE_STATE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new Handler().postDelayed(new Runnable(){
                        public void run(){
                            Intent manualIntent = new Intent(MainActivity.this, ContactActivity.class);
                            startActivity(manualIntent);
                        }
                    }, 1000);
                } else {
                    Toast.makeText(MainActivity.this, "Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show();
                }
                break;
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE:
                {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }
            case MY_PERMISSIONS_REQUEST_SEND_SMS:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }
            case MY_PERMISSIONS_REQUEST_RECEIVE_SMS:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }
        }
    }

    private void GetBlockers() {
        ivLoading.start();

        int iId;
        String strName;
        String strPhone;
        String strSMS;
        String strChecked;
        boolean bChecked;
        BlockItem item;

        this.database_adapter.openToRead();
        blockParam = new ArrayList<BlockItem>();
        try {
            Cursor cBlockers = this.database_adapter.select(BLOCK_USERS_TABLE);
            if (cBlockers != null) {
                if (cBlockers.moveToFirst()) {
                    do {
                        iId = cBlockers.getInt(0);
                        strName = cBlockers.getString(1);
                        strPhone = cBlockers.getString(2);
                        strSMS = cBlockers.getString(3);
                        strChecked = cBlockers.getString(4);

                        if (strChecked.equals("1")) {
                            bChecked = true;
                        } else {
                            bChecked = false;
                        }

                        item = new BlockItem(iId, strName, strPhone, strSMS, bChecked);
                        blockParam.add(item);
                    } while (cBlockers.moveToNext());
                }
            }

            if (blockParam != null && blockParam.size() > 0) {
                m_block_adapter = new BlockAdapter(MainActivity.this, R.layout.block_row, blockParam);
                lsvUsers.setAdapter(m_block_adapter);
                m_block_adapter.notifyDataSetChanged();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        this.database_adapter.close();
        ivLoading.stop();
    }

    private void DeleteBlockers(int id) {
        this.database_adapter.openToWrite();
        BlockItem item = blockParam.get(id);
        boolean bRes = this.database_adapter.deleteItem(MainActivity._instance.BLOCK_USERS_TABLE, item.getIndex());
        if (bRes) GetBlockers();
        this.database_adapter.close();
    }

    private void EditBlockers(int id) {
        BlockItem item = blockParam.get(id);

        Intent editIntent = null;
        editIntent = new Intent(MainActivity.this, ManualActivity.class);
        editIntent.putExtra("Id", item.getIndex());
        editIntent.putExtra("Name", item.getName());
        editIntent.putExtra("Phone", item.getPhone());
        editIntent.putExtra("Path", 1);
        startActivity(editIntent);
    }

    private void DetailBlocker(int id) {
        BlockItem item = blockParam.get(id);

        Intent detailIntent = null;
        detailIntent = new Intent(MainActivity.this, DetailActivity.class);
        detailIntent.putExtra("Id", item.getIndex());
        startActivity(detailIntent);
    }

    private class BlockAdapter extends ArrayAdapter<BlockItem> {

        private ArrayList<BlockItem> items;

        private ArrayList<BlockItem> orig;

        private TextView txtUser, txtPhone;

        private Switch swhBlock;

        private String strUser, strPhone;

        public BlockAdapter(Context context, int textViewResourceId, ArrayList<BlockItem> items) {
            super(context, textViewResourceId, items);
            this.items = items;
            this.orig = items;
            orig = new ArrayList<BlockItem>();
            orig.addAll(items);
        }

        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());

            items.clear();
            if (charText.length() == 0) {
                items.addAll(orig);

            } else {
                for (BlockItem postDetail : orig) {
                    if (charText.length() != 0 && postDetail.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                        items.add(postDetail);
                    } else if (charText.length() != 0 && postDetail.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                        items.add(postDetail);
                    }
                }
            }
            notifyDataSetChanged();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.block_row, null);
            }

            BlockItem blockItem = items.get(position);
            if (blockItem != null) {
                txtUser = (TextView) v.findViewById(R.id.txt_block_name);
                txtPhone = (TextView) v.findViewById(R.id.txt_block_phone);
                swhBlock = (Switch) v.findViewById(R.id.block_switch);

                strUser = blockItem.getName();
                strPhone = blockItem.getPhone();

                if (strUser != null) {
                    txtUser.setText(strUser);
                } else {
                    txtUser.setText("");
                }

                if (strPhone != null) {
                    txtPhone.setText(strPhone);
                } else {
                    txtPhone.setText("");
                }

                if (blockItem.getChecked()) {
                    swhBlock.setChecked(true);
                } else {
                    swhBlock.setChecked(false);
                }

                swhBlock.setTag(position);
                swhBlock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int iArg = (int) v.getTag();
                        BlockItem item = null;

                        for (int i = 0; i < blockParam.size(); i++) {
                            if (i == iArg) {
                                item = blockParam.get(i);
                                if (item.getChecked()) {
                                    item.setChecked(false);
                                    onUpdate(item.getIndex(), "0");
                                } else {
                                    item.setChecked(true);
                                    onUpdate(item.getIndex(), "1");
                                }
                                break;
                            }
                        }
                        notifyDataSetChanged();
                    }
                });
            }
            return v;
        }
    }

    public class DataItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            openContextMenu(view);
        }

    }

    private void onUpdate(int iIndex, String strChecked) {
        this.database_adapter.openToWrite();
        this.database_adapter.updateBlock(MainActivity._instance.BLOCK_USERS_TABLE, iIndex, strChecked);
        this.database_adapter.close();
    }

    private void onAdd() {
        final CharSequence[] options = {"Add user manually", "Add user from addressbook", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Users!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Add user manually")) {
                    Intent manualIntent = new Intent(MainActivity.this, ManualActivity.class);
                    startActivity(manualIntent);
                } else if (options[item].equals("Add user from addressbook")) {
                    onPermissionGoto();
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.setCancelable(false);
        builder.show();
    }
}
