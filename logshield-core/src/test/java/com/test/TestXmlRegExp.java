package com.test;

import tech.theaha.log.shield.ShieldTypes;
import tech.theaha.log.shield.ShieldProcessor;
import tech.theaha.log.shield.ShieldProcessorLoader;
import lombok.Data;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author inthendsun
 */
public class TestXmlRegExp {

    @Data
    class KeywordShield {
        String key;
        String shieldName;
    }

    @Test
    public void testReplace() {
        String jsonStr= "\n" +
                "                        <phone xsi:type=\"xsd:string\">13817654684</phone>\n" +
                "<mobile xsi:type=\"xsd:string\">18610932044</mobile>\n" +
                "<package xsi:type=\"ns1:HRSHotelPackage\" xsi:nil=\"true\"/>\n";

        String config = "mobile:MOBILE,password:PWD,phone:ALL";
        Map<String,KeywordShield> ksMap = getKsMap(config);

        Pattern p = genPattern(ksMap);
        System.out.println(p);

        Matcher m = p.matcher(jsonStr);
        StringBuffer newRe = new StringBuffer();
        while(m.find()) {
            int count = m.groupCount();
            for (int i=1;i<=count;i++) {
                System.out.println(i+"=" + m.group(i));
                if( i % 3 == 0 && m.group(i) != null) {
                    System.out.println("------------" + m.group(i-1));
                    ShieldProcessor sp = ShieldProcessorLoader.get(ksMap.get(m.group(i-1)).getShieldName());
                    m.appendReplacement(newRe,m.group().replaceFirst(Pattern.quote(m.group(i)),sp.shield(null,m.group(i))));
                }

            }
            System.out.println("============");
         }
        m.appendTail(newRe);
        System.out.println(newRe);
    }

    private Pattern genPattern(Map<String,KeywordShield> ksMap) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> iter = ksMap.keySet().iterator();
        while(iter.hasNext()) {
           String name =  iter.next();
            //(<(phone)\\s?.*?>(.*?)</phone>)|(<(mobile)\\s?.*?>(.*?)</mobile>)
            sb.append("(<("+name+")\\s?.*?>(.*?)</"+name+">)");
            if(iter.hasNext()) {
                sb.append("|");
            }
        }
        Pattern p = Pattern.compile(sb.toString());
        return p;
    }

    private Map<String,KeywordShield> getKsMap(String config) {
        String[] items = config.split(",");
        Map<String,KeywordShield> map =  new TreeMap<String,KeywordShield>();
        for (String item : items) {
            String[] pair = item.split(":");
            System.out.println(Arrays.toString(pair));
            KeywordShield ks =  new KeywordShield();
            ks.setKey(pair[0]);
            if(pair.length == 2) {
                ks.setShieldName(pair[1]);
            } else {
                ks.setShieldName(ShieldTypes.HIDDEN);
            }
            map.put(pair[0],ks);
        }
        return map;
    }

}
