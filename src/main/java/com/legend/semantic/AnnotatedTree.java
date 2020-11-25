package com.legend.semantic;

import com.legend.lexer.Keyword;
import com.legend.parser.ast.ASTNode;
import com.legend.parser.ast.ClassDeclaration;
import com.legend.parser.ast.FunctionDeclaration;
import com.legend.semantic.*;

import javax.naming.Name;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Legend
 * @data by on 20-11-15.
 * @description 带AST节点注释的树 存放语义分析的结果
 *  1.类型信息,包括基本类型和用户自定义类型
 *  2.变量和函数调用的消解
 *  3.作用域Scope.在Scope中包含了该作用域的所有符号,Variable Function Class等都是符号
 */
public class AnnotatedTree {

    // 语法树的根节点
    private ASTNode astRoot = null;

    // 解析出来的所有类型信息,包括函数、类、类的方法(后期包括枚举、数组等)
    public List<Type> types = new LinkedList<>();

    // ast节点对应的symbol
    public Map<ASTNode, Symbol> symbolOfNode = new HashMap<>();

    // 节点所属的作用域
    public Map<ASTNode, Scope> scopeOfNode = new HashMap<>();

    // 节点的类型,用于作类型推导
    public Map<ASTNode, Type> typeOfNode = new HashMap<>();

    // 全局命名空间
    private NameSpace nameSpace;

    // 语义分析过程中生成的信息
    protected List<CompilationLog> logs = new LinkedList<>();

    // 在构造函数里,引用的this().第二个函数是被调用的构造函数
    public Map<Function, Function> thisConstructorRef = new HashMap<>();

    // 在构造函数里,引用的super().第二个函数是被调用的构造函数
    public Map<Function, Function> superConstructorRef = new HashMap<>();

    public AnnotatedTree() {}

    /**
     * 记录编译报错和警告
     * @param message 消息内容
     * @param type 信息类型, CompilationLog中的INFO、WARNING、ERROR
     * @param ast 对应的AST节点
     */
    public void log(String message, int type, ASTNode ast) {
        CompilationLog log = new CompilationLog();
        log.ast = ast;
        log.message = message;
        log.line = ast.getToken().getLine();
        log.column = ast.getToken().getColumn();
        log.type = type;
        logs.add(log);
        System.err.println(log);
    }

    public void log(String message, ASTNode ast) {
        this.log(message, CompilationLog.ERROR, ast);
    }

    // 是否有编译错误
    public boolean hasCompilationError() {
        for (CompilationLog log : logs) {
            if (log.type == CompilationLog.ERROR) {
                return true;
            }
        }
        return false;
    }

    /**
     * 指定作用域查找变量
     * @param scope 作用域
     * @param name 变量名称
     * @return 变量
     */
    public Variable lookupVariable(Scope scope, String name) {
        Variable v = scope.getVariable(name);
        if (v == null && scope.enclosingScope != null) {
            return lookupVariable(scope.enclosingScope, name);
        }
        return v;
    }

    /**
     * 指定作用域查找类
     * @param scope 作用域
     * @param name 类名称
     * @return 类
     */
    public Class lookupClass(Scope scope, String name) {
        Class c = scope.getClass(name);
        if (c == null && scope.enclosingScope != null) {
            return lookupClass(scope.enclosingScope, name);
        }
        return c;
    }

    public boolean isModuleName(String name) {
        for (NameSpace nameSpace : nameSpace.subNameSpaces()) {
            if (name.equals(nameSpace.getName())) {
                return true;
            }
        }
        return false;
    }

    public Class lookupClassByModuleId(String id, String name) {
        Scope scope = null;
        for (NameSpace nameSpace : nameSpace.subNameSpaces()) {
            if (nameSpace.getName().equals(id)) {
                scope = nameSpace;
                break;
            }
        }
        return lookupClass(scope, name);
    }

    public NameSpace lookupModuleScope(String id) {
        for (NameSpace nameSpace : nameSpace.subNameSpaces()) {
            if (nameSpace.getName().equals(id)) {
                return nameSpace;
            }
        }
        return null;
    }

    // todo 单纯根据名称不严谨
    public Type lookupType(String idName) {
        Type rtn = null;
        for (Type type : types) {
            if (type.name().equals(idName)) {
                rtn = type;
                break;
            }
        }
        if (rtn == null) {

        }
        return rtn;
    }

    /**
     * 指定作用域查找函数
     * @param scope 作用域
     * @param name 函数名称
     * @param paramTypes 参数类型列表
     * @return 函数
     */
    public Function lookupFunction(Scope scope, String name, List<Type> paramTypes) {
        Function function = scope.getFunction(name, paramTypes);
        if (function == null && scope.enclosingScope != null) {
            return lookupFunction(scope.enclosingScope, name, paramTypes);
        }
        return function;
    }

    /**
     * 指定作用域查找函数类型的变量
     * @param scope 作用域
     * @param name 函数名称
     * @param paramTypes 参数类型列表
     * @return 函数变量
     */
    public Variable lookupFunctionVariable(Scope scope, String name, List<Type> paramTypes) {
        Variable variable = scope.getFunctionVariable(name, paramTypes);
        if (variable == null && scope.enclosingScope != null) {
            return lookupFunctionVariable(scope.enclosingScope, name, paramTypes);
        }
        return variable;
    }

    /**
     * 通过名称查找函数 查找范围包括父类 和 所属作用域
     * @param scope 作用域
     * @param name 名称
     * @return 函数
     */
    public Function lookupFunction(Scope scope, String name) {
        Function rtn = null;
        if (scope instanceof com.legend.semantic.Class) {
            rtn = getMethodOnlyByName((com.legend.semantic.Class) scope, name);
        } else {
            rtn = getFunctionOnlyByName(scope, name);
        }
        if (rtn == null && scope.enclosingScope != null) {
            rtn = lookupFunction(scope.enclosingScope, name);
        }
        return rtn;
    }

    // 对于方法 子类还需要从父类去查找
    private Function getMethodOnlyByName(com.legend.semantic.Class theClass, String name) {
        Function function = getFunctionOnlyByName(theClass, name);
        if (function == null && theClass.getParentClass() != null) {
            return getMethodOnlyByName(theClass.getParentClass(), name);
        }
        return function;
    }

    private Function getFunctionOnlyByName(Scope scope, String name) {
        for (Symbol s : scope.symbols) {
            if (s instanceof Function && s.name.equals(name)) {
                return (Function) s;
            }
        }
        return null;
    }

    /**
     * 查找节点所属作用域
     * @param node 节点
     * @return 作用域
     */
    public Scope enclosingScopeOfNode(ASTNode node) {
        Scope scope = scopeOfNode.get(node);
        if (scope == null && node.getParent() != null) {
            return enclosingScopeOfNode(node.getParent());
        }
        return scope;
    }

    /**
     * 查找节点所属作函数
     * @param node 节点
     * @return 函数作用域
     */
    public Function enclosingFunctionOfNode(ASTNode node){
        if (node.getParent() instanceof FunctionDeclaration){
            return (Function) scopeOfNode.get(node.getParent());
        } else if (node.getParent() == null){
            return null;
        }
        return enclosingFunctionOfNode(node.getParent());
    }

    /**
     * 查找节点所属作类
     * @param node 节点
     * @return 类作用域
     */
    public Class enclosingClassOfNode(ASTNode node) {
        if (node.getParent() instanceof ClassDeclaration) {
            return (com.legend.semantic.Class) scopeOfNode.get(node.getParent());
        } else if (node.getParent() == null) {
            return null;
        }
        return enclosingClassOfNode(node.getParent());
    }

    // 输出Scope中的内容,包括每个变量的名称、类型
    public String getScopeTreeString() {
        StringBuilder sb = new StringBuilder();
        scopeToString(sb, nameSpace, "");
        return sb.toString();
    }

    private void scopeToString(StringBuilder sb, Scope scope, String indent) {
        sb.append(indent).append(scope).append('\n');
        for (Symbol symbol : scope.symbols) {
            if (symbol instanceof Scope) {
                scopeToString(sb, (Scope) symbol, indent + "\t");
            } else {
                sb.append(indent).append("\t").append(symbol).append('\n');
            }
        }
    }

    public void setNameSpace(NameSpace nameSpace) {
        this.nameSpace = nameSpace;
    }

    public void setAstRoot(ASTNode astRoot) {
        this.astRoot = astRoot;
    }

    public ASTNode getAstRoot() {
        return astRoot;
    }

    public NameSpace getNameSpace() {
        return nameSpace;
    }
}
