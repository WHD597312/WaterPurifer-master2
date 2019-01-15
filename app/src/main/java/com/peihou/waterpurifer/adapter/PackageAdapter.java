package com.peihou.waterpurifer.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.donkingliang.groupedadapter.adapter.GroupedRecyclerViewAdapter;
import com.donkingliang.groupedadapter.holder.BaseViewHolder;
import com.peihou.waterpurifer.R;
import com.peihou.waterpurifer.activity.PackagesActivity;
import com.peihou.waterpurifer.database.dao.PackageschildDao;
import com.peihou.waterpurifer.database.dao.daoImp.PackageschildImpl;
import com.peihou.waterpurifer.pojo.Packages;
import com.peihou.waterpurifer.pojo.Packageschild;

import java.util.ArrayList;
import java.util.List;

public class PackageAdapter extends GroupedRecyclerViewAdapter {
    List<Packages> mData;
    List<Packageschild> mDatachild;
    List <PackagesAdapter> adapterList;
    Context mContext;
    private boolean isclick = false;
    PackagesAdapter adpter1, adapter2, adpter3;
    PackageschildImpl packageschildDao;
    PackagesActivity packagesActivity;

    public PackageAdapter(Context context) {
        super(context);
    }

    public PackageAdapter(Context context, boolean useBinding, List<Packages> list, Activity activity) {
        super(context, useBinding);
        packageschildDao = new PackageschildImpl(context.getApplicationContext());
        this.mData = list;
        this.mContext = context;
        this.packagesActivity = (PackagesActivity) activity;
        mDatachild = new ArrayList<>();
        adapterList = new ArrayList<>();
//        for (int i = 0;i<list.size();i++){
//           int z = list.get(i).getType();
//            if (z==1){
//                packages1.add(list.get(i));
//            }else if (z==2){
//
//                packages2.add(list.get(i));
//            }else {
//                packages3.add(list.get(i));
//            }
//
//        }

    }

    public void setData(List<Packages> list ) {
        this.mData = list;
        for (int i = 0;i<mData.size();i++){
            mDatachild = packageschildDao.findByParentId(mData.get(i).getPackageId());
            PackagesAdapter packadpter = new PackagesAdapter(mContext, mDatachild,packagesActivity);
            adapterList.add(packadpter);
        }

    }

    @Override
    public int getGroupCount() {
        return mData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        return 1;
    }

    @Override
    public boolean hasHeader(int groupPosition) {
        return true;
    }

    @Override
    public boolean hasFooter(int groupPosition) {
        return false;
    }

    @Override
    public int getHeaderLayout(int viewType) {
        return R.layout.item_packagetitle;
    }

    @Override
    public int getFooterLayout(int viewType) {
        return 0;
    }

    @Override
    public int getChildLayout(int viewType) {

        return R.layout.item_group;
    }

    @Override
    public void onBindHeaderViewHolder(BaseViewHolder holder, int groupPosition) {
        holder.setText(R.id.tv_pakage_title, mData.get(groupPosition).getPackageName());


    }

    @Override
    public void onBindFooterViewHolder(BaseViewHolder holder, int groupPosition) {

    }

    @Override
    public void onBindChildViewHolder(BaseViewHolder holder, final int groupPosition, final int childPosition) {
        RecyclerView recyclerView = (RecyclerView) holder.itemView.findViewById(R.id.rv_group);
        recyclerView.setNestedScrollingEnabled(false);
        if (!isclick){
            recyclerView.addItemDecoration(new SpaceItemDecoration(25, 22, 15));
        }

        if (adapterList.size()>0){
            recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
            recyclerView.setAdapter(adapterList.get(groupPosition));
            adapterList.get(groupPosition).SetOnItemClick(new PackagesAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    isclick=true;
                    PackagesAdapter packagesAdapter =adapterList.get(groupPosition);
                     packagesAdapter.setIndex(position);
                    for (int i = 0 ;i<adapterList.size();i++){
                        if (!adapterList.get(i).equals(packagesAdapter)){
                            adapterList.get(i).setIndex(-1);
                        }
                    }

                }

                @Override
                public void onLongClick(View view, int posotion) {

                }
            });

        }



//        if (groupPosition==1){
//            adapter2 = new PackagesAdapter(mContext,packages2);
//            recyclerView.setLayoutManager(new GridLayoutManager(mContext,3));
//            recyclerView.setAdapter(adapter2);
//            adapter2.SetOnItemClick(new PackagesAdapter.OnItemClickListener() {
//                @Override
//                public void onItemClick(View view, int position) {
//                    int myposition = position;
//                    //传到适配器  （适配器调用方法）
//                    adapter2.setIndex(myposition);
//                    adapter2.notifyDataSetChanged();
//                    adpter1.setIndex(-1);
//                    adpter1.notifyDataSetChanged();
//                    adpter3.setIndex(-1);
//                    adpter3.notifyDataSetChanged();
//                }
//
//                @Override
//                public void onLongClick(View view, int posotion) {
//
//                }
//            });
//        }
//        if (groupPosition==2){
//            adpter3 = new PackagesAdapter(mContext,packages3);
//            recyclerView.setLayoutManager(new GridLayoutManager(mContext,3));
//            recyclerView.setAdapter(adpter3);
//            adpter3.SetOnItemClick(new PackagesAdapter.OnItemClickListener() {
//                @Override
//                public void onItemClick(View view, int position) {
//                    int myposition = position;
//                    adpter1.setIndex(-1);
//                    adpter1.notifyDataSetChanged();
//                    adapter2.setIndex(-1);
//                    adapter2.notifyDataSetChanged();
//                    adpter3.setIndex(myposition);
//                    adpter3.notifyDataSetChanged();
//                }
//
//                @Override
//                public void onLongClick(View view, int posotion) {
//
//                }
            }
    }

