package tech.theaha.log.shield.bc;

import java.util.List;

public class ObjectShieldMeta {
	
	private List<StringShieldField> stringShieldFields;
	private List<NShieldField> nestedShieldFields;
	
	public List<StringShieldField> getStringShieldFields() {
		return stringShieldFields;
	}
	public List<NShieldField> getNestedShieldFields() {
		return nestedShieldFields;
	}
	public void setStringShieldFields(List<StringShieldField> stringShieldFields) {
		this.stringShieldFields = stringShieldFields;
	}
	public void setNestedShieldFields(List<NShieldField> nestedShieldFields) {
		this.nestedShieldFields = nestedShieldFields;
	}
	
}