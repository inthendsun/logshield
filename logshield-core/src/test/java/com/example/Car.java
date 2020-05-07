package com.example;

import tech.theaha.log.shield.NShield;
import tech.theaha.log.shield.OffSensitive;
import tech.theaha.log.shield.Shield;
import tech.theaha.log.shield.ShieldTypes;

import lombok.Data;

@Data
@OffSensitive
public class Car {
	
	
	private String carName;
	
	@Shield(type= ShieldTypes.ENGINE_NO)
	private String engineNo;
	
	
	@NShield
	private Human owner;
}
