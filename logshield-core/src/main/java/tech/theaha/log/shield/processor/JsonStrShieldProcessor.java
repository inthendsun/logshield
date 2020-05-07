package tech.theaha.log.shield.processor;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import tech.theaha.log.shield.ShieldTypes;
import tech.theaha.log.shield.ShieldProcessor;
import tech.theaha.log.shield.ShieldProcessorLoader;
import lombok.Data;

/**
 * @author psyche.red
 */
public class JsonStrShieldProcessor implements ShieldProcessor {

    private static ConcurrentHashMap<String,CacheConfigData> cachedData = new ConcurrentHashMap<String, CacheConfigData>();

    public static final String QUOTE = "\"";
    /**
     * 返回脱敏处理器的名字
     *
     * @return
     */
    @Override
    public String shieldName() {
        return ShieldTypes.JSON_STR;
    }

    /**
     * @param shieldConfig 脱敏注解
     * @param jsonStr    需要被脱敏的值
     * @return
     */
    @Override
    public String shield(String shieldConfig, String jsonStr) {
        if(jsonStr == null) {
            return jsonStr;
        }
        String config = shieldConfig;
        CacheConfigData configData = genConfigData(config);
        Matcher m = configData.p.matcher(jsonStr);
        StringBuffer resultStr = new StringBuffer();
        while(m.find()) {
            int count = m.groupCount();
            for (int i=1;i<=count;i++) {
                if( i % 3 == 0 && m.group(i) != null) {
                    ShieldProcessor sp = ShieldProcessorLoader.get(configData.ksMap.get(m.group(i-1)).getShieldName());
                    /*
                     * 优化value和key一样的问题
                     */
                    m.appendReplacement(resultStr, QUOTE + m.group(i-1)+ "\":\"" + sp.shield(null,m.group(i)) + QUOTE);
                }
            }
        }
        m.appendTail(resultStr);
        return resultStr.toString();
    }

    @Data
    class KeywordShield {
        String key;
        String shieldName;
    }

    @Data
    class CacheConfigData {
        Map<String,KeywordShield> ksMap;
        Pattern p;
    }

    private CacheConfigData genConfigData(String config) {
        if(cachedData.containsKey(config)) {
            return cachedData.get(config);
        }
        Map<String,KeywordShield> ksMap = getKsMap(config);
        StringBuilder patternStr = new StringBuilder();
        Iterator<String> iter = ksMap.keySet().iterator();
        while(iter.hasNext()) {
            String name =  iter.next();
            //("(mobile)"\\s*:\\s*"(.*?)")|("password":"(.*?)")
            patternStr.append("(\"("+name+")\"\\s*:\\s*\"(.*?)\")");
            if(iter.hasNext()) {
                patternStr.append("|");
            }
        }
        Pattern p = Pattern.compile(patternStr.toString());

        CacheConfigData configData = new CacheConfigData();
        configData.ksMap = ksMap;
        configData.p = p;
        cachedData.putIfAbsent(config,configData);
        return configData;
    }

    private Map<String,KeywordShield> getKsMap(String config) {
        String[] items = config.split(",");
        Map<String,KeywordShield> map =  new TreeMap<String,KeywordShield>();
        for (String item : items) {
            String[] pair = item.split(":");
            KeywordShield ks =  new KeywordShield();
            ks.setKey(pair[0]);
            //pair[0]是指定的mobile:ALL中的mobile
            //pair[1]是指定的mobile:ALL中的ALL
            if(pair.length == 2) {
                if(pair[1].equals(ShieldTypes.JSON_STR)) {
                    ks.setShieldName(ShieldTypes.HIDDEN);
                } else {
                    ks.setShieldName(pair[1]);
                }
            } else {
                ks.setShieldName(ShieldTypes.HIDDEN);
            }
            map.put(pair[0],ks);
        }
        return map;
    }
}
