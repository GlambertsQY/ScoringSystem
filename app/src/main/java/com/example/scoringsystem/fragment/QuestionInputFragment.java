package com.example.scoringsystem.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scoringsystem.R;
import com.example.scoringsystem.adapter.SearchAdapter;
import com.example.scoringsystem.base.BaseBackFragment;
import com.example.scoringsystem.fragment.account.LoginFragment;
import com.example.scoringsystem.fragment.account.RegisterFragment;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class QuestionInputFragment extends BaseBackFragment {
    private static final String TAG = QuestionInputFragment.class.getSimpleName();

    private static final String ARG_TITLE = "arg_title";
    private static QuestionInputFragment fragment;
    private String mTitle;
    private Toolbar mToolbar;

    private EditText mEtSubject, mEtTitle, mEtStandardAnswer;
    private Button mBtn;
    private ProgressDialog progressDialog;

    public static QuestionInputFragment newInstance(String title) {
        fragment = new QuestionInputFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TITLE, title);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mTitle = bundle.getString(ARG_TITLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_questioninput, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        progressDialog = new ProgressDialog(_mActivity);
        progressDialog.setMessage("录入中");
        progressDialog.setCanceledOnTouchOutside(false);

        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mToolbar.setTitle(mTitle);
        initToolbarNav(mToolbar);

        mEtSubject = (EditText) view.findViewById(R.id.text_input_subject);
        mEtTitle = (EditText) view.findViewById(R.id.text_input_title);
        mEtStandardAnswer = (EditText) view.findViewById(R.id.text_input_standardanswer);
        mBtn = (Button) view.findViewById(R.id.btn_question_input);

        showSoftInput(mEtSubject);

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strSubject = mEtSubject.getText().toString();
                String strTitle = mEtTitle.getText().toString();
                String strStandardAnswer = mEtStandardAnswer.getText().toString();

                if (TextUtils.isEmpty(strSubject.trim())) {
                    Toast.makeText(_mActivity, R.string.error_subject, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(strTitle.trim())) {
                    Toast.makeText(_mActivity, R.string.error_title, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(strStandardAnswer.trim())) {
                    Toast.makeText(_mActivity, R.string.error_standardanswer, Toast.LENGTH_SHORT).show();
                    return;
                }
                addQuestion(strSubject, strTitle, strStandardAnswer);
            }
        });
    }

    private void addQuestion(final String subject, final String title, final String standardanswer) {
        progressDialog.show();
        String url = "http://116.85.30.119/add_questionStandardAnswer?subject=" + subject + "&title=" + title
                + "&standardanswer=" + standardanswer;
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
                            // 录入成功
                            progressDialog.dismiss();
                            Toast.makeText(_mActivity, "录入成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (ret.equals("Fail")) {
                    _mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 录入失败
                            progressDialog.dismiss();
                            Toast.makeText(_mActivity, "录入失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
