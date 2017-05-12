package com.fredrick.mixedsignals;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.fredrick.mixedsignals.database.DatabaseAdapter;
import com.victor.loading.rotate.RotateLoading;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ManualActivity extends AppCompatActivity {

    @BindView(R.id.edt_manual_name)
    EditText edtName;
    @BindView(R.id.edt_manual_phone)
    EditText edtPhone;
    @BindView(R.id.manual_loading)
    RotateLoading ivLoading;

    private AlertDialog.Builder checkAlertDialog = null;
    protected DatabaseAdapter database_adapter;
    private int iPath = 0;
    private int iIndex = 0;
    private String strName, strPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        ButterKnife.bind(this);
        database_adapter = new DatabaseAdapter(this);

        if (checkAlertDialog == null)
            checkAlertDialog = new AlertDialog.Builder(this);
        checkAlertDialog.setIcon(R.mipmap.ic_dialog_alarm);
        checkAlertDialog.setTitle(R.string.txt_warning);
        checkAlertDialog.setPositiveButton(R.string.txt_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                return;
            }
        });

        initUI();
    }

    private void initUI() {
        Intent receiveIntent = getIntent();
        if (receiveIntent != null) {
            iIndex = receiveIntent.getIntExtra("Id", 0);
            strName = receiveIntent.getStringExtra("Name");
            strPhone = receiveIntent.getStringExtra("Phone");
            iPath = receiveIntent.getIntExtra("Path", 0);

            if (strName != null) {
                edtName.setText(strName);
                edtPhone.setText(strPhone);
            }
        }
    }

    @OnClick(R.id.iv_manual_back)
    public void onBackButtonClicked(View v) {
        finish();
    }

    @OnClick(R.id.iv_manual_done)
    public void onDoneButtonClicked(View v) {
        if (!onNameCheck()) {
            checkAlertDialog.setMessage(R.string.txt_input_name);
            checkAlertDialog.show();
            return;
        }

        if (!onPhoneCheck()) {
            checkAlertDialog.setMessage(R.string.txt_input_phone);
            checkAlertDialog.show();
            return;
        }
        ivLoading.start();

        this.database_adapter.openToWrite();
        if (iPath == 0) {
            this.database_adapter.insert(MainActivity._instance.BLOCK_USERS_TABLE, edtName.getText().toString(), edtPhone.getText().toString(), "User unavailable.", "1");
        } else {
            this.database_adapter.updateName(MainActivity._instance.BLOCK_USERS_TABLE, iIndex, edtName.getText().toString(), edtPhone.getText().toString());
        }
        this.database_adapter.close();

        ivLoading.stop();
        finish();
    }

    private boolean onNameCheck() {
        String strName = edtName.getText().toString();
        if (strName.length() < 1) {
            return false;
        }

        return true;
    }

    private boolean onPhoneCheck() {
        String strPassword = edtPhone.getText().toString();
        if (strPassword.length() < 1) {
            return false;
        }

        return true;
    }
}
