package com.legend.parser.ast;

import com.legend.lexer.Keyword;
import com.legend.lexer.Token;
import com.legend.lexer.TokenType;
import com.legend.parser.common.ASTListener;
import com.legend.parser.common.ASTVisitor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Legend
 * @data by on 20-11-8.
 * @description 抽象语法树节点
 */
public class ASTNode {

    private List<ASTNode> children = new ArrayList<>();
    protected ASTNode parent = null;
    protected ASTNodeType astNodeType;

    // 语法节点对应的单词
    protected Token token;
    // 节点备注
    protected String remark;

    public ASTNode getChild(int position) {
        return children.get(position);
    }

    public List<ASTNode> getChildren() {
        return children;
    }

    public void addChild(ASTNode child) {
        if (child == null) return;
        children.add(child);
        child.parent = this;
    }

    public void addChild(int pos, ASTNode child) {
        if (child == null) return;
        children.add(pos, child);
        child.parent = this;
    }

    public void setParent(ASTNode parent) {
        this.parent = parent;
    }

    public void removeChild(ASTNode child) {
        if (child == null) return;
        children.remove(child);
    }

    public ASTNode firstChild() {
        if (children.size() > 0) {
            return children.get(0);
        }
        return null;
    }

    public ASTNode lastChild() {
        if (children.size() > 0) {
            return children.get(children.size() - 1);
        }
        return null;
    }

    public ASTNode getParent() {
        return parent;
    }

    public ASTNodeType getAstNodeType() {
        return astNodeType;
    }

    @Override
    public String toString() {
        if (token == null) return "";
        return token.getText();
    }

    public Token getToken() {
        if (token == null && getChildren() != null) {
            for (ASTNode child : getChildren()) {
                if (child.getToken() != null) {
                    return child.getToken();
                }
            }
        }
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public void enter(ASTListener listener) {
        if (listener != null) {
            String cur = this.getClass().getSimpleName();
            java.lang.Class param = this.getClass();
            if (cur.endsWith("Literal")) {
                cur = "Literal";
                param = param.getSuperclass();
            }
            String enterMethodName = "enter" + cur;
            try {
                Method enterMethod = listener.getClass().getMethod(enterMethodName, param);
                enterMethod.invoke(listener, this);
            } catch (Exception ignored) {
            }
        }
    }

    public void exit(ASTListener listener) {
        if (listener != null) {
            String cur = this.getClass().getSimpleName();
            Class param = this.getClass();
            if (cur.endsWith("Literal")) {
                cur = "Literal";
                param = param.getSuperclass();
            }
            String exitMethodName = "exit" + cur;
            try {
                Method enterMethod = listener.getClass().getMethod(exitMethodName, param);
                enterMethod.invoke(listener, this);
            } catch (Exception ignored) {
//                ignored.printStackTrace();
            }
        }
    }

    public <T> T  accept(ASTVisitor<? extends T> visitor) {
        if (visitor == null) return null;
        String cur = this.getClass().getSimpleName();
        String methodName = "visit" + cur;
        try {
            Method method = visitor.getClass().getMethod(methodName, this.getClass());
            return (T) method.invoke(visitor, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T extends ASTNode> T getASTNode(Class<T> clazz) {
        return getASTNode(clazz, 0);
    }

    public <T extends ASTNode> T getASTNode(Class<T> clazz, int idx) {
        List<T> list = getASTNodes(clazz);
        if (list == null || idx >= list.size()) return null;
        return list.get(idx);
    }

    public <T extends ASTNode> List<T> getASTNodes(Class<T> clazz) {
        if (children.size() > 0) {
            List<T> result = null;
            for (ASTNode node : children) {
                if (clazz.isInstance(node)) {
                    if (result == null) {
                        result = new ArrayList<>();
                    }
                    result.add(clazz.cast(node));
                }
            }
            return result;
        }
        return null;
    }

    public TerminalNode getTerminalNode(TokenType type) {
        return getTerminalNode(type, 0);
    }

    public TerminalNode getTerminalNode(TokenType type, int idx) {
        List<TerminalNode> list = getTerminalNodes(type);
        if (list == null) {
            return null;
        }
        return list.get(idx);
    }

    public List<TerminalNode> getTerminalNodes(TokenType type) {
        if (children.size() > 0) {
            List<TerminalNode> result = null;
            for (ASTNode astNode : children) {
                if (astNode instanceof TerminalNode &&
                    astNode.token.getTokenType() == type) {
                    if (result == null) {
                        result = new ArrayList<>();
                    }
                    result.add((TerminalNode) astNode);
                }
            }
            return result;
        }
        return null;
    }

    public TerminalNode getTerminalNode(String text) {
        return getTerminalNode(text, 0);
    }

    public TerminalNode getTerminalNode(Keyword.Key key) {
        String[] keys = Keyword.getValuesByKey(key);
        for (String k : keys) {
            TerminalNode node = getTerminalNode(k, 0);
            if (node != null) return node;
        }
        return null;
    }

    public TerminalNode getTerminalNode(String text, int idx) {
        List<TerminalNode> list = getTerminalNodes(text);
        if (list == null) {
            return null;
        }
        return list.get(idx);
    }

    public List<TerminalNode> getTerminalNodes(String text) {
        if (children.size() > 0) {
            List<TerminalNode> result = null;
            for (ASTNode astNode : children) {
                if (astNode instanceof TerminalNode &&
                        astNode.token.getText().equals(text)) {
                    if (result == null) {
                        result = new ArrayList<>();
                    }
                    result.add((TerminalNode) astNode);
                }
            }
            return result;
        }
        return null;
    }

    public String getText() {
        StringBuilder sb = new StringBuilder();
        for (ASTNode child: children) {
            sb.append(child.getText());
        }
        return sb.toString();
    }

    public void dumpAST(String indent) {
        System.out.println(indent + this.astNodeType + "(" + toString() + ")");
        for (ASTNode node : getChildren()) {
            node.dumpAST("\t" + indent);
        }
    }

    public void dumpAST() {
        dumpAST("");
    }
}
