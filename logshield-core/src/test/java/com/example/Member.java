package com.example;
import tech.theaha.log.shield.OffSensitive;
import tech.theaha.log.shield.Shield;
import tech.theaha.log.shield.ShieldTypes;

import lombok.Data;
@OffSensitive
@Data
public class Member{
	@Shield(type= ShieldTypes.NAME)
	private String name;
	@Shield(type= ShieldTypes.PWD)
	private String password;
	@Shield(type= ShieldTypes.ID_NO)
	private String idNo;
	@Shield(type= ShieldTypes.MOBILE)
	private String mobile;
	@Shield(type= ShieldTypes.TELEPHONE)
	private String telephone;
	@Shield(type= ShieldTypes.OTHER_ID_NO)
	private String otherIdNo;
	@Shield(type= ShieldTypes.DRIVING_LICENSE_NO)
	private String drivingLicenseNo;
	@Shield(type= ShieldTypes.ADDRESS)
	private String address;
	@Shield(type= ShieldTypes.BANK_CARD_NO)
	private String bankCardNo;
	@Shield(type= ShieldTypes.EMAIL)
	private String email;
	@Shield(type= ShieldTypes.SOCIAL_ACCT)
	private String socialAccount;
	@Shield(type= ShieldTypes.CAR_NO)
	private String carNo;
	@Shield(type= ShieldTypes.ENGINE_NO)
	private String engineNo;
	@Shield(type= ShieldTypes.VIN)
	private String vin;
	@Shield(type= ShieldTypes.ALL)
	private String all;
	@Shield(type= ShieldTypes.HIDDEN)
	private String hidden;
	@Shield(type= ShieldTypes.JSON_STR,typeVal="dianhua:MOBILE,bankNo:BANK_CARD_NO")
	private String jsonStr;

	@Shield(type= ShieldTypes.HIDDEN)
	private String CardName;

	@Shield(type= ShieldTypes.XML_STR,typeVal="mobile:MOBILE,phone:NAME")
	private String xmlStr;
}

