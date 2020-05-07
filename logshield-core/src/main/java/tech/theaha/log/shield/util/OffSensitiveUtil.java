package tech.theaha.log.shield.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tech.theaha.log.shield.OffSensitive;
import tech.theaha.log.shield.bc.OffSensitiveHandler;
import tech.theaha.log.shield.bc.OffSensitiveHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OffSensitiveUtil {

	private static Logger logger = LoggerFactory.getLogger(OffSensitiveUtil.class);
	
	/**
	 * 脱敏
	 * @param oArr
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<OffSensitiveObjectContext> off(Object ... oArr) {
		List<OffSensitiveObjectContext> list = new ArrayList();
		// 防止死循环
		Set<Integer> recurRef = new HashSet<Integer>();
		try {
			for (Object object : oArr) {
				shieldObject(object, list, recurRef);
			}
		} catch (Exception e) {
            logger.error("脱敏异常，请联系组件开发同学",e);
		} 
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public static void restore(List<OffSensitiveObjectContext>  list) {
		// 恢复被shield的对象
		if (!list.isEmpty()) {
			for (OffSensitiveObjectContext ctx : list) {
				ctx.getHandler().restore(ctx.getObject(), ctx.getRestoreValue());
			}
		}
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void shieldObject(Object object, List<OffSensitiveObjectContext> shieldList, Set<Integer> recurRef) {
		if (object == null) {
			return;
		}
		if (recurRef.contains(System.identityHashCode(object))) {// 防止循环依赖
			return;
		}
		if (object.getClass().isArray()) {
			Object[] oArray = (Object[]) object;
			for (Object element : oArray) {
				shieldObject(element, shieldList, recurRef);
			}
			return;
		} else if (object instanceof Collection) {
			Collection<?> oCol = (Collection<?>) object;
			for (Object element : oCol) {
				shieldObject(element, shieldList, recurRef);
			}
			return;
		} else if (object instanceof Map) {
			Map map = (Map) object;
			Set set = map.keySet();
			Iterator iter = set.iterator();
			while (iter.hasNext()) {
				Object key = (Object) iter.next();
				Object value = map.get(key);
				shieldObject(key, shieldList, recurRef);
				shieldObject(value, shieldList, recurRef);
			}
			return;
		}
		if (!object.getClass().isAnnotationPresent(OffSensitive.class)) {
			return;
		}
		OffSensitiveHandler handler = OffSensitiveHandlerFactory.getHandler(object.getClass());
		if (handler != null) {
			// shield 对象，并获取到原始值
			Map<String, String> restoreValue = handler.shield(object);
			shieldList.add(new OffSensitiveObjectContext(object, handler, restoreValue));

			recurRef.add(System.identityHashCode(object));

			List<Object> nestShildList = handler.getNShieldFields(object);
			if (nestShildList != null) {
				for (Object nestObject : nestShildList) {
					if (nestObject != null) {
						shieldObject(nestObject, shieldList, recurRef);
					}
				}
			}
		}
	}
}
