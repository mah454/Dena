package ir.moke.dena.console.command;

import picocli.CommandLine;

@CommandLine.Command(
        name = "root",
        mixinStandardHelpOptions = true
)
public class RootCommand implements Runnable {
    @Override
    public void run() {

    }
}
