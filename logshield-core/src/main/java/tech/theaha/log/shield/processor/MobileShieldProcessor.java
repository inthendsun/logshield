package tech.theaha.log.shield.processor;

import tech.theaha.log.shield.ShieldTypes;
import tech.theaha.log.shield.ShieldProcessor;
import tech.theaha.log.shield.ShieldUtil;

/**
 * @author inthendsun
 */
public class MobileShieldProcessor implements ShieldProcessor {
    /**
     * 返回脱敏处理器的名字
     *
     * @return
     */
    @Override
    public String shieldName() {
        return ShieldTypes.MOBILE;
    }

    /**
     * @param config 脱敏注解配置
     * @param mobileNo      需要被脱敏的值
     * @return
     */
    @Override
    public String shield(String config, String mobileNo) {
        if(mobileNo == null) {
            return null;
        }
        return ShieldUtil.shield(mobileNo, 3,mobileNo.length() - 7);
    }
}
