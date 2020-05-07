package tech.theaha.log.shield.processor;

import tech.theaha.log.shield.ShieldTypes;
import tech.theaha.log.shield.ShieldProcessor;
import tech.theaha.log.shield.ShieldUtil;

/**
 * @author inthendsun
 */
public class VinShieldProcessor implements ShieldProcessor {
    /**
     * 返回脱敏处理器的名字
     *
     * @return
     */
    @Override
    public String shieldName() {
        return ShieldTypes.VIN;
    }

    /**
     * @param shieldConfig 脱敏注解配置
     * @param vin      需要被脱敏的值
     * @return
     */
    @Override
    public String shield(String shieldConfig, String vin) {
        if(vin == null) {
            return null;
        }
        return ShieldUtil.shield(vin, 4,vin.length() - 8);
    }
}
