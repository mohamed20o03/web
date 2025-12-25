package com.abdelwahab.CampusCard;

import org.springframework.boot.SpringApplication;

public class TestCampusCardApplication {

	public static void main(String[] args) {
		SpringApplication.from(CampusCardApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
