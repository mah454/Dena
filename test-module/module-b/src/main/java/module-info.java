import ir.moke.dena.api.IModule;
import ir.moke.module.b.ModuleRunner;

module ir.sample {
    requires dena.api;
    requires module.a;
    requires org.slf4j;
    opens ir.moke.module.b;
    provides IModule with ModuleRunner;
}