package com.fredrick.mixedsignals;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.fredrick.mixedsignals.database.DatabaseAdapter;
import com.fredrick.mixedsignals.model.BlockItem;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.txt_detail_name)
    TextView txtName;
    @BindView(R.id.txt_detail_phone)
    TextView txtPhone;
    @BindView(R.id.detail_switch)
    Switch schBlock;
    @BindView(R.id.detail_comment)
    EditText edtSMS;
    @BindView(R.id.iv_detail_edit)
    ImageView ivEdit;

    private int iIndex = 0;
    private String strName, strPhone, strSMS;
    private boolean bChecked = true;
    protected DatabaseAdapter database_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        database_adapter = new DatabaseAdapter(this);
        edtSMS.setEnabled(false);

        initUI();
    }

    private void initUI() {
        Intent receiveIntent = getIntent();
        if (receiveIntent != null) {
            iIndex = receiveIntent.getIntExtra("Id", 0);
            GetBlocker();
        }
    }

    private void GetBlocker() {
        int iId;
        String strChecked;

        this.database_adapter.openToRead();
        try {
            Cursor cBlockers = this.database_adapter.selectItem(MainActivity._instance.BLOCK_USERS_TABLE, iIndex);
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
                    } while (cBlockers.moveToNext());
                }
            }

            txtName.setText(strName);
            txtPhone.setText(strPhone);
            edtSMS.setText(strSMS);
            if (bChecked) {
                schBlock.setChecked(true);
            } else {
                schBlock.setChecked(false);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        this.database_adapter.close();
    }

    @OnClick(R.id.iv_detail_back)
    public void onBackButtonClicked(View v) {
        finish();
    }

    @OnClick(R.id.iv_detail_check)
    public void onCheckButtonClicked(View v) {
        String strCheck = "1";
        if (schBlock.isChecked()) {
            strCheck = "1";
        } else {
            strCheck = "0";
        }

        this.database_adapter.openToWrite();
        this.database_adapter.updatePhone(MainActivity._instance.BLOCK_USERS_TABLE, iIndex, edtSMS.getText().toString(), strCheck);
        this.database_adapter.close();

        finish();
    }

    @OnClick(R.id.iv_detail_edit)
    public void onEditButtonClicked(View v) {
        ivEdit.setVisibility(View.GONE);
        edtSMS.setEnabled(true);
    }
}
