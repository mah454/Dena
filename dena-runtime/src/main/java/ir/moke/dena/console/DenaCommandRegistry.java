package ir.moke.dena.console;

import ir.moke.dena.console.command.DenaCommand;
import ir.moke.dena.console.command.SystemCommand;
import org.jline.console.CmdDesc;
import org.jline.console.CommandInput;
import org.jline.console.CommandMethods;
import org.jline.console.CommandRegistry;
import org.jline.reader.Completer;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.reader.impl.completer.SystemCompleter;
import org.jline.utils.AttributedString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DenaCommandRegistry implements CommandRegistry {
    private final Map<String, CommandMethods> commandExecute = new HashMap<>();
    private final Map<String, List<String>> commandInfo = new HashMap<>();
    private final Map<String, String> aliasCommand = new HashMap<>();

    public DenaCommandRegistry() {
        // Module Commands
        commandExecute.put("list", new CommandMethods(DenaCommand::moduleList, this::defaultCompleter));
        commandExecute.put("load", new CommandMethods(DenaCommand::moduleLoad, this::defaultCompleter));
        commandExecute.put("unload", new CommandMethods(DenaCommand::moduleUnload, this::defaultCompleter));
        commandExecute.put("stop", new CommandMethods(DenaCommand::moduleStop, this::defaultCompleter));
        commandExecute.put("start", new CommandMethods(DenaCommand::moduleStart, this::defaultCompleter));

        // System Commands
        commandExecute.put("shutdown", new CommandMethods(SystemCommand::exit, this::defaultCompleter));
        commandExecute.put("gc", new CommandMethods(SystemCommand::gc, this::defaultCompleter));
        commandExecute.put("pid", new CommandMethods(SystemCommand::pid, this::defaultCompleter));

        // Command Description
        commandInfo.put("list", List.of("List all available modules"));
        commandInfo.put("load", List.of("Load module", "Usage: load <index>"));
        commandInfo.put("unload", List.of("Unload module", "Usage: unload <index>"));
        commandInfo.put("stop", List.of("Stop a running module", "Usage: stop <index>"));
        commandInfo.put("start", List.of("Start a loaded module", "Usage: start <index>"));
        commandInfo.put("shutdown", List.of("Shutdown system"));
        commandInfo.put("gc", List.of("Call jvm gc"));
        commandInfo.put("pid", List.of("Platform process id"));
    }

    @Override
    public String name() {
        return "Dena JPMS Available Commands";
    }

    public Set<String> commandNames() {
        return commandExecute.keySet();
    }

    public Map<String, String> commandAliases() {
        return aliasCommand;
    }

    public List<String> commandInfo(String command) {
        return commandInfo.get(command(command));
    }

    public boolean hasCommand(String command) {
        return commandExecute.containsKey(command) || aliasCommand.containsKey(command);
    }

    private String command(String name) {
        if (commandExecute.containsKey(name)) {
            return name;
        }
        return aliasCommand.get(name);
    }

    public SystemCompleter compileCompleters() {
        SystemCompleter out = new SystemCompleter();
        for (String c : commandExecute.keySet()) {
            out.add(c, commandExecute.get(c).compileCompleter().apply(c));
        }
        out.addAliases(aliasCommand);
        return out;
    }

    public Object invoke(CommandSession session, String command, Object... args) {
        return commandExecute.get(command(command)).execute().apply(new CommandInput(command, args, session));
    }

    @Override
    public CmdDesc commandDescription(List<String> args) {
        // No command → no completions → no builtins in help
        if (args == null || args.size() != 1) {
            return new CmdDesc(false);
        }

        String cmd = command(args.getFirst());
        if (cmd == null) {
            return new CmdDesc(false);
        }

        List<String> info = commandInfo(cmd);
        if (info == null) {
            return new CmdDesc(false);
        }

        // Convert description strings to AttributedString
        List<AttributedString> mainDesc = info.stream()
                .map(AttributedString::new)
                .toList();

        // Return only description; NO generic arg spec
        return new CmdDesc(mainDesc, List.of(), Map.of());
    }

    private List<Completer> defaultCompleter(String command) {
        return List.of(NullCompleter.INSTANCE);
    }
}