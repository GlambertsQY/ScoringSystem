package com.example.scoringsystem.fragment.home;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.scoringsystem.R;
import com.example.scoringsystem.activity.MainActivity;
import com.example.scoringsystem.base.BaseMainFragment;
import com.example.scoringsystem.bean.OCRBean.Block;
import com.example.scoringsystem.bean.OCRBean.Line;
import com.example.scoringsystem.bean.OCRBean.OcrResult;
import com.example.scoringsystem.bean.OCRBean.Word;
import com.example.scoringsystem.bean.QuestionStandardAnswerBean;
import com.example.scoringsystem.bean.SimilarityBean.Most_similarity;
import com.example.scoringsystem.bean.SimilarityBean.SentSimilarityBean;
import com.example.scoringsystem.bean.SimilarityBean.Showapi;
import com.example.scoringsystem.entity.Question;
import com.example.scoringsystem.fragment.SearchFragment;
import com.example.scoringsystem.fragment.SelectFragment;
import com.example.scoringsystem.utils.Constants;
import com.example.scoringsystem.utils.HttpUtil;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.show.api.ShowApiRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ScoringFragment extends BaseMainFragment
        implements Toolbar.OnMenuItemClickListener {
    private static final String TAG = "Fragmentation";
    private static final int REQ_MODIFY_FRAGMENT = 100;

    private String[] mTitles;
    private String[] mContents;

    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTO = 2;
    private static int CURRENT_ID_Q = 1;
    private Uri imageUri;
    private TextView text_subject, text_title, text_standard_answer, text_input;
    private Button button_scoring;
    //    private Button take_photo;
//    private Button take_album;
    private ProgressDialog progressDialog;
    private SentSimilarityBean sentSimilarityBean;

    // 手写文字识别webapi接口地址
    private static final String WEBOCR_URL = "http://webapi.xfyun.cn/v1/service/v1/ocr/handwriting";
    // 应用APPID(必须为webapi类型应用,并开通手写文字识别服务,参考帖子如何创建一个webapi应用：http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=36481)
    private static final String TEST_APPID = "5e53c696";
    // 接口密钥(webapi类型应用开通手写文字识别后，控制台--我的应用---手写文字识别---相应服务的apikey)
    private static final String TEST_API_KEY = "802f1967464907d12687c725778a7411";


    private Toolbar mToolbar;
//    private RecyclerView mRecy;
//    private HomeAdapter mAdapter;

    public static ScoringFragment newInstance() {
        return new ScoringFragment();
    }

    private TextInputLayout inputLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scoring, container, false);
        initView(view);
//        动态改动 当前Fragment的动画
//        setFragmentAnimator(fragmentAnimator);
        return view;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_anim:
                final PopupMenu popupMenu = new PopupMenu(_mActivity, mToolbar, GravityCompat.END);
                popupMenu.inflate(R.menu.home_pop);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.problem_select) {
                            startForResult(SelectFragment.newInstance("题目选择"), REQ_MODIFY_FRAGMENT);
                        }
                        popupMenu.dismiss();
                        return true;
                    }
                });
                popupMenu.show();
                break;
        }
        return true;
    }

    private void initView(View view) {
        progressDialog = new ProgressDialog(_mActivity);
        progressDialog.setMessage("加载中");
        progressDialog.setCanceledOnTouchOutside(false);

        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
//        mRecy = (RecyclerView) view.findViewById(R.id.recy);

        mTitles = getResources().getStringArray(R.array.array_title);
        mContents = getResources().getStringArray(R.array.array_content);

        mToolbar.setTitle(R.string.scoring);
        initToolbarNav(mToolbar, true);
        mToolbar.inflateMenu(R.menu.home);
        mToolbar.setOnMenuItemClickListener(this);

//        mAdapter = new HomeAdapter(_mActivity);
        LinearLayoutManager manager = new LinearLayoutManager(_mActivity);
//        mRecy.setLayoutManager(manager);
//        mRecy.setAdapter(mAdapter);

//        mAdapter.setOnItemClickListener(new OnItemClickListener() {
////            @Override
////            public void onItemClick(int position, View view) {
////                start(DetailFragment.newInstance(mAdapter.getItem(position).getSubject()));
////            }
//        });

        // Init Datas
//        List<Question> articleList = new ArrayList<>();
//        for (int i = 0; i < 15; i++) {
//            int index = (int) (Math.random() * 3);
//            Question article = new Question(mTitles[index], mContents[index]);
//            articleList.add(article);
//        }
//        mAdapter.setDatas(articleList);

        text_subject = (TextView) view.findViewById(R.id.text_scoring_subject);
        text_title = (TextView) view.findViewById(R.id.text_scoring_title);
        text_standard_answer = (TextView) view.findViewById(R.id.text_scoring_standard_answer);
        text_input = (TextView) view.findViewById(R.id.answer_text);
        inputLayout = (TextInputLayout) view.findViewById(R.id.answer);
        inputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File outputImage = new File(getActivity().getExternalCacheDir(),
                        "output_image.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    imageUri = FileProvider.getUriForFile(getActivity(),
                            "com.example.scoringsystem.fileprovider", outputImage);
                } else {
                    imageUri = Uri.fromFile(outputImage);
                }
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);
            }
        });

        button_scoring = view.findViewById(R.id.score_button);
//        take_photo = view.findViewById(R.id.scan_button);
//        take_album = view.findViewById(R.id.album_button);

        button_scoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sa = text_standard_answer.getText().toString();
                String a = text_input.getText().toString();
                if (!sa.equals("") && !a.equals("")) {
                    scoring(sa, a);
                } else {
                    Toast.makeText(_mActivity, "答案不能为空", Toast.LENGTH_SHORT).show();
                }
//                new Thread() {
//                    //在新线程中发送网络请求
//                    public void run() {
//                        final String appid = "149889";//要替换成自己的
//                        final String secret = "0e24b6922d1b4ce18de73db48a611ed1";//要替换成自己的
//                        try {
//                            final String res = new ShowApiRequest("https://route.showapi.com/1750-10", appid, secret)
//                                    .addTextPara("text_1", txt.getText().toString())
//                                    .addTextPara("text_2", input.getText().toString())
//                                    .addTextPara("model", "")
//                                    .post();
//                            Log.d("MainActivity", res);
//                            String jsonData = res;
//                            Gson gson = new Gson();
//                            final Showapi showapiData = gson.fromJson(jsonData, Showapi.class);
//                            if (showapiData == null) {
//                                getActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(getActivity(), "网络问题", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            } else if (!showapiData.getShowapi_res_code().equals("0")) {
//                                getActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Toast.makeText(getActivity(), "调用失败，不可输入空文本", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            } else {
//                                getActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if (showapiData.getShowapi_res_body().getData() == null) {
//                                            String str = txt.getText() + "\n" + showapiData.getShowapi_res_body().getRemark();
//                                            txt.setText(str);
//                                        } else {
//                                            String text1 = showapiData.getShowapi_res_body().getData().getTexts().getText_1();
//                                            String text2 = showapiData.getShowapi_res_body().getData().getTexts().getText_2();
//                                            String score = showapiData.getShowapi_res_body().getData().getScore();
//                                            String str =
//                                                    txt.getText() +
////                                            "\n输入1：" + text1 + '\n' +
////                                            "输入2：" + text2 + '\n' +
//                                                            "\n相似度：" + score;
//                                            txt.setText(str);
//                                        }
//                                    }
//                                });
//                            }
//                            System.out.println(res);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            Toast.makeText(_mActivity, "网络未连接", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }.start();
            }
        });

//        take_photo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                File outputImage = new File(getActivity().getExternalCacheDir(),
//                        "output_image.jpg");
//                try {
//                    if (outputImage.exists()) {
//                        outputImage.delete();
//                    }
//                    outputImage.createNewFile();
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
//                if (Build.VERSION.SDK_INT >= 24) {
//                    imageUri = FileProvider.getUriForFile(getActivity(),
//                            "com.example.scoringsystem.fileprovider", outputImage);
//                } else {
//                    imageUri = Uri.fromFile(outputImage);
//                }
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                startActivityForResult(intent, TAKE_PHOTO);
//            }
//        });
//
//        take_album.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (ContextCompat.checkSelfPermission(getActivity(),
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager
//                        .PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(getActivity(),
//                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                            1);
//                } else {
//                    openAlbum();
//                }
//            }
//        });
    }

    /**
     * 类似于 Activity的 onNewIntent()
     */
    @Override
    public void onNewBundle(Bundle args) {
        super.onNewBundle(args);
        Toast.makeText(_mActivity, args.getString("from"), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == REQ_MODIFY_FRAGMENT && resultCode == RESULT_OK && data != null) {
            text_subject.setText(data.getString("subject"));
            text_title.setText(data.getString("title"));
            text_standard_answer.setText(data.getString("answer"));
            CURRENT_ID_Q = Integer.parseInt(data.getString("id_q"));
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            text_input.setText("联网转换为序列化文本中。。。");
                        }
                    });
//                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
//                        picture.setImageBitmap(bitmap);
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                Map<String, String> header = constructHeader("en", "false");
                                InputStream is = getActivity().getContentResolver().openInputStream(imageUri);
                                Bitmap bitmap = BitmapFactory.decodeStream(is);
                                Matrix matrix = new Matrix();
                                matrix.setScale(0.5f, 0.5f);
                                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                                        bitmap.getHeight(), matrix, true);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] imageByteArray = baos.toByteArray();

                                String imageBase64 = new String(Base64.encodeBase64(imageByteArray), StandardCharsets.UTF_8);
                                String bodyParam = "image=" + imageBase64;
                                final String result = HttpUtil.doPost(WEBOCR_URL, header, bodyParam);
                                Log.d("MainActivity", result);
                                Gson gson = new Gson();
                                final OcrResult ocrResult = gson.fromJson(result, OcrResult.class);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (ocrResult == null) {
                                            text_input.setText("建议重试");
                                        } else if (!ocrResult.getCode().equals("0")) {
                                            text_input.setText("API错误");
                                        } else {
                                            String str = "";
                                            List<Block> blockList = ocrResult.getData().getBlock();
                                            for (Block b :
                                                    blockList) {
                                                for (Line l :
                                                        b.getLine()) {
                                                    for (Word w :
                                                            l.getWord()) {
                                                        str += w.getContent();
                                                    }
                                                }
                                            }
                                            text_input.setText(str);
                                        }
                                    }
                                });
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }.start();


                } else {
                    Toast.makeText(getActivity(), "未知错误", Toast.LENGTH_LONG).show();
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            text_input.setText("联网转换为序列化文本中。。。");
                        }
                    });
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                Map<String, String> header = constructHeader("en", "false");
                                InputStream is = handleImageOnKitkat(data);
                                byte[] imageByteArray = inputStream2ByteArray(is);
//                                byte[] imageByteArray = FileUtil.read2ByteArray("/storage/emulated/0/Download/ocr.jpg");
                                String imageBase64 = new String(Base64.encodeBase64(imageByteArray), "UTF-8");
                                String bodyParam = "image=" + imageBase64;
                                final String result = HttpUtil.doPost(WEBOCR_URL, header, bodyParam);
                                Log.d("MainActivity", result);
                                Gson gson = new Gson();
                                final OcrResult ocrResult = gson.fromJson(result, OcrResult.class);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (ocrResult == null) {
                                            text_input.setText("建议重试");
                                        } else if (!ocrResult.getCode().equals("0")) {
                                            text_input.setText("API错误");
                                        } else {
                                            String str = "";
                                            List<Block> blockList = ocrResult.getData().getBlock();
                                            for (Block b :
                                                    blockList) {
                                                for (Line l :
                                                        b.getLine()) {
                                                    for (Word w :
                                                            l.getWord()) {
                                                        str += w.getContent() + " ";
                                                    }
                                                }
                                            }
                                            text_input.setText(str);
                                        }
                                    }
                                });
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }.start();
                } else {
                    Toast.makeText(getActivity(), "未知错误", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }

    }

    /**
     * 组装http请求头
     *
     * @param language
     * @param location
     * @return Map<String, String></String,>
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    private static Map<String, String> constructHeader(String language, String location) throws UnsupportedEncodingException, ParseException {
        // 系统当前时间戳
        String X_CurTime = System.currentTimeMillis() / 1000L + "";
        // 业务参数
        String param = "{\"language\":\"" + language + "\"" + ",\"location\":\"" + location + "\"}";
        String X_Param = new String(Base64.encodeBase64(param.getBytes("UTF-8")));
        // 接口密钥
        String apiKey = TEST_API_KEY;
        // 讯飞开放平台应用ID
        String X_Appid = TEST_APPID;
        // 生成令牌
        String X_CheckSum = new String(Hex.encodeHex(DigestUtils.md5(apiKey + X_CurTime + X_Param)));
//        String X_CheckSum = DigestUtils.md5Hex(apiKey + X_CurTime + X_Param);
        // 组装请求头
        Map<String, String> header = new HashMap<String, String>();
        header.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        header.put("X-Param", X_Param);
        header.put("X-CurTime", X_CurTime);
        header.put("X-CheckSum", X_CheckSum);
        header.put("X-Appid", X_Appid);
        return header;
    }

    private byte[] inputStream2ByteArray(InputStream in) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }

    private InputStream handleImageOnKitkat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(getActivity().getApplicationContext(), uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
//                Uri contentUri = ContentUris.withAppendedId(Uri.parse(
//                        "content://downloads/public_downloads"
//                ), Long.valueOf(docId));
//                imagePath = getImagePath(contentUri, null);
                imagePath = docId.split(":")[1];
            } else if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                imagePath = Environment.getExternalStorageDirectory() + "/" + id;
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
//        displayImage(imagePath);
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            return new ByteArrayInputStream(baos.toByteArray());
//            picture = findViewById(R.id.picture);
//            picture.setImageBitmap(bitmap);
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Failed to get image",
                    Toast.LENGTH_LONG).show();
            return null;
        }
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getActivity().getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

//    private InputStream imagePath2InputStream(String imagePath){
//        if (imagePath != null) {
//            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//            return new ByteArrayInputStream(baos.toByteArray());
////            picture = findViewById(R.id.picture);
////            picture.setImageBitmap(bitmap);
//        } else {
//            Toast.makeText(MainActivity.this, "Failed to get image",
//                    Toast.LENGTH_LONG).show();
//            return null;
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(getActivity(), "You denied the permission",
                            Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    private void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    private void scoring(final String s1, final String s2) {
        progressDialog.show();
        if (s1 != null && s2 != null) {
            String url = Constants.REQUEST_HOST + "/sent_similarity?s1=" + s1 + "&s2=" + s2;

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

                    sentSimilarityBean = gson.fromJson(jsonData, SentSimilarityBean.class);

                    //list排个序
                    Collections.sort(sentSimilarityBean.getMost_similarity(), new Comparator<Most_similarity>() {
                        public int compare(Most_similarity o1, Most_similarity o2) {
                            //排序属性
                            if (o1.getSimilarity() < o2.getSimilarity()) {
                                return 1;
                            }
                            if (o1.getSimilarity() == o2.getSimilarity()) {
                                return 0;
                            }
                            return -1;
                        }
                    });
                    if (sentSimilarityBean.getStatus().equals("success")) {
                        _mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String str = "";
                                if ((int) (sentSimilarityBean.getSimilarity() * 10) != 0) {
                                    str = "总分：" + (int) (sentSimilarityBean.getSimilarity() * 10) + "\n\n得分点：\n";
                                    for (int i = 0; i < sentSimilarityBean.getMost_similarity().size(); i++) {
                                        if (i <= 5) {
                                            Most_similarity similarity = sentSimilarityBean.getMost_similarity().get(i);
                                            str = str + similarity.getWs1() + " 与 " + similarity.getWs2() + " 得分：" + similarity.getSimilarity() + "\n";
                                        } else {
                                            break;
                                        }
                                    }
                                } else {
                                    str = "总分：" + (int) (sentSimilarityBean.getSimilarity() * 10) + "\n\n无得分点";
                                }
                                progressDialog.dismiss();
                                if (MainActivity.getLoginState()) {
                                    new AlertDialog.Builder(_mActivity)
                                            .setTitle("评分结果(满分100)")
                                            .setMessage(str)
                                            .setPositiveButton("保存结果", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    progressDialog.show();
                                                    String username = MainActivity.getUsername();
                                                    String score = String.valueOf((int) (sentSimilarityBean.getSimilarity() * 10));
                                                    String id_q = String.valueOf(CURRENT_ID_Q);
                                                    String text_a = text_input.getText().toString();
                                                    String url = Constants.REQUEST_HOST + "/store_answer?username=" + username + "&score=" + score +
                                                            "&id_q=" + id_q + "&text_a=" + text_a;
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
                                                                        // 发送成功
                                                                        progressDialog.dismiss();
                                                                        Toast.makeText(_mActivity, "保存成功", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            } else if (ret.equals("Fail")) {
                                                                _mActivity.runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        // 发送失败
                                                                        progressDialog.dismiss();
                                                                        Toast.makeText(_mActivity, "保存失败", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    });
                                                }
                                            })
                                            .setNeutralButton("返回", null)
                                            .setNegativeButton("对结果不满意？", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    progressDialog.show();
                                                    String title = text_title.getText().toString();
                                                    String standardanswer = text_standard_answer.getText().toString();
                                                    String answer = text_input.getText().toString();
                                                    String score = String.valueOf((int) sentSimilarityBean.getSimilarity() * 10);
                                                    String url = Constants.REQUEST_HOST + "/sendError?title=" + title + "&standardanswer=" + standardanswer +
                                                            "&answer=" + answer + "&score=" + score;
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
                                                                        // 发送成功
                                                                        progressDialog.dismiss();
                                                                        Toast.makeText(_mActivity, "已将异常发送给管理员", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            } else if (ret.equals("Fail")) {
                                                                _mActivity.runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        // 登录失败
                                                                        progressDialog.dismiss();
                                                                        Toast.makeText(_mActivity, "发送异常失败", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    });
                                                }
                                            })
                                            .show();
                                } else {
                                    new AlertDialog.Builder(_mActivity)
                                            .setTitle("评分结果(满分100)")
                                            .setMessage(str)
                                            .setPositiveButton("好的", null)
                                            .show();
                                }
                            }
                        });
                    }
//                    JsonParser parser = new JsonParser();
//                    JsonArray jsonElements = parser.parse(jsonData).getAsJsonArray();
//                    questionList.clear();
//                    if (jsonElements != null && jsonElements.size() != 0) {
//                        for (JsonElement i : jsonElements) {
//                            QuestionStandardAnswerBean bean = gson.fromJson(i, QuestionStandardAnswerBean.class);
//                            Question question = new Question(bean.getSubject(), bean.getTitle(), bean.getStandardanswer());
//                            questionList.add(question);
//                        }
//                    }
//                    if (questionList.size() == 0) {
//                        _mActivity.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                mAdapter.notifyDataSetChanged();
//                                progressDialog.dismiss();
//                                findNothing_text.setVisibility(View.VISIBLE);
//                            }
//                        });
//                    } else {
//                        _mActivity.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                findNothing_text.setVisibility(View.GONE);
//                                mAdapter.notifyDataSetChanged();
//                                progressDialog.dismiss();
//                            }
//                        });
//                    }
                }
            });
        }
    }
}
