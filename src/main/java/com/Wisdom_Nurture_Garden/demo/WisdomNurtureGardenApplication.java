package com.Wisdom_Nurture_Garden.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.Wisdom_Nurture_Garden.demo.dao")
@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class})
public class WisdomNurtureGardenApplication {
	public static void main(String[] args) {
		SpringApplication.run(WisdomNurtureGardenApplication.class, args);
	}
}
