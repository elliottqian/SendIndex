package netease.beauty.sendindex;

import netease.beauty.mysql.SelectInfo;

/**
 * Created by hzqianwei on 2016/7/21.
 *
 */

public class Main {
    public static void main(String[] args) {
        SelectInfo si = new SelectInfo();

        // 0是主表名称  1是index名称
        si.selectInfo(args[0], args[1]);
    }
}

/**
 *
 *
 *
 *
 *

 public class Singleton {
    private static Singleton instance;
    private Singleton (){}

    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
    return instance;
    }
 }

 
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */