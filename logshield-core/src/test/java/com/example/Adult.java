package com.example;

import tech.theaha.log.shield.NShield;
import tech.theaha.log.shield.OffSensitive;
import tech.theaha.log.shield.Shield;
import tech.theaha.log.shield.ShieldTypes;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@OffSensitive
@Getter
@Setter
@ToString(callSuper=true)
public class Adult extends Human {
	@NShield
	private Car car;
	
	@Shield(type= ShieldTypes.ID_NO)
	private String idNo;
}
