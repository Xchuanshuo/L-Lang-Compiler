package com.legend.compiler;

import com.legend.common.MetadataArea;
import com.legend.exception.GeneratorException;
import com.legend.exception.LexicalException;
import com.legend.exception.ParseException;
import com.legend.gen.ByteCodeGenerator;
import com.legend.gen.ByteCodeProgram;
import com.legend.interpreter.LInterpreter;
import com.legend.ir.TACGenerator;
import com.legend.ir.TACProgram;
import com.legend.lexer.Lexer;
import com.legend.lexer.Token;
import com.legend.parser.Parser;
import com.legend.parser.Program;
import com.legend.parser.common.ASTIterator;
import com.legend.semantic.AnnotatedTree;
import com.legend.semantic.analyze.*;
import com.legend.vm.LVM;
import com.legend.vm.StringPool;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * @author Legend
 * @data by on 20-11-8.
 * @description 编译器启动类
 */
public class Compiler {

    public static void main(String[] args) throws Exception {
        String path = "/Users/legend/Projects/IdeaProjects/2020/编译原理/L-Lang-Compiler/example/array.l";
        Args arg = Args.parse(args);
        if (arg.help) {
            dumpHelp();
        } else if (arg.dumpTokens) {
            dumpTokens(arg);
        } else if (arg.dumpAST) {
            dumpAST(arg);
        } else if (arg.dumpIR) {
            dumpIR(arg);
        } else if (arg.dumpASM) {
            dumpASM(arg);
        } else if (arg.srcFile != null) {
            compile(arg.srcFile);
        } else if (arg.binFile != null) {
            runByBin(arg.binFile);
        }
    }

    private static void dumpHelp() {
        System.out.println("-s            Input a source file and output bin file.");
        System.out.println("-i            Input a binary file and execute.");
        System.out.println("--tokens      Dumps tokens and quit.");
        System.out.println("--ast         Dumps ast and quit.");
        System.out.println("--ir          Dumps IR and quit.");
        System.out.println("--asm         Dumps ASM and quit.");
        System.out.println("--main        Set the user main path.");
        System.out.println("--help        Prints the help message and quit.");
    }

    private static void dumpTokens(Args arg) throws Exception {
        List<Token> tokenList = Lexer.fromFile(arg.srcFile);
        for (Token token : tokenList) {
            System.out.println(token);
        }
    }

    private static void dumpAST(Args arg) throws Exception {
        Program program = Parser.fromFile(arg.srcFile);
        program.dumpAST();
    }

    private static void dumpIR(Args arg) throws Exception {
        AnnotatedTree at = new AnnotatedTree();
        Program program = parse(arg.srcFile, at);
        semanticAnalyze(at);
        TACProgram tacProgram = generateIR(at, program);
        tacProgram.dump();
    }

    private static void dumpASM(Args arg) throws Exception {
        AnnotatedTree at = new AnnotatedTree();
        Program program = parse(arg.srcFile, at);
        semanticAnalyze(at);
        TACProgram tacProgram = generateIR(at, program);
        ByteCodeProgram byteCodeProgram = generateByteCode(tacProgram);
        byteCodeProgram.dumpWithComments();
    }

    public static void compile(String path) throws Exception {
        AnnotatedTree at = new AnnotatedTree();
        Program program = parse(path, at);
        semanticAnalyze(at);
//        astInterpreter(at, program);
        TACProgram tacProgram = generateIR(at, program);
        ByteCodeProgram byteCodeProgram = generateByteCode(tacProgram);
//        MetadataArea.getInstance().dumpConstantPool();
//        byteCodeProgram.dumpWithComments();
        output(byteCodeProgram, path.replace(".l", ".bin"));
//        run(byteCodeProgram);
    }

    private static void output(ByteCodeProgram program, String path) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(path);
             DataOutputStream dos = new DataOutputStream(fos);
             ByteArrayOutputStream bios = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bios)){
            dos.writeInt(program.getEntry());
            byte[] codes = program.getByteCodes();
            dos.writeInt(codes.length);
            dos.write(codes);
            oos.writeObject(MetadataArea.getInstance());
            oos.flush();
            byte[] metaDataBytes = bios.toByteArray();
            dos.writeInt(metaDataBytes.length);
            dos.write(metaDataBytes);
            dos.flush();
            System.out.println(String.format("compile success~, {%s}", path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void runByBin(String path) throws Exception {
        try(FileInputStream fis = new FileInputStream(path);
            DataInputStream dis = new DataInputStream(fis)) {
            int entry = dis.readInt();
            int codeLen = dis.readInt();
            byte[] codes = new byte[codeLen];
            dis.readFully(codes, 0, codeLen);
            int metaDataLen = dis.readInt();
            byte[] metaDataBytes = new byte[metaDataLen];
            dis.readFully(metaDataBytes, 0, metaDataLen);
            ByteArrayInputStream bais = new ByteArrayInputStream(metaDataBytes);
            ObjectInputStream oos = new ObjectInputStream(bais);
            MetadataArea area = (MetadataArea) oos.readObject();

            LVM lvm = new LVM(codes, entry, area);
            lvm.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Program parse(String path, AnnotatedTree at) throws Exception{
        Program program = Parser.fromFile(path);
        at.setAstRoot(program);
//        program.dumpAST();
        return program;
    }

    public static void semanticAnalyze(AnnotatedTree at) {
        // 语义分析, 采用多阶段扫描
        ASTIterator astIterator = new ASTIterator();

        // 1.类、函数类型和作用域扫描
        TypeAndScopeScanner pass1 = new TypeAndScopeScanner(at);
        astIterator.traverse(pass1, at.getAstRoot());

        // 2.类型消解 把变量、类继承、函数声明的类型都解析出来,也就是所有声明时用到类型的地方
        TypeResolver pass2 = new TypeResolver(at);
        astIterator.traverse(pass2, at.getAstRoot());

        // 3.引用消解: 变量、函数引用的消解、类型推导
        RefResolver pass3 = new RefResolver(at);
        astIterator.traverse(pass3, at.getAstRoot());

        // 4.类型检查
        TypeChecker pass4 = new TypeChecker(at);
        astIterator.traverse(pass4, at.getAstRoot());

        // 5.其它语义检查
        SemanticValidator pass5 = new SemanticValidator(at);
        astIterator.traverse(pass5, at.getAstRoot());

        // 闭包分析
        ClosureAnalyzer analyzer = new ClosureAnalyzer(at);
        analyzer.analyzeClosure();
    }

    public static TACProgram generateIR(AnnotatedTree at, Program program) {
        TACProgram tacProgram = new TACProgram();
        // 生成三地址码
        TACGenerator irGenerator = new TACGenerator(at, tacProgram);
        program.accept(irGenerator);
        MetadataArea.getInstance().fillConstantPool(tacProgram.getInstructionList());
        return tacProgram;
    }

    public static ByteCodeProgram generateByteCode(TACProgram tacProgram) {
        // 生成 bytecode
        ByteCodeGenerator codeGenerator = new ByteCodeGenerator(tacProgram);
        codeGenerator.generate();
        ByteCodeProgram byteCodeProgram = codeGenerator.getProgram();
        return byteCodeProgram;
    }

    public static void astInterpreter(AnnotatedTree at, Program program) {
        // AST解释器
        LInterpreter interpreter = new LInterpreter(at);
        program.accept(interpreter);
    }

    public static void run(ByteCodeProgram byteCodeProgram) {
        // 虚拟机解释执行
        LVM lvm = new LVM(byteCodeProgram.getByteCodes(), byteCodeProgram.getEntry(),
                MetadataArea.getInstance());
        lvm.run();
    }
}
