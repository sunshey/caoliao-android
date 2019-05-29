package com.faceunity.beauty.entity;


import com.faceunity.R;
import com.faceunity.entity.Filter;
import java.util.HashMap;
import java.util.Map;

/**
 * 美颜参数SharedPreferences记录,目前仅以保存数据，可改造为以SharedPreferences保存数据
 * Created by tujh on 2018/3/7.
 */

public abstract class BeautyParameterModel {
    public static final String TAG = BeautyParameterModel.class.getSimpleName();
    public static boolean isHeightPerformance = false; //性能优先
    public static final String sStrFilterLevel = "FilterLevel_";
    public static Map<String, Float> sFilterLevel = new HashMap<>();
    public static Filter sFilterName = FilterEnum.nature_beauty.filter();
    public static float sSkinDetect = 1.0f;//精准磨皮
    public static float sHeavyBlur = 0.0f;//美肤类型
    public static float sHeavyBlurLevel = 0.7f;//磨皮
    public static float sBlurLevel = 0.7f;//磨皮
    public static float sColorLevel = 0.5f;//美白
    public static float sRedLevel = 0.5f;//红润
    public static float sEyeBright = 0.0f;//亮眼
    public static float sToothWhiten = 0.0f;//美牙
    public static float sFaceShape = 1.0f;//脸型
    public static float sFaceShapeLevel = 1.0f;//程度
    public static float sEyeEnlarging = 0.4f;//大眼
    public static float sEyeEnlargingOld = 0.4f;//大眼
    public static float sCheekThinning = 0.4f;//瘦脸
    public static float sCheekThinningOld = 0.4f;//瘦脸
    public static float sIntensityChin = 0.3f;//下巴
    public static float sIntensityForehead = 0.3f;//额头
    public static float sIntensityNose = 0.5f;//瘦鼻
    public static float sIntensityMouth = 0.4f;//嘴形

    public static boolean isOpen(int checkId) {
        if (checkId == R.id.beauty_radio_skin_white) {
            return sColorLevel > 0;
        } else if (checkId == R.id.beauty_radio_beauty_ruddy) {
            return sRedLevel > 0;
//            case R.id.beauty_box_eye_bright:
//                return !isHeightPerformance && sEyeBright > 0;
//            case R.id.beauty_box_tooth_whiten:
//                return !isHeightPerformance && sToothWhiten != 0;
        } else if (checkId == R.id.face_shape_radio_group) {
            return !isHeightPerformance && sFaceShape != 3;
//            case R.id.beauty_box_eye_enlarge:
//                if (sFaceShape == 4)
//                    return sEyeEnlarging > 0;
//                else
//                    return sEyeEnlargingOld > 0;
//            case R.id.beauty_box_cheek_thinning:
//                if (sFaceShape == 4)
//                    return sCheekThinning > 0;
//                else
//                    return sCheekThinningOld > 0;
//            case R.id.beauty_box_intensity_chin:
//                return !isHeightPerformance && sIntensityChin != 0.5;
//            case R.id.beauty_box_intensity_forehead:
//                return !isHeightPerformance && sIntensityForehead != 0.5;
//            case R.id.beauty_box_intensity_nose:
//                return !isHeightPerformance && sIntensityNose > 0;
//            case R.id.beauty_box_intensity_mouth:
//                return !isHeightPerformance && sIntensityMouth != 0.5;
        } else {
            return true;
        }
    }

    public static float getValue(int checkId) {
        if (checkId == R.id.beauty_seek_bar_shape_degree) {
            return sFaceShapeLevel;
        } else if (checkId == R.id.beauty_box_blur_level) {
            return sHeavyBlurLevel;
        } else if (checkId == R.id.beauty_radio_skin_white) {
            return sColorLevel;
        } else if (checkId == R.id.beauty_radio_beauty_ruddy) {
            return sRedLevel;
//            case R.id.beauty_box_eye_bright:
//                return !isHeightPerformance ? sEyeBright : 0;
//            case R.id.beauty_box_tooth_whiten:
//                return !isHeightPerformance ? sToothWhiten : 0;
        } else if (checkId == R.id.face_shape_radio_group) {
            return sFaceShape;
        } else if (checkId == R.id.beauty_seek_bar_eye) {
            if (!isHeightPerformance)
                return sEyeEnlarging;
            else
                return sEyeEnlargingOld;
        } else if (checkId == R.id.beauty_seek_bar_face_lift) {
            if (!isHeightPerformance)
                return sCheekThinning;
            else
                return sCheekThinningOld;
//            case R.id.beauty_box_intensity_chin:
//                return !isHeightPerformance ? sIntensityChin : 0.5f;
//            case R.id.beauty_box_intensity_forehead:
//                return !isHeightPerformance ? sIntensityForehead : 0.5f;
//            case R.id.beauty_box_intensity_nose:
//                return !isHeightPerformance ? sIntensityNose : 0;
//            case R.id.beauty_box_intensity_mouth:
//                return !isHeightPerformance ? sIntensityMouth : 0.5f;
        } else {
            return 0;
        }
    }

    public static void setValue(int checkId, float value) {
        if (checkId == R.id.beauty_seek_bar_shape_degree) {
            sFaceShapeLevel = value;

        } else if (checkId == R.id.beauty_box_blur_level) {
            sHeavyBlurLevel = value;

        } else if (checkId == R.id.beauty_radio_skin_white) {
            sColorLevel = value;

        } else if (checkId == R.id.beauty_radio_beauty_ruddy) {
            sRedLevel = value;

//            case R.id.beauty_box_eye_bright:
//                sEyeBright = value;
//                break;
//            case R.id.beauty_box_tooth_whiten:
//                sToothWhiten = value;
//                break;
        } else if (checkId == R.id.face_shape_radio_group) {
            sFaceShape = value;

        } else if (checkId == R.id.beauty_seek_bar_eye) {
            if (!isHeightPerformance)
                sEyeEnlarging = value;
            else
                sEyeEnlargingOld = value;

        } else if (checkId == R.id.beauty_seek_bar_face_lift) {
            if (!isHeightPerformance)
                sCheekThinning = value;
            else
                sCheekThinningOld = value;

//            case R.id.beauty_box_intensity_chin:
//                sIntensityChin = value;
//                break;
//            case R.id.beauty_box_intensity_forehead:
//                sIntensityForehead = value;
//                break;
//            case R.id.beauty_box_intensity_nose:
//                sIntensityNose = value;
//                break;
//            case R.id.beauty_box_intensity_mouth:
//                sIntensityMouth = value;
//                break;
        }
    }

    public static void setHeavyBlur(boolean isHeavy) {
        sHeavyBlur = isHeavy ? 1.0f : 0;
    }
}
