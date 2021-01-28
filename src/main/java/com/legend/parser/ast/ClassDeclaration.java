package com.legend.parser.ast;

import com.legend.exception.ParseException;
import com.legend.lexer.Keyword;
import com.legend.lexer.Token;
import com.legend.lexer.TokenType;
import com.legend.parser.common.PeekTokenIterator;
import com.legend.parser.common.PeekUtils;

import java.util.ArrayList;
import java.util.List;

import static com.legend.lexer.Keyword.Key.*;

/**
 * @author Legend
 * @data by on 20-11-11.
 * @description 类声明
 */
public class ClassDeclaration extends ASTNode {

    public ClassDeclaration() {
        this.astNodeType = ASTNodeType.CLASS_DECLARATION;
    }

    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        ClassDeclaration classDeclaration = new ClassDeclaration();
        TerminalNode classNode = new TerminalNode(it.nextMatch(CLASS));
        classDeclaration.addChild(classNode);
        TerminalNode className = new TerminalNode(it.nextMatch(TokenType.IDENTIFIER));
        classDeclaration.addChild(className);

        if (it.topIsEqual(EXTENDS)) {
            Token extendToken = it.nextMatch(EXTENDS);
            TerminalNode extend = new TerminalNode(extendToken);
            classDeclaration.addChild(extend);
            TypeType typeType = (TypeType) TypeType.parse(it);
            if (typeType == null|| typeType.classOrInterfaceType() == null) {
                throw new ParseException(extendToken);
            }
            classDeclaration.addChild(typeType);
        }
        if (it.topIsEqual(IMPLEMENTS)) {
            Token implementToken = it.nextMatch(IMPLEMENTS);
            TerminalNode implement = new TerminalNode(implementToken);
            classDeclaration.addChild(implement);
            TypeList typeList = (TypeList) TypeList.parse(it);
            if (typeList == null|| typeList.typeTypeList() == null) {
                throw new ParseException(implementToken);
            }
            classDeclaration.addChild(typeList);
        }
        ASTNode classBody = ClassBody.parse(className.getText(), it);
        classDeclaration.addChild(classBody);
        return classDeclaration;
    }

    public TerminalNode className() {
        return getTerminalNode(TokenType.IDENTIFIER);
    }

    public ClassBody classBody() {
        return getASTNode(ClassBody.class);
    }

    public TerminalNode EXTENDS() {
        return getTerminalNode(Keyword.getValueByKey(EXTENDS));
    }

    public TerminalNode IMPLEMENTS() {
        return getTerminalNode(Keyword.getValueByKey(IMPLEMENTS));
    }

    public TypeType typeType() {
        return getASTNode(TypeType.class);
    }

    public TypeList typeList() {
        return getASTNode(TypeList.class);
    }

    public static class ClassBody extends ASTNode {
        public ClassBody() {
            this.astNodeType = ASTNodeType.CLASS_BODY;
        }

        public static ASTNode parse(String className, PeekTokenIterator it) throws ParseException {
            ClassBody classBody = new ClassBody();
            it.nextMatch("{");
            ASTNode declaration;
            while ((declaration = MemberDeclaration.parse(it)) != null) {
                classBody.addChild(declaration);
            }
            it.nextMatch("}");
            if (classBody.memberDeclarationList() != null) {
                List<VariableDeclarators> membersInit = new ArrayList<>();
                List<VariableDeclarators> staticMembersInit = new ArrayList<>();
                for (MemberDeclaration member : classBody.memberDeclarationList()) {
                    if (member.variableDeclarators() != null) {
                        if (member.variableDeclarators().STATIC() != null) {
                            staticMembersInit.add(member.variableDeclarators());
                        } else {
                            membersInit.add(member.variableDeclarators());
                        }
                    }
                }
                classBody.addChild(createInitFuncMember(membersInit));
                classBody.addChild(createStaticInitFuncMember(className, staticMembersInit));
            }
            return classBody;
        }

        public List<MemberDeclaration> memberDeclarationList() {
            return getASTNodes(MemberDeclaration.class);
        }
    }

    public static class MemberDeclaration extends ASTNode {
        public MemberDeclaration() {
            this.astNodeType = ASTNodeType.MEMBER_DECLARATION;
        }

        public static ASTNode parse(PeekTokenIterator it) throws ParseException {
            MemberDeclaration member = new MemberDeclaration();
            if (PeekUtils.isVariableDeclaration(it)) {
                ASTNode variableDeclaration = VariableDeclarators.parse(it);
                if (it.topIsEqual(TokenType.SEMICOLON)) {
                    it.nextMatch(TokenType.SEMICOLON);
                }
                member.addChild(variableDeclaration);
            } else if (PeekUtils.isFuncDeclaration(it)) {
                ASTNode funcDeclaration = FunctionDeclaration.parse(it);
                member.addChild(funcDeclaration);
            } else if (it.topIsEqual(CLASS)) {
                ASTNode classDeclaration = ClassDeclaration.parse(it);
                member.addChild(classDeclaration);
            }
            if (member.getChildren().size() == 0) {
                return null;
            }
            return member;
        }

        public FunctionDeclaration functionDeclaration() {
            return getASTNode(FunctionDeclaration.class);
        }

        public VariableDeclarators variableDeclarators() {
            return getASTNode(VariableDeclarators.class);
        }

        public ClassDeclaration classDeclaration() {
            return getASTNode(ClassDeclaration.class);
        }
    }

    // 创建类的init函数, 用来初始化类成员
    private static MemberDeclaration createInitFuncMember(List<VariableDeclarators> variableDeclaratorsList) {
        MemberDeclaration initMemberFunc = new MemberDeclaration();
        FunctionDeclaration func = new FunctionDeclaration();
        TypeTypeOrVoid typeTypeOrVoid = new TypeTypeOrVoid();
        typeTypeOrVoid.addChild(new TerminalNode(new Token(TokenType.KEYWORD, Keyword.getValueByKey(VOID))));
        Token nameNode = new Token(TokenType.IDENTIFIER, "_init_");
        func.addChild(typeTypeOrVoid);
        func.addChild(new TerminalNode(nameNode));
        func.addChild(new FunctionDeclaration.FormalParameterList());
        Block funcBody = new Block();
        BlockStatements blockStatements = new BlockStatements();
        funcBody.addChild(blockStatements);
        Token thisToken = new Token(TokenType.KEYWORD, Keyword.getValueByKey(THIS));
        buildStatement(blockStatements, variableDeclaratorsList, thisToken);

        func.addChild(funcBody);
        initMemberFunc.addChild(func);
        return initMemberFunc;
    }


    private static ASTNode createStaticInitFuncMember(String className, List<VariableDeclarators> variableDeclaratorsList) {
        MemberDeclaration initMemberFunc = new MemberDeclaration();
        FunctionDeclaration func = new FunctionDeclaration();
        TypeTypeOrVoid typeTypeOrVoid = new TypeTypeOrVoid();
        typeTypeOrVoid.addChild(new TerminalNode(new Token(TokenType.KEYWORD, Keyword.getValueByKey(VOID))));
        Token nameNode = new Token(TokenType.IDENTIFIER, "_static_init_");
        Token staticNode = new Token(TokenType.KEYWORD, Keyword.getValueByKey(STATIC));

        func.addChild(new TerminalNode(staticNode));
        func.addChild(typeTypeOrVoid);
        func.addChild(new TerminalNode(nameNode));
        func.addChild(new FunctionDeclaration.FormalParameterList());
        Block funcBody = new Block();
        BlockStatements blockStatements = new BlockStatements();
        funcBody.addChild(blockStatements);
        Token classNameToken = new Token(TokenType.IDENTIFIER, className);
        buildStatement(blockStatements, variableDeclaratorsList, classNameToken);

        func.addChild(funcBody);
        initMemberFunc.addChild(func);
        return initMemberFunc;
    }

    private static void buildStatement(BlockStatements blockStatements,
                                List<VariableDeclarators> variableDeclaratorsList,
                                Token srcToken) {
        for (VariableDeclarators varInit : variableDeclaratorsList) {
            List<VariableDeclarator> vars = varInit.variableDeclaratorList();
            for (VariableDeclarator var : vars) { // 类成员暂不支持字面量形式数组初始化
                if (var.variableInitializer() != null && var.variableInitializer().expr() != null) {
                    BlockStatement blockStatement = new BlockStatement();
                    blockStatement.addChild(getAssignStmt(srcToken, var));
                    blockStatements.addChild(blockStatement);
                }
            }
        }
    }

    // 构造赋值表达式 1.普通成员 this.xx = xx 2.静态成员 ClassName.xx = xx
    private static Statement getAssignStmt(Token srcToken, VariableDeclarator var) {
        Statement statement = new Statement();
        Expr dotExpr = new Expr(ASTNodeType.BINARY_EXP, new Token(TokenType.DOT, "."));
        dotExpr.addChild(new TerminalNode(srcToken));
        Token nameToken = new Token(TokenType.IDENTIFIER, var.identifier().getText());
        dotExpr.addChild(new TerminalNode(nameToken));
        Expr assignExpr = new Expr(ASTNodeType.BINARY_EXP, new Token(TokenType.ASSIGN, "="));
        assignExpr.addChild(dotExpr);
        assignExpr.addChild(var.variableInitializer().expr());
        statement.addChild(assignExpr);
        return statement;
    }
}
