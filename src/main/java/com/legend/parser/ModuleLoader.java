package com.legend.parser;

/**
 * @author Legend
 * @data by on 20-11-22.
 * @description 模块装载器(用来处理多模块)
 */
public class ModuleLoader {

    private static final String ROOT_PATH = "/home/legend/Projects/IdeaProjects/2020/编译原理/L-Lang-Compiler/example";

    public static Program load(String moduleName) {
        try {
            return Parser.fromFile(getPath(moduleName));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getPath(String moduleName) {
        moduleName = moduleName.replaceAll("\\.", "/");
        return ROOT_PATH + "/" + moduleName + ".l";
    }

    public static void main(String[] args) throws Exception {
        Program program = ModuleLoader.load("array");
        program.dumpAST();
    }
}
