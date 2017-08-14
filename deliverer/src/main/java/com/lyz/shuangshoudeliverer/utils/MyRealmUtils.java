package com.lyz.shuangshoudeliverer.utils;

import android.content.Context;

import java.security.SecureRandom;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyRealmUtils {

    /**
     *存储每次扫描获取的纸箱信息，在发布的时候删除
     * @param context
     * @return
     */
    public static Realm getRealmCartonDetail(Context context) {
        byte[] key = new byte[64];
        new SecureRandom().nextBytes(key);
        Realm.init(context);

//        Migration migration = new Migration();

        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("questId.realm") //文件名
                .schemaVersion(1) //版本号
//                .migration(migration)//数据库版本迁移（数据库升级，当数据库中某个表添加字段或者删除字段）
                .deleteRealmIfMigrationNeeded()//声明版本冲突时自动删除原数据库。
                .build();//创建
        return Realm.getInstance(config);
    }


}
