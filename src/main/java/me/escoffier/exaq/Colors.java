package me.escoffier.exaq;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.PosixFilePermission;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.OptionalLong;
import java.util.Set;
import java.util.function.Supplier;

public class Colors {

    public static final String RESET = "\u001B[0m";

    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    public static final String BLACK_BACKGROUND = "\u001B[40m";
    public static final String RED_BACKGROUND = "\u001B[41m";
    public static final String GREEN_BACKGROUND = "\u001B[42m";
    public static final String YELLOW_BACKGROUND = "\u001B[43m";
    public static final String BLUE_BACKGROUND = "\u001B[44m";
    public static final String PURPLE_BACKGROUND = "\u001B[45m";
    public static final String CYAN_BACKGROUND = "\u001B[46m";
    public static final String WHITE_BACKGROUND = "\u001B[47m";

    public static final String COL_SIZE = "\033[12C";
    public static final String COL_ONE = "\033[2C";

    public static final String NEXT = "\033[4C";
    public static final String NEXT_MINUS_1 = "\033[3C";

    public static final String ITALIC = "\u001B[3m";
    public static final String UNDERLINE = "\u001B[4m";

    public static String doc(String name) {
        return UNDERLINE + YELLOW + name + RESET;
    }

    public static String exec(String name, boolean classify) {
        return GREEN + name + RESET + (classify ? "*" : "");
    }

    public static String dir(String dir, boolean classify) {
        return BLUE + dir + RESET + (classify ? "/" : "");
    }

    public static String file(File file, boolean classify, boolean extended, boolean inode, boolean color) {
        return name(file, extended, classify, inode, color);
    }

    private static String name(File file, boolean extended, boolean classify, boolean inode, boolean color) {
        if (!extended) {
            if (file.isDirectory()) {
                return dir(file.getName(), classify);
            }

            if (file.canExecute()) {
                return exec(file.getName(), classify);
            }

            if (isDocument(file)) {
                return doc(file.getName());
            }

            // Plain boring
            return file.getName();
        }
        // extended
        StringBuffer buffer = new StringBuffer();

        if (inode) {
            findInode(file)
                    .ifPresent(l -> buffer.append(RED).append(l).append(RESET));
            buffer.append(" ");
        }

        if (file.isDirectory()) {
            buffer.append(BLUE + "d" + RESET);
        } else {
            buffer.append(".");
        }

        try {
            Set<PosixFilePermission> permissions = Files
                    .getPosixFilePermissions(file.toPath(), LinkOption.NOFOLLOW_LINKS);

            ifReadable(() -> permissions.contains(PosixFilePermission.OWNER_READ), buffer);
            ifWritable(() -> permissions.contains(PosixFilePermission.OWNER_WRITE), buffer);
            ifExecutable(() -> permissions.contains(PosixFilePermission.OWNER_EXECUTE), buffer);

            ifReadable(() -> permissions.contains(PosixFilePermission.GROUP_READ), buffer);
            ifWritable(() -> permissions.contains(PosixFilePermission.GROUP_WRITE), buffer);
            ifExecutable(() -> permissions.contains(PosixFilePermission.GROUP_WRITE), buffer);

            ifReadable(() -> permissions.contains(PosixFilePermission.OTHERS_READ), buffer);
            ifWritable(() -> permissions.contains(PosixFilePermission.OTHERS_WRITE), buffer);
            ifExecutable(() -> permissions.contains(PosixFilePermission.OTHERS_EXECUTE), buffer);

            //            BasicFileAttributeView view = Files
            //                    .getFileAttributeView(file.toPath(), BasicFileAttributeView.class);
            //            if (view != null) {
            //                buffer.append("@");
            //            } else {
            //                buffer.append(" ");
            //            }
        } catch (Exception e) {
            // Windows or failed
            ifReadable(file::canRead, buffer);
            ifWritable(file::canWrite, buffer);
            ifExecutable(file::canExecute, buffer);
        }

        buffer.append(COL_ONE);

        if (file.isDirectory()) {
            buffer.append(padLeft("-", 5));
            buffer.append(NEXT_MINUS_1);
        } else {
            //            double size = file.length() / 1024.0;
            //            if (size < 1024) {
            //                buffer.append(GREEN).append(new DecimalFormat("##.##").format(size)).append("k" + RESET);
            //            } else {
            //                size = size / 1024.0;
            //                buffer.append(GREEN).append(new DecimalFormat("##.##").format(size)).append("m" + RESET);
            //            }
            buffer.append(GREEN).append(padLeft(getSize(file), 5)).append(RESET);
            buffer.append(NEXT_MINUS_1);
        }

        try {
            String owner = Files.getOwner(file.toPath(), LinkOption.NOFOLLOW_LINKS).getName();
            buffer.append(YELLOW).append(padLeft(owner, 8)).append(RESET);
        } catch (Exception e) {
            // ignore
        }

        Date lm = new Date(file.lastModified());
        String d1 = new SimpleDateFormat("dd LLL yyyy").format(lm);
        String d2 = new SimpleDateFormat("hh:mm").format(lm);

        buffer.append(BLUE).append(" ").append(d1).append(" ").append(d2).append(RESET);
        buffer.append(" ");

        if (file.isDirectory()) {
            buffer.append(dir(file.getName(), classify));
        } else if (file.canExecute()) {
            buffer.append(exec(file.getName(), classify));
        } else if (isDocument(file)) {
            buffer.append(doc(file.getName()));
        } else {
            buffer.append(file.getName());
        }

        return buffer.toString();
    }

    public static boolean isDocument(File file) {
        String name = file.getName();
        return name.endsWith(".xml")
                || name.endsWith(".md")
                || name.endsWith(".adoc");
    }

    private static void ifReadable(Supplier<Boolean> predicate, StringBuffer buffer) {
        if (predicate.get()) {
            buffer.append(YELLOW + "r" + RESET);
        } else {
            buffer.append("-");
        }
    }

    private static void ifWritable(Supplier<Boolean> predicate, StringBuffer buffer) {
        if (predicate.get()) {
            buffer.append(RED + "w" + RESET);
        } else {
            buffer.append("-");
        }
    }

    private static void ifExecutable(Supplier<Boolean> predicate, StringBuffer buffer) {
        if (predicate.get()) {
            buffer.append(UNDERLINE + GREEN + "x" + RESET);
        } else {
            buffer.append("-");
        }
    }

    private static String getSize(File file) {
        long size = file.length();

        if (size < 1024) {
            return Math.round(size) + "";
        }

        if (size < 1024 * 1024) {
            double d = Math.floor(size / 1024.0);
            if (d > 10) {
                return Math.round(d) + "k";
            } else {
                return d + "k";
            }
        }

        double d = Math.floor(size / 1024.0 / 1024.0);
        return Math.round(d) + "m";
    }

    public static String padLeft(String s, int n) {
        return String.format("%" + n + "s", s);
    }

    private static OptionalLong findInode(File file) {
        try {
            Long inode = (Long) Files.getAttribute(file.toPath(), "unix:ino");
            return inode != null ? OptionalLong.of(inode) : OptionalLong.empty();
        } catch (UnsupportedOperationException | IOException | IllegalArgumentException e) {
            // getting an inode is unsupported for this JVM or that filesystem
            return OptionalLong.empty();
        }
    }
}
