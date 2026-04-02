import ir.moke.dena.api.IModule;
import ir.moke.dena.api.ModuleMetadata;
import ir.moke.module.b.ModuleRunner;

@ModuleMetadata(maintainer = "Mahdi Sheikh Hosseini", description = "Sample Module", url = "google.com")
module ir.sample {
    requires dena.api;
    requires module.a;
    opens ir.moke.module.b;
    provides IModule with ModuleRunner;
}