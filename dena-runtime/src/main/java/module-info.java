import ir.moke.dena.api.ModuleMetadata;

@ModuleMetadata(maintainer = "Mahdi Sheikh Hosseini", url = "moke.ir", description = "test module")
module dena.runtime {
    uses ir.moke.dena.api.IModule;
    requires dena.api;
    requires com.fasterxml.jackson.annotation;
    requires org.slf4j;
    requires moke.utils;
    requires org.jline.reader;
    requires org.jline.builtins;
    requires org.jline.terminal;
    requires org.jline.console;
    requires ch.qos.logback.core;
}