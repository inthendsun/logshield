package tech.theaha.log.shield.bc;

import java.util.List;
import java.util.Map;

/**
 * 脱敏handler接口
 * @param <T>
 */
public interface OffSensitiveHandler<T> {
	
	/**
	 * 对对象进行掩码，并返回原始值
	 * @param obj
	 * @return key=被掩码属性的set方法名字,value=被掩码属性原始值
	 */
	public Map<String,String> shield(T obj);
	
	/**
	 * 获取对象需要掩码的嵌套对象
	 * @param obj
	 * @return
	 */
	public List<Object> getNShieldFields(T obj);
	
	/**
	 * 对对象值进行恢复
	 * @param obj 
	 * @param oriInfo shied方法返回的值
	 */
	public void restore(T obj,Map<String,String> oriInfo);
}
