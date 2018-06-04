
package com.android.taskallo.adapter.classify;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.taskallo.R;
import com.android.taskallo.bean.ClassifyTopBean;
import com.android.taskallo.core.utils.KeyConstant;
import com.android.taskallo.project.view.ProjListActivity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * @author gp
 */
public class Fragment0_1_Adapter extends RecyclerView.Adapter<Fragment0_1_Adapter.ViewHolder> {

    private final LayoutInflater mInflater;
    private Context context;
    private List<ClassifyTopBean> list;

    public interface OnItemClickLitener {
        void onItemClick(View view, int position, int text);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setmOnItemClickListener(OnItemClickLitener mOnItemClickListener) {
        this.mOnItemClickLitener = mOnItemClickListener;
    }

    public Fragment0_1_Adapter(Context context, List<ClassifyTopBean> list) {
        super();
        this.context = context;
        this.list = list;
        mInflater = LayoutInflater.from(context);
    }

    public void setList(List<ClassifyTopBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int vieype) {
        ViewHolder holder = new ViewHolder(mInflater.inflate(R.layout.item_frgment0_1,
                parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (list == null) {
            return;
        }
        final ClassifyTopBean categroyBean = list.get(position);
        final String name = categroyBean.getName();

        holder.tv_content.setText(name);
        holder.iconIv.setImageURI("");
        //为ItemView设置监听器
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProjListActivity.class);
                intent.putExtra(KeyConstant.ID, categroyBean.getId() + "");//原生手柄 id
                context.startActivity(intent);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView iconIv;
        private TextView tv_content;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_content = itemView.findViewById(R.id.singer_item_tv);
            iconIv = itemView.findViewById(R.id.classify_item_iv);
        }
    }
}














