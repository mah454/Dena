import ir.moke.dena.api.IModule;
import ir.moke.module.a.ModuleRunner;

module module.a {
    requires dena.api;
    requires moke.utils;
    exports ir.moke.module.a;
    provides IModule with ModuleRunner;
}