package app;

import javafx.application.Application;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class Main {
    @Option(name = "-help", usage = "print help")
    private boolean printHelp = false;

    public void run(String[] args) throws Exception {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e);
            parser.printUsage(System.err);
            return;
        }

        if (printHelp) {
            parser.printUsage(System.err);
            return;
        }

        Application.launch(AppManager.class, args);
    }

    public static void main(String[] args) throws Exception {
        new Main().run(args);
    }
}
