package com.faceunity.beauty.entity;

import com.faceunity.R;
import com.faceunity.entity.Filter;
import java.util.ArrayList;

/**
 * Created by tujh on 2018/1/30.
 */

public enum FilterEnum {

    nature("origin", R.drawable.nature, "原图", Filter.FILTER_TYPE_FILTER),
    delta("delta", R.drawable.delta, "复古", Filter.FILTER_TYPE_FILTER),
    electric("electric", R.drawable.electric, "科幻", Filter.FILTER_TYPE_FILTER),
    slowlived("slowlived", R.drawable.slowlived, "生活", Filter.FILTER_TYPE_FILTER),
    tokyo("tokyo", R.drawable.tokyo, "冷艳", Filter.FILTER_TYPE_FILTER),
    warm("warm", R.drawable.warm, "柔和", Filter.FILTER_TYPE_FILTER),

    nature_beauty("origin", R.drawable.nature, "原图", Filter.FILTER_TYPE_BEAUTY_FILTER),
    ziran("ziran", R.drawable.slowlived, "自然", Filter.FILTER_TYPE_BEAUTY_FILTER),
    danya("danya", R.drawable.tokyo, "淡雅", Filter.FILTER_TYPE_BEAUTY_FILTER),
    fennen("fennen", R.drawable.warm, "粉嫩", Filter.FILTER_TYPE_BEAUTY_FILTER),
    qingxin("qingxin", R.drawable.tokyo, "清新", Filter.FILTER_TYPE_BEAUTY_FILTER),
    hongrun("hongrun", R.drawable.warm, "红润", Filter.FILTER_TYPE_BEAUTY_FILTER);

    private String filterName;
    private int resId;
    private String description;
    private int filterType;

    FilterEnum(String name, int resId, String description, int filterType) {
        this.filterName = name;
        this.resId = resId;
        this.description = description;
        this.filterType = filterType;
    }

    public String filterName() {
        return filterName;
    }

    public int resId() {
        return resId;
    }

    public String description() {
        return description;
    }

    public Filter filter() {
        return new Filter(filterName, resId, description, filterType);
    }

    public static ArrayList<Filter> getFiltersByFilterType(int filterType) {
        ArrayList<Filter> filters = new ArrayList<>();
        for (FilterEnum f : FilterEnum.values()) {
            if (f.filterType == filterType) {
                filters.add(f.filter());
            }
        }
        return filters;
    }
}
