package tech.theaha.log.shield.log4j2;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.impl.DefaultLogEventFactory;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ParameterizedMessage;

import tech.theaha.log.shield.util.OffSensitiveObjectContext;
import tech.theaha.log.shield.util.OffSensitiveUtil;

/**
 * log4j2  版本日志脱敏方法
 * 参考@see org.apache.logging.log4j.core.impl.DefaultLogEventFactory
 * @author inthendsun
 */
public class ShieldLogEventFactory extends DefaultLogEventFactory{
	
	private static Class<?> reuseableClazz = null;
	private static Method makeMessageImmutable = null;
	
	static {
		try {
			reuseableClazz = Class.forName("org.apache.logging.log4j.message.ReusableParameterizedMessage");
		} catch(Exception e) {
		}
		try {
			makeMessageImmutable = Log4jLogEvent.class.getMethod("makeMessageImmutable");
		} catch(Exception e) {
		}
	}

	@Override
	public LogEvent createEvent(String loggerName, Marker marker, String fqcn, Level level, Message data,
			List<Property> properties, Throwable t) {
		List<OffSensitiveObjectContext> list = null;
		LogEvent event = null;
		try {
			//处理log4j2 2.3 start 或者是2.7版本的非web模式
			ParameterizedMessage newPmsg = null;
			if (data instanceof ParameterizedMessage) {
				Object[] oArr = data.getParameters();
				try {
					list = OffSensitiveUtil.off(oArr);
					if (!list.isEmpty()) {// 重新格式化
						newPmsg = new ParameterizedMessage(data.getFormat(), oArr);
					}
				} catch (Exception e) {
				} 
			} 
			//处理2.7版本
			else if (isReusable(data)) {
				Object[] oArr = data.getParameters();
				try {
					list = OffSensitiveUtil.off(oArr);
				} catch (Exception e) {
				} 
			}
			// end
			
			event =  super.createEvent(loggerName, marker, fqcn, level, newPmsg==null?data:newPmsg, properties, t);
			
			//处理2.7版本 使得LogEvent不可变
			if(event instanceof Log4jLogEvent && makeMessageImmutable != null) {
				Log4jLogEvent logEvent = (Log4jLogEvent)event; 
				logEvent.makeMessageImmutable();
			}
			
		} finally {
			if (list != null) {
				OffSensitiveUtil.restore(list);
			}
		}
		return event;
	}

	private boolean isReusable(Message data) {
		return reuseableClazz != null && reuseableClazz.isAssignableFrom(data.getClass());
	}
	
	
	

}
