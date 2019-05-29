package com.faceunity.beauty.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Camera;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.faceunity.FURenderer;
import com.faceunity.OnFUControlListener;
import com.faceunity.R;
import com.faceunity.beauty.entity.FilterEnum;
import com.faceunity.beauty.view.seekbar.DiscreteSeekBar;
import com.faceunity.entity.Filter;
import java.util.Arrays;
import java.util.List;
import static com.faceunity.beauty.entity.BeautyParameterModel.getValue;
import static com.faceunity.beauty.entity.BeautyParameterModel.sFilterLevel;
import static com.faceunity.beauty.entity.BeautyParameterModel.sFilterName;
import static com.faceunity.beauty.entity.BeautyParameterModel.sStrFilterLevel;
import static com.faceunity.beauty.entity.BeautyParameterModel.setValue;

/**
 * Created by tujh on 2017/8/15.
 */

public class BeautyControlView extends FrameLayout {

    private Context mContext;
    private OnFUControlListener mOnFUControlListener;
    private CheckGroup mBottomCheckGroup;
    //滤镜
    private RecyclerView mFilterRecyclerView;
    private FilterRecyclerAdapter mFilterRecyclerAdapter;
    private List<Filter> mFilters;
    private View seekBarGroup;
    private DiscreteSeekBar mBeautySeekBar;
    //0：女神 1：网红 2：自然 3：默认 4：自定义（新版美型） SDK默认为 3
    private static List<Integer> FaceShapeIdList = Arrays.asList(R.id.face_shape_3_default, R.id.face_shape_0_nvshen,
            R.id.face_shape_1_wanghong, R.id.face_shape_2_ziran);
    private View shapeLayoutGroup; //美型布局
//    private RelativeLayout mFaceShapeLayout;
//    private View mFaceShapeLine;
    private RadioGroup mFaceShapeRadioGroup;
    private DiscreteSeekBar styleSeekBar; //脸型程度
    private DiscreteSeekBar eyeSeekBar; //大眼
    private DiscreteSeekBar faceSeekBar; //瘦脸
//    private RadioButton mFaceShape4Radio;
    private RadioGroup blurLevelLayout; //磨皮
    private FURenderer mFuRenderer;

    public BeautyControlView(Context context) {
        this(context, null);
    }

    public BeautyControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BeautyControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mFilters = FilterEnum.getFiltersByFilterType(Filter.FILTER_TYPE_FILTER);
        LayoutInflater.from(context).inflate(R.layout.layout_beauty_control, this);
        initView();
    }

    private void initView() {
        initViewBottomRadio();
        initViewShapeBeauty();
        initViewFilterRecycler();
        initViewTop();
        this.setVisibility(View.GONE);
    }

    public void onResume() {
        updateViewSkinBeauty();
        updateViewFaceShape();
        updateViewFilterRecycler();
//        hideBottomLayoutAnimator();
    }

    public void initBeauty () {
        int mInputImageOrientation = getFrontCameraOrientation();
        //                .inputTextureType(faceunity.FU_ADM_FLAG_ENABLE_READBACK)
        mFuRenderer = new FURenderer.Builder(getContext())
//                .inputTextureType(faceunity.FU_ADM_FLAG_ENABLE_READBACK)
                .inputImageOrientation(mInputImageOrientation)
                .build();
        mOnFUControlListener= mFuRenderer;
    }

    public void setFURenderer(OnFUControlListener onFUControlListener) {
        mOnFUControlListener = onFUControlListener;
    }

    public OnFUControlListener getFURenderer() {
        return mOnFUControlListener;
    }

    public int getFrontCameraOrientation() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        int cameraId = 1;
        int numCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numCameras; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;
            }
        }
        return getCameraOrientation(cameraId);
    }

    public int getCameraOrientation(int cameraId) {
        try {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, info);
            return info.orientation;
        }catch (RuntimeException e){

        }
        return 0;
    }
    

    private void initViewBottomRadio() {
        mBottomCheckGroup = (CheckGroup) findViewById(R.id.beauty_radio_group);
        mBottomCheckGroup.setOnCheckedChangeListener(new CheckGroup.OnCheckedChangeListener() {
//            int checkedId_old = View.NO_ID;

            @Override
            public void onCheckedChanged(CheckGroup group, int checkedId) {
                clickViewBottomRadio(checkedId);
//                if (checkedId == R.id.beauty_radio_face_shape && checkedId_old == View.NO_ID) {
//                    changeBottomLayoutAnimator(0, Utils.dip2px(203));
//                } else if (checkedId == R.id.beauty_radio_face_shape) {
//                    changeBottomLayoutAnimator(Utils.dip2px(154), Utils.dip2px(203));
//                } else if (checkedId != View.NO_ID && checkedId_old == View.NO_ID) {
////                    int startHeight = Utils.dip2px(46);
//                    int endHeight = Utils.dip2px(154);
//                    changeBottomLayoutAnimator(0, endHeight);
//                } else if (checkedId == View.NO_ID && checkedId_old != View.NO_ID) {
////                    int endHeight = Utils.dip2px(46);
//                    int startHeight = getHeight();
//                    changeBottomLayoutAnimator(startHeight, 0);
//                }
//                checkedId_old = checkedId;
            }
        });
    }

    /**
     * 美型布局
     */
    private void initViewShapeBeauty () {
        shapeLayoutGroup = findViewById(R.id.face_shape_group);
        styleSeekBar = findViewById(R.id.beauty_seek_bar_shape_degree);
        eyeSeekBar = findViewById(R.id.beauty_seek_bar_eye);
        faceSeekBar = findViewById(R.id.beauty_seek_bar_face_lift);
        styleSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar SeekBar, int value, boolean fromUser) {
                if (!fromUser) return;
                float valueF = 1.0f * (value - SeekBar.getMin()) / 100;
                setValue(styleSeekBar.getId(), valueF);
                onChangeFaceBeautyLevel(styleSeekBar.getId());
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar SeekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar SeekBar) {

            }
        });
        eyeSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar SeekBar, int value, boolean fromUser) {
                if (!fromUser) return;
                float valueF = 1.0f * (value - SeekBar.getMin()) / 100;
                setValue(eyeSeekBar.getId(), valueF);
                onChangeFaceBeautyLevel(eyeSeekBar.getId());
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar SeekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar SeekBar) {

            }
        });

        faceSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar SeekBar, int value, boolean fromUser) {
                if (!fromUser) return;
                float valueF = 1.0f * (value - SeekBar.getMin()) / 100;
                setValue(faceSeekBar.getId(), valueF);
                onChangeFaceBeautyLevel(faceSeekBar.getId());
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar SeekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar SeekBar) {

            }
        });
    }

    private void updateViewSkinBeauty() {
        onChangeFaceBeautyLevel(R.id.beauty_box_blur_level);
        onChangeFaceBeautyLevel(R.id.beauty_radio_skin_white);
        onChangeFaceBeautyLevel(R.id.beauty_radio_beauty_ruddy);
    }

    /**
     * 更新美型
     */
    private void updateViewFaceShape() {
        int index = (int) getValue(R.id.face_shape_radio_group);
        ((AppCompatRadioButton)mFaceShapeRadioGroup.getChildAt(index)).setChecked(true);

        onChangeFaceBeautyLevel(R.id.beauty_seek_bar_shape_degree);
        onChangeFaceBeautyLevel(R.id.beauty_seek_bar_eye);
        onChangeFaceBeautyLevel(R.id.beauty_seek_bar_face_lift);
    }

    private void initViewFilterRecycler() {
        mFilterRecyclerView = (RecyclerView) findViewById(R.id.filter_recycle_view);
        mFilterRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mFilterRecyclerView.setAdapter(mFilterRecyclerAdapter = new FilterRecyclerAdapter());
        ((SimpleItemAnimator) mFilterRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    private void updateViewFilterRecycler() {
        mFilterRecyclerAdapter.setFilter(sFilterName);
    }

    private void initViewTop() {
        mFaceShapeRadioGroup = (RadioGroup) findViewById(R.id.face_shape_radio_group);
        mFaceShapeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                float value = FaceShapeIdList.indexOf(checkedId);
                setValue(R.id.face_shape_radio_group, value);
                onChangeFaceBeautyLevel(R.id.beauty_seek_bar_shape_degree);
                onChangeFaceBeautyLevel(R.id.beauty_seek_bar_eye);
                onChangeFaceBeautyLevel(R.id.beauty_seek_bar_face_lift);
            }
        });

        seekBarGroup = findViewById(R.id.beauty_seek_bar_group);
        mBeautySeekBar = (DiscreteSeekBar) findViewById(R.id.beauty_seek_bar);
        mBeautySeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar SeekBar, int value, boolean fromUser) {
                if (!fromUser) return;
                float valueF = 1.0f * (value - SeekBar.getMin()) / 100;
                if (mBottomCheckGroup.getCheckedCheckBoxId() == R.id.beauty_radio_skin_white) {
                    //美白
                    setValue(mBottomCheckGroup.getCheckedCheckBoxId(), valueF);
                    onChangeFaceBeautyLevel(mBottomCheckGroup.getCheckedCheckBoxId());
                } else if (mBottomCheckGroup.getCheckedCheckBoxId() == R.id.beauty_radio_beauty_ruddy) {
                    //红润
                    setValue(mBottomCheckGroup.getCheckedCheckBoxId(), valueF);
                    onChangeFaceBeautyLevel(mBottomCheckGroup.getCheckedCheckBoxId());
                }
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar SeekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar SeekBar) {

            }
        });

        //磨皮等级 6级
        blurLevelLayout = findViewById(R.id.beauty_box_blur_level);
        int[] level = {0, 1, 2, 3, 4, 5, 6};
        //边距16*2、间隙8*6
        int itemWidth = (getScreenWidth() - dip2px(80)) / 7;
        for (int i : level) {
            AppCompatRadioButton textView = new AppCompatRadioButton(getContext());
            textView.setId(i);
            textView.setWidth(itemWidth);
            textView.setHeight(itemWidth);
            textView.setGravity(Gravity.CENTER);
            textView.setButtonDrawable(null);
            textView.setIncludeFontPadding(false);

            if (i == 0) {
//                textView.setButtonDrawable(getResources().getDrawable(R.drawable.blur_level_close));
//                textView.setCompoundDrawablesWithIntrinsicBounds(
//                        getResources().getDrawable(R.drawable.blur_level_close), null, null, null);
//                textView.setBackground(getResources().getDrawable(R.drawable.blur_level_close));
                textView.setBackgroundResource(R.drawable.beauty_control_blur_level_close_color);
            } else {

//                textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                textView.setText(String.valueOf(i));
                textView.setTextColor(Color.parseColor("#FFFFFF"));
                textView.setTextSize(23);
                textView.setBackgroundResource(R.drawable.beauty_control_blur_level_bg_color);
            }

            blurLevelLayout.addView(textView);
        }
        blurLevelLayout.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                float valueF = 1.0f * checkedId / 6;
                setValue(R.id.beauty_box_blur_level, valueF);
                onChangeFaceBeautyLevel(R.id.beauty_box_blur_level);
            }
        });
    }

    /**
     * 获取屏幕的宽
     *
     * @return
     */
    public int getScreenWidth() {
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        if (dm.widthPixels > dm.heightPixels) {
            return dm.heightPixels;
        } else {
            return dm.widthPixels;
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public  int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

//    private void updateFaceShapeCheckedLine(final int checkedId) {
//        mFaceShapeLine.post(new Runnable() {
//            @Override
//            public void run() {
//                RadioButton radioButton = (RadioButton) findViewById(checkedId);
//                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mFaceShapeCheckedLine.getLayoutParams();
//                int textWidth = radioButton == null || radioButton.getVisibility() == GONE ? 0 : (int) radioButton.getPaint().measureText(radioButton.getText().toString());
//                params.width = textWidth;
//                params.leftMargin = radioButton == null || radioButton.getVisibility() == GONE ? 0 : (radioButton.getLeft() + (radioButton.getWidth() - textWidth) / 2);
//                mFaceShapeCheckedLine.setLayoutParams(params);
//            }
//        });
//    }

    private void onChangeFaceBeautyLevel(int viewId) {
        if (viewId == View.NO_ID) return;
//        ((BeautyBox) findViewById(viewId)).setOpen(isOpen(viewId));
        if (mOnFUControlListener == null) return;
        if (viewId == R.id.beauty_box_blur_level) {
            mOnFUControlListener.onBlurLevelSelected(getValue(viewId));

        } else if (viewId == R.id.beauty_seek_bar_shape_degree) {
            mOnFUControlListener.onFaceStylelSelected(getValue(viewId));

        } else if (viewId == R.id.beauty_radio_skin_white) {
            mOnFUControlListener.onColorLevelSelected(getValue(viewId));

        } else if (viewId == R.id.beauty_radio_beauty_ruddy) {
            mOnFUControlListener.onRedLevelSelected(getValue(viewId));

//            case R.id.beauty_box_eye_bright:
//                mOnFUControlListener.onEyeBrightSelected(getValue(viewId));
//                break;
//            case R.id.beauty_box_tooth_whiten:
//                mOnFUControlListener.onToothWhitenSelected(getValue(viewId));
//                break;
        } else if (viewId == R.id.face_shape_radio_group) {
            mOnFUControlListener.onFaceShapeSelected(getValue(viewId));

        } else if (viewId == R.id.beauty_seek_bar_eye) {
            mOnFUControlListener.onEyeEnlargeSelected(getValue(viewId));

        } else if (viewId == R.id.beauty_seek_bar_face_lift) {
            mOnFUControlListener.onCheekThinningSelected(getValue(viewId));

//            case R.id.beauty_box_intensity_chin:
//                mOnFUControlListener.onIntensityChinSelected(getValue(viewId));
//                break;
//            case R.id.beauty_box_intensity_forehead:
//                mOnFUControlListener.onIntensityForeheadSelected(getValue(viewId));
//                break;
//            case R.id.beauty_box_intensity_nose:
//                mOnFUControlListener.onIntensityNoseSelected(getValue(viewId));
//                break;
//            case R.id.beauty_box_intensity_mouth:
//                mOnFUControlListener.onIntensityMouthSelected(getValue(viewId));
//                break;
        }
    }

    private void clickViewBottomRadio(int viewId) {
        if (viewId == R.id.beauty_radio_filter) { //滤镜
            mFilterRecyclerAdapter.setFilterType(Filter.FILTER_TYPE_FILTER);
//            if (mFilterTypeSelect == Filter.FILTER_TYPE_FILTER) {
                mFilterRecyclerAdapter.setFilterProgress();
//            }
            mFilterRecyclerView.setVisibility(VISIBLE);
            seekBarGroup.setVisibility(View.GONE);
            shapeLayoutGroup.setVisibility(View.GONE);
            blurLevelLayout.setVisibility(View.GONE);

        } else if (viewId == R.id.beauty_radio_beauty_skin) { //磨皮
            mFilterRecyclerView.setVisibility(GONE);
            seekBarGroup.setVisibility(View.GONE);
            shapeLayoutGroup.setVisibility(View.GONE);
            blurLevelLayout.setVisibility(View.VISIBLE);
            setblurLevelLayout();
            onChangeFaceBeautyLevel(R.id.beauty_radio_beauty_skin);
            if (mOnFUControlListener != null) {
                mOnFUControlListener.onHeavyBlurSelected(0);
            }
        } else if (viewId == R.id.beauty_radio_skin_white) {//美白
            mFilterRecyclerView.setVisibility(GONE);
            seekBarGroup.setVisibility(View.VISIBLE);
            shapeLayoutGroup.setVisibility(View.GONE);
            blurLevelLayout.setVisibility(View.GONE);
            seekToSeekBar(mBeautySeekBar, viewId);
        } else if (viewId == R.id.beauty_radio_beauty_ruddy) {//红润
            mFilterRecyclerView.setVisibility(GONE);
            seekBarGroup.setVisibility(View.VISIBLE);
            shapeLayoutGroup.setVisibility(View.GONE);
            blurLevelLayout.setVisibility(View.GONE);
            seekToSeekBar(mBeautySeekBar, viewId);
        } else if (viewId == R.id.beauty_radio_face_shape) {//美型
            mFilterRecyclerView.setVisibility(GONE);
            seekBarGroup.setVisibility(View.GONE);
            shapeLayoutGroup.setVisibility(View.VISIBLE);
            blurLevelLayout.setVisibility(View.GONE);
            seekToSeekBar(styleSeekBar, R.id.beauty_seek_bar_shape_degree);
            seekToSeekBar(eyeSeekBar, R.id.beauty_seek_bar_eye);
            seekToSeekBar(faceSeekBar, R.id.beauty_seek_bar_face_lift);
        }
    }

    /**
     * 显示磨皮等级
     */
    private void setblurLevelLayout () {
        float value = getValue(R.id.beauty_box_blur_level);
        int index = Math.round(value * 6);
        ((RadioButton)blurLevelLayout.getChildAt(index)).setChecked(true);
    }

    private void seekToSeekBar(DiscreteSeekBar seekBar, int checkedId) {
        if (checkedId == View.NO_ID) return;
        float value = getValue(checkedId);
        int min = 0;
        int max = 100;
//        if (checkedId == R.id.beauty_box_intensity_chin || checkedId == R.id.beauty_box_intensity_forehead || checkedId == R.id.beauty_box_intensity_mouth) {
//            //下巴、额头、瘦鼻
//            min = -50;
//            max = 50;
//        }
        seekToSeekBar(seekBar, value, min, max);
    }

    private void seekToSeekBar(DiscreteSeekBar seekBar, float value) {
        seekToSeekBar(seekBar, value, 0, 100);
    }

    private void seekToSeekBar(DiscreteSeekBar seekBar, float value, int min, int max) {
        seekBar.setVisibility(VISIBLE);
        seekBar.setMin(min);
        seekBar.setMax(max);
        seekBar.setProgress((int) (value * (max - min) + min));
    }

    private int mFilterPositionSelect = 0;
    private int mFilterTypeSelect = Filter.FILTER_TYPE_FILTER;

    /**
     * 滤镜
     */
    class FilterRecyclerAdapter extends RecyclerView.Adapter<FilterRecyclerAdapter.HomeRecyclerHolder> {

        int filterType;
        @Override
        public FilterRecyclerAdapter.HomeRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new FilterRecyclerAdapter.HomeRecyclerHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_beauty_control_recycler, parent, false));
        }

        @Override
        public void onBindViewHolder(FilterRecyclerAdapter.HomeRecyclerHolder holder, final int position) {
            final List<Filter> filters = getItems(filterType);
            holder.filterImg.setImageResource(filters.get(position).resId());
            if (mFilterPositionSelect == position && filterType == mFilterTypeSelect) {
                holder.selectView.setVisibility(View.VISIBLE);
            } else {
                holder.selectView.setVisibility(View.INVISIBLE);
            }
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFilterPositionSelect = position;
                    mFilterTypeSelect = filterType;
                    setFilterProgress();
                    notifyDataSetChanged();
//                    mBeautySeekBar.setVisibility(VISIBLE);
                    if (mOnFUControlListener != null)
                        mOnFUControlListener.onFilterNameSelected(sFilterName = filters.get(mFilterPositionSelect));
                }
            });
        }

        @Override
        public int getItemCount() {
            return getItems(filterType).size();
        }

        public void setFilterType(int filterType) {
            this.filterType = filterType;
            notifyDataSetChanged();
        }

        public void setFilterLevels(float filterLevels) {
            setFilterLevel(getItems(mFilterTypeSelect).get(mFilterPositionSelect).filterName(), filterLevels);
        }

        public void setFilter(Filter filter) {
            mFilterTypeSelect = filter.filterType();
            mFilterPositionSelect = getItems(mFilterTypeSelect).indexOf(filter);
        }

        public void setFilterProgress() {
            getFilterLevel(getItems(mFilterTypeSelect).get(mFilterPositionSelect).filterName());
//            seekToSeekBar(getFilterLevel(getItems(mFilterTypeSelect).get(mFilterPositionSelect).filterName()));
        }

        public List<Filter> getItems(int type) {
            switch (type) {
                case Filter.FILTER_TYPE_FILTER:
                    return mFilters;
            }
            return mFilters;
        }

        public void setNewData(List<Filter> newData) {
            if(null!=mFilters) mFilters.clear();
            mFilters = newData;
            notifyDataSetChanged();
        }

        class HomeRecyclerHolder extends RecyclerView.ViewHolder {

            ImageView filterImg;
            ImageView selectView;

            public HomeRecyclerHolder(View itemView) {
                super(itemView);
                filterImg = (ImageView) itemView.findViewById(R.id.control_recycler_img);
                selectView = (ImageView) itemView.findViewById(R.id.control_recycler_img_select);
            }
        }
    }

    public interface OnBottomAnimatorChangeListener {
        void onBottomAnimatorChangeListener(float showRate);
    }

    public void setOnBottomAnimatorChangeListener(OnBottomAnimatorChangeListener onBottomAnimatorChangeListener) {
        mOnBottomAnimatorChangeListener = onBottomAnimatorChangeListener;
    }

    private OnBottomAnimatorChangeListener mOnBottomAnimatorChangeListener;

    private ValueAnimator mBottomLayoutAnimator;

    private void changeBottomLayoutAnimator(final int startHeight, final int endHeight) {
        if (mBottomLayoutAnimator != null && mBottomLayoutAnimator.isRunning()) {
            mBottomLayoutAnimator.end();
        }
        mBottomLayoutAnimator = ValueAnimator.ofInt(startHeight, endHeight).setDuration(150);
        mBottomLayoutAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int height = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = getLayoutParams();
                if (params == null) return;
                params.height = height;
                setLayoutParams(params);
                if (mOnBottomAnimatorChangeListener != null) {
                    float showRate = 1.0f * (height - startHeight) / (endHeight - startHeight);
                    mOnBottomAnimatorChangeListener.onBottomAnimatorChangeListener(startHeight > endHeight ? 1 - showRate : showRate);
                }
            }
        });
        mBottomLayoutAnimator.start();
    }

    public void showBottomLayoutAnimator() {
        if (!this.isShown()) {
            this.setVisibility(View.VISIBLE);
            clickViewBottomRadio(R.id.beauty_radio_filter);
            mBottomCheckGroup.check(mBottomCheckGroup.getChildAt(0).getId());
        }
    }

    public void hideBottomLayoutAnimator() {
        if (this.isShown()) {
            mBottomCheckGroup.check(View.NO_ID);
            this.setVisibility(View.GONE);
        }
    }

//    public void setHeightPerformance(boolean isHP) {
//        isHeightPerformance = isHP;
//        updateViewSkinBeauty();
//        updateViewFaceShape();
//        mSkinBeautyBoxGroup.check(View.NO_ID);
//        mFaceShapeBeautyBoxGroup.check(View.NO_ID);
//    }

    public float getFilterLevel(String filterName) {
        Float level = sFilterLevel.get(sStrFilterLevel + filterName);
        float l = level == null ? 1.0f : level;
        setFilterLevel(filterName, l);
        return l;
    }

    public void setFilterLevel(String filterName, float faceBeautyFilterLevel) {
        sFilterLevel.put(sStrFilterLevel + filterName, faceBeautyFilterLevel);
        if (mOnFUControlListener != null)
            mOnFUControlListener.onFilterLevelSelected(faceBeautyFilterLevel);
    }

    public void onDestroy() {
        if (null != mFuRenderer) {
            mFuRenderer.destroyItems();
            mFuRenderer = null;
        }
        if (null != mFilterRecyclerAdapter) mFilterRecyclerAdapter.setNewData(null);
        mContext = null;styleSeekBar=null;mBeautySeekBar=null;eyeSeekBar=null;
        faceSeekBar=null;blurLevelLayout=null; seekBarGroup=null;mBottomCheckGroup=null;
        mOnFUControlListener=null; mFilterRecyclerAdapter=null;
    }
}