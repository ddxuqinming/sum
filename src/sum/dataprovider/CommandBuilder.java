/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: CommandBuilder
 * Author:   sun2
 * Date:     2018/4/3 19:38
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package sum.dataprovider;
import sum.common.Func;

import java.sql.*;
/**
 * Copyright (C), 2015-2018,sum
 * Author:   xqm
 * Date:     2018/4/3 19:38
 * Description:  
 */
public class CommandBuilder {
    private String tableName;
    DataAccess xudb;

    public CommandBuilder(String tableName,DataAccess xudb){
        this.tableName=tableName;
        this.xudb=xudb;
     }
     public  SQLCommand createInsertCommand(DataRow dataRow){
        try {
            ResultSet rs= xudb.getResultSet("select * from " + tableName + " where 1=0");
            ResultSetMetaData rsmd = rs.getMetaData();
            rs.close();
            //生成sql
            String sql="insert into " + tableName ;
            String fields="";
            String values="";
            SQLCommand cmd=new SQLCommand();
            String columnName;
            for(int i=0;i<rsmd.getColumnCount();i++) {
                columnName= rsmd.getColumnName(i + 1);
               // names.put(rsmd.getColumnName(i+1), rsmd.getColumnType(i+1)  );
                if (! rsmd.isAutoIncrement(i)&& dataRow.getColumnIndex(columnName)>=0) {

                    fields += columnName + ",";
                    values+="?" + ",";
                    cmd.FieldValues.add(columnName,dataRow.getValue(columnName));
                }
            }
            sql=sql   + "(" + Func.trimEnd(fields,",") + ") values (" + Func.trimEnd(values,"'") + ")";
            cmd.sqlText = sql;
            return cmd;
        }catch (SQLException ex){
            throw new RuntimeException(ex);

        }


      }
}


