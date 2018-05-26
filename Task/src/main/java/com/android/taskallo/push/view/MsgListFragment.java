package com.android.taskallo.push.view;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.taskallo.R;
import com.android.taskallo.push.model.PushMessage;
import com.android.taskallo.push.presenter.IPushMsgListPresenter;
import com.android.taskallo.push.presenter.PushMsgListPresenter;
import com.android.taskallo.view.ActionItem;
import com.android.taskallo.view.QuickAction;
import com.android.taskallo.view.VideoTabView;
import com.androidkun.xtablayout.XTabLayout;

import java.util.List;

/**
 * 显示消息列表
 * Created by zeng on 2016/11/23.
 */
public class MsgListFragment extends Fragment implements IPushMsgListView {
    public String TAG = MsgListFragment.class.getSimpleName();
    private Context context;
    private ListView listView;
    private PushMsgLvAdapter adapter;

    private IPushMsgListPresenter presenter;

    protected QuickAction mItemClickQuickAction;
    /**
     * 当前点击的列表 1.下载列表 2.完成列表
     */
    private int itemType;
    private int itemPosition;

    private long labelId = 1;
    private TextView msgEmptyTv;
    private VideoTabView videoTabView;
    private XTabLayout mTabLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        presenter = new PushMsgListPresenter(context, this);

    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable Bundle
            savedInstanceState) {

        //获取初始参数
        labelId = getArguments().getLong("labelId", 1);

        View view = inflater.inflate(R.layout.fragment_msg, container, false);

        listView = (ListView) view.findViewById(R.id.msg_listView);
        msgEmptyTv = (TextView) view.findViewById(R.id.no_msg_tv);

        mTabLayout = (XTabLayout) view.findViewById(R.id.msg_top_tab);
        setTabLayout();

        adapter = new PushMsgLvAdapter(getActivity(), getFragmentManager());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                PushMessage msg = (PushMessage) adapter.getItem(position);

                if (msg.getIsRead() == 0) {
                    ImageView img = (ImageView) view.findViewById(R.id.img_1);
                    img.setBackgroundResource(R.drawable.msg_dot_gray);
                }
                //通知
                if (msg.getType() == PushMessage.MSG_TYPE_TZ) {

                    Intent intent = new Intent(context, NotifyMsgDetailActivity.class);
                    intent.putExtra("msg", msg);
                    context.startActivity(intent);
                    //活动
                } else {
                    Intent intent = new Intent(context, MessageDetailActivity.class);
                    intent.putExtra("msgId", msg.getMsgId());
                    intent.putExtra("type", msg.getType());
                    context.startActivity(intent);
                }

                //清除notification
                NotificationManager manager = (NotificationManager) context.getSystemService
                        (Context.NOTIFICATION_SERVICE);
                manager.cancelAll();

            }
        });

        initPop();

        return view;
    }

    private void setTabLayout() {
        // 添加tab
        mTabLayout.addTab(mTabLayout.newTab().setIcon(R.drawable.seletor_main_bottom_tab_01));
        mTabLayout.addTab(mTabLayout.newTab().setIcon(R.drawable.seletor_main_bottom_tab_02));
        mTabLayout.getTabAt(1).select();
        mTabLayout.getTabAt(0).select();
        // 这样可以自定义tab的布局与内容了
//        mTabLayout.getTabAt(0).setCustomView(R.layout.tab_custom_view);
//        mTabLayout.getTabAt(0).getCustomView();


        // 设置监听
        mTabLayout.setOnTabSelectedListener(new XTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(XTabLayout.Tab tab) {
                Log.d(TAG, "选中1: " + tab.getPosition());
            }


            @Override
            public void onTabUnselected(XTabLayout.Tab tab) {
                Log.d(TAG, "未选中2: " + tab.getPosition());
            }


            @Override
            public void onTabReselected(XTabLayout.Tab tab) {
                Log.d(TAG, "选中3: " + tab.getPosition());
            }
        });
    }

    private void initPop() {

        // 设置Action
        mItemClickQuickAction = new QuickAction(context, QuickAction.VERTICAL);

        ActionItem pointItem = new ActionItem(1, "删除消息", null);
        mItemClickQuickAction.addActionItem(pointItem);

        mItemClickQuickAction.setOnActionItemClickListener(new QuickAction
                .OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction source, int pos, int actionId) {

                if (pos == 0) {

                    //删除消息
                    PushMessage msg = (PushMessage) adapter.getItem(itemPosition);
                    presenter.deleteMsg(msg.getId());

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //刷新消息列表
                    presenter.showMsgList((int) labelId, 1, 20);    //默认显示公告的前20条
                }

                //取消弹出框
                mItemClickQuickAction.dismiss();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long
                    id) {

                itemType = 2;
                itemPosition = position;

                //显示弹出框
                mItemClickQuickAction.show(view);

                return true;
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.showMsgList((int) labelId, 1, 20);    //默认显示公告的前20条
    }

    @Override
    public void showMsgList(List<PushMessage> msgList) {
        if (msgList == null || msgList.size() == 0) {
            msgEmptyTv.setVisibility(View.VISIBLE);
            return;
        }
        msgEmptyTv.setVisibility(View.GONE);
        adapter.setDate(msgList);
        adapter.notifyDataSetChanged();
    }
}
