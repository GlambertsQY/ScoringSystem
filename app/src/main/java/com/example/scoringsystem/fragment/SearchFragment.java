package com.example.scoringsystem.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scoringsystem.R;
import com.example.scoringsystem.activity.MainActivity;
import com.example.scoringsystem.adapter.SearchAdapter;
import com.example.scoringsystem.adapter.SelectAdapter;
import com.example.scoringsystem.base.BaseBackFragment;
import com.example.scoringsystem.bean.AnswerBean;
import com.example.scoringsystem.bean.QuestionStandardAnswerBean;
import com.example.scoringsystem.entity.Answer;
import com.example.scoringsystem.entity.Question;
import com.example.scoringsystem.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class SearchFragment extends BaseBackFragment {
    public static final String TAG = SearchFragment.class.getSimpleName();
    private static final int REQ_MODIFY_FRAGMENT = 100;

    private static final String ARG_TITLE = "arg_title";
    static final String KEY_RESULT_TITLE = "title";
    private String mTitle;
    private Toolbar mToolbar;
    private RecyclerView recyclerView;
    private SearchAdapter mAdapter;
    private List<Answer> answerList = new ArrayList<>();
    private Button search_button;
    private TextView keyword_text, findNothing_text;
    private ProgressDialog progressDialog;
    private Spinner spinner;
    private static SearchFragment fragment;

    public static SearchFragment newInstance(String title) {
        fragment = new SearchFragment();
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
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initView(view);

        return view;
    }

    private void initView(View view) {
        progressDialog = new ProgressDialog(_mActivity);
        progressDialog.setMessage("加载中");
        progressDialog.setCanceledOnTouchOutside(false);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mToolbar.setTitle(mTitle);
        initToolbarNav(mToolbar);
        keyword_text = (TextView) view.findViewById(R.id.text_search_InSearch);
        findNothing_text = (TextView) view.findViewById(R.id.text_find_nothing_search);
        search_button = (Button) view.findViewById(R.id.button_search_InSearch);
        spinner = (Spinner) view.findViewById(R.id.spinner_search);

        recyclerView = (RecyclerView) view.findViewById(R.id.recy_search_InSearch);
//        mAdapter = new SelectAdapter(_mActivity);
        mAdapter = new SearchAdapter(_mActivity, answerList, fragment);
        LinearLayoutManager manager = new LinearLayoutManager(_mActivity);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(mAdapter);
//        mAdapter.setDatas(questionList);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mAdapter.setKeyword(keyword_text.getText().toString());
                String keyword = keyword_text.getText().toString();
                String subject = spinner.getSelectedItem().toString();
                queryAnswer(keyword, subject);
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String keyword = keyword_text.getText().toString();
                String subject = adapterView.getItemAtPosition(i).toString();
                queryAnswer(keyword, subject);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        initDelayView();
    }

    private void initDelayView() {

    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == REQ_MODIFY_FRAGMENT && resultCode == RESULT_OK && data != null) {
            mTitle = data.getString(KEY_RESULT_TITLE);
            mToolbar.setTitle(mTitle);
            // 保存被改变的 title
            getArguments().putString(ARG_TITLE, mTitle);
            Toast.makeText(_mActivity, R.string.modify_title, Toast.LENGTH_SHORT).show();
        }
    }

    private void queryAnswer(final String keyword, final String subject) {
        progressDialog.show();
        if (keyword != null) {
            String username = MainActivity.getUsername();
            String url = Constants.REQUEST_HOST + "/query_answer?keyword=" + keyword + "&subject=" + subject + "&username=" + username;

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
                    String jsonData = response.body().string();
                    Log.d(TAG, "onResponse: " + jsonData);
                    Gson gson = new Gson();

                    JsonParser parser = new JsonParser();
                    JsonArray jsonElements = parser.parse(jsonData).getAsJsonArray();
                    answerList.clear();
                    if (jsonElements != null && jsonElements.size() != 0) {
                        for (JsonElement i : jsonElements) {
                            AnswerBean bean = gson.fromJson(i, AnswerBean.class);
                            Answer answer = new Answer(bean.getTitle(), bean.getStandardanswer(), bean.getAnswer(), bean.getScore(), bean.getScore_time());
                            answerList.add(answer);
                        }
                    }
                    if (answerList.size() == 0) {
                        _mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                                progressDialog.dismiss();
                                findNothing_text.setVisibility(View.VISIBLE);
                            }
                        });
                    } else {
                        _mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                findNothing_text.setVisibility(View.GONE);
                                mAdapter.notifyDataSetChanged();
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            });

            Log.d(TAG, "queryQuestion: out");
        }
    }
}
