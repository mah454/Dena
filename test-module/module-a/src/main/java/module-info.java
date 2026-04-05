module module.a {
    requires dena.api;
    requires moke.utils;

    // Add these lines:
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.datatype.jsr310;  // Needed for Java 8 date/time support

    requires java.sql;
    requires org.slf4j;  // Often needed by Jackson for date types

    exports ir.moke.module.a;
    provides ir.moke.dena.api.IModule with ir.moke.module.a.ModuleRunner;
}