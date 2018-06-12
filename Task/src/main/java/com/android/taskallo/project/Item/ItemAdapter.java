package com.android.taskallo.project.Item;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.taskallo.R;
import com.android.taskallo.bean.ListInfo;
import com.android.taskallo.core.utils.TextUtil;
import com.android.taskallo.project.view.ProjListActivity;
import com.android.taskallo.util.ToastUtil;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.List;


public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int ITEM_TYPE_FOOTER = 2;
    private List<ListInfo> mList;
    private int dm_margin_left;
    ProjListActivity context;
    private LayoutInflater from;

    public ItemAdapter(Context context, List<ListInfo> list) {
        dm_margin_left = context.getResources().
                getDimensionPixelOffset(R.dimen.dm_200);
        from = LayoutInflater.from(context);
        mList = list;
    }

    public void setContext(ProjListActivity context) {
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return mList == null ? 1 : mList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return ITEM_TYPE_FOOTER;
        } else {
            return 0;
        }
    }

    /*底部Item*/
    class FootHolder extends RecyclerView.ViewHolder {
        public TextView footBt;

        public FootHolder(View itemView) {
            super(itemView);
            footBt = itemView.findViewById(R.id.proj_list_item_footer_add);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_FOOTER) {
            View view = from.inflate(R.layout.layout_proj_item_footer, parent, false);
            FootHolder viewHolder = new FootHolder(view);
            return viewHolder;
        } else {
            View inflate = from.inflate(R.layout.layout_proj_item, parent, false);
            ItemViewHolder itemViewHolder = new ItemViewHolder(inflate);
            return itemViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder hold, int position) {
        if (hold instanceof FootHolder) {//最后一个
            FootHolder footHolder = (FootHolder) hold;
            footHolder.footBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showAddCardAlertDialog(R.string.list_title);
                }
            });
        } else {
            ItemViewHolder holder = (ItemViewHolder) hold;
            holder.itemItemRV.setLayoutManager(
                    new ItemLayoutManager(holder.itemView.getContext()));
            RecyclerView.Adapter adapter = new ItemItemAdapter(context, position, 10);
            holder.itemItemRV.setAdapter(adapter);
            holder.mItemAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showAddCardAlertDialog(R.string.card_title);
                }
            });
            if (holder.itemItemRV.getItemDecorationAt(0) == null) {
                holder.itemItemRV.addItemDecoration(new ItemLineDecoration());
            }
            holder.itemItemRV.getItemAnimator().setAddDuration(0);
            holder.itemItemRV.getItemAnimator().setRemoveDuration(0);

            holder.mMenuBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopWindow(view);
                }
            });
        }
    }

    Button listCopyListBt;
    PopupWindow popWindow;//分享提醒

    private void showPopWindow(View v) {
        View inflate = from.inflate(R.layout.layout_proj_list_menu, null);
        listCopyListBt = (Button) inflate.findViewById(R.id.list_copy_list_bt);

        popWindow = new PopupWindow(inflate, LinearLayout.LayoutParams
                .WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        inflate.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int[] location = new int[2];
        // 使其聚集 ，要想监听菜单里控件的事件就必须要调用此方法
        popWindow.setFocusable(true);
        // 设置允许在外点击消失
        popWindow.setOutsideTouchable(false);
        // 获得位置 这里的v是目标控件，就是你要放在这个v的上面还是下面
        v.getLocationOnScreen(location);
        // 设置背景，这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        //软键盘不会挡着popupwindow
        popWindow.setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_ADJUST_RESIZE);

        popWindow.showAsDropDown(v, dm_margin_left, dm_margin_left / 10);
        listCopyListBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //前往去分享
                popWindow.dismiss();
            }
        });

    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        RecyclerView itemItemRV;
        TextView mItemTitle;
        Button mItemAdd;
        private EditText mItemEnterEt;
        private Button mMenuBt;

        ItemViewHolder(View itemView) {
            super(itemView);
            init(itemView);
        }

        private void init(View itemView) {
            itemItemRV = itemView.findViewById(R.id.item_recycler_view);
            mItemTitle = itemView.findViewById(R.id.proj_list_item_title);
            mMenuBt = itemView.findViewById(R.id.proj_list_item_menu_bt);
            mItemAdd = itemView.findViewById(R.id.item_add);
        }

    }

    //添加卡片
    private void showAddCardAlertDialog(final int hintText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Dialog_add_card);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.layout_dialog_add_card, null);
        Button btnPositive = (Button) v.findViewById(R.id.dialog_add_card_ok);
        final MaterialEditText etContent = (MaterialEditText) v.findViewById(R.id
                .dialog_add_card_title);
        etContent.setHint(hintText);
        etContent.setFloatingLabelText(context.getString(hintText));
        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    etContent.setError("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        final Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setContentView(v);//自定义布局应该在这里添加，要在dialog.show()的后面
        dialog.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);//可以设置显示的位置
        btnPositive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String str = etContent.getText().toString();
                if (TextUtil.isEmpty(str)) {
                    etContent.setError(context.getString(R.string.enter_cannot_empty));
                } else {
                    dialog.dismiss();
                }
                //添加卡片
                if (hintText == R.string.card_title) {
                    ToastUtil.show(context, "卡片");
                } else {
                    ToastUtil.show(context, "列表");
                    //添加列表

                }
            }
        });
    }
}
