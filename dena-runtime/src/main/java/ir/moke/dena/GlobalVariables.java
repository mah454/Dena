package ir.moke.dena;

import java.nio.file.Path;

public interface GlobalVariables {
    Path denaWorkingDirectory = Path.of(System.getProperty("dena.work-dir"));
    Path denaSharedDirectory = denaWorkingDirectory.resolve("shared");
    Path denaModulesDirectory = denaWorkingDirectory.resolve("modules");
}
