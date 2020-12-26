package com.legend.ir;

import com.legend.common.BuiltInFunction;
import com.legend.interpreter.NullObject;
import com.legend.parser.Program;
import com.legend.parser.ast.*;
import com.legend.parser.common.BaseASTVisitor;
import com.legend.semantic.*;
import com.legend.semantic.Class;
import com.legend.semantic.PrimitiveType;

import java.util.*;

import static com.legend.common.BuiltInFunction.BuiltIn.*;

/**
 * @author Legend
 * @data by on 20-12-7.
 * @description 三地址中间代码生成器
 */
public class TACGenerator extends BaseASTVisitor<Object> {

    // tac指令程序
    private TACProgram program;

    // 带注释的抽象节点树
    private AnnotatedTree at;
    // 建立变量与新创建变量的映射
    private Map<Variable, Variable> variableMap = new HashMap<>();
    private Queue<Object> tmpData = new LinkedList<>();
    private Stack<TACInstruction> breakStack = new Stack<>();

    public TACGenerator(AnnotatedTree at, TACProgram tacProgram) {
        this.at = at;
        this.program = tacProgram;
    }

    @Override
    public Object visitProgram(Program ast) {
        for (Type type : at.types) {
            if (type instanceof Function) {
                Function function = (Function) type;
                FunctionDeclaration fd = (FunctionDeclaration) function.getAstNode();
                visitFunctionDeclaration(fd);
            }
        }
        visitBlockStatements(ast.blockStatements());
        return program;
    }

    @Override
    public Object visitBlockStatements(BlockStatements ast) {
        if (ast.blockStatements() != null) {
            for (BlockStatement blockStatement : ast.blockStatements()) {
                visitBlockStatement(blockStatement);
            }
        }
        return super.visitBlockStatements(ast);
    }

    @Override
    public Object visitBlockStatement(BlockStatement ast) {
        if (ast.variableDeclarators() != null) {
            visitVariableDeclarators(ast.variableDeclarators());
        } else if (ast.statement() != null) {
            visitStatement(ast.statement());
        }
        return null;
    }

    @Override
    public Object visitVariableDeclarators(VariableDeclarators ast) {
        for (VariableDeclarator declarator : ast.variableDeclaratorList()) {
            visitVariableDeclarator(declarator);
        }
        return super.visitVariableDeclarators(ast);
    }

    @Override
    public Object visitVariableDeclarator(VariableDeclarator ast) {
        if (ast.variableInitializer() != null) {
            Object rtn = visitVariableInitializer(ast.variableInitializer());
            Symbol s = at.symbolOfNode.get(ast.identifier());
            if (s instanceof Variable) {
                Variable variable = s.getEnclosingScope()
                        .createTempVariable(at.typeOfNode.get(s.getAstNode()));
                if (!variableMap.containsKey(s)) {
                    variableMap.put((Variable) s, variable);
                }
                s = variableMap.get(s);
                TACInstruction instruction = new TACInstruction(TACType.ASSIGN, (Variable) s, rtn, null, "=");
                program.add(instruction);
//                if (rtn instanceof Constant) {
//                    // 常量分配新的名字
//                    Variable variable = s.getEnclosingScope()
//                            .createTempVariable(at.typeOfNode.get(s.getAstNode()));
//                    if (!variableMap.containsKey(s)) {
//                        variableMap.put((Variable) s, variable);
//                    }
//                    s = variableMap.get(s);
//                    TACInstruction instruction = new TACInstruction(TACType.ASSIGN, (Variable) s, rtn, null, "=");
//                    program.add(instruction);
//                } else if (rtn instanceof Variable){
//                    // 变量沿用表达式的名字
//                    variableMap.put((Variable) s, (Variable) rtn);
//                }
            }
        }
        return super.visitVariableDeclarator(ast);
    }

    @Override
    public Symbol visitVariableInitializer(VariableInitializer ast) {
        if (ast.expr() != null) {
            return visitExpr(ast.expr());
        } else if (ast.arrayInitializer() != null) {
            return visitArrayInitializer(ast.arrayInitializer());
        }
        return null;
    }

    @Override
    public Object visitFunctionDeclaration(FunctionDeclaration ast) {
        Function function = (Function) at.symbolOfNode.get(ast);
        String name = ast.funcName().getText() ;
        TACInstruction startLabel = new TACInstruction(TACType.LABEL, null,
                "function " + name, null, null);
        program.add(startLabel);
        if (function.getEnclosingScope() instanceof Class
                && !function.isStatic()) {
            // 对象实例方法的第一个参数为this引用
            function.getVariables().add(0, ((Class)
                    function.getEnclosingScope()).getThis());
        }
        for (Variable variable : function.getVariables()) {
            Variable tmp = function.createTempVariable(variable.getType());
            variableMap.put(variable, tmp);

            TACInstruction paramTAC = new TACInstruction(TACType.PARAM);
            paramTAC.setArg1(tmp);

            program.add(paramTAC);
        }
        if (ast.functionBody() != null) {
            visitBlock(ast.functionBody());
            // 函数声明结尾加上一条默认返回指令
            TACInstruction last = program.lastInstruction();
            if (last != null && last.getType() != TACType.RETURN) {
                program.add(new TACInstruction(TACType.RETURN));
            }
        }

        return super.visitFunctionDeclaration(ast);
    }

    @Override
    public Symbol visitArrayInitializer(ArrayInitializer ast) {
        int size = 0;
        List<VariableInitializer> list = ast.variableInitializerList();
        if (list != null) size = list.size();
        Type type = at.typeOfNode.get(ast);
        Variable result = at.enclosingScopeOfNode(ast).createTempVariable(type);
        TACInstruction createArrayTAC = genNewArray(result, type, size);
        program.add(createArrayTAC);
        if (list != null) {
            for (int i = 0;i < list.size();i++) {
                Symbol symbol = visitVariableInitializer(list.get(i));
                if (symbol != null) {
                    Constant idx = new Constant(PrimitiveType.Integer, i);
                    TACInstruction tac = new TACInstruction(TACType.ASSIGN, result, idx, symbol,null);
                    tac.setArrayAssign(true);
                    program.add(tac);
                }
            }
        }
        return result;
    }

    @Override
    public Symbol visitExpr(Expr ast) {
        Symbol symbol = null;
        if (ast instanceof Literal) {
            symbol = visitLiteral((Literal) ast);
        } else if (ast instanceof FunctionCall) {
            symbol = visitFunctionCall((FunctionCall) ast);
        } else if (ast instanceof ArrayCall) {
            symbol = visitArrayCall((ArrayCall) ast);
        } else if (ast instanceof TerminalNode) {
            Symbol s = at.symbolOfNode.get(ast);
            if (s instanceof Variable.This) {
                Function function = at.enclosingFunctionOfNode(ast);
                variableMap.put((Variable) s, variableMap.get(function.getVariables().get(0)));
            }
            if (s instanceof Variable) { // 普通变量分配新名字
                symbol = processVariable(ast, s);
            } else if (s instanceof Function || s instanceof Class) {
                symbol = s;
            }
        } else if (ast.getAstNodeType() == ASTNodeType.BINARY_EXP) {
            symbol = processBinaryExp(ast);
        }
        return symbol;
    }

    private Symbol processVariable(Expr ast, Symbol s) {
        Symbol symbol = null;
        if (!(s instanceof Variable.This) && ((Variable) s).isClassMember()) { // 类成员
            symbol = s.getEnclosingScope().createTempVariable(at.typeOfNode.get(s.getAstNode()));
            if (s.isStatic()) { // 静态成员(属于类)
                Class theClass = (Class) s.getEnclosingScope();
                TACInstruction staticFieldTAC = genGetStaticField((Variable) symbol, theClass, s);
                program.add(staticFieldTAC);
            } else { // 普通成员(属于对象)
                Function function = at.enclosingFunctionOfNode(ast);
                Variable thisV = variableMap.get(function.getVariables().get(0));
                TACInstruction fieldTAC = genGetField((Variable) symbol, thisV, s);
                program.add(fieldTAC);
            }
        } else {
            if (!variableMap.containsKey(s)) {
                Variable variable = s.getEnclosingScope()
                        .createTempVariable(at.typeOfNode.get(s.getAstNode()));
                variableMap.put((Variable) s, variable);
            }
            symbol = variableMap.get(s);
        }
        return symbol;
    }

    private Symbol processBinaryExp(Expr ast) {
        if (ast.isDotExpr()) {
            return translateDotExp(ast);
        }
        Object left = visitExpr(ast.expr(0));
        Object right = visitExpr(ast.expr(1));
        Scope scope = at.enclosingScopeOfNode(ast);
        Type expectedType = at.typeOfNode.get(ast);
        TACInstruction instruction = null;
        Variable result = null;
        switch (ast.getToken().getTokenType()) {
            case ADD:
            case SUB:
            case MUL:
            case DIV:
            case MOD:
            case LT:
            case LE:
            case GT:
            case GE:
            case EQUAL:
            case NOT_EQUAL:
            case LOGIC_OR:
            case LOGIC_AND:
            case BIT_AND:
            case BIT_OR:
            case XOR:
            case LSHIFT:
            case RSHIFT:
            case DOT:
                result = scope.createTempVariable(expectedType);
                instruction = new TACInstruction(TACType.ASSIGN, result,
                        left, right, ast.getToken().getText());
                break;
            case ASSIGN:
                result = (Variable) left;
                instruction = new TACInstruction(TACType.ASSIGN, result, right, null, "=");
                if (ast.expr(0) instanceof ArrayCall) {
                    instruction.setArrayAssign(true);
                    instruction.setArg2(instruction.getArg1());
                    instruction.setArg1(tmpData.poll());
                }
                break;
            case ADD_ASSIGN:
                instruction = processOpAssign(ast, left, right, "+");
                break;
            case SUB_ASSIGN:
                instruction = processOpAssign(ast, left, right, "-");
                break;
            case MUL_ASSIGN:
                instruction = processOpAssign(ast, left, right, "*");
                break;
            case DIV_ASSIGN:
                instruction = processOpAssign(ast, left, right, "/");
                break;
            case LSHIFT_ASSIGN:
                instruction = processOpAssign(ast, left, right, "<<");
                break;
            case RSHIFT_ASSIGN:
                instruction = processOpAssign(ast, left, right, ">>");
                break;
        }
        if (instruction != null) {
            program.add(instruction);
            result = instruction.getResult();
        }
        return result;
    }

    private Symbol translateDotExp(Expr ast) {
        Symbol result = null;
        Scope scope = at.enclosingScopeOfNode(ast);
        Object left = visitExpr(ast.leftChild());
        if (left instanceof Variable && ((Variable) left).isArrayType()
                && ast.rightChild().getToken().getText().equals("length")) {
            result = scope.createTempVariable(PrimitiveType.Integer);
            TACInstruction lengthTAC = genGetArrayLen((Variable) result, left);
            program.add(lengthTAC);
        } else if (ast.rightChild() instanceof ArrayCall) {
            result = visitArrayCall((ArrayCall) ast.rightChild());
        } else if (ast.functionCall() != null) {
            FunctionCall functionCall = null;
            // 可能存在functionCall().functionCall()的情况 优先取右孩子
            if (ast.rightChild() instanceof FunctionCall) {
                functionCall = (FunctionCall) ast.rightChild();
            } else {
                functionCall = (FunctionCall) ast.leftChild();
            }
            List<Symbol> args = getArgsSymbol(functionCall);
            Function function = (Function) at.symbolOfNode.get(functionCall);
            if (left instanceof Variable) { // 对象实例方法
                result = translateVirtualMethod(scope, (Variable) left, function, args);
            } else if (left instanceof Class){ // 类(静态)方法
                result = translateStaticFunction(scope, (Class) left, function, args);
            }
        } else {
            Variable variable = (Variable) at.symbolOfNode.get(ast);
            result = scope.createTempVariable(at.typeOfNode.get(ast));
            if (variable.isStatic()) {
                TACInstruction staticFieldTAC = genGetStaticField((Variable) result, left, variable);
                program.add(staticFieldTAC);
            } else {
                if (!(variable instanceof Variable.This)
                        && !(variable instanceof Variable.Super)) {
                    TACInstruction fieldTAC = genGetField((Variable) result, left, variable);
                    program.add(fieldTAC);
                }
            }
        }
        return result;
    }

    @Override
    public Symbol visitFunctionCall(FunctionCall ast) {
        String funcName = ast.identifier().getText();
        Scope scope = at.enclosingScopeOfNode(ast);
        Function function = (Function) at.symbolOfNode.get(ast);
        if (BuiltInFunction.isBuiltInFunc(funcName)) {
            // 内置函数
            return translateBuiltInFunction(ast);
        }
        System.out.println(scope + "---------------------------------");
        List<Symbol> args = getArgsSymbol(ast);
        if (function.isConstructor()) { // 类构造方法
            return translateConstructor(scope, function, args);
        } else if (!function.isStatic() && function.isMethod()) {
            // this隐式调用 位于对象的实例方法内部可以省略this关键字
            Function caller = at.enclosingFunctionOfNode(ast);
            if (caller == null) return null;
            Variable thisV = variableMap.get(caller.getVariables().get(0));
            return translateVirtualMethod(scope, thisV, function, args);
        } else if (!(ast.getParent() instanceof Expr)
                || !((Expr) ast.getParent()).isDotExpr()
                || ast != ((Expr) ast.getParent()).rightChild()){ // 全局方法
            return translateStaticFunction(scope, function, args);
        }
        return null;
    }

    private Symbol translateConstructor(Scope scope, Function function, List<Symbol> args) {
        Class theClass = (Class) function.getEnclosingScope();
        Variable result = scope.createTempVariable(theClass);
        TACInstruction newInstanceTAC = genNewInstance(result, theClass);
        program.add(newInstanceTAC);
        if (!(function instanceof DefaultConstructor)) {
            args.add(0, result);
            for (Symbol symbol : args) {
                TACInstruction argTAC = genArg(symbol);
                program.add(argTAC);
            }
            TACInstruction invokeSpecialTAC = genInvokeSpecial(result, result, function);
            program.add(invokeSpecialTAC);
        }
        return result;
    }

    private Symbol translateStaticFunction(Scope scope, Function function, List<Symbol> args) {
        return translateStaticFunction(scope, null, function, args);
    }

    private Symbol translateStaticFunction(Scope scope, Class theClass, Function function, List<Symbol> args) {
        for (Symbol symbol : args) {
            TACInstruction argTAC = genArg(symbol);
            program.add(argTAC);
        }
        Variable result = scope.createTempVariable(function.returnType());
        TACInstruction invokeStaticTAC = genInvokeStatic(result, theClass, function);
        program.add(invokeStaticTAC);
        return result;
    }

    private Symbol translateVirtualMethod(Scope scope, Variable classObj, Function function, List<Symbol> args) {
        args.add(0, classObj);
        for (Symbol symbol : args) {
            TACInstruction argTAC = genArg(symbol);
            program.add(argTAC);
        }
        Variable result = scope.createTempVariable(function.returnType());
        TACInstruction invokeVirtualTAC = genInvokeVirtual(result, classObj, function);
        program.add(invokeVirtualTAC);
        return result;
    }

    @Override
    public Symbol visitArrayCall(ArrayCall ast) {
        String name = ast.identifier().getText();
        Type type = at.lookupType(name);
        Scope scope = at.enclosingScopeOfNode(ast);
        if (type != null || ast.identifier().getToken().isBaseType()) { // 实例化一个数组
            Type curType = at.typeOfNode.get(ast);
            List<Symbol> symbolList = new ArrayList<>();
            for (Expr expr : ast.exprList()) {
                symbolList.add(visitExpr(expr));
            }
            return translateNewEmptyArray(scope, symbolList, 0, curType);
        } else { // 普通的数组调用
            return translateArrayCall(scope, ast);
        }
    }

    private Symbol translateArrayCall(Scope scope, ArrayCall ast) {
        Variable arrayV = (Variable) at.symbolOfNode.get(ast.identifier());
        if (arrayV.isClassMember()) {
            Variable result = scope.createTempVariable(arrayV.getType());
            if (arrayV.isStatic()) {
                Class theClass = (Class) arrayV.getEnclosingScope();
                TACInstruction staticFieldTAC = genGetStaticField(result, theClass, arrayV);
                program.add(staticFieldTAC);
            } else {
                // this隐式调用 位于对象的实例方法内部可以省略this关键字
                Function caller = at.enclosingFunctionOfNode(ast);
                if (caller == null) return null;
                Variable thisV = variableMap.get(caller.getVariables().get(0));
                TACInstruction fieldTAC = genGetField(result, thisV, arrayV);
                program.add(fieldTAC);
            }
            variableMap.put(arrayV, result);
        }
        arrayV = variableMap.get(arrayV);
        Type curType = arrayV.getType();
        int len = ast.exprList().size();
        Variable result = null;
        for (int i = 0;i < len;i++) {
            if (curType instanceof ArrayType) {
                curType = ((ArrayType) curType).baseType();
            }
            Symbol idx = visitExpr(ast.exprList().get(i));
            if (i == len - 1) {
                ASTNode parent = ast.getParent();
                // 数组调用赋值表达式的左孩子 表示是数组赋值
                if (parent instanceof Expr && parent.firstChild() == ast &&
                        parent.getToken().isAssignOperator()) {
                    tmpData.offer(idx);
                    return arrayV;
                }
            }
            result = scope.createTempVariable(curType);
            TACInstruction tac = new TACInstruction(TACType.ASSIGN,
                    result, arrayV, idx, "=");
            arrayV = result;
            program.add(tac);
        }
        return result;
    }

    private Symbol translateNewEmptyArray(Scope scope, List<Symbol> dimensionList, int d, Type type) {
        if (d == dimensionList.size() - 1) {
            Variable result = scope.createTempVariable(type);
            TACInstruction tac = new TACInstruction(TACType.NEW_ARRAY, result, type,
                    dimensionList.get(d), null);
            program.add(tac);
            return result;
        } else {
            Variable array = scope.createTempVariable(type);
            TACInstruction tac = new TACInstruction(TACType.NEW_ARRAY, array, type,
                    dimensionList.get(d), null);
            program.add(tac);
            Constant initVal = new Constant(PrimitiveType.Integer, 0);
            Variable v1 = scope.createTempVariable(PrimitiveType.Integer);
            TACInstruction v1AssignTAC = new TACInstruction(TACType.ASSIGN, v1, initVal, null, null);
            program.add(v1AssignTAC);
            TACInstruction startLabel = program.addLabel();
            TACInstruction endLabel = program.newLabel();
            Variable v2 = scope.createTempVariable(PrimitiveType.Integer);
            TACInstruction v2AssignTAC = new TACInstruction(TACType.ASSIGN, v2, v1, dimensionList.get(d), "<");
            TACInstruction iffTAC = genIfF(v2, endLabel);
            program.add(v2AssignTAC).add(iffTAC);

            Symbol res = translateNewEmptyArray(scope, dimensionList, d + 1, ((ArrayType)type).baseType());

            TACInstruction arrAssignTAC = new TACInstruction(TACType.ASSIGN, array, v1, res, "=");
            arrAssignTAC.setArrayAssign(true);
            TACInstruction incTAC = new TACInstruction(TACType.ASSIGN, v1, v1,
                    new Constant(PrimitiveType.Integer, 1), "+");
            TACInstruction gotoTAC = genGOTO(startLabel);
            program.add(arrAssignTAC).add(incTAC).add(gotoTAC);

            program.add(endLabel);
            return array;
        }
    }

    // 获取函数的参数值
    private List<Symbol> getArgsSymbol(FunctionCall functionCall) {
        List<Symbol> symbols = new ArrayList<>();
        for (Expr expr : functionCall.exprList().exprList()) {
            symbols.add(visitExpr(expr));
        }
        return symbols;
    }


    @Override
    public Constant visitLiteral(Literal ast) {
        Object rtn = null;
        String text = ast.getText();
        if (ast instanceof Literal.IntegerLiteral) {
            rtn = Long.valueOf(ast.getText());
        } else if (ast instanceof Literal.FloatLiteral) {
            rtn = Double.valueOf(ast.getText());
        } else if (ast instanceof Literal.StringLiteral) {
            rtn = text.substring(1, text.length() - 1);
        } else if (ast instanceof Literal.CharLiteral) {
            rtn = text.charAt(0);
        } else if (ast instanceof Literal.BooleanLiteral) {
            rtn = text.equals("true") ? Boolean.TRUE : Boolean.FALSE;
        } else if (ast instanceof Literal.NullLiteral) {
            rtn = NullObject.instance();
        }
        return new Constant(at.typeOfNode.get(ast), rtn);
    }

    @Override
    public Object visitStatement(Statement ast) {
        if (ast.IF() != null) {
            translateIfStmt(ast);
        } else if (ast.WHILE() != null) {
            translateWhileStmt(ast);
        } else if (ast.FOR() != null) {
            translateForStmt(ast);
        } else if (ast.block() != null) {
            visitBlock(ast.block());
        } else if (ast.RETURN() != null) {
            translateReturnStmt(ast);
        } else if (ast.BREAK() != null) {
            translateBreak();
        } else if (ast.SWITCH() != null) {
        } else if (ast.expr() != null) {
            visitExpr(ast.expr());
        }
        return super.visitStatement(ast);
    }

    @Override
    public Symbol visitParExpression(ParExpression ast) {
        return visitExpr(ast.expr());
    }

    @Override
    public Object visitBlock(Block ast) {
        if (ast.blockStatements() != null) {
            visitBlockStatements(ast.blockStatements());
        }
        return super.visitBlock(ast);
    }

    @Override
    public Object visitForInit(ForControl.ForInit forInit) {
        if (forInit.variableDeclarators() != null) {
            visitVariableDeclarators(forInit.variableDeclarators());
        } else if (forInit.exprList() != null) {
            visitExprList(forInit.exprList());
        }
        return super.visitForInit(forInit);
    }

    @Override
    public Symbol visitExprList(ExprList ast) {
        Symbol symbol = null;
        if (ast.exprList() != null) {
            for (Expr expr : ast.exprList()) {
                symbol = visitExpr(expr);
            }
        }
        return symbol;
    }

    private void translateBreak() {
        TACInstruction breakTAC = genGOTO(currentBreakLabel());
        program.add(breakTAC);
    }

    private void translateReturnStmt(Statement ast) {
        TACInstruction returnTAC = new TACInstruction(TACType.RETURN, null,
                null, null, null);
        if (ast.expr() != null) {
            Symbol rtn = visitExpr(ast.expr());
            returnTAC.setArg1(rtn);
        }
        program.add(returnTAC);
    }

    private void translateForStmt(Statement ast) {
        ForControl forControl = ast.forControl();
        if (forControl.forInit() != null) {
            visitForInit(forControl.forInit());
        }
        TACInstruction startLabel = program.addLabel();
        TACInstruction endLabel = program.newLabel();
        Symbol condition = visitExpr(forControl.expr());
        TACInstruction iffTAC = genIfF(condition, null);
        program.add(iffTAC);
        if (ast.statement(0) != null) {
            pushBreak(endLabel);
            visitStatement(ast.statement(0));
            popBreak();
        }
        visitExprList(forControl.forUpdate());
        TACInstruction gotoTAC = genGOTO(startLabel);
        program.add(gotoTAC);

        program.add(endLabel);

        iffTAC.setArg2(endLabel);
    }

    private void translateWhileStmt(Statement ast) {
        TACInstruction startLabel = program.addLabel();
        TACInstruction endLabel = program.newLabel();

        Symbol condition = visitParExpression(ast.parExpr());
        TACInstruction iffTAC = genIfF(condition, null);
        program.add(iffTAC);
        if (ast.statement(0) != null) {
            pushBreak(endLabel);
            visitStatement(ast.statement(0));
            popBreak();
        }
        TACInstruction gotoTAC = genGOTO(startLabel);
        program.add(gotoTAC);

        program.add(endLabel);
        iffTAC.setArg2(endLabel);
    }

    private void translateIfStmt(Statement ast) {
        Symbol condition = visitParExpression(ast.parExpr());
        TACInstruction iffTAC = genIfF(condition, null);
        program.add(iffTAC);
        TACInstruction gotoTAC = genGOTO(null);
        if (ast.statement(0) != null) {
            visitStatement(ast.statement(0));
            if (ast.statement(1) != null) {
                program.add(gotoTAC);
            }
        }
        if (ast.ELSE() != null && ast.statement(1) != null) {
            TACInstruction elseLabel = program.addLabel();
            iffTAC.setArg2(elseLabel);
            visitStatement(ast.statement(1));
        }
        TACInstruction endLabel = program.addLabel();
        if (ast.ELSE() == null) {
            iffTAC.setArg2(endLabel);
        }
        gotoTAC.setArg1(endLabel);
    }

    private TACInstruction processOpAssign(Expr ast, Object left, Object right,
                                           String op) {
        Scope scope = at.enclosingScopeOfNode(ast);
        Type expectedType = at.typeOfNode.get(ast);
        Object lastLeft = left;
        if (ast.expr(0) instanceof ArrayCall) {
            Variable result = scope.createTempVariable(expectedType);
            TACInstruction tac = new TACInstruction(TACType.ASSIGN,
                    result, left, tmpData.peek(), null);
            program.add(tac);
            left = result;
        }
        Variable tmpResult = scope.createTempVariable(expectedType);
        TACInstruction instruction = new TACInstruction(TACType.ASSIGN, tmpResult, left, right, op);
        program.add(instruction);
        if (ast.expr(0) instanceof ArrayCall) {
            instruction = new TACInstruction(TACType.ASSIGN, (Variable) lastLeft, tmpData.poll(), tmpResult, "=");
            instruction.setArrayAssign(true);
        } else {
            instruction = new TACInstruction(TACType.ASSIGN, (Variable) left, tmpResult, null, "=");
        }
        return instruction;
    }

    private TACInstruction genGOTO(TACInstruction label) {
        return new TACInstruction(TACType.GOTO, null, label, null, null);
    }

    private TACInstruction genIfF(Symbol condition, TACInstruction label) {
        return new TACInstruction(TACType.IF_F, null, condition, label, null);
    }

    private TACInstruction genIfT(Symbol condition, TACInstruction label) {
        return new TACInstruction(TACType.IF_T, null, condition, label, null);
    }

    private TACInstruction genNewArray(Variable result, Type type, int n) {
        return new TACInstruction(TACType.NEW_ARRAY, result, type, n, null);
    }

    private TACInstruction genGetArrayLen(Variable result, Object arg1) {
        return new TACInstruction(TACType.ARRAY_LEN, result, arg1, null, null);
    }

    private TACInstruction genNewInstance(Variable result, Class theClass) {
        return new TACInstruction(TACType.NEW_INSTANCE, result, theClass, null, null);
    }

    private TACInstruction genArg(Symbol arg) {
        return new TACInstruction(TACType.ARG, null, arg, null, null);
    }

    // 调用构造函数
    private TACInstruction genInvokeSpecial(Variable result, Variable classObj, Function function) {
        return new TACInstruction(TACType.INVOKE_SPECIAL, result, classObj, function, null);
    }

    // 调用普通的对象方法
    private TACInstruction genInvokeVirtual(Variable result, Variable classObj, Function function) {
        return new TACInstruction(TACType.INVOKE_VIRTUAL, result, classObj, function, null);
    }

    // 调用静态方法或全局作用域方法
    private TACInstruction genInvokeStatic(Variable result, Class theClass, Function function) {
        return new TACInstruction(TACType.INVOKE_STATIC, result, theClass, function, null);
    }

    private TACInstruction genInvokeStatic(Variable result, Function function) {
        return genInvokeStatic(result, null, function);
    }

    private TACInstruction genGetField(Variable result, Object arg1, Object arg2) {
        return new TACInstruction(TACType.GET_FIELD, result, arg1, arg2, null);
    }

    private TACInstruction genGetStaticField(Variable result, Object arg1, Object arg2) {
        return new TACInstruction(TACType.GET_STATIC_FIELD, result, arg1, arg2, null);
    }

    private TACInstruction genPrint() {
        return new TACInstruction(TACType.PRINT);
    }

    private void pushBreak(TACInstruction label) {
        breakStack.push(label);
    }

    private void popBreak() {
        breakStack.pop();
    }

    private TACInstruction currentBreakLabel() {
        if (breakStack.size() > 0) {
            return breakStack.peek();
        }
        return null;
    }

    private Symbol translateBuiltInFunction(FunctionCall ast) {
        String name = ast.identifier().getText();
        if (BuiltInFunction.getValueByKey(PRINT).equals(name) ||
                BuiltInFunction.getValueByKey(PRINTLN).equals(name)) {
            translatePrint(ast, name.equals(BuiltInFunction.getValueByKey(PRINTLN)));
        } else if (BuiltInFunction.getValueByKey(READ).equals(name)) {
            // todo 处理读
        } else if (BuiltInFunction.getValueByKey(WRITE).equals(name)) {
            // todo 处理写
        } else if (BuiltInFunction.getValueByKey(INT).equals(name)) {
            return translateCast(TACType.CAST_INT, ast);
        } else if (BuiltInFunction.getValueByKey(STR).equals(name)) {
            return translateCast(TACType.CAST_STR, ast);
        } else if (BuiltInFunction.getValueByKey(FLOAT).equals(name)) {
            return translateCast(TACType.CAST_FLOAT, ast);
        } else if (BuiltInFunction.getValueByKey(BYTE).equals(name)) {
            return translateCast(TACType.CAST_BYTE, ast);
        } else if (BuiltInFunction.getValueByKey(STR_LEN).equals(name)){
            return translateStrLen(ast);
        } else if (BuiltInFunction.getValueByKey(STR_AT).equals(name)){
//            List<Object> list = getArgsSymbol(ast);
//            if (list.size() < 2) return null;
//            Object idx = list.get(1);
//            if (idx instanceof Long) {
//                idx = ((Long) idx).intValue();
//            }
//            return String.valueOf(list.get(0)).charAt((Integer) idx);
            return null;
        } else {
            System.out.println("no exist built in function ["+ name + "]' implementation!");
        }
        return null;
    }

    private void translatePrint(FunctionCall ast, boolean isEnter) {
        TACInstruction printTAC = new TACInstruction(TACType.PRINT);
        Symbol symbol = visitExprList(ast.exprList());
        if (symbol != null) {
            printTAC.setArg1(symbol);
            program.add(printTAC);
        }
        if (isEnter) {
            TACInstruction printlnTAC = new TACInstruction(TACType.PRINT);
            printlnTAC.setArg1(new Constant(PrimitiveType.String, "\n"));
            program.add(printlnTAC);
        }
    }

    private Symbol translateCast(TACType tacType, FunctionCall ast) {
        Type type = PrimitiveType.Null;
        if (tacType == TACType.CAST_INT) {
            type = PrimitiveType.Integer;
        } else if (tacType == TACType.CAST_FLOAT) {
            type = PrimitiveType.Float;
        } else if (tacType == TACType.CAST_BYTE) {
            type = PrimitiveType.Byte;
        } else if (tacType == TACType.CAST_STR) {
            type = PrimitiveType.String;
        }
        Symbol target = visitExprList(ast.exprList());
        Scope scope = at.enclosingScopeOfNode(ast);
        Variable tmpVariable = scope.createTempVariable(type);
        TACInstruction cast = new TACInstruction(tacType,
                tmpVariable, target, null, null);
        program.add(cast);
        return tmpVariable;
    }

    private Symbol translateStrLen(FunctionCall ast) {
        Symbol target = visitExprList(ast.exprList());
        Scope scope = at.enclosingScopeOfNode(ast);
        Variable tmpVariable = scope.createTempVariable(PrimitiveType.Integer);
        TACInstruction strLenTAC = new TACInstruction(TACType.STR_LEN,
                tmpVariable, target, null, null);
        program.add(strLenTAC);
        return tmpVariable;
    }
}
