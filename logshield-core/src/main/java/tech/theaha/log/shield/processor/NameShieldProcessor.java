package tech.theaha.log.shield.processor;

import tech.theaha.log.shield.ShieldTypes;
import tech.theaha.log.shield.ShieldProcessor;
import tech.theaha.log.shield.ShieldUtil;

/**
 * @author inthendsun
 */
public class NameShieldProcessor implements ShieldProcessor {
    /**
     * 返回脱敏处理器的名字
     *
     * @return
     */
    @Override
    public String shieldName() {
        return ShieldTypes.NAME;
    }

    /**
     * @param shieldConfig 脱敏注解配置
     * @param name      需要被脱敏的值
     * @return
     */
    @Override
    public String shield(String shieldConfig, String name) {
        return ShieldUtil.shield(name, 1);
    }
}
