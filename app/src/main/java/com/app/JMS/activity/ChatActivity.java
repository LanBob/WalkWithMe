package com.app.JMS.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.app.JMS.ChatService;
import com.app.JMS.adapter.ChatAdapter;
import com.app.JMS.bean.AudioMsgBody;
import com.app.JMS.bean.ChatListBean;
import com.app.JMS.bean.FileMsgBody;
import com.app.JMS.bean.ImageMsgBody;
import com.app.JMS.bean.Message;
import com.app.JMS.bean.MsgSendStatus;
import com.app.JMS.bean.MsgType;
import com.app.JMS.ChatService.MyBinder;
import com.app.JMS.bean.SqlMessage;
import com.app.JMS.bean.TextMsgBody;
import com.app.JMS.bean.VideoMsgBody;
import com.app.JMS.util.ChatUiHelper;
import com.app.JMS.util.FileUtils;
import com.app.JMS.util.LogUtil;
import com.app.JMS.util.MessageUtil;
import com.app.JMS.util.PictureFileUtil;
import com.app.JMS.widget.MediaManager;
import com.app.JMS.widget.RecordButton;
import com.app.JMS.widget.StateButton;
import com.app.MainApplication;
import com.app.R;
import com.app.Util.LogOutUtil;
import com.app.Util.MyUrl;
import com.app.Util.StringUtil;
import com.app.modle.WebSocketUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dhh.websocket.WebSocketSubscriber;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.WebSocket;
import okio.ByteString;

/**
 * 第一步、setWebSocket(fromUserId);
 */
public class ChatActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.llContent)
    LinearLayout mLlContent;
    @BindView(R.id.rv_chat_list)
    RecyclerView mRvChat;
    @BindView(R.id.et_content)
    EditText mEtContent;
    @BindView(R.id.bottom_layout)
    RelativeLayout mRlBottomLayout;//表情,添加底部布局
    @BindView(R.id.ivAdd)
    ImageView mIvAdd;
    @BindView(R.id.ivEmo)
    ImageView mIvEmo;
    @BindView(R.id.btn_send)
    StateButton mBtnSend;//发送按钮
    @BindView(R.id.ivAudio)
    ImageView mIvAudio;//录音图片
    @BindView(R.id.btnAudio)
    RecordButton mBtnAudio;//录音按钮
    @BindView(R.id.rlEmotion)
    LinearLayout mLlEmotion;//表情布局
    @BindView(R.id.llAdd)
    LinearLayout mLlAdd;//添加布局
    @BindView(R.id.swipe_chat)
    SwipeRefreshLayout mSwipeRefresh;//下拉刷新
    private ChatAdapter mAdapter;
    public static String fromUserId = "1168";
    public static String toUserId = "13724158682";
    public static final int REQUEST_CODE_IMAGE = 0000;
    public static final int REQUEST_CODE_VEDIO = 1111;
    public static final int REQUEST_CODE_FILE = 2222;

    private List<Message> mReceiveMsgList = null;

    private Disposable disposable;

    private Map<String, String> map;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
//        发送给谁
        toUserId = intent.getStringExtra("userId");

        if (toUserId == null || !StringUtil.isMobile(toUserId) || "".equals(toUserId)) {
            toUserId = MyUrl.getKefu();
        }
        fromUserId = StringUtil.getValue("username");
        initContent();

    }

    private ImageView ivAudio;

    protected void initContent() {
        ButterKnife.bind(this);
        map = new HashMap<>();
        mReceiveMsgList = new ArrayList<>();
        mAdapter = new ChatAdapter(this, mReceiveMsgList);
        LinearLayoutManager mLinearLayout = new LinearLayoutManager(this);
        mRvChat.setLayoutManager(mLinearLayout);
        mRvChat.setAdapter(mAdapter);
        mSwipeRefresh.setOnRefreshListener(this);
        initChatUi();
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (ivAudio != null) {
                    ivAudio.setBackgroundResource(R.mipmap.audio_animation_list_right_3);
                    ivAudio = null;
                    MediaManager.reset();
                } else {
                    ivAudio = view.findViewById(R.id.ivAudio);
                    MediaManager.reset();
                    ivAudio.setBackgroundResource(R.drawable.audio_animation_right_list);
                    AnimationDrawable drawable = (AnimationDrawable) ivAudio.getBackground();
                    drawable.start();
                    MediaManager.playSound(ChatActivity.this, ((AudioMsgBody) mAdapter.getData().get(position).getBody()).getLocalPath(), new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            LogUtil.d("开始播放结束");
                            ivAudio.setBackgroundResource(R.mipmap.audio_animation_list_right_3);
                            MediaManager.release();
                        }
                    });
                }
            }
        });
//        获取和发送
        if (fromUserId != null && StringUtil.isMobile(fromUserId)) {
            Log.e("in", "insert set" + fromUserId);
//            startService();
//            setWebSocket(fromUserId,ChatActivity.this);
//            MessageUtil.setWebSocket(fromUserId,getApplicationContext().getFilesDir().getAbsolutePath());
            MessageUtil.clearCount(toUserId);
            reciveMessage();
        }
    }



    @Override
    protected void onDestroy() {
        if (disposable != null) {
            disposable.dispose();
        }
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        reciveMessage();
        super.onRestart();
    }

    public void reciveMessage() {

        Observable.interval(0, 5, TimeUnit.SECONDS)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return aLong + 1;
                    }
                })
                .take(30)
                .observeOn(AndroidSchedulers.mainThread())//ui线程中进行控件更新
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                    }
                }).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(Long num) {
                //消息是从谁发来的
                List<SqlMessage> sqlMessageList = StringUtil.getSqlMessage(fromUserId + toUserId);
////                        如果这个人有消息,进行转换
                for (SqlMessage sqlMessage : sqlMessageList) {
                    File f = new File(sqlMessage.getPath());

                    byte[] bytes = StringUtil.read(f);
//                    Message message = (Message) StringUtil.byteToObject(bytes);
                    Message message = (Message) MessageUtil.toObject(bytes);


                    if (mReceiveMsgList.size() == 0) {
                        message.setSentStatus(MsgSendStatus.SENT);
                        mReceiveMsgList.add(message);
                    }
                    int flag = 0;
                    for (int i = 0; i < mReceiveMsgList.size(); ++i) {
                        if (mReceiveMsgList.get(i).getUuid().equals(message.getUuid())) {
                            flag = 1;
                            break;
                        }
                    }
                    if (flag == 0) {
                        Log.e("UUID", message.getUuid() + "");
                        message.setSentStatus(MsgSendStatus.SENT);
                        mReceiveMsgList.add(message);
                    }
                }
                Log.e("UUID", "外面");
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                if (disposable != null) {
                    disposable.dispose();
                }
            }

            @Override
            public void onComplete() {
                //回复原来初始状态
                mAdapter.notifyDataSetChanged();
                if (disposable != null) {
                    disposable.dispose();
                }
            }
        });
    }


    @Override
    public void onRefresh() {
        //下拉刷新模拟获取历史消息

//        List<Message> mReceiveMsgList = new ArrayList<Message>();

//        //构建文本消息
//        Message mMessgaeText = getBaseReceiveMessage(MsgType.TEXT);
//        TextMsgBody mTextMsgBody = new TextMsgBody();
//        mTextMsgBody.setMessage("收到的消息");
//        mMessgaeText.setBody(mTextMsgBody);
//        mReceiveMsgList.add(mMessgaeText);
//        LogUtil.d("receive message " + mMessgaeText.toString());

//        //构建图片消息
//        Message mMessgaeImage = getBaseReceiveMessage(MsgType.IMAGE);
//        ImageMsgBody mImageMsgBody = new ImageMsgBody();
//        mImageMsgBody.setThumbUrl("http://pic19.nipic.com/20120323/9248108_173720311160_2.jpg");
//        mMessgaeImage.setBody(mImageMsgBody);
//        mReceiveMsgList.add(mMessgaeImage);
        ////删除
//          mReceiveMsgList.add(mMessgaeFile);


        mAdapter.notifyDataSetChanged();
        mSwipeRefresh.setRefreshing(false);
    }


    private void initChatUi() {
        //mBtnAudio
        final ChatUiHelper mUiHelper = ChatUiHelper.with(this);
        mUiHelper.bindContentLayout(mLlContent)
                .bindttToSendButton(mBtnSend)
                .bindEditText(mEtContent)
                .bindBottomLayout(mRlBottomLayout)
                .bindEmojiLayout(mLlEmotion)
                .bindAddLayout(mLlAdd)
                .bindToAddButton(mIvAdd)
                .bindToEmojiButton(mIvEmo)
                .bindAudioBtn(mBtnAudio)
                .bindAudioIv(mIvAudio)
                .bindEmojiData();

        //底部布局弹出,聊天列表上滑
        mRvChat.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    mRvChat.post(new Runnable() {
                        @Override
                        public void run() {
                            LogUtil.d("上滑");
                            if (mAdapter.getItemCount() > 0) {
                                mRvChat.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                            }
                        }
                    });
                }
            }
        });

        //点击空白区域关闭键盘
        mRvChat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mUiHelper.hideBottomLayout(false);
                mUiHelper.hideSoftInput();
                mEtContent.clearFocus();
                mIvEmo.setImageResource(R.mipmap.ic_emoji);
                return false;
            }
        });

        //
        ((RecordButton) mBtnAudio).setOnFinishedRecordListener(new RecordButton.OnFinishedRecordListener() {
            @Override
            public void onFinishedRecord(String audioPath, int time) {
                LogUtil.d("录音结束回调");
                File file = new File(audioPath);
                if (file.exists()) {
                    sendAudioMessage(audioPath, time);
                }
            }
        });

    }

    @OnClick({R.id.btn_send, R.id.rlPhoto, R.id.rlVideo, R.id.rlLocation, R.id.rlFile})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                    sendTextMsg(mEtContent.getText().toString());
                    mEtContent.setText("");
                break;
            case R.id.rlPhoto:
                Toast.makeText(ChatActivity.this,"您好，由于服务器原因，未开放图片聊天功能",Toast.LENGTH_LONG).show();
//                PictureFileUtil.openGalleryPic(ChatActivity.this, REQUEST_CODE_IMAGE);
                break;
            case R.id.rlVideo:
                Toast.makeText(ChatActivity.this,"您好，由于服务器原因，未开放视频聊天功能",Toast.LENGTH_LONG).show();
//                PictureFileUtil.openGalleryAudio(ChatActivity.this, REQUEST_CODE_VEDIO);
                break;
            case R.id.rlFile:
                Toast.makeText(ChatActivity.this,"您好，由于服务器原因，未开放文件聊天功能",Toast.LENGTH_LONG).show();
//                PictureFileUtil.openFile(ChatActivity.this, REQUEST_CODE_FILE);
                break;
            case R.id.rlLocation:
                Toast.makeText(ChatActivity.this,"您好，由于服务器原因，未开放位置功能",Toast.LENGTH_LONG).show();
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_FILE:
                    Toast.makeText(ChatActivity.this, "无法发送文件", Toast.LENGTH_SHORT).show();
//                    String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
//                    LogUtil.d("获取到的文件路径:"+filePath);
//                    sendFileMessage(mSenderId, mTargetId, filePath);
                    break;
                case REQUEST_CODE_IMAGE:

                    // 图片选择结果回调
                    List<LocalMedia> selectListPic = PictureSelector.obtainMultipleResult(data);
                    for (LocalMedia media : selectListPic) {
                        LogUtil.d("获取图片路径成功:" + media.getPath());
                        sendImageMessage(media);
                    }
                    break;
                case REQUEST_CODE_VEDIO:
                    // 视频选择结果回调
                    List<LocalMedia> selectListVideo = PictureSelector.obtainMultipleResult(data);
                    for (LocalMedia media : selectListVideo) {
                        LogUtil.d("获取视频路径成功:" + media.getPath());
                        sendVedioMessage(media);
                    }
                    break;
            }
        }
    }


    //文本消息
    private void sendTextMsg(String hello) {
        final Message mMessgae = getBaseSendMessage(MsgType.TEXT);
        TextMsgBody mTextMsgBody = new TextMsgBody();
        mTextMsgBody.setMessage(hello);
        mMessgae.setBody(mTextMsgBody);

        //开始发送
        mAdapter.addData(mMessgae);
        //模拟两秒后发送成功
//        if (chatService != null)
        updateMsg(mMessgae);
    }


    //图片消息
    private void sendImageMessage(final LocalMedia media) {
        final Message mMessgae = getBaseSendMessage(MsgType.IMAGE);
        ImageMsgBody mImageMsgBody = new ImageMsgBody();
        mImageMsgBody.setThumbUrl(media.getCompressPath());
        mMessgae.setBody(mImageMsgBody);
        //开始发送
        mAdapter.addData(mMessgae);
        //模拟两秒后发送成功
//        if (chatService != null)
        updateMsg(mMessgae);
    }


    //视频消息
    private void sendVedioMessage(final LocalMedia media) {
        final Message mMessgae = getBaseSendMessage(MsgType.VIDEO);
        //生成缩略图路径
        String vedioPath = media.getPath();
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(vedioPath);
        Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime();
        String imgname = System.currentTimeMillis() + ".jpg";
        String urlpath = Environment.getExternalStorageDirectory() + "/" + imgname;
        File f = new File(urlpath);
        try {
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            LogUtil.d("视频缩略图路径获取失败：" + e.toString());
            e.printStackTrace();
        }
        VideoMsgBody mImageMsgBody = new VideoMsgBody();
        mImageMsgBody.setExtra(urlpath);
        mMessgae.setBody(mImageMsgBody);

        //开始发送
        mAdapter.addData(mMessgae);
        //模拟两秒后发送成功
//        if (chatService != null)
        updateMsg(mMessgae);

    }


    //语音消息
    private void sendAudioMessage(final String path, int time) {
        final Message mMessgae = getBaseSendMessage(MsgType.AUDIO);
        AudioMsgBody mFileMsgBody = new AudioMsgBody();
        mFileMsgBody.setLocalPath(path);
        mFileMsgBody.setDuration(time);
        mMessgae.setBody(mFileMsgBody);
        //开始发送
        mAdapter.addData(mMessgae);
        //模拟两秒后发送成功
//        if (chatService != null)
        updateMsg(mMessgae);
    }

    /**
     * 设置从谁发来，到谁那里去
     *
     * @param msgType
     * @return
     */
    private Message getBaseSendMessage(MsgType msgType) {
        Message mMessgae = new Message();
        mMessgae.setUuid(UUID.randomUUID() + "");
//
        mMessgae.setSenderId(fromUserId);
        mMessgae.setTargetId(toUserId);
//
        mMessgae.setSentTime(System.currentTimeMillis());
        mMessgae.setSentStatus(MsgSendStatus.SENDING);
        mMessgae.setMsgType(msgType);
        return mMessgae;
    }


    private Message getBaseReceiveMessage(MsgType msgType) {
        Message mMessgae = new Message();
        mMessgae.setUuid(UUID.randomUUID() + "");
        mMessgae.setSenderId(toUserId);
        mMessgae.setTargetId(fromUserId);
        mMessgae.setSentTime(System.currentTimeMillis());
        mMessgae.setSentStatus(MsgSendStatus.SENDING);
        mMessgae.setMsgType(msgType);
        return mMessgae;
    }


    private void updateMsg(final Message mMessgae) {
        mRvChat.scrollToPosition(mAdapter.getItemCount() - 1);


        ByteString byteString = StringUtil.getByteString(mMessgae);

        MessageUtil.savaMessage(byteString,
                getApplicationContext().getFilesDir().getAbsolutePath());

        WebSocketUtil.sendByte(MyUrl.add_Wsurl(fromUserId),byteString);
//        if(MessageUtil.getMwebSocket()!= null)
//            MessageUtil.get

        //模拟2秒后发送成功
        new Handler().postDelayed(new Runnable() {
            public void run() {
                int position = 0;
                mMessgae.setSentStatus(MsgSendStatus.SENT);
                //更新单个子条目
                for (int i = 0; i < mAdapter.getData().size(); i++) {
                    Message mAdapterMessage = mAdapter.getData().get(i);
                    if (mMessgae.getUuid().equals(mAdapterMessage.getUuid())) {
                        position = i;
                        break;
                    }
                }
                mAdapter.notifyItemChanged(position);
            }
        }, 500);
    }


}
