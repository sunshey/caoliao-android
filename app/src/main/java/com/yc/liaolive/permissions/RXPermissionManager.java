package com.yc.liaolive.permissions;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.view.View;

import com.yc.liaolive.ui.dialog.QuireDialog;
import com.yc.liaolive.util.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

/**
 * Created by yangxueqin on 18/12/14.
 */

public class RXPermissionManager {
    public static final String TAG = "RXPermissionManager";

    public static final String PERMISSION_CAMERA = "android.permission.CAMERA";

    public static final String PERMISSION_CONTACTS = "android.permission.READ_CONTACTS";

    public static final String PERMISSION_MICROPHONE = "android.permission.RECORD_AUDIO";

    public static final String PERMISSION_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";

    public static final String PERMISSION_LOCATION = "android.permission.ACCESS_COARSE_LOCATION";

    public static final String PERMISSION_PHONE = "android.permission.READ_PHONE_STATE";

    public static final String PERMISSION_CALENDER = "android.permission.WRITE_CALENDAR";

    public static final String PERMISSION_READ_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";

    private static RXPermissionManager sSingleton;

    private Context mCtx;

    private Map<String, PublishSubject<Permission>> mSubjects = new HashMap<>();

    private PublishSubject<Boolean> mDialogSubject;

    public RXPermissionManager(Context ctx) {
        mCtx = ctx;
    }

    public static RXPermissionManager getInstance(Context ctx) {
        if (sSingleton == null) {
            sSingleton = new RXPermissionManager(ctx.getApplicationContext());
        }
        return sSingleton;
    }

    public PublishSubject<Boolean> getmDialogSubject() {
        return mDialogSubject;
    }

    /**
     * 跳转到处理权限的activity
     *
     * @param permissions 权限数组
     * @param isFullScreen 是否全屏
     */
    private void startShadowActivity(boolean isFullScreen, String[] permissions) {
        Intent intent = new Intent(mCtx, CLPermissionActivity.class);
        intent.putExtra("permissions", permissions);
        intent.putExtra("isFullScreen", isFullScreen);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mCtx.startActivity(intent);
    }

    /**
     * 判断是否版本是否android6.0++
     *
     * @return
     */
    private boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 判断是否同意权限
     *
     * @param permission 权限组
     * @return
     */
    @TargetApi(Build.VERSION_CODES.M)
    public boolean isGranted(String permission) {
        return !isMarshmallow() ||
                mCtx.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 判断权限是否已经被不再提示
     *
     * @param permission
     * @return
     */
    @TargetApi(Build.VERSION_CODES.M)
    public boolean isRevoked(String permission) {
        return mCtx.getPackageManager()
                .isPermissionRevokedByPolicy(permission, mCtx.getPackageName());
    }

    public Observable<List<Permission>> requestForPermission(final String... permissions) {
        return requestForPermission(false, permissions);
    }

    /**
     * 请求权限
     *
     * @param permissions
     * @param isFullScreen 是否全屏
     * @return 所有请求权限处理结果
     */
    public Observable<List<Permission>> requestForPermission(final boolean isFullScreen, final String... permissions) {
        if (permissions == null || permissions.length == 0) {
            throw new IllegalArgumentException(
                    "RXPermissionManager.request 至少需要一个参数");
        }
        mSubjects.clear();
        return Observable.just(true)
                .flatMap(new Func1<Boolean, Observable<Permission>>() {
                    @Override
                    public Observable<Permission> call(Boolean aBoolean) {
                        List<Observable<Permission>> list = new ArrayList<>(permissions.length);
                        List<String> unrequestedPermissions = new ArrayList<>();
                        for (String permission : permissions) {
                            if (isGranted(permission)) {
                                list.add(Observable.just(new Permission(permission, true, true)));
                                continue;
                            }

                            PublishSubject<Permission> subject = mSubjects.get(permission);
                            if (subject == null) {
                                unrequestedPermissions.add(permission);
                                subject = PublishSubject.create();
                                mSubjects.put(permission, subject);
                            }
                            list.add(subject);
                        }
                        if (!unrequestedPermissions.isEmpty()) {
                            startShadowActivity(isFullScreen, unrequestedPermissions
                                    .toArray(new String[unrequestedPermissions.size()]));
                        }
                        return Observable.concat(Observable.from(list));
                    }
                })
                .buffer(permissions.length);
    }


    /**
     * 请求权限
     *
     * @param permissions
     * @return 请求权限是否成功
     */
    @TargetApi(Build.VERSION_CODES.M)
    public Observable<Boolean> request(final String... permissions) {
        return requestForPermission(permissions)
                .flatMap(new Func1<List<Permission>, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(List<Permission> objects) {
                        for (Permission p : objects) {
                            if (!p.granted) {
                                return Observable.just(false);
                            }
                        }
                        return Observable.just(true);
                    }
                });

    }

    /**
     * 默认处理权限返回
     *
     * @param activity
     * @return
     */
    public Observable.Transformer<List<Permission>, Boolean> defultHandler(final Activity activity) {
        return this.defultHandler(activity, true);
    }

    /**
     * 默认处理权限返回
     *
     * @param activity
     * @return
     */
    public Observable.Transformer<List<Permission>, Boolean> defultHandler(final Activity activity, final boolean isCancel) {
        return new Observable.Transformer<List<Permission>, Boolean>() {

            @Override
            public Observable<Boolean> call(Observable<List<Permission>> listObservable) {
                return listObservable
                        .filter(new Func1<List<Permission>, Boolean>() {
                            @Override
                            public Boolean call(List<Permission> permissions) {
                                for (Permission p : permissions) {
                                    if (!p.granted && !p.shouldShowRequestPermissionRationale) {
                                        showNoshouldShowRequestPermissionRationaleDialog(activity,
                                                isCancel);
                                        return false;
                                    }
                                }
                                return true;
                            }
                        })
                        .flatMap(new Func1<List<Permission>, Observable<Boolean>>() {
                            @Override
                            public Observable<Boolean> call(List<Permission> objects) {
                                for (Permission p : objects) {
                                    if (!p.granted) {
                                        return Observable.just(false);
                                    }
                                }
                                return Observable.just(true);
                            }
                        });
            }
        };
    }

    /**
     * 默认处理权限返回
     * 注意该方法只能处理 单个权限  虽然是list 但只取了 第一个权限
     *
     * @return 1:同意权限   2:拒绝 但没有选"不再提示"  3:拒绝:选择了"不再提示"
     */
    public Observable.Transformer<List<Permission>, Integer> defultHandlerNoDialog() {
        return new Observable.Transformer<List<Permission>, Integer>() {

            @Override
            public Observable<Integer> call(Observable<List<Permission>> listObservable) {
                return listObservable
                        .flatMap(new Func1<List<Permission>, Observable<Integer>>() {
                            @Override
                            public Observable<Integer> call(List<Permission> objects) {
                                if (objects.size() > 0) {
                                    Permission p = objects.get(0);
                                    if (p.granted) {
                                        return Observable.just(1);
                                    } else if (p.shouldShowRequestPermissionRationale) {
                                        return Observable.just(2);
                                    } else {
                                        return Observable.just(3);
                                    }
                                }
                                return Observable.just(2);
                            }
                        });
            }
        };
    }


    /**
     * 处理未授权情况
     *
     * @param activity
     * @return
     */
    public Observable.Transformer<List<Permission>, List<Permission>> handlerNoGranted(final Activity activity) {
        return new Observable.Transformer<List<Permission>, List<Permission>>() {

            @Override
            public Observable<List<Permission>> call(Observable<List<Permission>> listObservable) {
                return listObservable
                        .doOnNext(new Action1<List<Permission>>() {
                            @Override
                            public void call(List<Permission> permissions) {
                                int count = 0;
                                boolean isShow = false;
                                for (Permission p : permissions) {
                                    if (!p.granted) {
                                        isShow = true;
                                    }
                                    if (!p.shouldShowRequestPermissionRationale) {
                                        count++;
                                    }
                                }
                                if (isShow && count == 0) {
                                    showNoGranted(activity);
                                }
                            }
                        });
            }
        };
    }


    /**
     * 处理请求权限回调结果
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     * @param shouldShowRequestPermissionRationale
     */
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults, boolean[] shouldShowRequestPermissionRationale) {
        for (int i = 0, size = permissions.length; i < size; i++) {

            PublishSubject<Permission> subject = mSubjects.get(permissions[i]);
            if (subject == null) {
                Logger.e(TAG,"RXPermissionManager.onRequestPermissionsResult invoked but didn't find the corresponding permission request.");
            } else {
                mSubjects.remove(permissions[i]);
                boolean granted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                subject.onNext(new Permission(permissions[i], granted,
                        shouldShowRequestPermissionRationale[i]));
                subject.onCompleted();
            }
        }
    }

    /**
     * 当权限被拒绝并且点击不再提醒时弹框提示
     */
    public void showNoshouldShowRequestPermissionRationaleDialog(final Activity activity) {
        showNoshouldShowRequestPermissionRationaleDialog(activity, false);
    }

    /**
     * 当权限被拒绝并且点击不再提醒时弹框提示
     */
    public void showNoshouldShowRequestPermissionRationaleDialog(final Activity activity, boolean canCancel) {
        QuireDialog dialog = QuireDialog.getInstance(activity)
                .showTitle(false)
                .setContentText("未取得您的使用权限，应用无法开启。请在应用权限中打开权限")
                .setCancelTitleVisible(View.GONE)
                .setSubmitTitleText("去设置")
                .showCloseBtn(canCancel)
                .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                    @Override
                    public void onConsent() {
                        Uri uri = Uri.parse("package:" + activity.getPackageName());
                        Intent intent = new Intent(
                                "android.settings.APPLICATION_DETAILS_SETTINGS",
                                uri);
                        activity.startActivity(intent);
                    }

                });
        dialog.setCanceledOnTouchOutside(canCancel);
        dialog.show();

    }

    /**
     * 不授权权限处理
     *
     * @param activity
     */
    public void showNoGranted(final Activity activity) {
        mDialogSubject = PublishSubject.create();
        QuireDialog dialog = QuireDialog.getInstance(activity)
                .showTitle(false)
                .setContentText("为了保证您正常使用，请点击允许。拒绝后应用将无法正常运行。")
                .setCancelTitleVisible(View.GONE)
                .setSubmitTitleText("去允许")
                .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                    @Override
                    public void onConsent() {
                        mDialogSubject.onNext(true);
                        mDialogSubject.onCompleted();
                    }

                });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    /**
     * 当权限被拒绝时弹窗，点击去设置按钮跳转设置页
     * @param tips 弹窗显示文案，不能为空
     * @param canCancel 是否可以取消
     */
    public void showRejectDialog(final Activity activity, String tips, boolean canCancel) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        QuireDialog dialog = QuireDialog.getInstance(activity)
                .setTitleText("  ")//如果不显示title但是显示右侧关闭按钮时，会重叠到conetnt上
                .setContentText(tips)
                .setCancelTitleVisible(View.GONE)
                .setSubmitTitleText("去设置")
                .showCloseBtn(canCancel)
                .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                    @Override
                    public void onConsent() {
                        Uri uri = Uri.parse("package:" + activity.getPackageName());
                        Intent intent = new Intent(
                                "android.settings.APPLICATION_DETAILS_SETTINGS",
                                uri);
                        activity.startActivity(intent);
                    }

                });
        dialog.setCanceledOnTouchOutside(canCancel);
        dialog.show();

    }
}
