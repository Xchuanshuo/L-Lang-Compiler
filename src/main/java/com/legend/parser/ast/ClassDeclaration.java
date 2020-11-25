package com.legend.parser.ast;

import com.legend.exception.ParseException;
import com.legend.lexer.Keyword;
import com.legend.lexer.Token;
import com.legend.lexer.TokenType;
import com.legend.parser.common.ASTListener;
import com.legend.parser.common.PeekTokenIterator;
import com.legend.parser.common.PeekUtils;

import java.util.List;

import static com.legend.lexer.Keyword.Key.CLASS;
import static com.legend.lexer.Keyword.Key.EXTENDS;
import static com.legend.lexer.Keyword.Key.IMPLEMENTS;

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
        ASTNode classBody = ClassBody.parse(it);
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

        public static ASTNode parse(PeekTokenIterator it) throws ParseException {
            ClassBody classBody = new ClassBody();
            it.nextMatch("{");
            ASTNode declaration;
            while ((declaration = MemberDeclaration.parse(it)) != null) {
                classBody.addChild(declaration);
            }
            it.nextMatch("}");
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
}
