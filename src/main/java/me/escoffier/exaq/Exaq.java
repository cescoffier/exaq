package me.escoffier.exaq;

import picocli.CommandLine;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Singleton;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@CommandLine.Command(name = "exaq", description = "exaq  is a modern replacement for ls build with Quarkus. "
        + "It uses colours for information by default, helping you distinguish between many types of files, such as whether"
        + " you are the owner, or in the owning group.")
@Singleton // Must use singleton to avoid proxies
public class Exaq implements Runnable {

    @CommandLine.Parameters(paramLabel = "files", arity = "0..*") List<String> directories;

    @CommandLine.Option(names = { "-1", "--oneline"}, description = "Display one entry per line. Cannot we used with `--long`.") boolean oneLine;

    @CommandLine.Option(names = { "-F", "--classify"}, description = "Display file kind indicators next to file names.") boolean classify;

    @CommandLine.Option(names = { "-l", "--long"}, description = "Display extended file metadata as a table. Cannot be used with `--oneline`") boolean extended;

    @CommandLine.Option(names = { "-a", "--all"}, description = "Show hidden and \"dot\" files.  Use this twice to also show the `.' and `..'  directories.") boolean all;

    @CommandLine.Option(names = { "-D", "--only-dirs"}, description = "List only directories, not files.") boolean directoryOnly;

    @CommandLine.Option(names = { "-r", "--reverse"}, description = "Reverse the sort order.") boolean reverse;

    @CommandLine.Option(names = { "-d", "--list-dirs"}, description = "List directories as regular files, rather than recursing and listing their contents.") boolean dirInsteadOfFile;

    @CommandLine.Option(names = { "-i", "--inode"}, description = "List each file's inode number") boolean inode;

    @CommandLine.Option(names = {"--color"}, description = "Whether to use terminal colours. By default, colors are enabled", defaultValue = "true") boolean color;


    @Override
    public void run() {
        if (oneLine  && extended) {
            System.out.println("Cannot use --long and --one-line together.");
            return;
        }
        File cwd = new File(System.getProperty("user.dir"));
        List<File> directories = getDirectories(cwd);
        for (File directory : directories) {
            list(directory);
        }
    }

    public void list(File file) {
        if (file.isDirectory()  && ! dirInsteadOfFile) {
            String join = "\t";
            if (! oneLine) {
                join = "\n";
            }
            System.out.println(Arrays.stream(file.listFiles())
                    .filter(f -> {
                        if (all) {
                            return true;
                        }
                        return ! f.getName().startsWith(".");
                    })
                    .filter(f -> {
                        if (! directoryOnly) {
                            return true;
                        }
                        return f.isDirectory();
                    })
                    .sorted((f1, f2) -> {
                        if (reverse) {
                            return f2.getName().compareTo(f1.getName());
                        } else {
                            return f1.getName().compareTo(f2.getName());
                        }
                    })
                    .map(f -> Colors.file(f, classify, extended, inode, color)).collect(Collectors.joining(join)));
        } else {
            System.out.println(Colors.file(file, classify, extended, inode, color));
        }
    }

    public List<File> getDirectories(File cwd) {
        if (directories == null || directories.isEmpty()) {
            return List.of(cwd);
        } else {
            return directories.stream().map(File::new).map(f -> {
                if (f.isAbsolute()) {
                    return f;
                } else {
                    return new File(cwd, f.getPath());
                }
            }).collect(Collectors.toList());
        }
    }


}
