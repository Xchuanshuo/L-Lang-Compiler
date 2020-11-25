package com.legend.parser;

import com.legend.exception.LexicalException;
import com.legend.exception.ParseException;
import com.legend.lexer.Lexer;
import com.legend.lexer.Token;
import com.legend.parser.common.PeekTokenIterator;

import java.io.*;
import java.util.List;

/**
 * @author Legend
 * @data by on 20-11-13.
 * @description 语法分析器
 */
public class Parser {

    public static Program fromFile(String src) throws IOException, LexicalException, ParseException {
        List<Token> tokenList = Lexer.fromFile(src);
        PeekTokenIterator it = new PeekTokenIterator(tokenList);
        return Program.parse(it);
    }

    public Program analyze(String code) throws LexicalException, ParseException {
        Lexer lexer = new Lexer();
        List<Token> tokenList = lexer.analyze(code);
        PeekTokenIterator it = new PeekTokenIterator(tokenList);
        return Program.parse(it);
    }

    public Program analyze(PeekTokenIterator it) throws LexicalException, ParseException {
        return Program.parse(it);
    }
}
