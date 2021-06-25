# Exaq


```
Usage: exaq [-1adDFilr] [--color] [files...]
exaq  is a modern replacement for ls build with Quarkus. It uses colours for
information by default, helping you distinguish between many types of files,
such as whether you are the owner, or in the owning group.
[files...]
-1, --oneline     Display one entry per line. Cannot we used with `--long`.
-a, --all         Show hidden and "dot" files.  Use this twice to also show
the `.' and `..'  directories.
--color       Whether to use terminal colours. By default, colors are
enabled
-d, --list-dirs   List directories as regular files, rather than recursing
and listing their contents.
-D, --only-dirs   List only directories, not files.
-F, --classify    Display file kind indicators next to file names.
-i, --inode       List each file's inode number
-l, --long        Display extended file metadata as a table. Cannot be used
with `--oneline`
-r, --reverse     Reverse the sort order.
```

