package cn.anytec.security.util;

import java.util.*;

/**
 * Created by imyzt on 2018/10/15 16:18
 */
public class SearchUtil {

    /**
     * 从数组中查找最接近目标数据或相等的数字的索引位置.
     * @param src 数组
     * @param targetNum 目标数字
     * @param sort 是否需要排序
     * @return
     */
    public static int searchKey(List<Long> src, long targetNum, boolean sort) {

        if (sort) {
            Collections.sort(src);
        }

        if (src.size() <= 1) {
            return 0;
        }

        long minDifference = Math.abs(src.get(0) - targetNum);
        int minIndex = 0;
        for (int i = 1; i < src.size(); i++) {
            long temp = Math.abs(src.get(i) - targetNum);
            if (temp < minDifference) {
                minIndex = i;
                minDifference = temp;
            }
        }
        return minIndex;
    }

}
