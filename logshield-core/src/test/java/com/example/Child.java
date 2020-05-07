package com.example;

import tech.theaha.log.shield.OffSensitive;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@OffSensitive
@Getter
@Setter
@ToString(callSuper=true)
public class Child extends Human {
	private Pet pet;
}
