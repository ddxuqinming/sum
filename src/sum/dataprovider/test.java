/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: test
 * Author:   sun2
 * Date:     2018/3/28 19:54
 * Description: 测试类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package sum.dataprovider;
import java.math.BigDecimal;
import java.sql.Types;
import java.sql.*;
import java.util.HashMap;
import  sum.common.*;
import java.util.*;



/**
 * Copyright (C), 2015-2018,sum
 * Author:   xqm
 * Date:     2018/3/28 19:54
 * Description:  测试类
 */
public class test {


    public static void main(String[] args)  {
        System.out.println("loading... xqm");
       new test().testDataAccess();

        List<Object> list = new ArrayList<Object>();
        list.add(new Integer(5));
        Object a = list.get(0);
    }

    private   void  testDataTable(){
       DataTable dtb=new DataTable();
        dtb.columns().add("FID","ID", java.sql.Types.TINYINT );
        dtb.columns().add("FName","姓名", Types.VARCHAR );
        DataRow drw=dtb.newRow();
        drw.Add("FID",1);
        drw.Add("FName","xqm");

        System.out.println("size=" + dtb.rows(0).getValue("FName"));


    }

    private   void  testDataAccess() {
        DataAccess xudb=new DataAccess();
        xudb.initMySqlUrl("localhost",3306,"xusoft","root","111","");
       // System.out.println(xudb.getUrl());
       // xudb.beginTrans();
        int i= xudb.exeSql("update uemployee set FName='AA2' where  FName=?",  new Object[]{"7"});
      //  xudb.rollbackTrans();
         DataTable  dtb= xudb.getDataTable("select  null as FName from uEmployee where 1=1");
        dtb.columns(0).setCaption("姓名");
        double name= dtb.rows(0).getAsIntegerZ("FName")  ;
//
         HashMap<String,Object> hst=xudb.getItem("select * from uemployee", new DataAccess.ResultSetHandler<HashMap<String, Object>>() {
         @Override
         public HashMap<String, Object> handle(ResultSet rs) throws SQLException {
             HashMap<String,Object> hst2=new HashMap<>();
             if (rs.next()) {

                 hst2.put("FID",rs.getString("FID"));
                 hst2.put("FName",rs.getString("FName"));
             }
             return hst2;
         }
     });

         KeyValueListOf<String,Object> hst3=new KeyValueListOf<>();



         hst3.add("111F1Name","1");
        hst3.add("F55ID","2");
        hst3.add("111F1Name2","3");
        System.out.println("v=" + hst3  );

    }

}
