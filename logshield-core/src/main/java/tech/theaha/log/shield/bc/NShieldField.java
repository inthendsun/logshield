package tech.theaha.log.shield.bc;


public class NShieldField {
	private String getMethodName;
	private String setMethodName;
	private Class<?> returnType;
	public String getGetMethodName() {
		return getMethodName;
	}
	public String getSetMethodName() {
		return setMethodName;
	}
	public Class<?> getReturnType() {
		return returnType;
	}
	public void setGetMethodName(String getMethodName) {
		this.getMethodName = getMethodName;
	}
	public void setSetMethodName(String setMethodName) {
		this.setMethodName = setMethodName;
	}
	public void setReturnType(Class<?> returnType) {
		this.returnType = returnType;
	}
	
}
