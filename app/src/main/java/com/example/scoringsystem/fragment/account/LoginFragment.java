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
import com.example.scoringsystem.bean.QuestionStandardAnswerBean;
import com.example.scoringsystem.entity.Question;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

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
public class LoginFragment extends BaseBackFragment {
    private static final String TAG = LoginFragment.class.getSimpleName();
    private EditText mEtAccount, mEtPassword;
    private Button mBtnLogin, mBtnRegister;
    private ProgressDialog progressDialog;

    private OnLoginSuccessListener mOnLoginSuccessListener;

    public static LoginFragment newInstance() {

        Bundle args = new Bundle();

        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginSuccessListener) {
            mOnLoginSuccessListener = (OnLoginSuccessListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLoginSuccessListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        progressDialog = new ProgressDialog(_mActivity);
        progressDialog.setMessage("登录中");
        progressDialog.setCanceledOnTouchOutside(false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mEtAccount = (EditText) view.findViewById(R.id.login_account);
        mEtPassword = (EditText) view.findViewById(R.id.login_password);
        mBtnLogin = (Button) view.findViewById(R.id.btn_login);
        mBtnRegister = (Button) view.findViewById(R.id.btn_register_InLogin);

        toolbar.setTitle(R.string.login);
        initToolbarNav(toolbar);

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strAccount = mEtAccount.getText().toString();
                String strPassword = mEtPassword.getText().toString();
                if (TextUtils.isEmpty(strAccount.trim())) {
                    Toast.makeText(_mActivity, R.string.error_username, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(strPassword.trim())) {
                    Toast.makeText(_mActivity, R.string.error_pwd, Toast.LENGTH_SHORT).show();
                    return;
                }
                queryUser(strAccount, strPassword);
            }
        });

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start(RegisterFragment.newInstance());
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnLoginSuccessListener = null;
    }

    public interface OnLoginSuccessListener {
        void onLoginSuccess(String account);
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        hideSoftInput();
    }

    private void queryUser(final String username, final String password) {
        progressDialog.show();
        if (username != null) {
            String url = "http://116.85.30.119/queryUser?username=" + username + "&" + "password=" + password;
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.d(TAG, "onFailure: ");
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
                                pop();
                            }
                        });
                    } else if (ret.equals("Fail")) {
                        _mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 登录失败
                                progressDialog.dismiss();
                                Toast.makeText(_mActivity, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

        }
    }
}
