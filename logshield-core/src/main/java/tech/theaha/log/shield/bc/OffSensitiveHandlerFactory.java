package tech.theaha.log.shield.bc;

import tech.theaha.log.shield.OffSensitive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 获取脱敏handler的工厂
 * @author inthendsun
 */
public class OffSensitiveHandlerFactory {
	
	private static Logger log = LoggerFactory.getLogger(OffSensitiveHandlerFactory.class);


	
	@SuppressWarnings("rawtypes")
	static Map<Class<?>,OffSensitiveHandler> handlers = new ConcurrentHashMap<Class<?>, OffSensitiveHandler>();
	
	@SuppressWarnings("rawtypes")
	private static final OffSensitiveHandler NOP = new OffSensitiveHandler() {
		final Map empty = new HashMap<String,String>(0);
		@Override
		public Map shield(Object obj) {return empty;}
        @Override
		public void restore(Object obj, Map oriInfo) {return;}
        @Override
		public List getNShieldFields(Object obj) {return null;}
	};
	
	@SuppressWarnings({ "unchecked" })
	public static <T> OffSensitiveHandler<T> getHandler(Class<T> clazz) {
		if(!clazz.isAnnotationPresent(OffSensitive.class)) {
			return null;
		}
		OffSensitiveHandler<T> handler = handlers.get(clazz);
		if(handler == null) {
			synchronized (clazz) {
				handler = handlers.get(clazz);
				if(handler == null) {
					long start = System.currentTimeMillis();
					initHandler(clazz, "/tmp");
					handler = handlers.get(clazz);
					if(handler == null) {
						log.info(" create handler clazz={} error!,elapsed {} millis" , clazz.getSigners(), 
								System.currentTimeMillis() - start);
					} else {
						log.info(" create clazz={} elapsed {} millis" , handler.getClass().getSimpleName(), 
								System.currentTimeMillis() - start);
					}
				}
			}
		}
		if(handler == NOP) {
			return null;
		}
		return handler;
	}
	
	private static void initHandler(Class<?> clazz,String outFolder) {
        ObjectShieldMeta meta = null;
        try {
            meta = ObjectShieldMetaExtractor.extract(clazz);
		} catch(Exception e) {
            log.info("提取脱敏handler异常,{} ",e.getMessage());
        }
		if(meta == null) {
			handlers.put(clazz, NOP);
			return;
		}
		if(meta.getStringShieldFields() == null && meta.getNestedShieldFields() == null) {
            handlers.put(clazz, NOP);
            return;
        }
		try {
			OffSensitiveHandler<?> handler = OffSensitiveHandlerDump.get(clazz.getName()+"OffSensitive", clazz,meta,outFolder);
			handlers.put(clazz, handler);
		} catch (Exception e) {
			log.error("脱敏对象生成异常",e);
		}
	}
}
