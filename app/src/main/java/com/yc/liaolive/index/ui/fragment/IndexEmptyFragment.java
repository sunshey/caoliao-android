package com.yc.liaolive.index.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * TinyHung@Outlook.com
 * 2019/3/4
 */

public class IndexEmptyFragment extends Fragment {

    private String mHostUrl;
    private int mIndex;

    public static IndexEmptyFragment newInstance(String hostUrl, int index) {
        IndexEmptyFragment fragment=new IndexEmptyFragment();
        Bundle bundle=new Bundle();
        bundle.putString("hostUrl",hostUrl);
        bundle.putInt("index",index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if(null!=arguments){
            mHostUrl = arguments.getString("hostUrl");
            mIndex = arguments.getInt("index", 0);
        }
    }
}