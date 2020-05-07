package tech.theaha.log.shield.processor;

import tech.theaha.log.shield.ShieldTypes;
import tech.theaha.log.shield.ShieldProcessor;
import tech.theaha.log.shield.ShieldUtil;

/**
 * @author inthendsun
 */
public class BankCardNoShieldProcessor implements ShieldProcessor {
    /**
     * 返回脱敏处理器的名字
     *
     * @return
     */
    @Override
    public String shieldName() {
        return ShieldTypes.BANK_CARD_NO;
    }

    /**
     * @param shieldConfig 脱敏注解配置
     * @param bankCardNo      需要被脱敏的值
     * @return
     */
    @Override
    public String shield(String shieldConfig, String bankCardNo) {
        if(bankCardNo == null) {
            return null;
        }
        return ShieldUtil.shield(bankCardNo, 4,bankCardNo.length() - 8);
    }
}
