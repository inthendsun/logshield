package com.example;

import java.util.List;

import tech.theaha.log.shield.NShield;
import tech.theaha.log.shield.OffSensitive;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@OffSensitive
@Getter
@Setter
@ToString
public class Family {
	
	@NShield
	private Adult father;
	
	@NShield
	private Adult mother;
	
	@NShield
	private List<Child> childs;
	
}
