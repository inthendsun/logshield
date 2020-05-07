package tech.theaha.log.shield.processor;

import tech.theaha.log.shield.ShieldTypes;
import tech.theaha.log.shield.ShieldProcessor;

/**
 * @author inthendsun
 */
public class HiddenShieldProcessor implements ShieldProcessor {

    public static final String DEFAULT_HIDDEN = "";

    /**
     * 返回脱敏处理器的名字
     *
     * @return
     */
    @Override
    public String shieldName() {
        return ShieldTypes.HIDDEN;
    }

    /**
     * @param shieldConfig 脱敏注解配置
     * @param value      需要被脱敏的值
     * @return
     */
    @Override
    public String shield(String shieldConfig, String value) {
        return DEFAULT_HIDDEN;
    }
}
