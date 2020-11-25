package com.legend.parser.ast;

import com.legend.exception.ParseException;
import com.legend.lexer.Keyword;
import com.legend.lexer.Token;
import com.legend.lexer.TokenType;
import com.legend.parser.common.PeekTokenIterator;

import java.util.*;

import static com.legend.lexer.Keyword.Key.AS;
import static com.legend.lexer.Keyword.Key.IMPORT;

/**
 * @author Legend
 * @data by on 20-11-21.
 * @description import声明 用来导入外部模块
 */
public class ImportDeclaration extends ASTNode {

    // 建立模块到别名的映射
    private Map<String, String> libMap;
    private static int defaultModuleName = 0;

    public ImportDeclaration() {
        this.astNodeType = ASTNodeType.IMPORT_DECLARATION;
    }

    public static ASTNode parse(PeekTokenIterator it) throws ParseException {
        ImportDeclaration importDeclaration = new ImportDeclaration();
        while (it.topIsEqual(IMPORT)) {
            it.nextMatch(IMPORT);
            if (!it.topIsEqual(TokenType.IDENTIFIER)) {
                throw new ParseException(it.peek());
            }
            StringBuilder sb = new StringBuilder();
            sb.append(it.nextMatch(TokenType.IDENTIFIER).getText());
            while (it.topIsEqual(TokenType.DOT)) {
                sb.append(it.nextMatch(TokenType.DOT).getText());
                Token token = it.nextMatch(TokenType.IDENTIFIER);
                sb.append(token.getText());
            }
            String alias = null;
            if (it.topIsEqual(AS)) {
                it.nextMatch(AS);
                alias = it.nextMatch(TokenType.IDENTIFIER).getText();
            }
            if (it.topIsEqual(TokenType.SEMICOLON)) {
                it.nextMatch(TokenType.SEMICOLON);
            }
            importDeclaration.addLibPath(alias, sb.toString());
        }
        return importDeclaration;
    }


    public void addLibPath(String path) {
        addLibPath(null, path);
    }

    public void addLibPath(String alias, String path) {
        if (alias == null) {
            alias = "default_" + defaultModuleName++ + "";
        }
        if (libMap == null) {
            libMap = new LinkedHashMap<>();
        }
        libMap.put(alias, path);
    }

    public Map<String, String> getLibPathMap() {
        return libMap;
    }
}
