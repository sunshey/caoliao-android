package com.yc.liaolive.manager;

import android.content.Context;
import com.yc.liaolive.msg.model.bean.DaoMaster;
import com.yc.liaolive.msg.model.bean.DaoSession;
import org.greenrobot.greendao.query.QueryBuilder;

/**
 * TinyHung@outlook.com
 * 2017/6/28 15:26
 * 数据库的单列获取和初始化
 */
public class DBDaoManager {

    private static String  DB_NAME="caoliao_data.db";//数据库名称
    private volatile  static DBDaoManager mDaoManager;//多线程访问
    private  static DaoMaster.DevOpenHelper mHelper;
    private static  DaoMaster mDaoMaster;
    private static DaoSession mDaoSession;
    private Context context;

    /**
     * 使用单例模式获得操作数据库的对象
     * @return
     */
    public  static DBDaoManager getInstance(){
        DBDaoManager instance = null;
        if (mDaoManager==null){
            synchronized (DBDaoManager.class){
                if (instance==null){
                    instance = new DBDaoManager();

                    mDaoManager = instance;
                }
            }
        }
        mDaoManager.closeDaoSession();
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        return mDaoManager;
    }


    /**
     * 初始化Context对象
     * @param context
     */
    public void init(Context context){
        this.context = context;
    }

    /**
     * 判断数据库是否存在，如果不存在则创建
     * @return
     */
    public DaoMaster getDaoMaster(){
        if (null == mDaoMaster){
            mHelper =  new DaoMaster.DevOpenHelper(context,DB_NAME,null);
            mDaoMaster = new DaoMaster(mHelper.getWritableDatabase());
        }
        return mDaoMaster;
    }

    /**
     * 完成对数据库的增删查找
     * @return
     */
    public DaoSession getDaoSession(){
        if (null == mDaoSession){
            if (null == mDaoMaster){
                mDaoMaster = getDaoMaster();
            }
            mDaoSession = mDaoMaster.newSession();
        }
        return mDaoSession;
    }

    /**
     * 设置debug模式开启或关闭，默认关闭
     * @param flag
     */
    public void setDebug(boolean flag){
        QueryBuilder.LOG_SQL = flag;
        QueryBuilder.LOG_VALUES = flag;
    }

    /**
     * 关闭数据库
     */
    public void closeDataBase(){
        closeHelper();
        closeDaoSession();
    }

    public void closeDaoSession(){
        if (null != mDaoSession){
            mDaoSession.clear();
            mDaoSession = null;
        }
    }

    public  void  closeHelper(){
        if (mHelper!=null){
            mHelper.close();
            mHelper = null;
        }
    }
}
