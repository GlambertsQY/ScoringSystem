package com.example.scoringsystem.fragment.account;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.scoringsystem.R;
import com.example.scoringsystem.base.BaseBackFragment;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by YoKeyword on 16/2/14.
 */
public class RegisterFragment extends BaseBackFragment {
    private EditText mEtAccount, mEtPassword, mEtPhone, mEtEmail;
    private Button mBtnRegister;
    private LoginFragment.OnLoginSuccessListener mOnLoginSuccessListener;
    private ProgressDialog progressDialog;

    private static final String TAG = RegisterFragment.class.getSimpleName();

    public static RegisterFragment newInstance() {

        Bundle args = new Bundle();

        RegisterFragment fragment = new RegisterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginFragment.OnLoginSuccessListener) {
            mOnLoginSuccessListener = (LoginFragment.OnLoginSuccessListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLoginSuccessListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        progressDialog = new ProgressDialog(_mActivity);
        progressDialog.setMessage("注册中");
        progressDialog.setCanceledOnTouchOutside(false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mEtAccount = (EditText) view.findViewById(R.id.register_account);
        mEtPassword = (EditText) view.findViewById(R.id.register_password);
        mEtPhone = (EditText) view.findViewById(R.id.register_phone);
        mEtEmail = (EditText) view.findViewById(R.id.register_mail);

        mBtnRegister = (Button) view.findViewById(R.id.btn_register_InRegister);

        showSoftInput(mEtAccount);

        toolbar.setTitle(R.string.register);
        initToolbarNav(toolbar);

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strAccount = mEtAccount.getText().toString();
                String strPassword = mEtPassword.getText().toString();
                String strPhone = mEtPhone.getText().toString();
                String strEmail = mEtEmail.getText().toString();

                if (TextUtils.isEmpty(strAccount.trim())) {
                    Toast.makeText(_mActivity, R.string.error_username, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(strPassword.trim())) {
                    Toast.makeText(_mActivity, R.string.error_pwd, Toast.LENGTH_SHORT).show();
                    return;
                }
                addUser(strAccount, strPassword, strPhone, strEmail);
            }
        });
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        hideSoftInput();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnLoginSuccessListener = null;
    }

    private void addUser(final String username, final String password, final String phone, final String mail) {
        progressDialog.show();
        if (username != null) {
            String url = "http://116.85.30.119/add_user?username=" + username + "&password=" + password
                    + "&phone=" + phone + "&mail=" + mail;
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.d(TAG, "onFailure: ");
                    Toast.makeText(_mActivity, "网络未连接", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String ret = response.body().string();
                    Log.d(TAG, "onResponse: " + ret);
                    if (ret.equals("OK")) {

                        _mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 登录成功
                                mOnLoginSuccessListener.onLoginSuccess(username);
                                progressDialog.dismiss();
                                popTo(LoginFragment.class, true);
                            }
                        });
                    } else if (ret.equals("Fail")) {
                        _mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 登录失败
                                progressDialog.dismiss();
                                Toast.makeText(_mActivity, "用户名重复请修改", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

        }
    }
}
