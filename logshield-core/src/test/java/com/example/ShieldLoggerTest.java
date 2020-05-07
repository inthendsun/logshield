package com.example;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShieldLoggerTest {
	
	private static Logger log = LoggerFactory.getLogger(ShieldLoggerTest.class);

    @Test
    public void testMemberXml() {
        Member u = buildMember("小王");
        String xmlStr= "\n" +
                "                        <phone xsi:type=\"xsd:string\">13817654684</phone>\n" +
                "<mobile xsi:type=\"xsd:string\">18610932044</mobile>\n" +
                "<package xsi:type=\"ns1:HRSHotelPackage\" xsi:nil=\"true\"/>\n";
        u.setXmlStr(xmlStr);

        for (int i = 0; i < 100; i++) {
            long start = System.currentTimeMillis();
            log.info("{}",u);
            log.info("times {}", System.currentTimeMillis()-start);
            Assert.assertEquals(xmlStr, u.getXmlStr());
        }
    }
	
	@Test
	public void testMember() {
		Member u = buildMember("小王");
		u.setCardName("ssss");
		String jsonStr = "{\"dianhua\":\"18610932046\",\"bankNo\" : \"6226020111102222\"}";
        String xmlStr= "\n" +
                "                        <phone xsi:type=\"xsd:string\">13817654684</phone>\n" +
                "<mobile xsi:type=\"xsd:string\">18610932044</mobile>\n" +
                "<package xsi:type=\"ns1:HRSHotelPackage\" xsi:nil=\"true\"/>\n";
		u.setJsonStr(jsonStr);
		u.setXmlStr(xmlStr);
		log.info("{}",u);
		Assert.assertEquals("小王", u.getName());
		Assert.assertEquals("weinxi_account", u.getSocialAccount());
		Assert.assertEquals(jsonStr, u.getJsonStr());
        Assert.assertEquals("ssss", u.getCardName());

        Assert.assertEquals(xmlStr, u.getXmlStr());
	}
	
	@Test
	public void testListMember() {
		List<Member> list = new ArrayList<Member>();
		for (int i = 0; i < 2; i++) {
			list.add(buildMember("张三"+i));
		}
		log.info("{}" ,list);
		for (int i = 0; i < list.size(); i++) {
			Assert.assertEquals("张三"+i, list.get(i).getName());
		}
	}
	
	@Test
	public void testArrayMember() {
		Object[] arr = new Member[2];
		arr[0] = buildMember("小心");
		arr[1] = buildMember("大胆");
		log.info("{}" ,arr);
		
		Assert.assertEquals("小心", ((Member)arr[0]).getName());
		Assert.assertEquals("大胆", ((Member)arr[1]).getName());
	}
	
	@Test
	public void testFamily() {
		Adult father = buildAdult("爸爸");
		Adult mother = buildAdult("妈妈");
		
		Family family = new Family();
		family.setFather(father);
		family.setMother(mother);
		
		Pet pet = new Pet();
		pet.setName("狗狗");
		
		Child son = buildChild("儿子",pet);
		Child dau = buildChild("女儿",pet);
		
		List<Child> childs = new ArrayList<Child>();
		childs.add(son);
		childs.add(dau);
		family.setChilds(childs);
		
		log.info("{}" ,family);
		Assert.assertEquals("狗狗", pet.getName());
		
		Assert.assertEquals("女儿", dau.getName());
		Assert.assertEquals("儿子", son.getName());
		Assert.assertEquals("爸爸", father.getName());
		Assert.assertEquals("妈妈", mother.getName());
	}
	
	
	@Test
	public void testAdultCar() {
		
		Adult adult = buildAdult("小王");
		
		Car car = new Car();
		car.setCarName("奔驰");
		car.setEngineNo("123456787909");
		
		adult.setCar(car);
		
		log.info("{}" ,adult);
	}
	
	@Test
	public void testRecRef() {
		
		Adult u = buildAdult("小王");
		
		Car car = new Car();
		car.setCarName("奔驰");
		car.setEngineNo("123456787909");
		car.setOwner(u);
		
		u.setCar(car);
		
		log.info("{}" ,u);
	}
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void testMap() {
		Member key = buildMember("王");
		Member value = buildMember("李");
		
		Member key2 = buildMember("张");
		Member value2 = buildMember("高");
		
		@SuppressWarnings("rawtypes")
		Map map = new HashMap();
		map.put(key, value);
		map.put(key2, value2);
		log.info("{}" ,map);
	}
	
	private static Member buildMember(String i) {
		Member u = new Member();
		u.setAll("xxxxxxxxxxxxx");
		u.setAddress("广州省深圳市福田区益田路");
		u.setBankCardNo("6225123456780000");
		u.setCarNo("粤B12345");
		u.setDrivingLicenseNo("123456789012345678");
		u.setEmail("test@163.com");
		u.setEngineNo("12345678901234567");
		u.setIdNo("210881198810281968");
		u.setMobile("18610932044");
		u.setName(i);
		u.setOtherIdNo("05033788");
		u.setPassword("mypassword");
		u.setSocialAccount("weinxi_account");
		u.setTelephone("04177118767");
		u.setVin("12345678901234567");
		u.setHidden("隐藏内容");
		return u;
	}
	
	
	private static Human buildHuman(String name) {
		Human u = new Human();
		u.setName(name);
		return u;
	}
	
	private static Adult buildAdult(String name) {
		Adult u = new Adult();
		u.setIdNo("210881198810281968");
		u.setName(name);
		return u;
	}
	
	private static Child buildChild(String name,Pet pet) {
		Child u = new Child();
		u.setName(name);
		u.setPet(pet);
		return u;
	}
}

