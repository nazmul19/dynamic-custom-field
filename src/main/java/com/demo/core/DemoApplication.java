package com.demo.core;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.common.dynamicextensions.napi.FormDataManager;
import edu.common.dynamicextensions.napi.impl.FormDataManagerImpl;
import liquibase.integration.spring.SpringLiquibase;

@Configuration
@SpringBootApplication
@ComponentScan(basePackages = {"com.demo.core", "edu.wustl.dynamicextensions.formdesigner"})
public class DemoApplication {
	
	@Autowired
	private DataSource dataSource;
	
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	
//	@Bean
//	public SpringLiquibase liquibase() {
//	    SpringLiquibase liquibase = new SpringLiquibase();
//	    liquibase.setChangeLog("classpath:db/db.changelog-master.xml");
//	    liquibase.setDataSource(dataSource);
//	    return liquibase;
//	}
	
	@Bean
	public FormDataManager formDataManager() {
		return new FormDataManagerImpl(true);
	}
	
	@Bean
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
	    ObjectMapper mapper = new ObjectMapper();
	    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	    MappingJackson2HttpMessageConverter converter = 
	        new MappingJackson2HttpMessageConverter(mapper);
	    return converter;
	}
}
