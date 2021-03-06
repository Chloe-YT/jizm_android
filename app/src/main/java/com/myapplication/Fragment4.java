package com.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import util.SyncUtil;
import util.UserUtil;

import static android.content.Context.MODE_PRIVATE;
import static util.SyncUtil.HOST_IP;

public class Fragment4 extends Fragment implements View.OnClickListener{

    private View mView;

    ImageView imageViewb;
    ImageView imageViewh;
    ImageView personalDateEnter;
    ImageView userAccount;
    ImageView settingEnter;
    TextView userName;
    TextView userTel;
    TextView syncTextView;
   // TextView

    public static final MediaType MEDIA_TYPE_JSON
            = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient.Builder()
                                        .connectTimeout(3, TimeUnit.SECONDS)
                                        .build();

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        //注意View对象的重复使用，以便节省资源
        if(mView == null) {
            mView = inflater.inflate(R.layout.fragment4,container,false);
        }

        //初始化页面组件数据
        initInfomation();

        //设置事件监听
        setActionListener();



        return mView;
    }

    private void initInfomation(){
        imageViewb=(ImageView)mView.findViewById(R.id.h_back) ;
        imageViewh=(ImageView)mView.findViewById(R.id.h_head) ;
        personalDateEnter =(ImageView)mView.findViewById(R.id.personal_enter);
        userAccount =(ImageView)mView.findViewById(R.id.user_account);
        settingEnter =(ImageView) mView.findViewById(R.id.setting_enter);


        syncTextView=(TextView) mView.findViewById(R.id.syncTextView);
        userName=(TextView)mView.findViewById(R.id.user_name);
        userTel=(TextView)mView.findViewById(R.id.user_val);



//        Glide.with(getActivity()).
//                load(R.drawable.user_back).bitmapTransform(new BlurTransformation(getActivity(), 25), new CenterCrop()).
//                into(imageViewb);
//
//        Glide.with(getActivity()).load(R.drawable.user_head)
//                .bitmapTransform(new CropCircleTransformation(getActivity()))
//                .into(imageViewh);

        userName.setText("乔大");
        userTel.setText("132546785");




    }


    public void setActionListener(){
        imageViewh.setOnClickListener(this);
        personalDateEnter.setOnClickListener(this);
        userAccount.setOnClickListener(this);
        settingEnter.setOnClickListener(this);
        syncTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.h_head:

                break;


            case R.id.personal_enter://个人信息修改
                Intent intent1 = new Intent(getActivity(),PersonalDataEditActivity.class);
                startActivity(intent1);
                break;

            case R.id.user_account://个人账户
                Intent intent2 = new Intent(getActivity(),AccountEditActivity.class);
                startActivityForResult(intent2, 2);
                break;

            case R.id.setting_enter://设置
                Intent intent3 = new Intent(getActivity(),EditSettingActivity.class);
                startActivity(intent3);
                break;

            case R.id.syncTextView: {//更新数据
                //isUploadSuccess=false;
                //isDownloadSuccess=false;
                Toast.makeText(getActivity(),"同步中...请稍候",Toast.LENGTH_SHORT).show();

             /*   download();
                if(isDownloadSuccess==true){       //下载成功才执行上传
                    upload();
                }

                if(isDownloadSuccess==true && isUploadSuccess==true){
                    Toast.makeText(getActivity(),"同步成功！",Toast.LENGTH_SHORT).show();
                    syncTextView.setText("更新数据（上次同步时间:"+(new Date())+")" );
                }
                else{
                    Toast.makeText(getActivity(),"同步失败",Toast.LENGTH_SHORT).show();
                }

                System.out.println("isDownloadSuccess="+isDownloadSuccess+",isUploadSuccess="+isUploadSuccess);*/

                download();
            }
                default:
                    break;

        }
    }

    /**
     * 执行上传任务
     */
    private void upload(){
        JSONObject syncRecordsJsonObject= SyncUtil.getAllSyncRecords();
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON,
                syncRecordsJsonObject.toJSONString());

        //这里的主机地址要填电脑的ip地址 ,token要填用户登录时获取的token，超过一定时间会失效，
        // 需要重新获取
        Request request = new Request.Builder()
                .url("http://"+HOST_IP+":8080/app/synchronization")
                .post(requestBody)
                .addHeader("token", UserUtil.getToken())
                .build();

        Log.d("发送上传请求","发送了上传请求");

        //使用异步请求（用同步发送请求需要在子线程上发起，因为安卓较新的版本都不允许在主线程发送网络请求）
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(e instanceof SocketTimeoutException){//判断超时异常
                    Looper.prepare();
                    Toast.makeText(getActivity(),"上传时出错：网络连接超时",
                            Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    e.printStackTrace();
                }
                if(e instanceof ConnectException){//判断连接异常，我这里是报Failed to connect to 10.7.5.144
                    Looper.prepare();
                    Toast.makeText(getActivity(),"上传时出错：连接到服务器失败",
                            Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    e.printStackTrace();
                }
                else{
                    Looper.prepare();
                    Toast.makeText(getActivity(),"上传时出错：其他错误",
                            Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    e.printStackTrace();
                }

            }


            @Override
            public void onResponse(Call call, Response response)  {
                try (ResponseBody responseBody = response.body()) {
                    String responseString=responseBody.string();
                    JSONObject resultJson=JSONObject.parseObject(responseString);
                    int statusCode=resultJson.getInteger("code");
                    String message=resultJson.getString("msg");
                    Log.d("处理上传相应","接收到了上传响应结果，状态码为"+statusCode);

                    //如果响应结果状态码为成功的
                    if(statusCode>=200 && statusCode<400) {
                        //取出返回的数据；更新本地记录状态
                        JSONObject dataJson = resultJson.getJSONObject("data");
                        Log.d("处理上传响应","响应结果为成功（200），开始更新本地记录同步状态");
                        SyncUtil.processUploadResult(dataJson);


                        Looper.prepare();
                        Toast.makeText(getActivity(),"同步成功！",Toast.LENGTH_LONG).show();
                        Looper.loop();

                        Log.d("处理上传响应","!!!更新本地记录同步状态完成，同步成功！");
                    }
                    //响应结果为失败类型
                    else{
                        String errorMessage="响应状态码："+statusCode+",错误信息："+message+'\n';
                        Looper.prepare();
                        Toast.makeText(getActivity(),"上传本地记录失败\n"+errorMessage,
                                Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }
                } catch (IOException e) {
                    Looper.prepare();
                    Toast.makeText(getActivity(),"转换响应结果为字符串时出错",Toast.LENGTH_SHORT)
                            .show();
                    Looper.loop();
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 向服务器发起请求，获取待下载数据并对本地数据库执行相应更新
     */
    private void download(){
        JSONObject lastUpdateDates= SyncUtil.getTableLastUpdateTime();
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON,
                lastUpdateDates.toJSONString());

        //这里的主机地址要填电脑的ip地址 ,token要填用户登录时获取的token，超过一定时间会失效，
        // 需要重新获取
        Request request = new Request.Builder()
                .url("http://"+HOST_IP+":8080/app/download")
                .post(requestBody)
                .addHeader("token", UserUtil.getToken())
                .build();

        Log.d("发送下载请求","开始尝试下载");

        //使用异步请求（用同步发送请求需要在子线程上发起，因为安卓较新的版本都不允许在主线程发送网络请求）
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(e instanceof SocketTimeoutException){//判断超时异常
                    Looper.prepare();
                    Toast.makeText(getActivity(),"下载时出错：网络连接超时",
                            Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    e.printStackTrace();
                }
                if(e instanceof ConnectException){//判断连接异常，我这里是报Failed to connect to 10.7.5.144
                    Looper.prepare();
                    Toast.makeText(getActivity(),"下载时出错：连接到服务器失败",
                            Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    e.printStackTrace();
                }
                else{
                    Looper.prepare();
                    Toast.makeText(getActivity(),"下载时出错：其他错误",
                            Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    e.printStackTrace();
                }

            }


            @Override
            public void onResponse(Call call, Response response)  {
                try (ResponseBody responseBody = response.body()) {
                    String responseString=responseBody.string();
                    JSONObject resultJson=JSONObject.parseObject(responseString);
                    int statusCode=resultJson.getInteger("code");
                    String message=resultJson.getString("msg");

                    //如果响应结果状态码为成功的
                    if(statusCode>=200 && statusCode<400) {
                        //取出返回的数据；更新本地记录状态
                        JSONObject dataJson = resultJson.getJSONObject("data");

                        SyncUtil.processDownloadResult(dataJson);

                        Log.d("处理下载请求","下载成功，开始执行上传");
                        upload();

                        //syncTextView.setText("更新数据（上次同步时间："+new Date()+")");

                    }
                    //响应结果为失败类型
                    else{
                        String errorMessage="响应状态码："+statusCode+",错误信息："+message+'\n';
                        Looper.prepare();
                        Toast.makeText(getActivity(),"下载服务器待同步记录失败\n"+errorMessage,
                                Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }
                } catch (IOException e) {
                    Looper.prepare();
                    Toast.makeText(getActivity(),"转换响应结果为字符串时出错",Toast.LENGTH_SHORT)
                            .show();
                    Looper.loop();
                    e.printStackTrace();
                }
                catch (Exception e){
                    Looper.prepare();
                    Toast.makeText(getActivity(),"其他错误，详细信息见控制台",Toast.LENGTH_SHORT)
                            .show();
                    Looper.loop();
                    e.printStackTrace();
                }
            }
        });
    }

}
