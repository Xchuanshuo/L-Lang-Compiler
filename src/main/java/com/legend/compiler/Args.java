package com.legend.compiler;

/**
 * @author Legend
 * @data by on 21-2-16.
 * @description 命令行参数
 */
public class Args {

    protected String binFile;
    protected String srcFile;
    protected String userMainPath;
    protected String sysLibPath;
    protected boolean help = false;
    protected boolean dumpTokens = false;
    protected boolean dumpIR = false;
    protected boolean dumpASM = false;
    protected boolean dumpAST = false;
    public static final String LIB = "lib";
    public static final String MAIN = "main";

    public static Args parse(String[] args) {
        Args arg = new Args();
        for (int i = 0;i < args.length;i++) {
            String a = args[i];
            if ("-s".equals(a)) {
                arg.srcFile = args[++i];
            } else if ("-i".equals(a)) {
                arg.binFile = args[++i];
            } else if ("--tokens".equals(a)) {
                arg.dumpTokens = true;
            } else if ("--ast".equals(a)) {
                arg.dumpAST = true;
            } else if ("--ir".equals(a)) {
                arg.dumpIR = true;
            } else if ("--asm".equals(a)) {
                arg.dumpASM = true;
            } else if ("--help".equals(a)) {
                arg.help = true;
            } else if ("--lib".equals(a)) {
                arg.sysLibPath = args[++i];
                System.setProperty(LIB, arg.sysLibPath);
            } else if ("--main".equals(a)) {
                arg.userMainPath = args[++i];
                System.setProperty(MAIN, arg.userMainPath);
            }
        }
        if (arg.srcFile == null && arg.binFile == null) {
            System.out.println("lcc: error: no input file");
            System.out.println("lcc: error: Try \"lc --help\" for usage");
            return arg;
        }
        return arg;
    }


}
