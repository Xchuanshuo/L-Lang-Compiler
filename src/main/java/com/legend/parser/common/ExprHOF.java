package com.legend.parser.common;

import com.legend.exception.ParseException;
import com.legend.parser.ast.ASTNode;

/**
 * @author Legend
 * @data by on 20-11-9.
 * @description
 */
@FunctionalInterface
public interface ExprHOF {

    ASTNode hof() throws ParseException;
}
