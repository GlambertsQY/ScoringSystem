package com.example.scoringsystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scoringsystem.R;
import com.example.scoringsystem.entity.Answer;
import com.example.scoringsystem.fragment.SearchFragment;
import com.example.scoringsystem.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

//TODO 搞定adapter，实现search功能，换启动及开屏图片
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {
    private static final String TAG = "SearchAdapter";
    private List<Answer> mItems = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context mContext;
    private SearchFragment mFragment;
    private final int RESULT_OK = -1;

    private OnItemClickListener mClickListener;

    //fragment特别版，构造函数需要给context
    public SearchAdapter(Context context, List<Answer> answerList, SearchFragment mFragment) {
        this.mInflater = LayoutInflater.from(context);
        this.mItems = answerList;
        this.mContext = context;
        this.mFragment = mFragment;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_home_search, parent, false);
        final MyViewHolder holder = new MyViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
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
        Answer item = mItems.get(position);
        holder.aTitle.setText(item.getTitle());
        holder.aStandardAnswer.setText(item.getStandardAnswer());
        holder.aAnswer.setText(item.getAnswer());
        String scoreAndTime = item.getScore() + " " + item.getScore_time();
        holder.aScoreAndTime.setText(scoreAndTime);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView aTitle, aStandardAnswer, aAnswer, aScoreAndTime;

        public MyViewHolder(View itemView) {
            super(itemView);
            aTitle = (TextView) itemView.findViewById(R.id.answer_title);
            aStandardAnswer = (TextView) itemView.findViewById(R.id.answer_standard_answer);
            aAnswer = (TextView) itemView.findViewById(R.id.answer_answer);
            aScoreAndTime = (TextView) itemView.findViewById(R.id.answer_scoreAndtime);
        }
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

//    private void queryQuestion(final String queryStr) {
//
//        if (queryStr != null) {
////            new Thread() {
////                @Override
////                public void run() {
//            String url = "http://116.85.30.119/questionstandardanswerlist?keyword=" + queryStr;
//
//            OkHttpClient client = new OkHttpClient();
//            Request request = new Request.Builder()
//                    .url(url)
//                    .build();
//            Call call = client.newCall(request);
//            call.enqueue(new Callback() {
//                @Override
//                public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                    Log.d(TAG, "onFailure: ");
//                }
//
//                @Override
//                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                    String jsonData = response.body().string();
//                    Log.d(TAG, "onResponse: " + jsonData);
//                    Gson gson = new Gson();
//
//                    JsonParser parser = new JsonParser();
//                    JsonArray jsonElements = parser.parse(jsonData).getAsJsonArray();
//                    mItems.clear();
//                    if (jsonElements != null && jsonElements.size() != 0) {
//                        for (JsonElement i : jsonElements) {
//                            QuestionStandardAnswerBean bean = gson.fromJson(i, QuestionStandardAnswerBean.class);
//                            Question question = new Question(bean.getSubject(), bean.getTitle(), bean.getStandardanswer());
//                            mItems.add(question);
//                        }
//                    }
//
//                }
//            });
//            notifyDataSetChanged();
//
//            Log.d(TAG, "queryQuestion: in");
//
//            Log.d(TAG, "queryQuestion: out");
//
//        }
//
//    }
}
