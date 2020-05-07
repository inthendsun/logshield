package com.example;

import tech.theaha.log.shield.OffSensitive;
import tech.theaha.log.shield.Shield;
import tech.theaha.log.shield.ShieldTypes;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@OffSensitive
@Getter
@Setter
@ToString
public class Human {
	
	private boolean man;
	
	@Shield(type= ShieldTypes.NAME)
	private String name;
	
	private int age;
}
