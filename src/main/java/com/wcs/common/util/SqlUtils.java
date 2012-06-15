package com.wcs.common.util;

import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.Set;

/**
 * <p>Project: BTC</p>
 * <p>Description: 组装sql语句</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 *
 * @author <a href="mailto:hujianguang@wcs-global.com">胡建光</a>
 */
public final class SqlUtils {

    /**
     *  <p>Description: 构建sql插入语句</p>
     * @param tableName  表名
     * @param sqlMap   需要插入的字段名，字段值
     * @return    完整的sql语句
     */
    public static String buildInsertSql(String tableName, Map<String, String> sqlMap) {

        //表名或字段名值为空，返回
        if (StringUtils.isEmpty(tableName) || sqlMap.isEmpty()) {
            return null;
        }

        StringBuilder sqlBuff = new StringBuilder("INSERT INTO ");

        sqlBuff.append(tableName);

        sqlBuff.append(" (");

        Set<String> fieldSet = sqlMap.keySet();

        int length = fieldSet.size();

        int i = 0;
        for (String field : fieldSet) {

            sqlBuff.append(field);

            //去掉字段名结尾的逗号
            sqlBuff.append(length != ++i ? "," :"");
        }

        sqlBuff.append(") VALUES (");

        i = 0;
        for (String field : fieldSet) {
            sqlBuff.append("'");
            sqlBuff.append(sqlMap.get(field));
            sqlBuff.append("'");

            //去掉字段值结尾的逗号
            sqlBuff.append(length != ++i ? "," :"");
        }

        sqlBuff.append(")");

        return sqlBuff.toString();

    }

}
