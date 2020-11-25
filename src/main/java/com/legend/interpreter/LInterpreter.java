package com.legend.interpreter;

import com.legend.common.BuiltInFunction;

import com.legend.exception.InterpreterException;
import com.legend.parser.Program;
import com.legend.parser.ast.*;
import com.legend.parser.common.BaseASTVisitor;
import com.legend.semantic.*;
import com.legend.semantic.Class;
import com.legend.semantic.FunctionType;
import com.legend.semantic.PrimitiveType;

import java.util.*;

import static com.legend.common.BuiltInFunction.BuiltIn.*;

/**
 * @author Legend
 * @data by on 20-11-18.
 * @description L-Lang解释器
 */
public class LInterpreter extends BaseASTVisitor<Object> {

    // 带注释的ast
    private AnnotatedTree at;
    // 保存栈帧
    private Stack<StackFrame> stack = new Stack<>();

    public LInterpreter(AnnotatedTree at) {
        this.at = at;
    }

    @Override
    public Object visitProgram(Program ast) {
        Object rtn = null;
        List<BlockStatements> allModules = ast.allBlockStatements();
        for (int i = 0;i < allModules.size();i++) {
            BlockStatements module = allModules.get(i);
            pushStack(new StackFrame((BlockScope) at.scopeOfNode.get(module)));
            if (i == allModules.size() - 1) {
                if (ast.blockStatements() != null) {
                    rtn = visitBlockStatements(module);
                }
                popStack();
            } else {
                // 其它模块执行变量初始化
                initVariableDeclarators(module);
            }
        }
        return rtn;
    }

    private void initVariableDeclarators(BlockStatements blockStatements) {
        List<BlockStatement> list = blockStatements.blockStatements();
        if (list != null) {
            for (BlockStatement b : list) {
                if (b.variableDeclarators() != null) {
                    visitVariableDeclarators(b.variableDeclarators());
                }
            }
        }
    }

    @Override
    public Object visitBlockStatements(BlockStatements ast) {
        Object rtn = null;
        if (ast.blockStatements() != null) {
            for (BlockStatement blockStatement : ast.blockStatements()) {
                rtn = visitBlockStatement(blockStatement);
                if (rtn instanceof BreakObject) {
                    break;
                } else if (rtn instanceof ReturnObject) {
                    break;
                }
            }
        }
        return rtn;
    }

    @Override
    public Object visitBlockStatement(BlockStatement ast) {
        Object rtn = null;
        if (ast.variableDeclarators() != null) {
            rtn = visitVariableDeclarators(ast.variableDeclarators());
        } else if (ast.statement() != null) {
            rtn = visitStatement(ast.statement());
        }
        return rtn;
    }

    @Override
    public Object  visitVariableDeclarators(VariableDeclarators ast) {
        Object rtn = null;
        for (VariableDeclarator declarator : ast.variableDeclaratorList()) {
            rtn = visitVariableDeclarator(declarator);
        }
        return rtn;
    }

    @Override
    public Object visitVariableDeclarator(VariableDeclarator ast) {
        Object rtn = null;
        Variable variable = (Variable) at.symbolOfNode.get(ast.identifier());
        LValue lValue = getLValue(variable);
        if (ast.variableInitializer() != null) {
            rtn = visitVariableInitializer(ast.variableInitializer());
            if (rtn instanceof LValue) {
                rtn = ((LValue) rtn).getValue();
            } else if (rtn instanceof ArrayObject) {
                ArrayObject arrayObject = (ArrayObject) rtn;
                ((ArrayType) variable.getType()).setLength((int) arrayObject.getSize());
                arrayObject.setType((ArrayType) variable.getType());
            }
            lValue.setValue(rtn);
        }
        return rtn;
    }

    @Override
    public Object visitVariableInitializer(VariableInitializer ast) {
        Object rtn = null;
        if (ast.expr() != null) {
            rtn = visitExpr(ast.expr());
        } else if (ast.arrayInitializer() != null) {
            rtn = visitArrayInitializer(ast.arrayInitializer());
        }
        return rtn;
    }

    @Override
    public Object visitArrayInitializer(ArrayInitializer ast)  {
        ArrayObject newArrayObject = new ArrayObject();
        List<Object> list = new ArrayList<>();
        int max = calArraySize(ast);
        if (max == 0) {
            newArrayObject.setLength(0, 0);
            newArrayObject.setObjects(new Object[0]);
            return newArrayObject;
        }
        Object lastObj = null;
        for (VariableInitializer variableInitializer : ast.variableInitializerList()) {
            List<Object> newList = new ArrayList<>();
            Object res = visitVariableInitializer(variableInitializer);
            if (lastObj != null && res.getClass() != lastObj.getClass()) {
                throw new InterpreterException(ast);
            }
            if (res instanceof ArrayObject) {
                // 因为是递归调用,新的ArrayObject需要子ArrayObject的相关信息
                newList.addAll(Arrays.asList(((ArrayObject) res).getObjects()));
                ArrayObject old = (ArrayObject) res;
                for (Map.Entry<Long, Long> entry : old.getDimensionMap().entrySet()) {
                    newArrayObject.setLength(entry.getKey() + 1,
                            Math.max(entry.getValue(), newArrayObject.getLength(entry.getKey() + 1)));
                }
            } else {
                newList.add(res);
            }
            lastObj = res;
            list.addAll(fillDefaultValue(max, newList));
        }
        newArrayObject.setObjects(list.toArray());
        newArrayObject.setLength(0, ast.variableInitializerList().size());
        if (newArrayObject.getDimensionMap().size() >= 3) {
            return processArray(ast, newArrayObject.getDimensionMap(), 0);
        }
        return newArrayObject;
    }


    private Object processArray(ArrayInitializer ast, Map<Long, Long> dimensionMap, int d) {
        ArrayObject arrayObject = new ArrayObject();
        List<Object> list = new ArrayList<>();
        if (d == dimensionMap.size() - 1) {
            if (ast.variableInitializerList() == null) {
                arrayObject.setObjects(new Object[0]);
                return arrayObject;
            }
            for (VariableInitializer initializer : ast.variableInitializerList()) {
                Object res = visitExpr(initializer.expr());
                list.add(res);
            }
            arrayObject.setObjects(list.toArray());
            return arrayObject;
        } else {
            long max = dimensionMap.get((long)d);
            int len = ast.variableInitializerList().size();
            for (int i = 0;i < len;i++) {
                VariableInitializer initializer = ast.variableInitializerList().get(i);
                Object res = processArray(initializer.arrayInitializer(), dimensionMap, d + 1);
                if (!(res instanceof ArrayObject)) {
                    throw new InterpreterException(ast);
                }
                List<Object> tList = new ArrayList<>(Arrays.asList(((ArrayObject) res).getObjects()));
                list.addAll(fillDefaultValue((int) arrayObject.getCount(dimensionMap, d+1, max), tList));
            }
            if (len < max) {
                list.addAll(fillDefaultValue((int) ((max-len)*arrayObject.getCount(dimensionMap, d + 1, max)),
                        new ArrayList<>()));
            }
        }
        arrayObject.setObjects(list.toArray());
        arrayObject.setDimensionMap(dimensionMap);
        return arrayObject;
    }

    private List<Object> fillDefaultValue(int max, List<Object> objects) {
        List<Object> list = new ArrayList<>(objects);
        for (int i = list.size();i < max;i++) {
            list.add(null);
        }
        return list;
    }

    private int calArraySize(ArrayInitializer array) {
        int max = 0;
        if (array == null || array.variableInitializerList() == null) return max;
        for (VariableInitializer initializer : array.variableInitializerList()) {
            if (initializer.arrayInitializer() != null &&
                    initializer.arrayInitializer().variableInitializerList() != null) {
                max = Math.max(initializer.arrayInitializer()
                        .variableInitializerList().size(), max);
            } else {
                max = 1;
            }
        }
        return max;
    }

    @Override
    public Object visitBlock(Block ast) {
        BlockScope scope = (BlockScope) at.scopeOfNode.get(ast);
        if (scope != null) {
            StackFrame frame = new StackFrame(scope);
            pushStack(frame);
        }
        Object rtn = visitBlockStatements(ast.blockStatements());
        if (scope != null) {
            popStack();
        }
        return rtn;
    }

    @Override
    public Object visitExprList(ExprList ast) {
        Object rtn = null;
        if (ast.exprList() != null) {
            for (Expr expr : ast.exprList()) {
                rtn = visitExpr(expr);
            }
        }
        return rtn;
    }

    @Override
    public Object visitExpr(Expr ast) {
        Object rtn = null;
        if (ast instanceof Literal) {
            rtn = visitLiteral((Literal) ast);
        } else if (ast instanceof FunctionCall) {
            rtn = visitFunctionCall((FunctionCall) ast);
        } else if (ast instanceof ArrayCall) {
            rtn = visitArrayCall((ArrayCall) ast);
        } else if (ast instanceof TerminalNode) {
            Symbol symbol = at.symbolOfNode.get(ast);
            if (symbol instanceof Variable) {
                rtn = getLValue((Variable) symbol);
            } else if (symbol instanceof Function) {
                rtn = new FunctionObject((Function) symbol);
            }
        } else if (ast.getAstNodeType() == ASTNodeType.BINARY_EXP) {
            // 处理二元表达式
            rtn = processBinaryExpr(ast);
        } else if (ast.getAstNodeType() == ASTNodeType.UNARY_EXP) {
            Object value = visitExpr(ast.expr(0));
            if (value instanceof LValue) {
                value = ((LValue) value).getValue();
            }
            switch (ast.getToken().getTokenType()) {
                case BANG:
                    rtn = !(Boolean) value;
                    break;
            }
        }
        return rtn;
    }

    @Override
    public Object visitLiteral(Literal ast) {
        Object rtn = null;
        String text = ast.getText();
        if (ast instanceof Literal.IntegerLiteral) {
            rtn = Long.valueOf(ast.getText());
        } else if (ast instanceof Literal.FloatLiteral) {
            rtn = Double.valueOf(ast.getText());
        } else if (ast instanceof Literal.StringLiteral) {
            rtn = text.substring(1, text.length() - 1);
        } else if (ast instanceof Literal.CharLiteral) {
            rtn = text.charAt(1);
        } else if (ast instanceof Literal.BooleanLiteral) {
            rtn = text.equals("true") ? Boolean.TRUE : Boolean.FALSE;
        } else if (ast instanceof Literal.NullLiteral) {
            rtn = NullObject.instance();
        }
        return rtn;
    }

    @Override
    public Object visitStatement(Statement ast) {
        Object rtn = null;
        if (ast.IF() != null) {
            rtn = processIfStmt(ast);
        } else if (ast.WHILE() != null) {
            rtn = processWhileStmt(ast);
        } else if (ast.FOR() != null) {
            rtn = processForStmt(ast);
        } else if (ast.block() != null) {
            rtn = visitBlock(ast.block());
        } else if (ast.RETURN() != null) {
            rtn = processReturnStmt(ast);
        } else if (ast.BREAK() != null) {
            rtn = BreakObject.instance();
        } else if (ast.SWITCH() != null) {

        } else if (ast.expr() != null) {
            // todo
            rtn = visitExpr(ast.expr());
        }
        return rtn;
    }

    @Override
    public Object visitForInit(ForControl.ForInit ast) {
        Object rtn = null;
        if (ast.variableDeclarators() != null) {
            rtn = visitVariableDeclarators(ast.variableDeclarators());
        } else if (ast.exprList() != null) {
            rtn = visitExprList(ast.exprList());
        }
        return rtn;
    }

    @Override
    public Object visitParExpression(ParExpression ast) {
        return visitExpr(ast.expr());
    }

    @Override
    public Object visitFunctionCall(FunctionCall ast) {
        if (ast.THIS() != null) {
            thisConstructor(ast);
            return null;
        } else if (ast.SUPER() != null) {
            thisConstructor(ast);
            return null;
        }
        Object rtn = null;
        String functionName = ast.identifier().getText();
        Symbol symbol = at.symbolOfNode.get(ast);
        if (symbol instanceof DefaultConstructor) {
            return createAndInitClassObject(((DefaultConstructor) symbol).Class());
        } else if (BuiltInFunction.isBuiltInFunc(functionName)) {
            return processBuiltInFunction(ast);
        }
        FunctionObject functionObject = getFunctionObject(ast);
        Function function = functionObject.function;
        if (function.isConstructor()) { // 构造函数返回类对象
            Class theClass = (Class) function.getEnclosingScope();
            ClassObject classObject = createAndInitClassObject(theClass);
            methodCall(classObject, ast, false);
            return classObject;
        }
        List<Object> paramValues = getParamValues(ast);
        rtn = functionCall(functionObject, paramValues);
        return rtn;
    }

    private void thisConstructor(FunctionCall ast) {
        Symbol symbol = at.symbolOfNode.get(ast);
        if (symbol instanceof DefaultConstructor) {
            return;
        } else if (symbol instanceof Function){
            Function function = (Function) symbol;
            FunctionObject functionObject = new FunctionObject(function);
            List<Object> paramValues = getParamValues(ast);
            functionCall(functionObject, paramValues);
        }
    }

    private Object methodCall(ClassObject classObject, FunctionCall ast, boolean isSuper) {
        Object rtn = null;
        StackFrame stackFrame = new StackFrame(classObject);
        pushStack(stackFrame);
        FunctionObject functionObject = getFunctionObject(ast);
        popStack();

        // 方法需要处理重写
        Function function = functionObject.function;
        Class theClass = classObject.type;
        if (!function.isConstructor() && !isSuper) {
            Function override = theClass.getFunction(function.name(), function.paramTypeList());
            if (override != null && override != function) {
                function = override;
                functionObject.setFunction(function);
            }
        }

        List<Object> paramValues = getParamValues(ast);
        pushStack(stackFrame);
        rtn = functionCall(functionObject, paramValues);
        popStack();
        return rtn;
    }

    private Object functionCall(FunctionObject functionObject, List<Object> paramValues) {
        StackFrame stackFrame = new StackFrame(functionObject);
        pushStack(stackFrame);

        FunctionDeclaration ast = (FunctionDeclaration) functionObject.function.getAstNode();
        FunctionDeclaration.FormalParameterList formalParameters = ast.formalParameters();
        if (ast.formalParameters().formalParameterList() != null) {
            List<FunctionDeclaration.FormalParameter> parameterList =
                    formalParameters.formalParameterList();
            for (int i = 0;i < parameterList.size();i++) {
                FunctionDeclaration.FormalParameter parameter = parameterList.get(i);
                Symbol symbol = at.symbolOfNode.get(parameter.identifier());
                LValue lValue = getLValue((Variable) symbol);
                lValue.setValue(paramValues.get(i));
            }
        }
        Object rtn = visitBlock(ast.functionBody());
        if (rtn instanceof ReturnObject) {
            rtn = ((ReturnObject) rtn).returnValue;
        }
        popStack();
        return rtn;
    }

    @Override
    public Object visitArrayCall(ArrayCall ast) {
        String name = ast.identifier().getText();
        Type type = at.lookupType(name);
        if (type != null || ast.identifier().getToken().isBaseType()) { // 实例化一个数组
            ArrayObject arrayObject = new ArrayObject();
            int size = 1;
            for (int i = 0;i < ast.exprList().size();i++) {
                Object value = visitExpr(ast.exprList().get(i));
                if (value instanceof LValue) {
                    value = ((LValue) value).getValue();
                }
                arrayObject.setLength(i, (long) value);
                size *= (long)value;
            }
            arrayObject.setObjects(new Object[size]);
            return arrayObject;
        } else { // 普通的数组调用
            Variable variable = (Variable) at.symbolOfNode.get(ast.identifier());
            LValue lValue = getLValue(variable);
            ArrayObject arrayObject = (ArrayObject) lValue.getValue();
            int len = ast.exprList().size();
            int idx = 0;
            for (int i = 0;i < len;i++) {
                Object value = visitExpr(ast.exprList().get(i));
                if (value instanceof LValue) {
                    value = ((LValue) value).getValue();
                }
                idx += arrayObject.getCount(i, (Long) value);
            }
            if (len == arrayObject.getDimension()) {
                // 获取数组元素
//                System.out.println("获取数组元素------" + idx);
                return new ArrayLValue(variable, arrayObject, idx);
            } else {
                // 获取数组新的对象
//                System.out.println("获取新的数组对象------" + idx);
                return arrayObject.getArrayObject(len - 1, idx);
            }
        }
    }

    @Override
    public Object visitClassBody(ClassDeclaration.ClassBody ast) {
        Object rtn = null;
        if (ast.memberDeclarationList() != null) {
            for (ClassDeclaration.MemberDeclaration member : ast.memberDeclarationList()) {
                rtn = visitMemberDeclaration(member);
            }
        }
        return rtn;
    }

    @Override
    public Object visitMemberDeclaration(ClassDeclaration.MemberDeclaration ast) {
        Object rtn = null;
        if (ast.variableDeclarators() != null) {
            rtn = visitVariableDeclarators(ast.variableDeclarators());
        }
        return rtn;
    }


    // 获取函数的参数值
    private List<Object> getParamValues(FunctionCall functionCall) {
        List<Object> values = new ArrayList<>();
        for (Expr expr : functionCall.exprList().exprList()) {
            Object value = visitExpr(expr);
            if (value instanceof LValue) {
                value = ((LValue) value).getValue();
            }
            values.add(value);
        }
        return values;
    }

    /**
     * 根据函数调用ast获取函数对象
     * 1.函数类型的变量直接返回变量对应的函数对象
     * 2.普通函数直接创建一个新的函数对象
     * @param ast 函数调用ast
     * @return 函数对象
     */
    private FunctionObject getFunctionObject(FunctionCall ast) {
        if (ast.identifier() == null) return null;
        FunctionObject functionObject = null;
        Function function = null;
        Symbol symbol = at.symbolOfNode.get(ast);
        if (symbol instanceof Variable) {
            LValue lValue = getLValue((Variable) symbol);
            Object value = lValue.getValue();
            if (value instanceof FunctionObject) {
                functionObject = (FunctionObject) value;
                function = functionObject.function;
            }
        } else if (symbol instanceof Function) {
            function = (Function) symbol;
        } else {
            String text = ast.identifier().getText();
            at.log("unable find a function or function variable " + text, ast);
            return null;
        }
        if (functionObject == null) {
            functionObject = new FunctionObject(function);
        }
        return functionObject;
    }

    private void println(FunctionCall ast) {
        println(ast, true);
    }

    private void println(FunctionCall ast, boolean isEnter) {
        Object value = visitExprList(ast.exprList());
        if (value != null) {
            if (value instanceof LValue) {
                value = ((LValue) value).getValue();
            }
            if (isEnter) {
                System.out.println(value);
            } else {
                System.out.print(value);
            }
        } else {
            System.out.println();
        }
    }

    // 栈帧入栈
    private void pushStack(StackFrame newFrame) {
        if (stack.size() > 0) {
            // 从栈顶依次查找
            for (int i = stack.size() - 1;i > 0;i--) {
                StackFrame frame = stack.get(i);
                if (frame.scope.getEnclosingScope() == newFrame.scope.getEnclosingScope()) {
                    // 同级作用域(同级函数调用)
                    newFrame.parentFrame = frame.parentFrame;
                    break;
                } else if (frame.scope == newFrame.scope.getEnclosingScope()) {
                    // 下级作用域 (if、for等语句块)
                    newFrame.parentFrame = frame;
                    break;
                } else if (newFrame.object instanceof FunctionObject) {
                    // 函数对象需要用接收变量的作用域的来判断
                    FunctionObject functionObject = (FunctionObject) newFrame.object;
                    if (functionObject.receiver != null &&
                        functionObject.receiver.getEnclosingScope() == frame.scope) {
                        newFrame.parentFrame = frame;
                        break;
                    }
                }
            }
            if (newFrame.parentFrame == null) {
                newFrame.parentFrame = stack.peek();
            }
        }
        stack.push(newFrame);
    }

    private void popStack() {
        stack.pop();
                                                    }

    public LValue getLValue(Variable variable) {
        StackFrame frame = stack.peek();
        LObject valueContainer = null;
        while (frame != null) {
            if (frame.scope.containsSymbol(variable)) {
                valueContainer = frame.object;
                break;
            }
            frame = frame.parentFrame;
        }
        if (valueContainer == null) {
            frame = stack.peek();
            // 正常作用域找不到 尝试从闭包变量中找
            while (frame != null) {
                if (frame.contains(variable)) {
                    valueContainer = frame.object;
                    break;
                }
                frame = frame.parentFrame;
            }
        }
        return new MyLValue(valueContainer, variable);
    }

    /**
     * 将闭包变量打包到容器中
     * @param function 目标函数
     * @param container 存放运行时变量的容器
     */
    private void packageClosureValues(Function function, LObject container) {
        if (function.getClosureVariables() != null) {
            for (Variable variable :function.getClosureVariables()) {
                LValue lValue = getLValue(variable);
                Object value = lValue.getValue();
                container.fields.put(variable, value);
            }
        }
    }

    /**
     * 为类中函数类型的变量对应的函数对象打包闭包变量,
     * 一个闭包变量可能被多个函数共享, 所以要将所有的闭包变量打包到类对象中
     * @param classObject 类对象
     */
    private void packageClosureValues(ClassObject classObject) {
        LObject container = new LObject();
        for (Variable variable : classObject.fields.keySet()) {
            if (variable.getType() instanceof FunctionType) {
                Object object = classObject.fields.get(variable);
                if (object != null) {
                    FunctionObject functionObject = (FunctionObject) object;
                    packageClosureValues(functionObject.function, container);
                }
            }
        }
        classObject.fields.putAll(container.fields);
    }

    private ClassObject createAndInitClassObject(Class theClass) {
        ClassObject object = new ClassObject();
        object.type = theClass;
        Stack<Class> stack = new Stack<>();
        stack.push(theClass);
        while (theClass.getParentClass() != null) {
            theClass = theClass.getParentClass();
            stack.push(theClass);
        }
        StackFrame stackFrame = new StackFrame(object);
        pushStack(stackFrame);
        while (!stack.isEmpty()) {
            Class clazz = stack.pop();
            defaultClassObjectInit(clazz, object);
        }
        popStack();
        return object;
    }

    private void defaultClassObjectInit(Class clazz, ClassObject object) {
        for (Symbol symbol : clazz.getSymbols()) {
            if (symbol instanceof Variable) {
                // 变量初始化为null
                object.fields.put((Variable) symbol, null);
            }
        }
        ClassDeclaration.ClassBody classBody = ((ClassDeclaration)clazz.getAstNode()).classBody();
        visitClassBody(classBody);
    }

    private Object processReturnStmt(Statement ast) {
        Object rtn = null;
        if (ast.expr() != null) {
            rtn = visitExpr(ast.expr());
            if (rtn instanceof LValue) {
                rtn = ((LValue) rtn).getValue();
            }
            // 打包涉及到的闭包变量
            if (rtn instanceof FunctionObject) {
                FunctionObject functionObject = (FunctionObject) rtn;
                packageClosureValues(functionObject.function, functionObject);
            } else if (rtn instanceof ClassObject) {
                ClassObject classObject = (ClassObject) rtn;
                packageClosureValues(classObject);
            }
        }
        rtn = new ReturnObject(rtn);
        return rtn;
    }

    private Object processForStmt(Statement ast) {
        Object rtn = null;
        BlockScope scope = (BlockScope) at.scopeOfNode.get(ast);
        StackFrame frame = new StackFrame(scope);
        pushStack(frame);
        ForControl forControl = ast.forControl();
        // 执行一次初始化语句
        if (forControl.forInit() != null) {
            rtn = visitForInit(forControl.forInit());
        }
        while (true) {
            Boolean condition = true;
            if (forControl.expr() != null) {
                Object value = visitExpr(forControl.expr());
                if (value instanceof LValue) {
                    condition = (Boolean) ((LValue) value).getValue();
                } else {
                    condition = (Boolean) value;
                }
            }
            if (!condition) break;
            if (ast.statement(0) != null) {
                rtn = visitStatement(ast.statement(0));
            }
            if (rtn instanceof BreakObject) {
                rtn = null;
                break;
            } else if (rtn instanceof ReturnObject) {
                break;
            }
            rtn = visitExprList(forControl.forUpdate());
        }
        popStack();
        return rtn;
    }

    private Object processWhileStmt(Statement ast) {
        Object rtn = null;
        while (true) {
            Boolean condition;
            Object value = visitParExpression(ast.parExpr());
            if (value instanceof LValue) {
                condition = (Boolean) ((LValue) value).getValue();
            } else {
                condition = (Boolean)value;
            }
            if (!condition) break;
            if (ast.statement(0) != null) {
                rtn = visitStatement(ast.statement(0));
            }
            if (rtn instanceof BreakObject) {
                rtn = null;
                break;
            } else if (rtn instanceof ReturnObject) {
                break;
            }
        }
        return rtn;
    }

    private Object processIfStmt(Statement ast) {
        Object rtn = null;
        Object condition = visitParExpression(ast.parExpr());
        if (condition instanceof LValue) {
            condition = ((LValue) condition).getValue();
        }
        if (condition == Boolean.TRUE && ast.statement(0) != null) {
            rtn = visitStatement(ast.statement(0));
        } else if (condition == Boolean.FALSE && ast.ELSE() != null){
            rtn = visitStatement(ast.statement(1));
        }
        return rtn;
    }

    private Object processBinaryExpr(Expr ast) {
        if (ast.isDotExpr()) {
            return processDotExpr(ast);
        }
        Object rtn = null;
        Object left = visitExpr(ast.expr(0));
        Object right = visitExpr(ast.expr(1));
        Object leftValue = left;
        Object rightValue = right;
        if (leftValue instanceof LValue) {
            leftValue = ((LValue) leftValue).getValue();
        }
        if (rightValue instanceof LValue) {
            rightValue = ((LValue) rightValue).getValue();
        }
        Type expectedType = at.typeOfNode.get(ast);
        Type type1 = at.typeOfNode.get(ast.expr(0));
        Type type2 = at.typeOfNode.get(ast.expr(1));
        switch (ast.getToken().getTokenType()) {
            case ADD:
                rtn = BinExprCalculate.add(leftValue, rightValue, expectedType);
                break;
            case SUB:
                rtn = BinExprCalculate.sub(leftValue, rightValue, expectedType);
                break;
            case MUL:
                rtn = BinExprCalculate.mul(leftValue, rightValue, expectedType);
                break;
            case DIV:
                rtn = BinExprCalculate.div(leftValue, rightValue, expectedType);
                break;
            case MOD:
                rtn = BinExprCalculate.mod(leftValue, rightValue, expectedType);
                break;
            case LT:
                rtn = BinExprCalculate.lt(leftValue, rightValue, PrimitiveType.getUpperType(type1, type2));
                break;
            case LE:
                rtn = BinExprCalculate.le(leftValue, rightValue, PrimitiveType.getUpperType(type1, type2));
                break;
            case GT:
                rtn = BinExprCalculate.gt(leftValue, rightValue, PrimitiveType.getUpperType(type1, type2));
                break;
            case GE:
                rtn = BinExprCalculate.ge(leftValue, rightValue, PrimitiveType.getUpperType(type1, type2));
                break;
            case EQUAL:
                rtn = BinExprCalculate.eq(leftValue, rightValue, PrimitiveType.getUpperType(type1, type2));
                break;
            case NOT_EQUAL:
                rtn = !BinExprCalculate.eq(leftValue, rightValue, PrimitiveType.getUpperType(type1, type2));
                break;
            case LOGIC_OR:
                rtn = (Boolean)leftValue || (Boolean)rightValue;
                break;
            case LOGIC_AND:
                rtn = (Boolean)leftValue && (Boolean)rightValue;
                break;
            case BIT_AND:
                rtn = BinExprCalculate.bitAnd(leftValue, rightValue, PrimitiveType.getUpperType(type1, type2));
                break;
            case BIT_OR:
                rtn = BinExprCalculate.bitOr(leftValue, rightValue, PrimitiveType.getUpperType(type1, type2));
                break;
            case XOR:
                rtn = BinExprCalculate.xor(leftValue, rightValue, PrimitiveType.getUpperType(type1, type2));
                break;
            case LSHIFT:
                rtn = BinExprCalculate.leftShift(leftValue, rightValue, PrimitiveType.getUpperType(type1, type2));
                break;
            case RSHIFT:
                rtn = BinExprCalculate.rightShift(leftValue, rightValue, PrimitiveType.getUpperType(type1, type2));
                break;
            case ASSIGN:
                if (left instanceof LValue) {
                    ((LValue) left).setValue(rightValue);
                } else {
                    System.out.println("Unsupported assignment operation!");
                    System.out.println(left);
                }
                break;
            case ADD_ASSIGN:
                rtn = BinExprCalculate.add(leftValue, rightValue, expectedType);
                if (left instanceof LValue) {
                    ((LValue) left).setValue(rtn);
                }
                break;
            case SUB_ASSIGN:
                rtn = BinExprCalculate.sub(leftValue, rightValue, expectedType);
                if (left instanceof LValue) {
                    ((LValue) left).setValue(rtn);
                }
                break;
            case MUL_ASSIGN:
                rtn = BinExprCalculate.mul(leftValue, rightValue, expectedType);
                if (left instanceof LValue) {
                    ((LValue) left).setValue(rtn);
                }
                break;
            case DIV_ASSIGN:
                rtn = BinExprCalculate.div(leftValue, rightValue, expectedType);
                if (left instanceof LValue) {
                    ((LValue) left).setValue(rtn);
                }
                break;
            case LSHIFT_ASSIGN:
                rtn = BinExprCalculate.leftShift(leftValue, rightValue, expectedType);
                if (left instanceof LValue) {
                    ((LValue) left).setValue(rtn);
                }
                break;
            case RSHIFT_ASSIGN:
                rtn = BinExprCalculate.rightShift(leftValue, rightValue, expectedType);
                if (left instanceof LValue) {
                    ((LValue) left).setValue(rtn);
                }
                break;
            case DOT:
                rtn = processDotExpr(ast);
                break;
        }
        return rtn;
    }

    private Object processDotExpr(Expr ast) {
        Object rtn = null;
        Object left = visitExpr(ast.expr(0));
        Object leftValue = left;
        if (leftValue instanceof LValue) {
            leftValue = ((LValue) leftValue).getValue();
        }
        if (!(left instanceof LValue) && !(left instanceof ArrayObject)) {
            if (left == null && ast.functionCall() != null) {
                rtn = visitFunctionCall(ast.functionCall());
            } else if (left == null && at.isModuleName(ast.expr(0).getText())){
                Variable variable = (Variable) at.symbolOfNode.get(ast);
                return getLValue(variable);
            } else if (left != null){
                System.out.println("Expecting an Object Reference.");
            }
        } else {
            if (leftValue instanceof ClassObject) {
                ClassObject classObject = (ClassObject) leftValue;
                Variable leftVar = (Variable) at.symbolOfNode.get(ast.expr(0));
                if (ast.functionCall() != null) {
                    rtn = methodCall(classObject, ast.functionCall(), leftVar instanceof Variable.Super);
                } else if (ast.arrayCall() != null) {
                    rtn = visitArrayCall(ast.arrayCall());
                } else {
                    String idName = ast.getChild(1).getText();
                    Variable variable = (Variable) at.symbolOfNode.get(ast);
                    if (!(variable instanceof Variable.This)
                            && !(variable instanceof Variable.Super)) {
                        variable = at.lookupVariable(classObject.type, idName);
                    }
                    rtn = new MyLValue(classObject, variable);
                }
            } else if (leftValue instanceof ArrayObject) {
                ArrayObject arrayObject = (ArrayObject) leftValue;
                rtn = arrayObject.getLength(0);
            }
        }
        return rtn;
    }

    private Object processBuiltInFunction(FunctionCall ast) {
        String name = ast.identifier().getText();
        if (BuiltInFunction.getValueByKey(PRINT).equals(name) ||
                BuiltInFunction.getValueByKey(PRINTLN).equals(name)) {
            println(ast, name.equals(BuiltInFunction.getValueByKey(PRINTLN)));
        } else if (BuiltInFunction.getValueByKey(READ).equals(name)) {
            // todo 处理读
        } else if (BuiltInFunction.getValueByKey(WRITE).equals(name)) {
            // todo 处理写
        } else if (BuiltInFunction.getValueByKey(INT).equals(name)) {
            Object value = visitExprList(ast.exprList());
            if (value instanceof LValue) {
                value = ((LValue) value).getValue();
            }
            if (value instanceof Double) {
                return ((Double) value).longValue();
            } else if (value instanceof Float) {
                return ((Float) value).longValue();
            }
            return Long.parseLong(String.valueOf(value));
        } else if (BuiltInFunction.getValueByKey(STR).equals(name)) {
            Object value = visitExprList(ast.exprList());
            if (value instanceof LValue) {
                value = ((LValue) value).getValue();
            }
            return String.valueOf(value);
        } else if (BuiltInFunction.getValueByKey(FLOAT).equals(name)) {
            Object value = visitExprList(ast.exprList());
            if (value instanceof LValue) {
                value = ((LValue) value).getValue();
            }
            return Double.parseDouble(String.valueOf(value));
        } else if (BuiltInFunction.getValueByKey(BYTE).equals(name)) {
            Object value = visitExprList(ast.exprList());
            if (value instanceof LValue) {
                value = ((LValue) value).getValue();
            }
            return Byte.parseByte(String.valueOf(value));
        } else if (BuiltInFunction.getValueByKey(STR_LEN).equals(name)){
            Object value = visitExprList(ast.exprList());
            if (value instanceof LValue) {
                value = ((LValue) value).getValue();
            }
            return String.valueOf(value).length();
        } else if (BuiltInFunction.getValueByKey(STR_AT).equals(name)){
            List<Object> list = getParamValues(ast);
            if (list.size() < 2) return null;
            Object idx = list.get(1);
            if (idx instanceof Long) {
                idx = ((Long) idx).intValue();
            }
            return String.valueOf(list.get(0)).charAt((Integer) idx);
        } else {
            System.out.println("no exist built in function ["+ name + "]' implementation!");
        }
        return null;
    }
}
