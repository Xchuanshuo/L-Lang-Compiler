package com.legend.compiler;

import com.legend.exception.LexicalException;
import com.legend.exception.ParseException;
import com.legend.interpreter.LInterpreter;
import com.legend.lexer.Lexer;
import com.legend.lexer.Token;
import com.legend.parser.Parser;
import com.legend.parser.Program;
import com.legend.parser.common.ASTIterator;
import com.legend.semantic.AnnotatedTree;
import com.legend.semantic.analyze.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Legend
 * @data by on 20-11-8.
 * @description 编译器启动类
 */
public class Compiler {

    public static void main(String[] args) throws IOException, LexicalException, ParseException {
        String path = "/home/legend/Projects/IdeaProjects/2020/编译原理/" +
                "L-Lang-Compiler/example/array.l";
//        List<Token> tokenList = Lexer.fromFile(path);
//        for (Token token : tokenList) {
//            System.out.println(token);
//        }
        Program program = Parser.fromFile(path);
        AnnotatedTree at = new AnnotatedTree();
        at.setAstRoot(program);
//        program.dumpAST();

        ASTIterator astIterator = new ASTIterator();
        // 语义分析, 采用多阶段扫描

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

        // AST解释器
        LInterpreter interpreter = new LInterpreter(at);
        program.accept(interpreter);
    }
}