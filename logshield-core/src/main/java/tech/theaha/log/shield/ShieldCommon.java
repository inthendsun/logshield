package tech.theaha.log.shield;

public class ShieldCommon {

	public static String name(String name) {
		return ShieldUtil.shield(name, 1);
	}

	public static String idNo(String idNo) {
		if(idNo == null) {
			return null;
		}
		return ShieldUtil.shield(idNo, 4,idNo.length() - 8);
	}

	public static String drivingLicenseNo(String drivingNo) {
		if(drivingNo == null) {
			return null;
		}
		return ShieldUtil.shield(drivingNo, 4,drivingNo.length() - 8);
	}

	public static String otherIdNo(String otherIdNo) {
		return ShieldUtil.shield(otherIdNo, -4);
	}

	public static String address(String address) {
		if(address == null) {
			return null;
		}
		return ShieldUtil.shield(address, address.indexOf("市")+1);
	}

	public static String mobile(String mobileNo) {
		if(mobileNo == null) {
			return null;
		}
		return ShieldUtil.shield(mobileNo, 3,mobileNo.length() - 7);
	}

	public static String telephone(String telephone) {
		if(telephone == null) {
			return null;
		}
		return ShieldUtil.shield(telephone, telephone.length() - 4 );
	}

	public static String bankCardNo(String bankCardNo) {
		if(bankCardNo == null) {
			return null;
		}
		return ShieldUtil.shield(bankCardNo, 4,bankCardNo.length() - 8);
	}

	public static String email(String email) {
		if(email == null) {
			return null;
		}
		return ShieldUtil.shield(email, 0,email.indexOf("@"));
	}

	public static String socialAccout(String socialAccount) {
		if(socialAccount == null) {
			return null;
		}
		return ShieldUtil.shield(socialAccount, -4);
	}

	public static String carNo(String carNo) {
		if(carNo == null) {
			return null;
		}
		return ShieldUtil.shield(carNo, carNo.length()-4,carNo.length());
	}

	public static String vin(String vin) {
		if(vin == null) {
			return null;
		}
		return ShieldUtil.shield(vin, 4,vin.length() - 8);
	}
	
	public static String engineNo(String engineNo) {
		if(engineNo == null) {
			return null;
		}
		int length = engineNo.length();
		int middle = (int) Math.ceil(length/2d);
		return ShieldUtil.shield(engineNo, Math.max(0,middle-2),4);
	}

	public static String all(String vin) {
		return ShieldUtil.shield(vin, 0);
	}
	
	public static final String DEFAULT_PWD = "****";
	public static String pwd(String password) {
		return DEFAULT_PWD;
	}
	
	public static final String DEFAULT_HIDDEN = "";
	public static String hidden(String hidden) {
		return DEFAULT_HIDDEN;
	}
}
