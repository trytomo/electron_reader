package com.example.uhf_bt.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.uhf_bt.MainActivity;
import com.example.uhf_bt.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class UHFSetFragment extends Fragment implements View.OnClickListener {

    Button btnGetPower;
    Button btnSetPower;
    Spinner spPower;
    Spinner SpinnerMode;
    Button BtSetFre;
    Button BtGetFre;
    MainActivity context;
    RadioButton rbUsHop;
    RadioButton rbBRA;
    RadioButton rbOtherHop;
    Spinner spFreHop;
    Button btnSetFreHop;
    Button btnbeepOpen;
    Button btnbeepClose;

    private String[] arrayPower;

    private final static int GET_FRE = 1;
    private final static int GET_POWER = 2;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_FRE:
                    int idx = (int) msg.obj;
                    if (idx != -1) {
                        int count = SpinnerMode.getCount();
                        SpinnerMode.setSelection(idx > count - 1 ? count - 1 : idx);
                    } else if (msg.arg1 == 1) {
                        context.showToast(R.string.uhf_msg_read_frequency_fail);
                    }
                    break;
                case GET_POWER:
                    int iPower = (int) msg.obj;
                    if(arrayPower != null && iPower > -1) {
                        for (int i = 0; i < arrayPower.length; i++) {
                            if (iPower == Integer.valueOf(arrayPower[i])) {
                                spPower.setSelection(i);
                                break;
                            }
                        }
                    } else if (msg.arg1 == 1) {
                        context.showToast(R.string.uhf_msg_read_power_fail);
                    }
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_uhfset, container, false);
        init(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = (MainActivity) getActivity();
    }

    private void init(View view) {
        btnGetPower = (Button) view.findViewById(R.id.btnGetPower);
        btnSetPower = (Button) view.findViewById(R.id.btnSetPower);

        spPower = (Spinner) view.findViewById(R.id.spPower);
        arrayPower = getResources().getStringArray(R.array.arrayPower);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, arrayPower);
        spPower.setAdapter(adapter);

        SpinnerMode = (Spinner) view.findViewById(R.id.SpinnerMode);
        BtSetFre = (Button) view.findViewById(R.id.BtSetFre);
        BtGetFre = (Button) view.findViewById(R.id.BtGetFre);

        rbUsHop = (RadioButton) view.findViewById(R.id.rbUsHop);
        rbBRA = (RadioButton) view.findViewById(R.id.rbBRA);
        rbOtherHop = (RadioButton) view.findViewById(R.id.rbOtherHop);
        spFreHop = (Spinner) view.findViewById(R.id.spFreHop);
        btnSetFreHop = (Button) view.findViewById(R.id.btnSetFreHop);

        btnbeepOpen = (Button) view.findViewById(R.id.btnbeepOpen);
        btnbeepClose = (Button) view.findViewById(R.id.btnbeepClose);

        rbOtherHop.setOnClickListener(this);
        rbUsHop.setOnClickListener(this);
        rbBRA.setOnClickListener(this);
        btnSetFreHop.setOnClickListener(this);
        btnGetPower.setOnClickListener(this);
        btnSetPower.setOnClickListener(this);
        BtSetFre.setOnClickListener(this);
        BtGetFre.setOnClickListener(this);

        btnbeepOpen.setOnClickListener(this);
        btnbeepClose.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        new Thread() {
            @Override
            public void run() {
                getFre(false);
                getPower(false);
            }
        }.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnGetPower:
                getPower(true);
                break;
            case R.id.btnSetPower:
                setPower();
                break;
            case R.id.BtGetFre:
                getFre(true);
                break;
            case R.id.BtSetFre:
                setFre();
                break;
            case R.id.btnSetFreHop:
                setFre2();
                break;
            case R.id.rbUsHop:
                OnClick_rbUsHop();
                break;
            case R.id.rbBRA:
                OnClick_rbBRA();
                break;
            case R.id.rbOtherHop:
                OnClick_rbOtherHop();
                break;
            case R.id.btnbeepClose:
                if (context.uhf.setBeep(false)) {
                    context.showToast(R.string.setting_succ);
                } else {
                    context.showToast(R.string.setting_fail);
                }
                break;
            case R.id.btnbeepOpen:
                if (context.uhf.setBeep(true)) {
                    context.showToast(R.string.setting_succ);
                } else {
                    context.showToast(R.string.setting_fail);
                }
                break;
        }
    }

    private void sendMessage(int what, Object obj, int arg1) {
        Message msg = mHandler.obtainMessage(what, obj);
        msg.arg1 = arg1;
        mHandler.sendMessage(msg);
    }

    private void getPower(boolean showToast) {
        int iPower = context.uhf.getPower();
        sendMessage(GET_POWER, iPower, showToast ? 1 : 0);
    }

    private void setPower() {
        int iPower = Integer.valueOf(spPower.getSelectedItem().toString());
        if (context.uhf.setPower(iPower)) {
            Toast.makeText(context, R.string.uhf_msg_set_power_succ, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.uhf_msg_set_power_fail, Toast.LENGTH_SHORT).show();
        }
    }

    public void getFre(boolean showToast) {
        int idx = context.uhf.getFrequencyMode();
        switch (idx) {
            case 0x01:
                idx = 0;
                break;
            case 0x02:
                idx = 1;
                break;
            case 0x04:
                idx = 2;
                break;
            case 0x08:
                idx = 3;
                break;
            case 0x016:
                idx = 4;
                break;
            case 0x032:
                idx = 5;
                break;
        }
        sendMessage(GET_FRE, idx, showToast ? 1 : 0);
    }

    private void setFre() {
        byte f = 0;
        switch (SpinnerMode.getSelectedItemPosition()) {
            case 0:
                f = 0x01;
                break;
            case 1:
                f = 0x02;
                break;
            case 2:
                f = 0x04;
                break;
            case 3:
                f = 0x08;
                break;
            case 4:
                f = 0x016;
                break;
            case 5:
                f = 0x032;
                break;
        }
        if (context.uhf.setFrequencyMode(f)) {
            context.showToast(R.string.uhf_msg_set_frequency_succ);
        } else {
            context.showToast(R.string.uhf_msg_set_frequency_fail);
        }
    }

    private void setFre2() {
        if (context.uhf.setFreHop(new Float(spFreHop.getSelectedItem().toString().trim()).floatValue())) {
            context.showToast(R.string.uhf_msg_set_frequency_succ);
        } else {
            context.showToast(R.string.uhf_msg_set_frequency_fail);
        }
    }

    private void setCW(int flag, boolean showToast) {
        boolean res = context.uhf.setCW(flag);
        if (showToast) {
            if (res) {
                context.showToast(getString(R.string.setting_succ));
            } else {
                context.showToast(getString(R.string.setting_fail));
            }
        }
    }

    public void OnClick_rbUsHop() {
        ArrayAdapter adapter = ArrayAdapter.createFromResource(context, R.array.arrayFreHop_us, android.R.layout.simple_spinner_item);
        spFreHop.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void OnClick_rbBRA() {
        ArrayAdapter adapter = ArrayAdapter.createFromResource(context, R.array.arrayFreHop_bra, android.R.layout.simple_spinner_item);
        spFreHop.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void OnClick_rbOtherHop() {
        ArrayAdapter adapter = ArrayAdapter.createFromResource(context, R.array.arrayFreHop, android.R.layout.simple_spinner_item);
        spFreHop.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}
