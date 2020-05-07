package tech.theaha.log.shield.bc;

import tech.theaha.log.shield.Shield;

/**
 * @author inthendsun
 */
public class StringShieldField {
	private Shield shield;
	private String getMethodName;
	private String setMethodName;
	public String getGetMethodName() {
		return getMethodName;
	}
	public void setGetMethodName(String getMethodName) {
		this.getMethodName = getMethodName;
	}
	public String getSetMethodName() {
		return setMethodName;
	}
	public void setSetMethodName(String setMethodName) {
		this.setMethodName = setMethodName;
	}
	public Shield getShield() {
		return shield;
	}
	public void setShield(Shield shield) {
		this.shield = shield;
	}
}
