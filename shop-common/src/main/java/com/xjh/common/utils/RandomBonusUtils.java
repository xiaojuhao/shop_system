package com.xjh.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 代码来源：https://blog.csdn.net/paincupid/article/details/82054647
 */
public class RandomBonusUtils {
    private static final Random RANDOM_NUMBER = new Random();

    private static boolean canReward(double rate) {
        return Math.random() <= rate;
    }

    private static int randomNumber(int lower, int upper) {
        if (lower > upper) {
            return randomNumber(upper, lower);
        }
        if (lower == upper) {
            return lower;
        }
        return lower + RANDOM_NUMBER.nextInt(upper - lower + 1);
    }

    private static int getRandomValWithSpecifySubRate(int boundMin, int boundMax, int subMin, int subMax, double subRate) {
        if (canReward(subRate)) {
            return randomNumber(subMin, subMax);
        }
        return randomNumber(boundMin, boundMax);
    }

    public static Integer randomBonusWithSpecifyBound(int totalBonus, int totalNum, int sendedBonus, int sendedNum, int rdMin, int rdMax, double bigRate) {
        int avg = totalBonus / totalNum; // 平均值
        int leftLen = avg - rdMin;
        int rightLen = rdMax - avg;

        int[] bounds = calculateBounds(totalBonus, totalNum, sendedBonus, sendedNum, rdMin, rdMax, bigRate, avg, leftLen, rightLen);
        int boundMin = bounds[0];
        int boundMax = bounds[1];

        if (boundMin == boundMax) {
            return randomNumber(boundMin, boundMax);
        }

        double currAvg = sendedNum == 0 ? (double) avg : (sendedBonus / (double) sendedNum);
        double middle = (boundMin + boundMax) / 2.0;
        int subMin = boundMin, subMax = boundMax;
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

    private static int[] calculateBounds(int totalBonus, int totalNum, int sendedBonus, int sendedNum, int rdMin, int rdMax, double bigRate, int avg, int leftLen, int rightLen) {
        int boundMin, boundMax;
        if (leftLen == rightLen) {
            boundMin = Math.max((totalBonus - sendedBonus - (totalNum - sendedNum - 1) * rdMax), rdMin);
            boundMax = Math.min((totalBonus - sendedBonus - (totalNum - sendedNum - 1) * rdMin), rdMax);
        } else if (rightLen > leftLen) {
            int standardRdMax = avg + leftLen;
            int localRdMax = canReward(bigRate) ? rdMax : standardRdMax;
            boundMin = Math.max((totalBonus - sendedBonus - (totalNum - sendedNum - 1) * standardRdMax), rdMin);
            boundMax = Math.min((totalBonus - sendedBonus - (totalNum - sendedNum - 1) * rdMin), localRdMax);
        } else {
            int standardRdMin = avg - rightLen;
            int localRdMin = canReward(bigRate) ? rdMin : standardRdMin;
            boundMin = Math.max((totalBonus - sendedBonus - (totalNum - sendedNum - 1) * rdMax), localRdMin);
            boundMax = Math.min((totalBonus - sendedBonus - (totalNum - sendedNum - 1) * standardRdMin), rdMax);
        }
        return new int[]{boundMin, boundMax};
    }

    public static List<Integer> createBonusList(Integer totalBonus, Integer totalNum, Integer rdMin, Integer rdMax, double bigRate) {
        int sendedBonus = 0;
        int sendedNum = 0;
        List<Integer> bonusList = new ArrayList<>();
        while (sendedNum < totalNum) {
            Integer bonus = randomBonusWithSpecifyBound(totalBonus, totalNum, sendedBonus, sendedNum, rdMin, rdMax, bigRate);
            bonusList.add(bonus);
            sendedNum++;
            sendedBonus += bonus;
        }
        return bonusList;
    }
}
