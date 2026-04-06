package ir.moke.dena;

import java.nio.file.Path;

public interface GlobalVariables {
    Path denaWorkingDirectory = Path.of(System.getProperty("dena.work-dir"));
    Path denaModulesDirectory = denaWorkingDirectory.resolve("lib/modules");
}
