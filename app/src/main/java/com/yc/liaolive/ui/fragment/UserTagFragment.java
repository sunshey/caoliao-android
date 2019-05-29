package com.yc.liaolive.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import com.yc.liaolive.R;
import com.yc.liaolive.VideoApplication;
import com.yc.liaolive.ui.adapter.UserTagAdapter;
import com.yc.liaolive.base.BaseFragment;
import com.yc.liaolive.base.RxBasePresenter;
import com.yc.liaolive.bean.TagInfo;
import com.yc.liaolive.contants.Constant;
import com.yc.liaolive.databinding.FragmentUserTagBinding;
import com.yc.liaolive.user.manager.UserManager;
import com.yc.liaolive.user.IView.UserServerContract;
import com.yc.liaolive.util.Logger;
import com.yc.liaolive.util.ToastUtils;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagFlowLayout;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.TreeSet;

/**
 * TinyHung@Outlook.com
 * 2018/10/17
 * 用户标签选择
 */

public class UserTagFragment extends BaseFragment<FragmentUserTagBinding,RxBasePresenter> {

    private static final String TAG = "UserTagFragment";
    private UserTagAdapter mAdapter;

    @Override
    protected void initViews() {
        bindingView.tvImageTips.setText(Html.fromHtml("<font color='#E6646E'>*</font>最多支持选择三个标签"));
        bindingView.tagFlow.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                return false;
            }
        });

        bindingView.tagFlow.setOnSelectListener(new TagFlowLayout.OnSelectListener() {
            @Override
            public void onSelected(Set<Integer> selectPosSet) {
            }
        });

        bindingView.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Set<Integer> selectedList = bindingView.tagFlow.getSelectedList();
                    if(null!=selectedList){
                        //取到角标后取出实际数据对应的ID
                        Iterator<Integer> iterator = selectedList.iterator();
                        List<Integer> tags=new ArrayList<>();
                        while (iterator.hasNext()) {
                            tags.add(iterator.next());
                        }
                        StringBuilder stringBuilder=new StringBuilder();
                        int count=0;
                        //取出实际数据
                        List<TagInfo> tagInfos = VideoApplication.getInstance().getTags();
                        if(null!=tagInfos){
                            for (int i = 0; i < tags.size(); i++) {
                                for (int i1 = 0; i1 < tagInfos.size(); i1++) {
                                    if(tags.get(i)==i1){
                                        if(0!=count) stringBuilder.append(",");
                                        stringBuilder.append(tagInfos.get(i1).getId());
                                        count++;
                                    }
                                }
                            }
                        }
                        showProgressDialog("设置中，请稍后..");
                        UserManager.getInstance().modityUserData(Constant.MODITUTY_KEY_LABEL, stringBuilder.toString(), new UserServerContract.OnNetCallBackListener() {
                            @Override
                            public void onSuccess(Object object) {
                                closeProgressDialog();
                                if(null!=object && object instanceof String){
                                    UserManager.getInstance().setLabel((String) object);
                                    getActivity().finish();
                                }
                            }

                            @Override
                            public void onFailure(int code, String errorMsg) {
                                closeProgressDialog();
                                ToastUtils.showCenterToast(errorMsg);
                            }
                        });
                    }
                }catch (RuntimeException e){

                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user_tag;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        UserManager.getInstance().getTags(1, new UserServerContract.OnNetCallBackListener() {
            @Override
            public void onSuccess(Object object) {
                if(null!=object && object instanceof List){
                    if(null!=bindingView){
                        List<TagInfo> tags= (List<TagInfo>) object;
                        VideoApplication.getInstance().setTags(tags);
                        mAdapter = new UserTagAdapter(getActivity(),tags);
                        bindingView.tagFlow.setAdapter(mAdapter);
                        //回显用户选中的标签,根据ID获取实际数据的index来回显
                        String label = UserManager.getInstance().getLabel();
                        if(!TextUtils.isEmpty(label)){
                            Set<Integer> set=new TreeSet<>();
                            String[] split = label.split("\\,");
                            for (int i = 0; i < split.length; i++) {
                                for (int i1 = 0; i1 < tags.size(); i1++) {
                                    try {
                                        if(tags.get(i1).getId()==Integer.valueOf(split[i])){
                                            set.add(i1);
                                        }
                                    }catch (RuntimeException e){

                                    }
                                }
                            }
                            //回显选中的项
                            mAdapter.setSelectedList(set);
                        }
                    }
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {

            }
        });
    }
}
