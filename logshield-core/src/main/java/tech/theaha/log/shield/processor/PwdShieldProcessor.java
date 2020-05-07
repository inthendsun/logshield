package tech.theaha.log.shield.processor;

import tech.theaha.log.shield.ShieldTypes;
import tech.theaha.log.shield.ShieldProcessor;

/**
 * @author inthendsun
 */
public class PwdShieldProcessor implements ShieldProcessor {

    public static final String DEFAULT_PWD = "****";

    /**
     * 返回脱敏处理器的名字
     *
     * @return
     */
    @Override
    public String shieldName() {
        return ShieldTypes.PWD;
    }

    /**
     * @param shieldConfig 脱敏注解配置
     * @param pwd      需要被脱敏的值
     * @return
     */
    @Override
    public String shield(String shieldConfig, String pwd) {
        return DEFAULT_PWD;
    }

}
