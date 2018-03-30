/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: DataRow
 * Author:   sun2
 * Date:     2018/3/27 18:48
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package sum.dataprovider;
import sum.common.KeyValueListOf;
import java.util.*;
/**
 * Copyright (C), 2015-2018,sum
 * Author:   xqm
 * Date:     2018/3/27 18:48
 * Description:  
 */
public class DataRow {
    private KeyValueListOf<String,  Object> fieldValueColl;
    private DataRowState rowState;

    public DataRow()   {
        fieldValueColl=new KeyValueListOf<String,  Object>();
    }

    public  void Add(String columnName, Object value){
        this.fieldValueColl.add(columnName,value);
    }

    public Object getValue(String columnName){
      return   this.fieldValueColl.getValue(columnName);
     }
    public Object getValue(int columnIndex){
        return   this.fieldValueColl.getValue(columnIndex);
    }
    public  int size(){
        return fieldValueColl.size();
    }
    public  void setDataRowState(DataRowState dataRowState){
        rowState=dataRowState;
    }
    public  DataRowState getDataRowState(){
       return  rowState;
    }
}
