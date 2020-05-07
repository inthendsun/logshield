package tech.theaha.log.shield.processor;

import tech.theaha.log.shield.ShieldTypes;
import tech.theaha.log.shield.ShieldProcessor;
import tech.theaha.log.shield.ShieldUtil;

/**
 * @author inthendsun
 */
public class DrivingLicenseNoShieldProcessor implements ShieldProcessor {
    /**
     * 返回脱敏处理器的名字
     *
     * @return
     */
    @Override
    public String shieldName() {
        return ShieldTypes.DRIVING_LICENSE_NO;
    }

    /**
     * @param shieldConfig 脱敏注解配置
     * @param drivingNo      需要被脱敏的值
     * @return
     */
    @Override
    public String shield(String shieldConfig, String drivingNo) {
        if(drivingNo == null) {
            return null;
        }
        return ShieldUtil.shield(drivingNo, 4,drivingNo.length() - 8);
    }
}
