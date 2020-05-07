package tech.theaha.log.shield.bc;

import tech.theaha.log.shield.NShield;
import tech.theaha.log.shield.OffSensitive;
import tech.theaha.log.shield.Shield;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对象掩码Meta抽取
 * @author inthendsun
 */
public class ObjectShieldMetaExtractor {

    private static Logger log = LoggerFactory.getLogger(ObjectShieldMetaExtractor.class);

    static Map<Class<?>, ObjectShieldMeta> cache = new ConcurrentHashMap<Class<?>, ObjectShieldMeta>();

    public static ObjectShieldMeta extract(Class<?> clazz) {

        ObjectShieldMeta meta = cache.get(clazz);

        if (!clazz.isAnnotationPresent(OffSensitive.class)) {
            return meta;
        }
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(clazz);
        } catch (IntrospectionException e) {
            log.error("IntrospectionException", e);
            return meta;
        }

        List<StringShieldField> stringShieldList = null;
        List<NShieldField> nestedShieldList = null;

        PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor pd : pds) {
            if (pd.getReadMethod() == null || pd.getWriteMethod() == null) {
                continue;
            }
            if(isStringShield(pd)) {
                Field field = FieldUtils.getField(clazz, pd.getName(), true);
                if(field == null) {
                    field = FieldUtils.getField(clazz, StringUtils.capitalize(pd.getName()), true);
                }
                if(field == null) {
                    continue;
                }
                Shield shield = field.getAnnotation(Shield.class);
                if (shield == null) {
                    continue;
                }
                if(meta == null) {
                    meta = new ObjectShieldMeta();
                    cache.put(clazz, meta);
                }
                if(stringShieldList == null) {
                    stringShieldList = new ArrayList<StringShieldField>();
                }
                meta.setStringShieldFields(stringShieldList);
                StringShieldField gsetter = new StringShieldField();
                gsetter.setGetMethodName(pd.getReadMethod().getName());
                gsetter.setSetMethodName(pd.getWriteMethod().getName());
                gsetter.setShield(shield);
                stringShieldList.add(gsetter);

            } else if(isNShield(pd,clazz)) {
                if(meta == null) {
                    meta = new ObjectShieldMeta();
                    cache.put(clazz, meta);
                }
                if(nestedShieldList == null) {
                    nestedShieldList = new ArrayList<NShieldField>();
                }
                meta.setNestedShieldFields(nestedShieldList);
                NShieldField nestShield = new NShieldField();
                nestShield.setGetMethodName(pd.getReadMethod().getName());
                nestShield.setReturnType(pd.getReadMethod().getReturnType());
                nestShield.setSetMethodName(pd.getWriteMethod().getName());
                nestedShieldList.add(nestShield);
            }
        }
        return meta;
    }


    private static boolean isNShield(PropertyDescriptor pd,Class<?> clazz) {
        Field field = FieldUtils.getField(clazz, pd.getName(), true);
        return field.getAnnotation(NShield.class) != null;
    }

    private static boolean isStringShield(PropertyDescriptor pd) {
        return (pd.getReadMethod().getReturnType() == String.class &&
                pd.getReadMethod().getParameterTypes().length == 0 &&
                pd.getWriteMethod().getReturnType() == void.class &&
                pd.getWriteMethod().getParameterTypes().length == 1 &&
                pd.getWriteMethod().getParameterTypes()[0] == String.class);
    }
}
