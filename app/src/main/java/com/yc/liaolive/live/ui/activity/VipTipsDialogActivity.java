package com.yc.liaolive.live.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.yc.liaolive.R;
import com.yc.liaolive.common.CaoliaoController;
import com.yc.liaolive.recharge.ui.VipActivity;
import com.yc.liaolive.view.widget.RoundImageView;

/**
 * 非VIP弹出提示框
 * 小姐姐设置了只有VIP用户才能视频通话哟~
 * Created by yangxueqin on 19/1/9.
 */

public class VipTipsDialogActivity extends Activity implements View.OnClickListener {


    /**
     * @param tips 提示
     */
    public static void startVipTipsDialog (String tips, String avatar) {
        Intent intent = CaoliaoController.createIntent(VipTipsDialogActivity.class.getName());
        intent.putExtra("tips", tips);
        intent.putExtra("avatar", avatar);
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
        setContentView(R.layout.vip_tips_dialog_layout);
        initView();
    }

    private void initView () {
        Intent intent = getIntent();
//        int type = intent.getIntExtra("type", 0);
        String tips = intent.getStringExtra("tips");
        String avatar = intent.getStringExtra("avatar");
        if (TextUtils.isEmpty(tips)) {
            finish();
            return;
        }

        TextView title = findViewById(R.id.dialog_title);
        TextView buyVip = findViewById(R.id.to_buy_vip);
        TextView cancel = findViewById(R.id.cancel_btn);
        title.setText(tips);
        buyVip.setOnClickListener(this);
        cancel.setOnClickListener(this);

        RoundImageView imageView = findViewById(R.id.dialog_avatar);
        if (!TextUtils.isEmpty(avatar)) {
            imageView.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(avatar)
                    .asBitmap()
                    .placeholder(R.drawable.ic_default_item_cover)
                    .error(R.drawable.ic_default_item_cover)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存源资源和转换后的资源
                    .into(new BitmapImageViewTarget(imageView) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            super.setResource(resource);
                        }
                    });
        } else {
            imageView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.to_buy_vip:
                VipActivity.start(VipTipsDialogActivity.this, 1);
                finish();
                break;
            case R.id.cancel_btn:
                finish();
                break;
        }
    }
}
