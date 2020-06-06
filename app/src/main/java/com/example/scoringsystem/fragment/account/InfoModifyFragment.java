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
import com.example.scoringsystem.bean.UserBean;
import com.example.scoringsystem.entity.Question;
import com.example.scoringsystem.utils.Constants;
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

public class InfoModifyFragment extends BaseBackFragment {
    private EditText mEtAccount, mEtPassword, mEtPhone, mEtEmail;
    private Button mBtnModify;
    private ProgressDialog progressDialog;
    private static final String ARG_TITLE = "arg_title";
    private static String OLD_USERNAME;

    private static final String TAG = InfoModifyFragment.class.getSimpleName();
    private LoginFragment.OnLoginSuccessListener mOnLoginSuccessListener;

    public static InfoModifyFragment newInstance(String old_username) {

        Bundle args = new Bundle();
        OLD_USERNAME = old_username;
        InfoModifyFragment fragment = new InfoModifyFragment();
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
        View view = inflater.inflate(R.layout.fragment_info_modify, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        progressDialog = new ProgressDialog(_mActivity);
        progressDialog.setMessage("请稍候");
        progressDialog.setCanceledOnTouchOutside(false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mEtAccount = (EditText) view.findViewById(R.id.modify_account);
        mEtPassword = (EditText) view.findViewById(R.id.modify_password);
        mEtPhone = (EditText) view.findViewById(R.id.modify_phone);
        mEtEmail = (EditText) view.findViewById(R.id.modify_mail);
        mBtnModify = (Button) view.findViewById(R.id.btn_modify_info);
        showSoftInput(mEtAccount);
        toolbar.setTitle("信息修改");
        initToolbarNav(toolbar);

        queryUser(OLD_USERNAME);
        mBtnModify.setOnClickListener(new View.OnClickListener() {
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
                updateUser(strAccount, strPassword, strPhone, strEmail);
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

    private void updateUser(final String username, final String password, final String phone, final String mail) {
        progressDialog.show();
        String url = Constants.REQUEST_HOST + "/update_user?old_username=" + OLD_USERNAME + "&new_username=" + username
                + "&password=" + password
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
                _mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(_mActivity, "网络未连接", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String ret = response.body().string();
                Log.d(TAG, "onResponse: " + ret);
                if (ret.equals("OK")) {
                    _mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 修改成功
                            mOnLoginSuccessListener.onLoginSuccess(username);
                            progressDialog.dismiss();
                            popTo(LoginFragment.class, true);
                        }
                    });
                } else if (ret.equals("Fail")) {
                    _mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 修改失败
                            progressDialog.dismiss();
                            Toast.makeText(_mActivity, "用户名重复请修改", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void queryUser(final String username) {
        progressDialog.show();
        String url = Constants.REQUEST_HOST + "/query_userByName?username=" + username;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure: ");
                _mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(_mActivity, "网络未连接", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String jsonData = response.body().string();
                _mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "onResponse: " + jsonData);
                        Gson gson = new Gson();
                        UserBean bean = gson.fromJson(jsonData, UserBean.class);
                        mEtAccount.setText(bean.getUsername());
                        mEtPassword.setText(bean.getPassword());
                        mEtPhone.setText(bean.getPhone());
                        mEtEmail.setText(bean.getMail());
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }
}
