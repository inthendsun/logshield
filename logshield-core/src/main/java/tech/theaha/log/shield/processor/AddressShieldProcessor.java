package tech.theaha.log.shield.processor;

import tech.theaha.log.shield.ShieldTypes;
import tech.theaha.log.shield.ShieldProcessor;
import tech.theaha.log.shield.ShieldUtil;

/**
 * @author inthendsun
 */
public class AddressShieldProcessor implements ShieldProcessor {
    /**
     * 返回脱敏处理的名字
     *
     * @return
     */
    @Override
    public String shieldName() {
        return ShieldTypes.ADDRESS;
    }

    /**
     * @param shieldConfig 脱敏注解配置
     * @param address      需要被脱敏的值
     * @return
     */
    @Override
    public String shield(String shieldConfig, String address) {
        if(address == null) {
            return null;
        }
        return ShieldUtil.shield(address, address.indexOf("市")+1);
    }
}
