package com.Alan.eva.ui.core;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * 抽象适配器，目的在于减少减少各种适配器的代码量,
 * <br/>getView()方法需要每个实现类自己重写，抽象类中没有处理
 *
 * @param <T> 泛型来传入抽象类型的列表
 * @author wei19
 */
public abstract class AbsAdapter<T> extends BaseAdapter {
    private Activity mActivity;
    private ArrayList<T> dataList;
    private LayoutInflater miInflater;

    private boolean showChoose = false;

    public AbsAdapter(ArrayList<T> dataList, Activity context) {
        this.dataList = dataList;
        this.mActivity = context;
        this.miInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList == null ? null : dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return dataList == null ? 0 : position;
    }

    public ArrayList<T> getDataList() {
        return dataList;
    }

    public void setDataList(ArrayList<T> dataList) {
        this.dataList = dataList;
    }

    public Activity getActivity() {
        return mActivity;
    }

    protected LayoutInflater getMiInflater() {
        return miInflater;
    }

    protected void showTips(String tips) {
        if (mActivity != null) {
            Toast.makeText(getActivity(), tips, Toast.LENGTH_SHORT).show();
        }
    }

    private IItemAction iItemAction;

    public void setIItemAction(IItemAction iItemAction) {
        this.iItemAction = iItemAction;
    }

    protected IItemAction getIItemAction() {
        return iItemAction;
    }

    /**
     * 单条列表做什么事
     */
    public interface IItemAction {
        void onModifyData(int position);

        void onDeleteData(int position);
    }


    public boolean isShowChoose() {
        return showChoose;
    }

    public void setShowChoose(boolean showChoose) {
        this.showChoose = showChoose;
        this.notifyDataSetChanged();
    }
}
