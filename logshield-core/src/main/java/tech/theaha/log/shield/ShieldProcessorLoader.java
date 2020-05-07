package tech.theaha.log.shield;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

/**
 *
 * 参考：https://blog.csdn.net/top_code/article/details/51934459
 *
 * @author inthendsun
 */
public class ShieldProcessorLoader {

    static Map<String,ShieldProcessor> processorMap = new HashMap<String, ShieldProcessor>();

    static {
        ServiceLoader<ShieldProcessor> serviceLoader = ServiceLoader.load(ShieldProcessor.class);
        Iterator<ShieldProcessor> it = serviceLoader.iterator();
        while (it != null && it.hasNext()) {
            ShieldProcessor sp = it.next();
            processorMap.put(sp.shieldName(),sp);
        }
    }

    public static ShieldProcessor get(String shieldName){
        ShieldProcessor p = processorMap.get(shieldName);
        if(p == null) {
            p =  processorMap.get(ShieldTypes.PWD);
        }
        return p;
    }

}
