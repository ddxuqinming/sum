/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: KeyValueOf
 * Author:   sun2
 * Date:     2018/3/26 20:48
 * Description: 带key，value的map
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package sum.common;
import java.util.*;
/**
 * Copyright (C), 2015-2018,sum
 * Author:   xqm
 * Date:     2018/3/26 20:48
 * Description:  带key，value的map
 */
public class KeyValueListOf<TKey,TValue> {

    private   Map<TKey, TValue>  collName ;// Map存放key
    private   ArrayList <TKey> lstIndex ;//存放列索引

    public KeyValueListOf() {
        this.collName = new HashMap<TKey, TValue>() ;
        this.lstIndex=new ArrayList<TKey>();
    }

    public  void add(TKey key,TValue value){
         collName.put(key,value);
         lstIndex.add(key);

    }

    public  TValue getValue(TKey key ){
       return collName.get(key );
    }

    public  TValue getValue(int index){
        TKey key=lstIndex.get(index);
        return getValue(key );
    }
    public  void remove(TKey key){
        collName.remove(key);
        lstIndex.remove(key);

    }
    public  int size( ){
        return lstIndex.size();
    }

}
