package com.fredrick.mixedsignals;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.fredrick.mixedsignals.database.DatabaseAdapter;
import com.fredrick.mixedsignals.model.ContactItem;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContactActivity extends AppCompatActivity {

    @BindView(R.id.lsv_contact_users)
    ListView lsvContact;
    @BindView(R.id.contact_loading)
    RotateLoading ivLoading;

    public ArrayList<ContactItem> contactParam = new ArrayList<ContactItem>();
    public ContactAdapter m_contact_adapter;
    protected DatabaseAdapter database_adapter;
    private Cursor cursor ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        ButterKnife.bind(this);
        database_adapter = new DatabaseAdapter(this);

        GetContacts();
    }

    @OnClick(R.id.iv_contact_back)
    public void onBackButtonClicked(View v) {
        finish();
    }

    @OnClick(R.id.iv_contact_check)
    public void onCheckButtonClicked(View v) {
        ContactItem item;
        if (contactParam != null && contactParam.size() > 0) {
            for (int i = 0; i < contactParam.size(); i++) {
                item = contactParam.get(i);
                if (item.getChecked()) {
                    onAddContacts(item);
                }
            }
        }
        finish();
    }

    public void GetContacts() {
        ivLoading.start();
        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        String strName, strPhone;
        ContactItem item;
        while (cursor.moveToNext()) {
            strName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            strPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            item = new ContactItem(strName, strPhone, false);
            contactParam.add(item);
        }

        if (contactParam != null && contactParam.size() > 0) {
            m_contact_adapter = new ContactAdapter(ContactActivity.this, R.layout.contact_row, contactParam);
            lsvContact.setAdapter(m_contact_adapter);
            m_contact_adapter.notifyDataSetChanged();
        }

        cursor.close();
        ivLoading.stop();
    }

    private class ContactAdapter extends ArrayAdapter<ContactItem> {

        private ArrayList<ContactItem> items;

        private TextView txtUser, txtPhone;

        private CheckBox chkContact;

        private String strUser, strPhone;

        private boolean bChecked;

        public ContactAdapter(Context context, int textViewResourceId, ArrayList<ContactItem> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.contact_row, null);
            }

            ContactItem contactItem = items.get(position);
            if (contactItem != null) {
                txtUser = (TextView) v.findViewById(R.id.txt_contact_name);
                txtPhone = (TextView) v.findViewById(R.id.txt_contact_phone);
                chkContact = (CheckBox) v.findViewById(R.id.chk_contact);

                strUser = contactItem.getName();
                strPhone = contactItem.getPhone();

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

                if (contactItem.getChecked()) {
                    chkContact.setChecked(true);
                } else {
                    chkContact.setChecked(false);
                }

                chkContact.setTag(position);
                chkContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int iArg = (int) v.getTag();
                        ContactItem item = null;

                        for (int i = 0; i < contactParam.size(); i++) {
                            if (i == iArg) {
                                item = contactParam.get(i);
                                if (item.getChecked()) {
                                    item.setChecked(false);
                                } else {
                                    item.setChecked(true);
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

    private void onAddContacts(ContactItem item) {
        ivLoading.start();

        this.database_adapter.openToWrite();
        this.database_adapter.insert(MainActivity._instance.BLOCK_USERS_TABLE, item.getName(), item.getPhone(), "User unavailable", "1");
        this.database_adapter.close();

        ivLoading.stop();
    }
}
