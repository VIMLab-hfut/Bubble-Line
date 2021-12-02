package cn.hfut.adv.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kawinth
 */

public enum HsSiteValue {
    /***/
    SW_TH1("sw_th1", "天海一库水位"),
    SW_TH2("sw_th2", "天海二库水位"),
    SW_WLQ2("sw_wlq2", "新二库水位"),
    SW_XH("sw_xh", "西海水库水位");

    /**地点值*/
    private final String site;
    /**说明*/
    private final String note;

    private HsSiteValue(String site, String note){
        this.site = site;
        this.note = note;
    }

    public String getSite() {
        return site;
    }

    public String getNote() {
        return note;
    }

    public static List<Map> toList(){
        HsSiteValue[] values = HsSiteValue.values();
        List<Map> list = new ArrayList<>();
        for (HsSiteValue item : values) {
            Map hsSiteValue = new HashMap();
            hsSiteValue.put("site", item.getSite());
            hsSiteValue.put("note", item.getNote());
            list.add(hsSiteValue);
        }
        return list;
    }
}
