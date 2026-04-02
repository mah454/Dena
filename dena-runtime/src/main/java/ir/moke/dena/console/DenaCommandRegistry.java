package ir.moke.dena.console;

import org.jline.console.CmdDesc;
import org.jline.console.CommandInput;
import org.jline.console.CommandMethods;
import org.jline.console.CommandRegistry;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.reader.impl.completer.SystemCompleter;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DenaCommandRegistry implements CommandRegistry {
    private LineReader reader;
    private final Map<String, CommandMethods> commandExecute = new HashMap<>();
    private final Map<String, List<String>> commandInfo = new HashMap<>();
    private final Map<String, String> aliasCommand = new HashMap<>();
    private Exception exception;

    public DenaCommandRegistry() {
        commandExecute.put("list", new CommandMethods(ModuleCommand::moduleList, this::defaultCompleter));
        commandExecute.put("load", new CommandMethods(ModuleCommand::moduleLoad, this::defaultCompleter));
        commandExecute.put("stop", new CommandMethods(ModuleCommand::moduleStop, this::defaultCompleter));
        commandExecute.put("start", new CommandMethods(ModuleCommand::moduleStart, this::defaultCompleter));

        // Command Description
        commandInfo.put("list", List.of("List all available modules"));
        commandInfo.put("load", List.of("Load a module from directory", "Usage: load <index>"));
        commandInfo.put("stop", List.of("Stop a running module", "Usage: stop <index>"));
        commandInfo.put("start", List.of("Start a loaded module", "Usage: start <index>"));
    }

    @Override
    public String name() {
        return "Dena JPMS Available Commands";
    }

    public void setLineReader(LineReader reader) {
        this.reader = reader;
    }

    private Terminal terminal() {
        return reader.getTerminal();
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

    public Object invoke(CommandSession session, String command, Object... args) throws Exception {
        exception = null;
        Object out = commandExecute.get(command(command)).execute().apply(new CommandInput(command, args, session));
        if (exception != null) {
            throw exception;
        }
        return out;
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
        List<AttributedString> mainDesc =
                info.stream().map(AttributedString::new).toList();

        // Return only description; NO generic arg spec
        return new CmdDesc(mainDesc, List.of(), Map.of());
    }

    private List<Completer> defaultCompleter(String command) {
        return List.of(NullCompleter.INSTANCE);
    }
}