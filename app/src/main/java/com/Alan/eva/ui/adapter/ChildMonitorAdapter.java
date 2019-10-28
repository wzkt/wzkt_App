package com.Alan.eva.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;

import com.Alan.eva.R;
import com.Alan.eva.http.core.IResultHandler;
import com.Alan.eva.http.get.DeleteMonitorGet;
import com.Alan.eva.model.ChildMonitorData;
import com.Alan.eva.result.Res;
import com.Alan.eva.tools.LogUtil;
import com.Alan.eva.tools.Tools;
import com.Alan.eva.ui.core.AbsAdapter;
import com.Alan.eva.ui.dialog.OperateDialog;

import java.util.ArrayList;

/**
 * Created by CW on 2017/3/23.
 * 孩子监护人列表适配器
 */
public class ChildMonitorAdapter extends AbsAdapter<ChildMonitorData> {

    public ChildMonitorAdapter(ArrayList<ChildMonitorData> dataList, Activity context) {
        super(dataList, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = getMiInflater().inflate(R.layout.item_child_detail_monitor_list, parent, false);
            holder = new Holder();
            holder.tv_child_monitor_phone = (AppCompatTextView) convertView.findViewById(R.id.tv_child_monitor_phone);
            holder.tv_child_monitor_delete = (AppCompatTextView) convertView.findViewById(R.id.tv_child_monitor_delete);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        ChildMonitorData item = getDataList().get(position);
        String bid = item.getBid();
        String phone = item.getPhone();
        holder.tv_child_monitor_phone.setText(phone);
        holder.tv_child_monitor_delete.setOnClickListener(new OnDelete(bid, position));
        return convertView;
    }

    private class Holder {
        AppCompatTextView tv_child_monitor_phone;
        AppCompatTextView tv_child_monitor_delete;
    }

    private class OnDelete implements View.OnClickListener, IResultHandler {
        private final int DELETE_MONITOR = 0x0066;
        private String bid;
        private int pos;

        OnDelete(String bid, int pos) {
            this.bid = bid;
            this.pos = pos;
        }

        @Override
        public void onClick(View v) {
            OperateDialog dialog = new OperateDialog(getActivity());
            dialog.setContent("删除之后，该监护人将无法继续对孩子进行监护。是否要删除？");
            dialog.setOk("删除");
            dialog.setOnOk(v1 -> {
                dialog.dismiss();
                DeleteMonitorGet get = new DeleteMonitorGet();
                get.code(DELETE_MONITOR);
                get.handler(this);
                get.setBid(bid);
                get.get();
            });
            int wid = getActivity().getResources().getDimensionPixelOffset(R.dimen.size_300);
            dialog.create(wid, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();
        }

        @Override
        public void handleStart(int code) {
            if (code == DELETE_MONITOR) {
                LogUtil.info("删除开始");
            }
        }

        @Override
        public void handleResult(String result, int code) {
            if (code == DELETE_MONITOR) {
                Res res = Tools.json2Bean(result, Res.class);
                showTips(res.msg());
                if (res.isOk()) {
                    ArrayList<ChildMonitorData> list = getDataList();
                    list.remove(pos);
                    ChildMonitorAdapter.this.setDataList(list);
                    ChildMonitorAdapter.this.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void handleFinish(int code) {
            if (code == DELETE_MONITOR) {
                LogUtil.info("删除结束");
            }
        }

        @Override
        public void handleError(int code) {
            if (code == DELETE_MONITOR) {
                LogUtil.info("删除错误");
                showTips("删除失败，请重试");
            }
        }
    }
}
