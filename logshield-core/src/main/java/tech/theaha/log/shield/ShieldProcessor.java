package tech.theaha.log.shield;

/**
 * 脱敏注解实现类
 * @author inthendsun
 */
public interface ShieldProcessor {

    /**
     * 返回脱敏处理器的名字
     * @return
     */
    public String shieldName();

    /**
     *
     * @param shieldConfig 脱敏注解配置
     * @param value 需要被脱敏的值
     * @return
     */
    public String shield(String shieldConfig,String value);
}
