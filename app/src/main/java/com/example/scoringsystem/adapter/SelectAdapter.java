package com.example.scoringsystem.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scoringsystem.R;
import com.example.scoringsystem.bean.QuestionBean.QuestionBean;
import com.example.scoringsystem.bean.QuestionStandardAnswerBean;
import com.example.scoringsystem.bean.StandardAnswerBean.StandardAnswerBean;
import com.example.scoringsystem.entity.Question;
import com.example.scoringsystem.fragment.SelectFragment;
import com.example.scoringsystem.listener.OnItemClickListener;
import com.example.scoringsystem.utils.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.MyViewHolder> {
    private static final String TAG = "SelectAdapter";
    private List<Question> mItems = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context mContext;
    private SelectFragment mFragment;
    private final int RESULT_OK = -1;

    private OnItemClickListener mClickListener;

    //fragment特别版，构造函数需要给context
    public SelectAdapter(Context context, List<Question> questionList, SelectFragment mFragment) {
        this.mInflater = LayoutInflater.from(context);
        this.mItems = questionList;
        this.mContext = context;
        this.mFragment = mFragment;
    }

//    public void setDatas(List<Question> items) {
//        queryQuestion("");
////        mItems.addAll(items);
//    }

//    public void setKeyword(String keyword) {
//        queryQuestion(keyword);
//        notifyDataSetChanged();
//    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_home, parent, false);
        final MyViewHolder holder = new MyViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Bundle bundle = new Bundle();
                bundle.putString("subject", mItems.get(position).getSubject());
                bundle.putString("title", mItems.get(position).getTitle());
                bundle.putString("answer", mItems.get(position).getAnswer());
                mFragment.setFragmentResult(RESULT_OK, bundle);
                mFragment.pop();

                Toast.makeText(mContext, "Success!", Toast.LENGTH_SHORT).show();
                if (mClickListener != null) {
                    mClickListener.onItemClick(position, v);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Question item = mItems.get(position);
        holder.qSubject.setText(item.getSubject());
        holder.qTitle.setText(item.getTitle());
        holder.qAnswer.setText(item.getAnswer());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView qSubject, qTitle, qAnswer;

        public MyViewHolder(View itemView) {
            super(itemView);
            qSubject = (TextView) itemView.findViewById(R.id.question_subject);
            qTitle = (TextView) itemView.findViewById(R.id.question_title);
            qAnswer = (TextView) itemView.findViewById(R.id.question_answer);
        }
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
}
