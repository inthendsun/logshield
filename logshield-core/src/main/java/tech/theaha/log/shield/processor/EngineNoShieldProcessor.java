package tech.theaha.log.shield.processor;

import tech.theaha.log.shield.ShieldTypes;
import tech.theaha.log.shield.ShieldProcessor;
import tech.theaha.log.shield.ShieldUtil;

/**
 * @author inthendsun
 */
public class EngineNoShieldProcessor implements ShieldProcessor {
    /**
     * 返回脱敏处理器的名字
     *
     * @return
     */
    @Override
    public String shieldName() {
        return ShieldTypes.ENGINE_NO;
    }

    /**
     * @param shieldConfig 脱敏注解配置
     * @param engineNo      需要被脱敏的值
     * @return
     */
    @Override
    public String shield(String shieldConfig, String engineNo) {
        if(engineNo == null) {
            return null;
        }
        int length = engineNo.length();
        int middle = (int) Math.ceil(length/2d);
        return ShieldUtil.shield(engineNo, Math.max(0,middle-2),4);
    }
}
