package com.yc.liaolive.recharge.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yc.liaolive.R;
import com.yc.liaolive.common.CaoliaoController;
import com.yc.liaolive.recharge.model.bean.VipListItem;
import com.yc.liaolive.recharge.model.bean.VipRechargePoppupBean;
import com.yc.liaolive.util.Utils;

import java.util.List;

/**
 * 新用户领券弹窗页面
 * type 0:支付成功弹窗  1：登录成功弹窗
 * Created by yangxueqin on 18/11/24.
 */

public class VipRewardDialogActivity extends Activity implements View.OnClickListener {


    /**
     * 新用户领券弹窗页面
     * @param type 0:支付成功弹窗  1：登录成功弹窗
     * @param bean
     */
    public static void startRewardDialog (int type, VipRechargePoppupBean bean) {
        Intent intent = CaoliaoController.createIntent(VipRewardDialogActivity.class.getName());
        intent.putExtra("type", type);
        intent.putExtra("popbean", bean);
        CaoliaoController.startActivity(intent);
    }

    @Override
    public void finish() {
        super.finish();
        this.overridePendingTransition(0, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vip_reward_dialog_layout);
        initView();
    }

    private void initView () {
        Intent intent = getIntent();
//        int type = intent.getIntExtra("type", 0);
        VipRechargePoppupBean bean = intent.getParcelableExtra("popbean");
        if (bean == null || bean.getList() == null || bean.getList().size() == 0) {
            finish();
            return;
        }

        TextView btn = findViewById(R.id.dialog_ok_btn);
        ImageView close = findViewById(R.id.reward_dialog_close);
        close.setOnClickListener(this);
        btn.setOnClickListener(this);

        setData(bean.getList());
    }

    private void setData (List<VipListItem> listBeans) {
        LinearLayout content = findViewById(R.id.dialog_itemLy);
        int itemWidth = Utils.dip2px(77);
        int marginBottom = Utils.dip2px(12);
        int marginLeft = Utils.dip2px(14);
        for (VipListItem bean : listBeans) {

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setPadding(marginLeft, 0, marginLeft, 0);

            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(this)
                    .load(bean.getImg_url())
                    .crossFade()
                    .placeholder(R.drawable.ic_default_user_head)
                    .error(R.drawable.ic_default_user_head)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .centerCrop().skipMemoryCache(true)
                    .into(imageView);
            LinearLayout.LayoutParams imageParam = new LinearLayout.LayoutParams(itemWidth, itemWidth);
            imageParam.bottomMargin = marginBottom;
            linearLayout.addView(imageView, imageParam);

            TextView textView = new TextView(this);
            textView.setText(bean.getText());
            textView.setTextSize(13);
            textView.setTextColor(getResources().getColor(R.color.gray_55));
            textView.setWidth(itemWidth);
            textView.setGravity(Gravity.CENTER);
            linearLayout.addView(textView);

            content.addView(linearLayout);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reward_dialog_close:
            case R.id.dialog_ok_btn:
                finish();
                break;
        }
    }
}
