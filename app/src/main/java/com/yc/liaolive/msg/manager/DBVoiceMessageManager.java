package com.yc.liaolive.msg.manager;

import android.content.Context;
import android.util.Log;
import com.yc.liaolive.msg.dao.DBBaseDao;
import com.yc.liaolive.msg.model.bean.ResetVoiceMessage;
import com.yc.liaolive.msg.model.bean.ResetVoiceMessageDao;
import org.greenrobot.greendao.query.QueryBuilder;
import java.util.List;

/**
 * TinyHung@outlook.com
 * 2017/7/10 语音消息记录数据库
 * 上传视频信息记录
 */

public class DBVoiceMessageManager extends DBBaseDao<ResetVoiceMessage> {

    public DBVoiceMessageManager(Context context) {
        super(context);
    }

    /**
     * 通过ID查询对象
     * @return
     */
    public ResetVoiceMessage queryVoiceByID(Long id){
        return daoSession.getResetVoiceMessageDao().load(id);
    }

    /**
     * 通过ID获取对象
     * @return
     */
    private List<ResetVoiceMessage> getVoiceByID(Long id){
        QueryBuilder queryBuilder =  daoSession.getResetVoiceMessageDao().queryBuilder();
        queryBuilder.where(ResetVoiceMessageDao.Properties.Id.eq(id));
        int size = queryBuilder.list().size();
        if (size > 0){
            return queryBuilder.list();
        }else{
            return null;
        }
    }

    /**
     * 根据ID进行数据库的删除操作
     * @param id
     */
    public void deleteVoiceFromID(Long id){
        daoSession.getResetVoiceMessageDao().deleteByKey(id);
    }

    /**
     * 根据IDS同步删除数据库操作
     * @param ids
     */
    private void deleteVoicesFromIDS(List<Long> ids){
        daoSession.getResetVoiceMessageDao().deleteByKeyInTx(ids);
    }

    /**
     * 根据对象插入一条消息
     * @param voiceMessage
     */
    public boolean insertVoiceFromObject(ResetVoiceMessage voiceMessage) {
        ResetVoiceMessageDao resetVoiceMessageDao = daoSession.getResetVoiceMessageDao();
        ResetVoiceMessage unique = resetVoiceMessageDao.queryBuilder().where(ResetVoiceMessageDao.Properties.Id.eq(voiceMessage.getId())).unique();
        try {
            if(null==unique){
                resetVoiceMessageDao.insertInTx(voiceMessage);
                return true;
            }else{
                updateVoiceInfo(voiceMessage);
                return false;
            }
        }catch (RuntimeException e){
            return false;
        }
    }

    /**
     * 获取所有语音列表
     * @return
     */
    public synchronized List<ResetVoiceMessage> getAllVoiceLists(){
        return daoSession.getResetVoiceMessageDao().queryBuilder().orderAsc(ResetVoiceMessageDao.Properties.Id).list();
    }

    /**
     * 根据对象删除对象
     * @param resetVoiceMessage
     */
    public void deleteVoiceFromVoice(ResetVoiceMessage resetVoiceMessage) {
        try {
            daoSession.getResetVoiceMessageDao().delete(resetVoiceMessage);
        }catch (Exception e){

        }
    }


    /**
     * 更新一条消息
     * @param resetVoiceMessage
     */
    public synchronized void updateVoiceInfo(ResetVoiceMessage resetVoiceMessage) {
        try {
            Log.d(TAG,"更新="+resetVoiceMessage.getId());
            daoSession.getResetVoiceMessageDao().update(resetVoiceMessage);
        }catch (RuntimeException e){

        }
    }

    /**
     * 删除所有消息记录
     */
    public void deteleAllVoices() {
        daoSession.getResetVoiceMessageDao().deleteAll();
    }

    /**
     * 分页加载数据
     * @param page 页数
     * @param count 一页中的条数 加载第几页中的多少条数据
     */
    public List<ResetVoiceMessage> queryVoiceListOfPage(int page, int count) {
        return daoSession.getResetVoiceMessageDao().queryBuilder().offset((page-1)*count).limit(count).list();
    }
}
