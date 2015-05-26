﻿package ssq.utils;

import java.util.HashMap;
import java.util.Map;

public class Utilities
{

    public static final Boolean            LOG_ON          = true;
    public static final Boolean            DEBUG_ON        = true;                              //预处理也有debug版的选项
                                                                                                 
    public static Map<String, SqlAccesser> sqliteAccessers = new HashMap<String, SqlAccesser>();
    
    public static SqlAccesser addSqliteAccesser(String sqliteAccesserName)
    {
        if (sqliteAccessers.containsKey(sqliteAccesserName))
        {
            return sqliteAccessers.get(sqliteAccesserName);
        }
        else
        {
            SqlAccesser sqliteAccesser = new SqliteAccesser(sqliteAccesserName);
            sqliteAccessers.put(sqliteAccesserName, sqliteAccesser);
            return sqliteAccesser;
        }
    }
}
