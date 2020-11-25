package com.legend.parser.ast;

import com.legend.exception.ParseException;
import com.legend.lexer.TokenType;
import com.legend.parser.common.PeekTokenIterator;

import java.util.List;

/**
 * @author Legend
 * @data by on 20-11-12.
 * @description 函数声明
 */
public class FunctionDeclaration extends ASTNode {

    public FunctionDeclaration() {
        this.astNodeType = ASTNodeType.FUNCTION_DECLARATION;
    }

    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        FunctionDeclaration func = new FunctionDeclaration();
        TypeTypeOrVoid type = (TypeTypeOrVoid) TypeTypeOrVoid.parse(it);
        if (!it.topIsEqual("(")) { // 普通函数
            func.addChild(type);
            TerminalNode funcName = new TerminalNode(it.nextMatch(TokenType.IDENTIFIER));
            func.addChild(funcName);
        } else { // 构造函数
            TerminalNode funcName = null;
            if (type != null) {
                funcName = type.typeType().classOrInterfaceType().identifiers().get(0);
            }
            func.addChild(funcName);
        }
        ASTNode formalList = FormalParameterList.parse(it);
        func.addChild(formalList);

        if (it.topIsEqual(TokenType.SEMICOLON)) {
            it.nextMatch(TokenType.SEMICOLON);
        } else if (it.topIsEqual("{")){
            ASTNode block = Block.parse(it);
            func.addChild(block);
        }
        return func;
    }

    public TypeTypeOrVoid typeTypeOrVoid() {
        return getASTNode(TypeTypeOrVoid.class);
    }

    public TerminalNode funcName() {
        return getTerminalNode(TokenType.IDENTIFIER);
    }

    public FormalParameterList formalParameters() {
        return getASTNode(FormalParameterList.class);
    }

    public Block functionBody() {
        return getASTNode(Block.class);
    }

    static public class FormalParameterList extends ASTNode {
        public FormalParameterList() {
            this.astNodeType = ASTNodeType.FORMAL_LIST;
        }

        public static ASTNode parse(PeekTokenIterator it) throws ParseException {
            FormalParameterList parameterList = new FormalParameterList();
            it.nextMatch("(");
            ASTNode parameter;
            while (!it.topIsEqual(")") &&
                   (parameter = FormalParameter.parse(it)) != null) {
                parameterList.addChild(parameter);
                if (it.topIsEqual(",")) {
                    it.nextMatch(",");
                }
            }
            it.nextMatch(")");
            return parameterList;
        }

        public List<FormalParameter> formalParameterList() {
            return getASTNodes(FormalParameter.class);
        }
    }

    static public class FormalParameter extends ASTNode {

        private boolean isVariableParameter = false;

        public FormalParameter() {
            this.astNodeType = ASTNodeType.FORMAL_PARAMETER;
        }

        public static ASTNode parse(PeekTokenIterator it) throws ParseException {
            FormalParameter parameter = new FormalParameter();
            ASTNode typeType = TypeType.parse(it);
            parameter.addChild(typeType);
            int count = 0;
            while (it.topIsEqual(TokenType.DOT)) {
                it.nextMatch(TokenType.DOT);
                count++;
            }
            if (count == 3) {
                parameter.setVariableParameter(true);
            }
            TerminalNode parameterName = new TerminalNode(it.nextMatch(TokenType.IDENTIFIER));
            parameter.addChild(parameterName);
            if (parameter.isVariableParameter() &&
                !it.topIsEqual(TokenType.RIGHT_PAREN)) {
                throw new ParseException(it.peek());
            }
            return parameter;
        }

        public TypeType typeType() {
            return getASTNode(TypeType.class);
        }

        public TerminalNode identifier() {
            return getTerminalNode(TokenType.IDENTIFIER);
        }

        public void setVariableParameter(boolean flag) {
            isVariableParameter = flag;
        }

        public boolean isVariableParameter() {
            return isVariableParameter;
        }

        @Override
        public void dumpAST(String indent) {
            System.out.println(indent + this.astNodeType + "(" + toString() + ")");
            for (ASTNode node : getChildren()) {
                if (node instanceof TerminalNode) {
                    String extra = isVariableParameter ? "variable":"";
                    System.out.println("\t" + indent +
                            node.astNodeType + "(" + node.getText() + ")" + "["+ extra +"]");
                } else {
                    node.dumpAST("\t" + indent);
                }
            }
        }
    }
}
