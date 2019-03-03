package com.app.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.R;
import com.app.Util.MyUrl;
import com.app.Util.StringUtil;
import com.app.activity.Authenticate;
import com.app.activity.EditOwnData;
import com.app.activity.Follow_collection_star;
import com.app.activity.Login;
import com.app.activity.ManagerEntity;
import com.app.activity.Mineitem;
import com.app.commonAdapter.Com_ViewHolder;
import com.app.commonAdapter.MultiItemTypeSupport;
import com.app.commonAdapter.MutiCommomAdapter;
import com.app.entity.HeadImage;
import com.app.entity.Person_setting;
import com.app.modle.HttpMethods;
import com.app.modle.ResponseResult;
import com.app.entity.MineRecycleItemDao;
import com.app.view.CircleImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;


import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


/**
 * 特别提醒，由于bug，在设置多种类型的item的时候，由于使用position来绑定视图数据，因此，每增加一种类型要空掉第一个数据
 * Created by donglinghao on 2016-01-28.
 */
public class MineFragment extends Fragment {
    /**
     * 目的：设置“我的”页面的UI
     */
    private RecyclerView recyclerView;
    private MineRecycleItemDao mineRecycleItemDaoData;
    ImageView settingImage;
    private CircleImageView head_image;
    public static final int REQUEST_CODE_IMAGE = 0000;


    //设置View对象
    private View mRootView;
    //刷新的对象的
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView settting_button;
    private LinearLayout beforecheckin;
    private LinearLayout altercheckin;

    //子项的数据添加到listDao，每一个子项的数据就是一个MineRecycleItemDao，所以，一个item对象就
    //要通过listDao来设置它的值
    List<MineRecycleItemDao> listDao;

    //====================图片对应关系==============================================
    //自定义的图片，用来放在listDao
    Integer[] image = {R.drawable.gril,
            R.drawable.mine_member_apply, R.drawable.mine_travel,
            R.drawable.mine_order, R.drawable.mine_chage_password,
            R.drawable.mine_feedback, R.drawable.mine_back_login,
            R.drawable.mine_add_account,
            R.drawable.mine_about, R.drawable.mine_feedback};
    String[] mineItem = {
            "账号", "申请成为导游", "我的旅行", "关注发现", "修改密码", "反馈", "退出登录", "修改个人信息", "关于", "管理员入口"
    };
    //====================图片对应关系==============================================

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            //任意一个layout对象，比如让message的layout对象，MineFragmendt的layout对象
            mRootView = inflater.inflate(R.layout.mine_fragment, container, false);
            //得到一个View，指定的Item的View

            /**
             * 特别注意在这里卡住好久，这里需要通过View对象来findViewById，别的途径都不行
             * 这个就能够得到RecycleView对象
             */
            recyclerView = mRootView.findViewById(R.id.recyclerView);

            /**
             *设置布局管理器
             * 注意：直接通过getContext()获得此Fragment所属的Activity对象
             */

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));//设置布局管理器，这里选择用竖直的列表

            listDao = new ArrayList<>(image.length);

            //=================设置listDao的数据，也就是每一个itemView的数据==================
            for (int i = 0; i < image.length; ++i) {
                mineRecycleItemDaoData = new MineRecycleItemDao();
                mineRecycleItemDaoData.setMessage(mineItem[i]);
                mineRecycleItemDaoData.setImage(image[i]);
                mineRecycleItemDaoData.setIndex(i);//从1-8有效
                listDao.add(mineRecycleItemDaoData);
            }
            //=================设置listDao的数据，也就是每一个itemView的数据==================
            //=======================设置种类型的布局(position有坑)==========================
            MultiItemTypeSupport m = new MultiItemTypeSupport() {
                @Override
                public int getLayoutId(int itemType) {
                    if (itemType == 0) {
                        return R.layout.mine_header;
                    } else {
                        return R.layout.mine_item;
                    }
                }

                @Override
                public int getItemViewType(int position, Object o) {
                    if (position == 0) {
                        return 0;//头部
                    } else return 1;
                }
            };
            //=======================设置种类型的布局===========

            recyclerView.setAdapter(new MutiCommomAdapter<MineRecycleItemDao>(getContext(), listDao, m) {
                @Override
                public void convert(final Com_ViewHolder holder, final MineRecycleItemDao mineRecycleItemDao) {
                    final int i = holder.getItemViewType();
                    if (i != 0) {
                        //处理常规item
                        holder.setText(R.id.mine_textView, mineRecycleItemDao.getMessage());
                        holder.setImageResource(R.id.mine_imageView, mineRecycleItemDao.getImage());
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //控制跳转  , "申请成为导游", "我的旅行", "关注发现", "修改密码", "反馈", "退出登录", "添加账号", "关于"
                                String b = "";
                                b = StringUtil.getValue("isAlreadyLogin");
                                Log.e("myindex", "" + mineRecycleItemDao.getIndex() + " -->" + mineRecycleItemDao.getMessage());

                                switch (mineRecycleItemDao.getIndex()) {
                                    case 1://申请成为导游
                                        if ("Y".equals(b)) {
                                            //需要修改,改为指向导游验证
                                            Intent intent1 = new Intent(getActivity(), Authenticate.class);
                                            intent1.putExtra("index", "1");
                                            getActivity().startActivity(intent1);
                                        } else {
                                            Toast.makeText(getContext(), "请登录", Toast.LENGTH_SHORT).show();
                                            Intent intent_login = new Intent(getActivity(), Login.class);
                                            intent_login.putExtra("index", "7");
                                            getActivity().startActivity(intent_login);
                                        }
                                        break;
                                    case 2://我的旅行
                                        if ("Y".equals(b)) {
                                            Intent intent2 = new Intent(getActivity(), Mineitem.class);
                                            intent2.putExtra("index", "2");
                                            getActivity().startActivity(intent2);
                                        } else {
                                            Toast.makeText(getContext(), "请登录", Toast.LENGTH_SHORT).show();
                                            Intent intent_login = new Intent(getActivity(), Login.class);
                                            intent_login.putExtra("index", "7");
                                            getActivity().startActivity(intent_login);
                                        }
                                        break;
                                    case 3://关注发现
                                        if ("Y".equals(b)) {
                                            Intent intent3 = new Intent(getActivity(), Follow_collection_star.class);
                                            getActivity().startActivity(intent3);
                                        } else {
                                            Toast.makeText(getContext(), "请登录", Toast.LENGTH_SHORT).show();
                                            Intent intent_login = new Intent(getActivity(), Login.class);
                                            intent_login.putExtra("index", "7");
                                            getActivity().startActivity(intent_login);
                                        }
                                        break;
                                    case 4://修改密码
                                        if ("Y".equals(b)) {
                                            Intent intent4 = new Intent(getActivity(), Login.class);
                                            intent4.putExtra("index", "4");
                                            getActivity().startActivity(intent4);
                                        } else {
                                            Toast.makeText(getContext(), "请登录", Toast.LENGTH_SHORT).show();
                                            Intent intent_login = new Intent(getActivity(), Login.class);
                                            intent_login.putExtra("index", "7");
                                            getActivity().startActivity(intent_login);
                                        }
                                        break;
                                    case 5://反馈
                                        Intent intent5 = new Intent(getActivity(), Mineitem.class);
                                        intent5.putExtra("index", "5");
                                        getActivity().startActivity(intent5);
                                        break;
                                    case 6://申请退出登录
                                        Intent intent6 = new Intent(getActivity(), Mineitem.class);
                                        intent6.putExtra("index", "6");
                                        //getActivity().startActivity(intent6);
                                        getActivity().startActivityForResult(intent6, 1);
                                        break;

                                    case 7://修改个人信息
                                        if ("Y".equals(b)) {
                                            Toast.makeText(getContext(), "修改个人信息", Toast.LENGTH_SHORT).show();
                                            Intent intent_edit = new Intent(getActivity(), EditOwnData.class);
                                            intent_edit.putExtra("index", "7");//设置信息
                                            getActivity().startActivity(intent_edit);
                                        } else {
                                            Toast.makeText(getContext(), "请登录", Toast.LENGTH_SHORT).show();
                                            Intent intent_login = new Intent(getActivity(), Login.class);
                                            intent_login.putExtra("index", "7");
                                            getActivity().startActivity(intent_login);
                                        }
                                        break;
                                    case 8://关于
                                        Intent intent8 = new Intent(getActivity(), Mineitem.class);
                                        intent8.putExtra("index", "8");
                                        getActivity().startActivity(intent8);
                                        break;
                                    default:
                                        Intent intent9 = new Intent(getActivity(), ManagerEntity.class);
                                        startActivity(intent9);
                                        break;
                                }
                            }
                        });

                    } else {//就是点击头像区域
                        beforecheckin = holder.itemView.findViewById(R.id.beforecheckin);
                        altercheckin = holder.itemView.findViewById(R.id.aftercheckin);
                        settingImage = holder.itemView.findViewById(R.id.setting);
                        head_image = holder.itemView.findViewById(R.id.head_image);
                        settingImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), EditOwnData.class);
                                getActivity().startActivity(intent);
                            }
                        });

                        String b = StringUtil.getValue("isAlreadyLogin");

                        //如果已经登录，那么通过id和token去请求数据，请求成功，则获取数据并保存
                        if ("Y".equals(b)) {
                            final String username = StringUtil.getValue("username");
                            String isAlreadySetOwnData = StringUtil.getValue("isAlreadySetOwnData");
                            Long userID = null;
                            if (username != null)
                                userID = Long.valueOf(username);
                            Log.d("isAlreadySetOwnData", isAlreadySetOwnData + "");

                            //登录，有userName且已经有个人信息
                            if ((userID != 0 || username != null) && "Y".equals(isAlreadySetOwnData)) {
                                beforecheckin.setVisibility(View.GONE);
                                altercheckin.setVisibility(View.VISIBLE);

                                //已经登录，且不为
                                //==================================================获取个人信息
                                Observer<ResponseResult<Person_setting>> observer = new Observer<ResponseResult<Person_setting>>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(ResponseResult<Person_setting> personResponseResult) {

                                        Person_setting person_setting = personResponseResult.getData();
                                        if (person_setting != null) {
                                            //空指针异常
                                            TextView mine_head_textView_name = holder.itemView.findViewById(R.id.mine_head_name);
                                            TextView mine_head_textView_sex = holder.itemView.findViewById(R.id.mine_head_sex);
                                            TextView mine_head_textView_introduce = holder.itemView.findViewById(R.id.mine_head_introduce);
                                            mine_head_textView_name.setText(person_setting.getAlias());
                                            mine_head_textView_sex.setText(person_setting.getSex());
                                            mine_head_textView_introduce.setText(person_setting.getIntroduce());
                                        }

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.e("e", "eee" + e.getMessage());
                                    }

                                    @Override
                                    public void onComplete() {
                                        Log.e("com", "eee");
                                    }
                                };
                                //==================================================获取个人信息
                                Observer<ResponseResult<HeadImage>> responseResultObserver = new Observer<ResponseResult<HeadImage>>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(ResponseResult<HeadImage> headImageResponseResult) {
                                        HeadImage headImage = headImageResponseResult.getData();
                                        if (headImage != null && headImageResponseResult.getCode() == 1) {
                                            CircleImageView circleImageView = holder.itemView.findViewById(R.id.head_image);
                                            RequestOptions options = new RequestOptions()
                                                    .centerCrop()
                                                    .placeholder(R.drawable.gray_bg)
                                                    .error(R.drawable.chat_girl)
                                                    .priority(Priority.HIGH)
                                                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

                                            Glide.with(getActivity())
                                                    .load(MyUrl.add_Path(headImage.getHead_image()))
                                                    // .listener(mRequestListener)
                                                    .apply(options)
                                                    .into(circleImageView);
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                };

                                HttpMethods.getInstance()
                                        .getPerson(userID, observer);
                                HttpMethods.getInstance()
                                        .getHeadImage(userID.toString(), responseResultObserver);

                            } else {
                                beforecheckin.setVisibility(View.GONE);
                                altercheckin.setVisibility(View.VISIBLE);
                                altercheckin.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getActivity(), EditOwnData.class);
                                        intent.putExtra("username", username);
                                        getActivity().startActivity(intent);
                                    }
                                });
                            }
                            //==================================================
                        } else {
                            beforecheckin.setVisibility(View.VISIBLE);
                            altercheckin.setVisibility(View.GONE);
                            beforecheckin.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getActivity(), Login.class);
                                    getActivity().startActivity(intent);
                                }
                            });
                        }
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

