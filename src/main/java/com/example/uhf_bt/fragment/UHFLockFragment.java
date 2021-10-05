package com.example.uhf_bt.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.example.uhf_bt.MainActivity;
import com.example.uhf_bt.R;
import com.example.uhf_bt.Utils;
import com.rscja.deviceapi.RFIDWithUHFBLE;
import androidx.fragment.app.Fragment;


public class UHFLockFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "UHFLockFragment";
    private MainActivity mContext;
    EditText EtAccessPwd_Lock;
    Button btnLock;
    EditText etLockCode;
    CheckBox cb_filter_lock;
    EditText etPtr_filter_lock;
    EditText etLen_filter_lock;
    EditText etData_filter_lock;
    RadioButton rbEPC_filter_lock;
    RadioButton rbTID_filter_lock;
    RadioButton rbUser_filter_lock;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_uhflock, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = (MainActivity) getActivity();
        etLockCode = (EditText) getView().findViewById(R.id.etLockCode);
        EtAccessPwd_Lock = (EditText) getView().findViewById(R.id.EtAccessPwd_Lock);
        btnLock = (Button) getView().findViewById(R.id.btnLock);

        etPtr_filter_lock = (EditText) getView().findViewById(R.id.etPtr_filter_lock);
        etLen_filter_lock = (EditText) getView().findViewById(R.id.etLen_filter_lock);

        rbEPC_filter_lock = (RadioButton) getView().findViewById(R.id.rbEPC_filter_lock);
        rbTID_filter_lock = (RadioButton) getView().findViewById(R.id.rbTID_filter_lock);
        rbUser_filter_lock = (RadioButton) getView().findViewById(R.id.rbUser_filter_lock);

        cb_filter_lock = (CheckBox) getView().findViewById(R.id.cb_filter_lock);
        etData_filter_lock = (EditText) getView().findViewById(R.id.etData_filter_lock);

        rbEPC_filter_lock.setOnClickListener(this);
        rbTID_filter_lock.setOnClickListener(this);
        rbUser_filter_lock.setOnClickListener(this);
        btnLock.setOnClickListener(this);

        cb_filter_lock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    String data = etData_filter_lock.getText().toString().trim();
                    String rex = "[\\da-fA-F]*";
                    if (data == null || data.isEmpty() || !data.matches(rex)) {
                        mContext.showToast("The filtered data must be hexadecimal data");
                        cb_filter_lock.setChecked(false);
                        return;
                    }
                }
            }
        });

        etLockCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.tvLockCode);
                final View vv = LayoutInflater.from(mContext).inflate(R.layout.uhf_dialog_lock_code, null);
                builder.setView(vv);
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        etLockCode.getText().clear();
                    }
                });

                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        RadioButton rbOpen = (RadioButton) vv.findViewById(R.id.rbOpen);
                        RadioButton rbLock = (RadioButton) vv.findViewById(R.id.rbLock);
                        CheckBox cbPerm = (CheckBox) vv.findViewById(R.id.cbPerm);

                        CheckBox cbKill = (CheckBox) vv.findViewById(R.id.cbKill);
                        CheckBox cbAccess = (CheckBox) vv.findViewById(R.id.cbAccess);
                        CheckBox cbEPC = (CheckBox) vv.findViewById(R.id.cbEPC);
                        CheckBox cbTid = (CheckBox) vv.findViewById(R.id.cbTid);
                        CheckBox cbUser = (CheckBox) vv.findViewById(R.id.cbUser);
                        String mask = "";
                        String value = "";
                        int[] data = new int[20];
                        if (cbUser.isChecked()) {
                            data[11] = 1;
                            if (cbPerm.isChecked()) {
                                data[0] = 1;
                                data[10] = 1;
                            }
                            if (rbLock.isChecked()) {
                                data[1] = 1;
                            }
                        }
                        if (cbTid.isChecked()) {
                            data[13] = 1;
                            if (cbPerm.isChecked()) {
                                data[12] = 1;
                                data[2] = 1;
                            }
                            if (rbLock.isChecked()) {
                                data[3] = 1;
                            }
                        }
                        if (cbEPC.isChecked()) {
                            data[15] = 1;
                            if (cbPerm.isChecked()) {
                                data[14] = 1;
                                data[4] = 1;
                            }
                            if (rbLock.isChecked()) {
                                data[5] = 1;
                            }
                        }
                        if (cbAccess.isChecked()) {
                            data[17] = 1;
                            if (cbPerm.isChecked()) {
                                data[16] = 1;
                                data[6] = 1;
                            }
                            if (rbLock.isChecked()) {
                                data[7] = 1;
                            }
                        }
                        if (cbKill.isChecked()) {
                            data[19] = 1;
                            if (cbPerm.isChecked()) {
                                data[18] = 1;
                                data[8] = 1;
                            }
                            if (rbLock.isChecked()) {
                                data[9] = 1;
                            }
                        }
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append("0000");
                        for (int k = data.length - 1; k >= 0; k--) {
                            stringBuffer.append(data[k] + "");
                        }

                        String code = binaryString2hexString(stringBuffer.toString());
                        Log.i(TAG, "  tempCode=" + stringBuffer.toString() + "  code=" + code);

                        etLockCode.setText(code.replace(" ", "0") + "");
                    }
                });
                builder.create().show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rbEPC_filter_lock:
                etPtr_filter_lock.setText("32");
                break;
            case R.id.rbTID_filter_lock:
            case R.id.rbUser_filter_lock:
                etPtr_filter_lock.setText("0");
                break;
            case R.id.btnLock:
                lock();
                break;
        }
    }

    public void lock() {
        String strPWD = EtAccessPwd_Lock.getText().toString().trim();
        String strLockCode = etLockCode.getText().toString().trim();

        if (!TextUtils.isEmpty(strPWD)) {
            if (strPWD.length() != 8) {
                mContext.showToast(R.string.uhf_msg_addr_must_len8);
                return;
            } else if (!Utils.vailHexInput(strPWD)) {
                mContext.showToast(R.string.rfid_mgs_error_nohex);
                return;
            }
        } else {
            mContext.showToast(R.string.rfid_mgs_error_nopwd);
            return;
        }

        if (TextUtils.isEmpty(strLockCode)) {
            mContext.showToast(R.string.rfid_mgs_error_nolockcode);
            return;
        }
        boolean result = false;
        if (cb_filter_lock.isChecked()) {
            String filterData = etData_filter_lock.getText().toString();
            if (filterData == null || filterData.isEmpty()) {
                mContext.showToast("Filter data cannot be empty");
                return;
            }
            if (etPtr_filter_lock.getText().toString() == null || etPtr_filter_lock.getText().toString().isEmpty()) {
                mContext.showToast("Filter start address cannot be empty");
                return;
            }
            if (etLen_filter_lock.getText().toString() == null || etLen_filter_lock.getText().toString().isEmpty()) {
                mContext.showToast("Filter data length cannot be empty");
                return;
            }
            int filterPtr = Integer.parseInt(etPtr_filter_lock.getText().toString());
            int filterCnt = Integer.parseInt(etLen_filter_lock.getText().toString());
            int filterBank =RFIDWithUHFBLE.Bank_EPC;
            if (rbEPC_filter_lock.isChecked()) {
                filterBank =RFIDWithUHFBLE.Bank_EPC;
            } else if (rbTID_filter_lock.isChecked()) {
                filterBank =RFIDWithUHFBLE.Bank_TID;
            } else if (rbUser_filter_lock.isChecked()) {
                filterBank =RFIDWithUHFBLE.Bank_USER;
            }

            result = mContext.uhf.lockMem(strPWD,
                    filterBank,
                    filterPtr,
                    filterCnt,
                    filterData,
                    strLockCode);
        } else {
            result = mContext.uhf.lockMem(strPWD, strLockCode);
        }

        if (!result) {
            mContext.showToast(R.string.rfid_mgs_lock_fail);
            Utils.playSound(2);
        } else {
            mContext.showToast(R.string.rfid_mgs_lock_succ);
            Utils.playSound(1);
        }
    }

    public static String binaryString2hexString(String bString) {
        if (bString == null || bString.equals("") || bString.length() % 8 != 0)
            return null;
        StringBuffer tmp = new StringBuffer();
        int iTmp = 0;
        for (int i = 0; i < bString.length(); i += 4) {
            iTmp = 0;
            for (int j = 0; j < 4; j++) {
                iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j - 1);
            }
            tmp.append(Integer.toHexString(iTmp));
        }
        return tmp.toString();
    }
}
