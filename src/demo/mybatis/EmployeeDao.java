/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: EmployeeDao
 * Author:   sun2
 * Date:     2018/4/14 17:34
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package demo.mybatis;
import   org.apache.ibatis.session.SqlSession;
/**
 * Copyright (C), 2015-2018,sum
 * Author:   xqm
 * Date:     2018/4/14 17:34
 * Description:  
 */
public class EmployeeDao {


    public void add(Employee emp)  throws Exception{
        SqlSession sqlSession = null;
        try{
            sqlSession = MybatisUtil.getSqlSession();
            //事务开始（默认）

          sqlSession.insert(Employee.class.getName()+".add",emp);
            //事务提交
         sqlSession.commit();
        }catch(Exception e){
            e.printStackTrace();
            //事务回滚
            sqlSession.rollback();
            throw e;
        }finally{
            MybatisUtil.closeSqlSession();
        }

    }
    public static void main(String[] args) throws Exception {




        EmployeeDao dao=new EmployeeDao();
        Employee emp=new Employee();
        emp.setFName("mybait1");
        emp.setFAge(122d);
       dao.add(emp);
    }
}
