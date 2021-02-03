package com.legend.semantic.analyze;

import com.legend.common.BuiltInFunction;
import com.legend.lexer.Keyword;
import com.legend.parser.Program;
import com.legend.parser.ast.*;
import com.legend.parser.common.BaseASTListener;
import com.legend.semantic.*;
import com.legend.semantic.Class;
import com.legend.semantic.FunctionType;
import com.legend.semantic.PrimitiveType;

import java.util.LinkedList;
import java.util.List;

import static com.legend.common.BuiltInFunction.BuiltIn.*;
import static com.legend.common.BuiltInFunction.BuiltIn.BYTE;
import static com.legend.lexer.Keyword.Key.SUPER;
import static com.legend.lexer.Keyword.Key.THIS;


/**
 * @author Legend
 * @data by on 20-11-16.
 * @description 语义分析第三步: 引用消解
 *  对变量、函数参数、函数调用等引用消解 类型推导
 */
public class RefResolver extends BaseASTListener {

    private AnnotatedTree at;
    private List<FunctionCall> thisConstructorList = new LinkedList<>();
    private List<FunctionCall> superConstructorList = new LinkedList<>();

    public RefResolver(AnnotatedTree at) {
        this.at = at;
    }

    @Override
    public void exitProgram(Program ast) {
        for (FunctionCall theConstructor : thisConstructorList) {
            resolveThisConstructor(theConstructor);
        }
        for (FunctionCall theConstructor : superConstructorList) {
            resolveSuperConstructor(theConstructor);
        }
    }

    private void resolveThisConstructor(FunctionCall ast) {
        Class theClass = at.enclosingClassOfNode(ast);
        if (theClass == null) {
            at.log("this constructor must be called inside a class", ast);
            return;
        }
        Function function = at.enclosingFunctionOfNode(ast);
        if (function == null || !function.isConstructor()) {
            at.log("this constructor must be called inside a construction function", ast);
            return;
        }
        if (!isInFirstStatement(ast, (FunctionDeclaration) function.getAstNode())) {
            at.log("this() must be first statement in a constructor", ast);
            return;
        }
        List<Type> paramTypes = getParamTypes(ast);
        Function constructor = theClass.findConstructor(paramTypes);
        if (constructor != null) {
            at.symbolOfNode.put(ast, constructor);
            at.typeOfNode.put(ast, theClass);
            at.thisConstructorRef.put(function, constructor);
        } else if (paramTypes.size() == 0) {
            at.symbolOfNode.put(ast, theClass.defaultConstructor());
            at.typeOfNode.put(ast, theClass);
            at.thisConstructorRef.put(function, theClass.defaultConstructor());
        } else {
            at.log("unable find a constructor inside a class " + theClass, ast);
        }
    }

    private void resolveSuperConstructor(FunctionCall ast) {
        Class theClass = at.enclosingClassOfNode(ast);
        if (theClass == null) {
            at.log("super constructor must be called inside a class", ast);
            return;
        }
        Class parentClass = theClass.getParentClass();
        if (parentClass == null) {
            at.log("super cant not find a parent class", ast);
            return;
        }
        Function function = at.enclosingFunctionOfNode(ast);
        if (function == null || !function.isConstructor()) {
            at.log("super constructor must be called inside a construction function", ast);
            return;
        }
        if (!isInFirstStatement(ast, (FunctionDeclaration) function.getAstNode())) {
            at.log("super() must be first statement in a constructor", ast);
            return;
        }
        List<Type> paramTypes = getParamTypes(ast);
        Function constructor = parentClass.findConstructor(paramTypes);
        if (constructor != null) {
            at.symbolOfNode.put(ast, constructor);
            at.typeOfNode.put(ast, parentClass);
            at.superConstructorRef.put(function, constructor);
        } else if (paramTypes.size() == 0) {
            at.symbolOfNode.put(ast, parentClass.defaultConstructor());
            at.typeOfNode.put(ast, parentClass);
            at.superConstructorRef.put(function, parentClass.defaultConstructor());
        } else {
            at.log("unable find a constructor inside a class " + theClass, ast);
        }
    }

    @Override
    public void exitExpr(Expr ast) {
        Type type = null;
        if (ast.isDotExpr()) {
            Symbol left = at.symbolOfNode.get(ast.getChild(0));
            if (ast.getChild(0) instanceof ArrayCall && ast.getChild(1).getText().equals("length")) {
                type = PrimitiveType.Integer;
            } else if (!(left instanceof Variable)){
                // 不是变量的.表达式有两种情况 1.内部类 2.类的静态字段 3.外部模块
                if (left instanceof Class) { // 内部类的情况
                    type = findInnerClassDotStaticType(ast, (Class) left);
                } else if(left instanceof Function) { // 左操作数为函数调用
                    Function function = (Function) left;
                    Class theClass = (Class) function.returnType();
                    type = findClassDotType(ast, theClass);
                } else if (left instanceof NameSpace) { // 外部模块(命名空间NameSpace)
                    type = findNameSpaceDotType(ast, (Scope) left);
                }
                if (type == null) {
                    at.log("symbol is not a qualified object: " + left, ast);
                }
            } else if (((Variable) left).getType() instanceof Class) {
                Class theClass = (Class) ((Variable) left).getType();
                type = findClassDotType(ast, theClass);
            } else if (((Variable) left).getType() instanceof ArrayType) {
                String text = ast.getChild(1).getText();
                if (text.equals("length")) {
                    type = PrimitiveType.Integer;
                    at.symbolOfNode.put(ast, left);
                }
            }
        } else if (ast.getToken() != null && ast.exprs().size() >= 2) {
            type = getBinaryExprType(ast);
        }
        at.typeOfNode.put(ast, type);
    }

    private Type findClassDotType(Expr ast, Class theClass) {
        Type type = null;
        if (ast.functionCall() != null) { // .后面接函数调用
            type = at.typeOfNode.get(ast.functionCall());
            at.symbolOfNode.put(ast, at.symbolOfNode.get(ast.functionCall()));
        } else if (ast.arrayCall() != null) { // .后面接数组调用
            type = at.typeOfNode.get(ast.arrayCall());
        } else { // .后面接字段
            String fieldName = ast.getChild(1).getText();
            Variable variable = theClass.getVariable(fieldName);
            if (variable == null) {
                at.log("unable find a field [" + fieldName + "] in Class " + theClass, ast);
            } else {
                at.symbolOfNode.put(ast, variable);
                type = variable.getType();
            }
        }
        return type;
    }

    private Type findInnerClassDotStaticType(Expr ast, Class theClass) {
        Type type = null;
        if (ast.functionCall() != null) { // 静态方法或模块全局方法
            type = at.typeOfNode.get(ast.functionCall());
            at.symbolOfNode.put(ast, at.symbolOfNode.get(ast.functionCall()));
        } else {
            if (ast.arrayCall() != null) {
                type = at.typeOfNode.get(ast.arrayCall());
                at.symbolOfNode.put(ast, at.symbolOfNode.get(ast.arrayCall()));
                return type;
            }
            String name = "";
            if (ast.rightChild() instanceof ArrayCall) {
                name = ast.rightChild().getChild(0).getText();
            } else {
                name = ast.getChild(1).getText();
            }
            Class newClass = theClass.getClass(name);
            if (newClass != null) { // 内部类
                type = newClass;
                at.symbolOfNode.put(ast, newClass);
            } else { // 静态字段
                Variable variable = theClass.findStaticVariable(name);
                type = variable.getType();
                at.symbolOfNode.put(ast, variable);
            }
        }
        return type;
    }

    private Type findNameSpaceDotType(Expr ast, Scope scope) {
        Type type = null;
        if (ast.functionCall() != null) {
            type = at.typeOfNode.get(ast.functionCall());
        } else {
            String name = ast.getChild(1).getText();
            Class newClass = at.lookupClass(scope, name);
            if (newClass != null) {
                type = newClass;
                at.symbolOfNode.put(ast, newClass);
            } else {
                Variable variable = at.lookupVariable(scope, name);
                if (variable != null) {
                    type = variable.getType();
                    at.symbolOfNode.put(ast, variable);
                } else {
                    Function function = at.lookupFunction(scope, name);
                    if (function != null) {
                        type = function;
                        at.symbolOfNode.put(ast, function);
                    }
                }
            }
        }
        return type;
    }

    private Type getBinaryExprType(Expr ast) {
        Type type = null;
        Type type1 = at.typeOfNode.get(ast.getChild(0));
        Type type2 = at.typeOfNode.get(ast.getChild(1));
        switch (ast.getToken().getTokenType()) {
            case ADD:
                if (type1 == PrimitiveType.String || type2 == PrimitiveType.String) {
                    type = PrimitiveType.String; // 字符串能跟任何类型相加
                } else if (type1 instanceof PrimitiveType && type2 instanceof PrimitiveType) {
                    type = PrimitiveType.getUpperType(type1, type2);
                } else {
                    at.log("operand should be PrimitiveType " +
                            "for additive and multiplicative expression", ast);
                }
                break;
            case SUB:
            case MUL:
            case DIV:
            case MOD:
                if (type1 instanceof PrimitiveType && type2 instanceof PrimitiveType) {
                    type = PrimitiveType.getUpperType(type1, type2);
                } else {
                    at.log("operand should be PrimitiveType " +
                            "for additive and multiplicative expression", ast);
                }
                break;
            case EQUAL:
            case NOT_EQUAL:
            case LT:
            case LE:
            case GT:
            case GE:
            case LOGIC_AND:
            case LOGIC_OR:
            case BANG:
                type = PrimitiveType.Boolean;
                break;
            case ASSIGN:
            case ADD_ASSIGN:
            case SUB_ASSIGN:
            case MUL_ASSIGN:
            case DIV_ASSIGN:
            case MOD_ASSIGN:
            case RSHIFT_ASSIGN:
            case LSHIFT_ASSIGN:
            case URSHIFT_ASSIGN:
            case AND_ASSIGN:
            case XOR_ASSIGN:
                type = type1;
                break;
            case LSHIFT:
            case RSHIFT:
            case BIT_OR:
            case BIT_AND:
            case XOR:
                type = PrimitiveType.Integer;
                break;
        }
        return type;
    }

    @Override
    public void exitVariableInitializer(VariableInitializer ast) {
        if (ast.expr() != null) {
            at.typeOfNode.put(ast, at.typeOfNode.get(ast.expr()));
        } else if (ast.arrayInitializer() != null) {
            at.typeOfNode.put(ast, at.typeOfNode.get(ast.arrayInitializer()));
        }
    }

    @Override
    public void exitVariableDeclarator(VariableDeclarator ast) {
        processId(ast.identifier());
    }

    @Override
    public void exitTerminalNode(TerminalNode ast) {
        if ((!ast.getToken().isId() && !ast.getToken().isThisOrSuper())
            || ast.getParent() instanceof FunctionCall) {
            return;
        }
        ASTNode parent = ast.getParent();
        if (parent instanceof ArrayCall && parent.getParent() instanceof Expr
                && ((Expr) parent.getParent()).isDotExpr()
                && parent.getChild(0) == ast) {
            return;
        }
        if (parent instanceof FunctionDeclaration.FormalParameter) {
            processId(ast);
        }
        if (parent instanceof VariableInitializer) {
            // int a = b 类似的表达式对b进行消解
            resolveVariable(ast);
        } else if (parent instanceof Expr) {
            if (((Expr) parent).isDotExpr()) {
                // .表达式只需要消解左孩子
                if (ast == parent.getChild(0)) {
                    resolveVariable(ast);
                }
            } else if (parent instanceof ArrayCall
                    && parent.getChild(0) != ast) {
                resolveVariable(ast);
            } else {
                resolveVariable(ast);
            }
        } else if (parent instanceof ExprList
                || parent instanceof ParExpression) {
            resolveVariable(ast);
        } else if (parent instanceof Statement
                && parent.getChild(1) == ast) { // 消解return a 中的a
            resolveVariable(ast);
        }
    }

    private void resolveVariable(TerminalNode ast) {
        Scope scope = at.enclosingScopeOfNode(ast);
        String name = ast.getText();
        if (ast.getToken().isThisOrSuper()) {
            Class theClass = at.enclosingClassOfNode(ast);
            if (theClass != null) {
                if (Keyword.isMatchKey(THIS, name)) {
                    Variable.This thisRef = theClass.getThis();
                    at.symbolOfNode.put(ast, thisRef);
                } else if (Keyword.isMatchKey(SUPER, name)) {
                    Variable.Super superRef = theClass.getSuper();
                    at.symbolOfNode.put(ast, superRef);
                }
                at.typeOfNode.put(ast, theClass);
            } else {
                at.log("keyword \"this\" or \"super\" can only be used inside a class", ast);
            }
        } else {
            Variable variable = at.lookupVariable(scope, name);
            if (variable != null) {
                at.typeOfNode.put(ast, variable.getType());
                at.symbolOfNode.put(ast, variable);
            } else {
                Function function = at.lookupFunction(scope, name);
                boolean isClassDot = ast.getParent() instanceof Expr
                        && ((Expr) ast.getParent()).isDotExpr();
                if (function != null && !isClassDot) {
                    // 函数调用单独消解了 这里查找到的函数只有一种情况 也就是直接获取函数对象
                    // 所以节点类型对应的应该是函数本身
                    at.typeOfNode.put(ast, function);
                    at.symbolOfNode.put(ast, function);
                } else {
                    Class theClass = at.lookupClass(scope, name);
                    if (theClass != null) { // 类
                        at.typeOfNode.put(ast, theClass);
                        at.symbolOfNode.put(ast, theClass);
                    } else if (at.isModuleName(name)){ // 导入外部模块
                        NameSpace nameSpace = at.lookupModuleScope(name);
                        at.symbolOfNode.put(ast, nameSpace);
                    } else {
                        at.log("unable find a variable or function or class: " + name, ast);
                    }
                }
            }
        }
    }

    private void processId(TerminalNode id) {
        String name = id.getText();
        Scope scope = at.enclosingScopeOfNode(id);
        if (Scope.getVariable(scope, name) != null) {
            at.log("Variable or parameter already Declared:" + name, id);
        }
        Variable variable = (Variable) at.symbolOfNode.get(id);
        // 引用消解时再将变量放入作用域的符号表中
        if (variable.isClassMember() && variable.isStatic()) {
            ((Class)scope).addStatic(variable);
        } else {
            scope.addSymbol(variable);
        }
    }

    @Override
    public void exitArrayCall(ArrayCall ast) {
        String name = ast.identifier().getText();
        Type type = at.lookupType(name);
        if (type != null || ast.identifier().getToken().isBaseType()) { // 实例化一个数组
            type = type == null ? PrimitiveType.getBaseTypeByText(name) : type;
            for (int i = 0;i < ast.exprList().size();i++) {
                ArrayType arrayType = new ArrayType();
                arrayType.setBaseType(type);
                type = arrayType;
            }
        } else { // 数组调用
            Scope scope = at.enclosingScopeOfNode(ast);
            Variable variable = at.lookupVariable(scope, ast.identifier().getText());
            Class theClass = at.enclosingClassOfNode(ast);
            if (variable == null && theClass != null) {
                variable = theClass.findStaticVariable(ast.identifier().getText());
            }
            at.symbolOfNode.put(ast.identifier(), variable);
            ArrayType originalType = (ArrayType) variable.getType();
            for (int i = 0;i < ast.exprList().size();i++) {
                type = originalType.baseType();
                if (type instanceof ArrayType) {
                    originalType = (ArrayType) type;
                }
            }
        }
        at.typeOfNode.put(ast, type);
    }

    @Override
    public void exitFunctionCall(FunctionCall ast) {
        String name = ast.identifier().getText();
        if (Keyword.isMatchKey(THIS, name)) {
            thisConstructorList.add(ast);
            return;
        } else if (Keyword.isMatchKey(SUPER, name)) {
            superConstructorList.add(ast);
            return;
        } else if (BuiltInFunction.isBuiltInFunc(name)) {
            // todo 临时代码 支持println等内建函数
            processBuildInFunction(ast);
            return;
        }
        List<Type> paramTypes = getParamTypes(ast);
        resolveFunctionCall(ast, name, paramTypes);
    }

    private void resolveFunctionCall(FunctionCall ast, String name, List<Type> paramTypes) {
        boolean found = false;
        if (ast.getParent() instanceof Expr && ((Expr) ast.getParent()).isDotExpr()
                && ast == ((Expr) ast.getParent()).rightChild()) {
            // 处理a.fun()形式的点表达式(1.实例对象 2.内部类 3.跨模块调用(不同命名空间) 4.函数.函数)
            found = processDotExprFunCall((Expr) ast.getParent(), ast, name, paramTypes);
        }
        if(found) return;
        // 当前所属作用域查找函数与函数变量
        Scope scope = at.enclosingScopeOfNode(ast);
        findFunctionByScope(scope, ast, name, paramTypes);
    }

    // 1.先尝试从类方法里面找 2. 1未找到则到类的符号表中去找对应签名的函数变量
    private boolean processDotExprFunCall(Expr expr, FunctionCall ast,
                                          String name, List<Type> paramTypes) {
        Symbol symbol = at.symbolOfNode.get(expr.expr(0));
        boolean isFound = false;
        if (!(symbol instanceof Variable) || !(((Variable) symbol).getType() instanceof Class)) {
            if (symbol instanceof Class) { // 内部类
                isFound = findInnerClassDotExpr(symbol, ast, name, paramTypes);
            } else if (symbol instanceof Function) { // 函数返回的对象类里面寻找
                Class theClass = (Class) ((Function) symbol).returnType();
                isFound = findFunctionByScope(theClass, ast, name, paramTypes);
            } else if (symbol instanceof Scope) { // 命名空间(从其它模块查找)
                Scope scope = (Scope) symbol;
                isFound = findFunctionByScope(scope, ast, name, paramTypes);
            } else {
                System.out.println(symbol);
                at.log("unable resolveVariable in a class", ast);
            }
        } else { // 实例化的对象里面去查找
            Class theClass = (Class) ((Variable) symbol).getType();
            Function function = theClass.getFunction(name, paramTypes);
            if (function != null) {
                at.symbolOfNode.put(ast, function);
                at.typeOfNode.put(ast, function.returnType());
                isFound = true;
            } else {
                Variable variable = theClass.getFunctionVariable(name, paramTypes);
                if (variable != null) {
                    FunctionType functionType = (FunctionType) variable.getType();
                    at.symbolOfNode.put(ast, variable);
                    at.typeOfNode.put(ast, functionType.returnType());
                    isFound = true;
                } else {
                    at.log("unable find a method " + name + " in Class " + theClass, ast);
                }
            }
        }
        return isFound;
    }

    // 处理内部类.构造函数()
    private boolean findInnerClassDotExpr(Symbol symbol, FunctionCall ast,
                                          String name, List<Type> paramTypes) {
        Class theClass = (Class) symbol;
        Function function = theClass.findStaticMethod(name, paramTypes);
        if (function != null) {
            at.typeOfNode.put(ast, function.returnType());
            at.symbolOfNode.put(ast, function);
            return true;
        }
        theClass = theClass.getClass(name);
        at.typeOfNode.put(ast, theClass);
        Function constructor = theClass.findConstructor(paramTypes);
        if (constructor != null) {
            at.symbolOfNode.put(ast, constructor);
            return true;
        } else if (ast.exprList().exprList().size() == 0) {
            at.symbolOfNode.put(ast, theClass.defaultConstructor());
            return true;
        } else {
            at.log("unknown class constructor: " + ast.getText(), ast);
        }
        return false;
    }

    private boolean findFunctionByScope(Scope scope, FunctionCall ast,
                                        String name, List<Type> paramTypes) {
        boolean found = false;
        Function function = at.lookupFunction(scope, name, paramTypes);
        if (function != null) {
            at.symbolOfNode.put(ast, function);
            at.typeOfNode.put(ast, function.returnType());
            found = true;
        } else {
            Variable variable = at.lookupFunctionVariable(scope, name, paramTypes);
            if (variable != null) {
                FunctionType functionType = (FunctionType) variable.getType();
                at.symbolOfNode.put(ast, variable);
                at.typeOfNode.put(ast, functionType.returnType());
                found = true;
            }
        }
        if (found) return true;
        // 查找是不是类的构造函数
        Class theClass = at.lookupClass(scope, name);
        if (theClass == null) {
            at.log("unknown function or function variable: " + ast.getText(), ast);
        } else {
            Function constructor = theClass.findConstructor(paramTypes);
            if (constructor != null) {
                at.symbolOfNode.put(ast, constructor);
                found = true;
            } else if (ast.exprList().exprList().size() == 0) {
                at.symbolOfNode.put(ast, theClass.defaultConstructor());
                found = true;
            } else {
                at.log("unknown class constructor: " + ast.getText(), ast);
            }
            at.typeOfNode.put(ast, theClass);
        }
        return found;
    }

    // 获取函数调用传入的参数类型
    private List<Type> getParamTypes(FunctionCall functionCall) {
        List<Type> paramTypes = new LinkedList<>();
        if (functionCall.exprList() != null) {
            for (Expr expr : functionCall.exprList().exprList()) {
                Type type = at.typeOfNode.get(expr);
                paramTypes.add(type);
            }
        }
        return paramTypes;
    }

    @Override
    public void exitLiteral(Literal ast) {
        if (ast instanceof Literal.CharLiteral) {
            at.typeOfNode.put(ast, PrimitiveType.Char);
        } else if (ast instanceof Literal.BooleanLiteral) {
            at.typeOfNode.put(ast, PrimitiveType.Boolean);
        } else if (ast instanceof Literal.FloatLiteral) {
            at.typeOfNode.put(ast, PrimitiveType.Float);
        } else if (ast instanceof Literal.IntegerLiteral) {
            at.typeOfNode.put(ast, PrimitiveType.Integer);
        } else if (ast instanceof Literal.StringLiteral) {
            at.typeOfNode.put(ast, PrimitiveType.String);
        } else if (ast instanceof Literal.NullLiteral) {
            at.typeOfNode.put(ast, PrimitiveType.Null);
        }
    }

    @Override
    public void exitArrayInitializer(ArrayInitializer ast) {
        Type baseType = null;
        if (ast.variableInitializerList() != null) {
            baseType = at.typeOfNode.get(ast.variableInitializerList().get(0));
            if (baseType == null) {
                baseType = PrimitiveType.Null;
            }
        }
        ArrayType arrayType = new ArrayType();
        arrayType.setBaseType(baseType);
        at.typeOfNode.put(ast, arrayType);
    }

    private boolean isInFirstStatement(FunctionCall ast, FunctionDeclaration fd) {
        if (fd.functionBody().blockStatements() != null &&
                fd.functionBody().blockStatements().blockStatements().get(0).statement() != null &&
                fd.functionBody().blockStatements().blockStatements().get(0).statement().expr() == ast) {
            return true;
        }
        return false;
    }

    private void processBuildInFunction(FunctionCall ast) {
        String name = ast.identifier().getText();
        Type type = null;
        if (BuiltInFunction.isMatchKey(INT, name)) {
            type = PrimitiveType.Integer;
        } else if (BuiltInFunction.isMatchKey(STR, name)) {
            type = PrimitiveType.String;
        } else if (BuiltInFunction.isMatchKey(FLOAT, name)) {
            type = PrimitiveType.Float;
        } else if (BuiltInFunction.isMatchKey(BYTE, name)) {
            type = PrimitiveType.Integer;
        } else if (BuiltInFunction.isMatchKey(STR_LEN, name)) {
            type = PrimitiveType.Integer;
        }
        if (type != null) {
            at.typeOfNode.put(ast, type);
        }
    }

}
