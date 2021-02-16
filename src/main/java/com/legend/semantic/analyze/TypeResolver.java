package com.legend.semantic.analyze;

import com.legend.parser.ast.*;
import com.legend.parser.ast.FunctionType;
import com.legend.parser.common.BaseASTListener;
import com.legend.semantic.*;
import com.legend.semantic.Class;
import com.legend.semantic.PrimitiveType;


/**
 * @author Legend
 * @data by on 20-11-15.
 * @description 类型消解
 *  把变量、类继承、函数声明的类型都解析出来
 */
public class TypeResolver extends BaseASTListener {

    private AnnotatedTree at = null;

    public TypeResolver(AnnotatedTree at) {
        this.at = at;
    }

    @Override
    public void exitVariableDeclarators(VariableDeclarators ast) {
        Type type = at.typeOfNode.get(ast.typeType());
        // int a = 1, b, c = 5;
        for (VariableDeclarator v : ast.variableDeclaratorList()) {
            Variable variable = (Variable) at.symbolOfNode.get(v.identifier());
            variable.setStatic(ast.STATIC() != null);
            if (variable.isClassMember() && variable.isStatic()) {
                ((Class)variable.getEnclosingScope()).addStatic(variable);
            }
            variable.setType(type);
            at.typeOfNode.put(v.identifier(), type);
        }
    }

    @Override
    public void exitVariableDeclarator(VariableDeclarator ast) {
        processId(ast.identifier());
    }

    @Override
    public void exitTerminalNode(TerminalNode ast) {
        if (ast.getParent() instanceof FunctionDeclaration.FormalParameter) {
            // 因为id统一采用终端节点, 函数形参需进行处理
            processId(ast);
        }
    }

//    private void processId(TerminalNode id) {
//        String name = id.getText();
//        Scope scope = at.enclosingScopeOfNode(id);
//        // 两种情况下新建变量 1.变量声明 2.函数参数
//        Variable variable = new Variable(name, scope, id);
//        at.symbolOfNode.put(id, variable);
//    }

    private void processId(TerminalNode id) {
        String name = id.getText();
        Scope scope = at.enclosingScopeOfNode(id);
        if (Scope.getVariable(scope, name) != null) {
            at.log("Variable or parameter already Declared:" + name, id);
        }
        Variable variable = new Variable(name, scope, id);
        at.symbolOfNode.put(id, variable);
        // 引用消解时再将变量放入作用域的符号表中
        if (variable.isClassMember() && variable.isStatic()) {
            ((Class)scope).addStatic(variable);
        } else {
            scope.addSymbol(variable);
        }
    }

    @Override
    public void exitFormalParameter(FunctionDeclaration.FormalParameter ast) {
        Type type = at.typeOfNode.get(ast.typeType());
        Variable variable = (Variable) at.symbolOfNode.get(ast.identifier());
        variable.setType(type);

        Scope scope = at.enclosingScopeOfNode(ast);
        if (scope instanceof Function) {
            ((Function) scope).getVariables().add(variable);
        }
    }

    @Override
    public void exitFunctionDeclaration(FunctionDeclaration ast) {
        Function function = (Function) at.scopeOfNode.get(ast);
        if (ast.typeTypeOrVoid() != null) {
            function.setReturnType(at.typeOfNode.get(ast.typeTypeOrVoid()));
        } else {
            // todo 构造函数的返回值类型应该是类本身
        }
        Scope scope = at.enclosingScopeOfNode(ast);
        Function found = at.lookupFunction(scope, function.name(), function.paramTypeList());
        if (found != null && found != function) {
            at.log("Function or method already Declared:" + ast.getText(), ast);
        }
    }

    @Override
    public void enterClassDeclaration(ClassDeclaration ast) {
        Class theClass = (Class) at.scopeOfNode.get(ast);
        if (ast.EXTENDS() != null) {
            String name = ast.typeType().getText();
            Class parentClass = (Class) at.lookupType(name);
            if (parentClass != null) {
                theClass.setParentClass(parentClass);
            } else {
                at.log("unknown class: " + name , ast);
            }
        }
    }

    @Override
    public void exitTypeTypeOrVoid(TypeTypeOrVoid ast) {
        if (ast.VOID() != null) {
            at.typeOfNode.put(ast, VoidType.instance());
        } else if (ast.typeType() != null) {
            at.typeOfNode.put(ast, at.typeOfNode.get(ast.typeType()));
        }
    }

    @Override
    public void exitTypeType(TypeType ast) {
        Type type = null;
        if (ast.classOrInterfaceType() != null) {
            type = at.typeOfNode.get(ast.classOrInterfaceType());
        } else if (ast.functionType() != null) {
            type = at.typeOfNode.get(ast.functionType());
        } else if (ast.primitiveType() != null) {
            type = at.typeOfNode.get(ast.primitiveType());
        }
        if (ast.leftParen() != null) {
            // 数组类型处理
            for (int i = 0;i < ast.leftParen().size();i++) {
                ArrayType arrayType = new ArrayType();
                arrayType.setBaseType(type);
                type = arrayType;
            }
        }
        at.typeOfNode.put(ast, type);
    }

    @Override
    public void enterClassOrInterfaceType(ClassOrInterfaceType ast) {
        if (ast.identifiers() != null) {
            Scope scope = at.enclosingScopeOfNode(ast);
            Class theClass = null;
            for (int i = 0;i < ast.identifiers().size();i++) {
                String name = ast.identifiers().get(i).getText();
                if (at.isModuleName(name)) {
                    scope = at.lookupModuleScope(name);
                } else {
                    if (theClass == null) {
                        theClass = at.lookupClass(scope, name);
                    } else {
                        theClass = theClass.getClass(name);
                    }
                }
            }
            at.typeOfNode.put(ast, theClass);
        }
    }

    @Override
    public void exitFunctionType(FunctionType ast) {
        DefaultFunctionType functionType = new DefaultFunctionType();
        functionType.setReturnType(at.typeOfNode.get(ast.typeTypeOrVoid()));
        if (ast.typeList().typeTypeList() != null) {
            TypeList typeList = ast.typeList();
            for (TypeType type : typeList.typeTypeList()) {
                Type t = at.typeOfNode.get(type);
                functionType.paramTypeList().add(t);
            }
        }
        at.types.add(functionType);
        at.typeOfNode.put(ast, functionType);
    }

    @Override
    public void exitPrimitiveType(com.legend.parser.ast.PrimitiveType ast) {
        Type type = null;
        if (ast.BYTE() != null) {
            type = PrimitiveType.Byte;
        } else if (ast.CHAR() != null) {
            type = PrimitiveType.Char;
        } else if (ast.BOOLEAN() != null) {
            type = PrimitiveType.Boolean;
        } else if (ast.INT() != null) {
            type = PrimitiveType.Integer;
        } else if (ast.FLOAT() != null) {
            type = PrimitiveType.Float;
        } else if (ast.STRING() != null) {
            type = PrimitiveType.String;
        }
        at.typeOfNode.put(ast, type);
    }
}
