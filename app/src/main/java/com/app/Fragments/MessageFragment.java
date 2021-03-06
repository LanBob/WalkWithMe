package com.app.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.JMS.activity.ChatListActivity;
import com.app.JMS.activity.SplashActivity;
import com.app.R;
import com.app.Util.StringUtil;
import com.app.activity.MessageInterScore;
import com.app.activity.Message_activity;
import com.app.commonAdapter.Com_Adapter;
import com.app.commonAdapter.Com_ViewHolder;
import com.app.entity.Message_dao;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by donglinghao on 2016-01-28.
 */
public class MessageFragment extends Fragment {
    private List<Message_dao> list;
    private RecyclerView recyclerView;
    private String message_name[] = {"消息", "相互验证", "通知"};
    private Integer message_image[] = {R.mipmap.message_confirm,
            R.mipmap.message_message,
            R.mipmap.message_tongzhi};
    private Message_dao dao;
    private View mRootView;
    private String url;
    private String userId;
    private String isAlreadyLogin;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.message_fragment, container, false);
            userId = StringUtil.getValue("username");
            isAlreadyLogin = StringUtil.getValue("isAlreadyLogin");

            //=========三个消息类型，设置数据，数据来源================================================================
            list = new ArrayList(message_image.length);
            recyclerView = mRootView.findViewById(R.id.message_recycleview);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            //数据设置
            for (int i = 0; i < message_image.length; ++i) {
                dao = new Message_dao();
                dao.setMessage_image(message_image[i]);
                dao.setMessage_name(message_name[i]);
                list.add(dao);
            }
            //=========三个消息类型，设置数据，数据来源================================================================

            recyclerView.setAdapter(new Com_Adapter<Message_dao>(getContext(), R.layout.message_recyclerview_item, list) {
                @Override
                public void convert(Com_ViewHolder holder, final Message_dao message_dao) {
                    if(message_dao != null){
                        holder.setText(R.id.message_text, message_dao.getMessage_name());
                        holder.setImageResource(R.id.message_image, message_dao.getMessage_image());
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getContext(), " " + message_dao.getMessage_name(), Toast.LENGTH_SHORT).show();
                                switch (message_dao.getMessage_name()) {
                                    case "消息":
                                        if("N".equals(isAlreadyLogin) || userId == null || !StringUtil.isMobile(userId)){
                                            Toast.makeText(getContext(), "通知未开放", Toast.LENGTH_SHORT).show();
                                        }else {
                                            Intent intent = new Intent(getActivity(), ChatListActivity.class);
                                            intent.putExtra("chat_item", "daoyou");
                                            getActivity().startActivity(intent);
                                        }

                                        break;
                                    case "相互验证":
                                        if ("Y".equals(isAlreadyLogin) && userId != null && !"".equals(userId)) {
                                            Intent intent1 = new Intent(getActivity(), MessageInterScore.class);
                                            getActivity().startActivity(intent1);
                                        } else {
                                            Toast.makeText(getContext(), "请先进行登录", Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                    case "通知":
                                        if (true) {
                                            Toast.makeText(getContext(), "通知未开放", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Intent intent0 = new Intent(getActivity(), Message_activity.class);
                                            intent0.putExtra("chat_item", "tongzhi");
                                            getActivity().startActivity(intent0);
                                        }
                                        break;
                                    default:
                                        Intent intent3 = new Intent(getActivity(), Message_activity.class);
                                        intent3.putExtra("chat_item", "daoyou");
                                        getActivity().startActivity(intent3);
                                        break;
                                }
                            }
                        });
                    }

                }
            });



        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }
}
