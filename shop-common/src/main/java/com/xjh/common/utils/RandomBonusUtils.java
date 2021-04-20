package com.xjh.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 代码来源：https://blog.csdn.net/paincupid/article/details/82054647
 */
public class RandomBonusUtils {
    private static boolean canReward(double rate) {
        return Math.random() <= rate;
    }

    static Random RANDOM_NUMBER = new Random();

    private static int randomNumber(int lower, int upper) {
        if (lower > upper) {
            return randomNumber(upper, lower);
        }
        if (lower == upper) {
            return lower;
        }
        int randInt = RANDOM_NUMBER.nextInt(upper - lower + 1);
        return lower + randInt;
    }

    private static int getRandomValWithSpecifySubRate(int boundMin, int boundMax, int subMin, int subMax,
            double subRate) {
        if (canReward(subRate)) {
            return randomNumber(subMin, subMax);
        }
        return randomNumber(boundMin, boundMax);
    }

    /**
     * 随机分配第n个红包
     *
     * @param totalBonus  总红包量
     * @param totalNum    总份数
     * @param sendedBonus 已发送红包量
     * @param sendedNum   已发送份数
     * @param rdMin       随机下限
     * @param rdMax       随机上限
     * @return rs
     */
    public static Integer randomBonusWithSpecifyBound(int totalBonus, int totalNum, int sendedBonus,
            int sendedNum, int rdMin, int rdMax, double bigRate) {
        int avg = totalBonus / totalNum; // 平均值
        int leftLen = avg - rdMin;
        int rightLen = rdMax - avg;
        int boundMin, boundMax;

        // 大范围设置小概率
        if (leftLen == rightLen) {
            boundMin = Math.max((totalBonus - sendedBonus - (totalNum - sendedNum - 1) * rdMax), rdMin);
            boundMax = Math.min((totalBonus - sendedBonus - (totalNum - sendedNum - 1) * rdMin), rdMax);
        } else if (rightLen > leftLen) {
            // 上限偏离
            // 右侧对称上限点
            int standardRdMax = avg + leftLen;
            int localRdMax = canReward(bigRate) ? rdMax : standardRdMax;
            boundMin = Math.max((totalBonus - sendedBonus - (totalNum - sendedNum - 1) * standardRdMax), rdMin);
            boundMax = Math.min((totalBonus - sendedBonus - (totalNum - sendedNum - 1) * rdMin), localRdMax);
        } else {
            // 下限偏离
            int standardRdMin = avg - rightLen; // 左侧对称下限点
            int localRdMin = canReward(bigRate) ? rdMin : standardRdMin;
            boundMin = Math.max((totalBonus - sendedBonus - (totalNum - sendedNum - 1) * rdMax), localRdMin);
            boundMax = Math.min((totalBonus - sendedBonus - (totalNum - sendedNum - 1) * standardRdMin), rdMax);
        }

        // 已发平均值偏移修正-动态比例
        if (boundMin == boundMax) {
            return randomNumber(boundMin, boundMax);
        }
        // 当前已发平均值
        double currAvg = sendedNum == 0 ? (double) avg : (sendedBonus / (double) sendedNum);
        double middle = (boundMin + boundMax) / 2.0;
        int subMin = boundMin, subMax = boundMax;
        // 期望值
        double exp = avg - (currAvg - avg) * sendedNum / (double) (totalNum - sendedNum);
        if (middle > exp) {
            subMax = (int) Math.round((boundMin + exp) / 2.0);
        } else {
            subMin = (int) Math.round((exp + boundMax) / 2.0);
        }
        int expBound = (boundMin + boundMax) / 2;
        int expSub = (subMin + subMax) / 2;
        double subRate = (exp - expBound) / (double) (expSub - expBound);
        return getRandomValWithSpecifySubRate(boundMin, boundMax, subMin, subMax, subRate);
    }

    /**
     * 生成红包一次分配结果
     *
     * @param totalBonus total
     * @param totalNum   spit num
     * @param rdMin      min
     * @param rdMax      max
     * @param bigRate    指定大范围区间的概率
     * @return rs
     */
    public static List<Integer> createBonusList(Integer totalBonus, Integer totalNum, Integer rdMin, Integer rdMax,
            double bigRate) {
        int sendedBonus = 0;
        int sendedNum = 0;
        List<Integer> bonusList = new ArrayList<>();
        while (sendedNum < totalNum) {
            Integer bonus =
                    randomBonusWithSpecifyBound(totalBonus, totalNum, sendedBonus, sendedNum, rdMin, rdMax, bigRate);
            bonusList.add(bonus);
            sendedNum++;
            sendedBonus += bonus;
        }
        return bonusList;
    }
}
