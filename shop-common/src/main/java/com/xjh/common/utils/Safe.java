package com.xjh.common.utils;

import java.util.Collection;
import java.util.List;

/**
 * TODO 类实现描述
 *
 * @author yinguoliang
 * @since 2021年3月21日 上午2:05:01
 */
public class Safe {
    public static void run(Runnable run) {
        try {
            run.run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void runAll(Collection<Runnable> runs) {
        try {
            for (Runnable run : runs) {
                run(run);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
