module EduCore {
    requires static lombok;
    requires jakarta.persistence;
    requires jakarta.validation;
    requires spring.boot;
    requires spring.context;
    requires spring.data.commons;
    requires spring.data.jpa;
    requires spring.web;
    requires org.hibernate.orm.core;
    requires io.swagger.v3.oas.annotations;
    requires spring.boot.autoconfigure;
    requires io.github.cdimascio.dotenv.java;
    requires spring.tx;
}