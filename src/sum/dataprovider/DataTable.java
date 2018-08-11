/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: DataTable
 * Author:   sun2
 * Date:     2018/3/17 18:15
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package sum.dataprovider;

/**
 * Copyright (C), 2015-2018,sum
 * Author:   xqm
 * Date:     2018/3/17 18:15
 * Description: 数据表
 */
public class DataTable {


    private  String tableName;
    private  DataRowCollection dataRows;
    private  DataColumnCollection dataColumns;

    public  DataTable(){
        dataRows=new DataRowCollection();
        dataColumns=new DataColumnCollection();
     }
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    public DataRowCollection rows(){
        return  dataRows;
      }
    public DataRow  rows(int index){
        return  dataRows.getDataRow(index);
    }
    public DataColumnCollection columns(){
        return  dataColumns;
    }
    public DataColumn  columns(int index){
        return  dataColumns.getDataColumn(index);
    }
    public DataColumn  columns(String columnName){
        return  dataColumns.getDataColumn(columnName);
    }
    public  DataRow newRow(){
        DataRow row=new DataRow();
        row.dataTable=this;
        row.setDataRowState(DataRowState.Added);
        int size=dataColumns.size();
        for (int i=0;i<size;i++){
            row.Add(dataColumns.getDataColumn(i).getColumnName(),null);
        }
        this.dataRows.add(row);
        return row;
     }
    public void acceptChanges(){
        for (int i=0;i<this.rows().size();i++)
            this.rows(i).setDataRowState(DataRowState.Unchanged);

     }
}

