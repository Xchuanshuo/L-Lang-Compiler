package com.legend.parser;

import com.legend.exception.ParseException;
import com.legend.parser.ast.*;
import com.legend.parser.common.PeekTokenIterator;

import java.util.*;

/**
 * @author Legend
 * @data by on 20-11-13.
 * @description 抽象语法树程序入口
 */
public class Program extends ASTNode {

    private Map<BlockStatements, String> programModuleMap = new HashMap<>();

    public Program() {
        this.astNodeType = ASTNodeType.PROGRAM;
    }

    public static Program parse(PeekTokenIterator it) throws ParseException {
        Program program = new Program();
        ImportDeclaration imports = (ImportDeclaration) ImportDeclaration.parse(it);
        ASTNode blockStatements = BlockStatements.parse(it);
        Set<String> set = new HashSet<>();

        if (imports.getLibPathMap() != null) {
            for (Map.Entry<String, String> m : imports.getLibPathMap().entrySet()) {
                Program module = ModuleLoader.load(m.getValue());
                if (module == null) continue;
                if (m.getKey().startsWith("default_")) {
                    List<BlockStatement> blockStatementList = module.blockStatements().blockStatements();
                    if (blockStatementList != null) {
                        for (int i = blockStatementList.size() - 1;i >= 0;i--) {
                            String s = blockStatementList.get(i).getText();
                            if (set.contains(s)) continue;
                            set.add(s);
                            blockStatements.addChild(0, blockStatementList.get(i));
                        }
                    }
                } else {
                    program.addChild(0, module.blockStatements());
                    program.setNewModule(module.blockStatements(), m.getKey());
                }
            }
        }
        program.addChild(blockStatements);
//        program.setNewModule((BlockStatements) blockStatements, "_main_");
        return program;
    }

    public void setNewModule(BlockStatements program, String id) {
        programModuleMap.put(program, id);
    }

    public Map<BlockStatements, String> getAllModules() {
        return programModuleMap;
    }

    public String getModuleId(BlockStatements blockStatements) {
        return programModuleMap.get(blockStatements);
    }

    public List<BlockStatements> allBlockStatements() {
        return getASTNodes(BlockStatements.class);
    }

    public BlockStatements blockStatements() {
        return lastBlockStatements();
    }

    public BlockStatements lastBlockStatements() {
        List<BlockStatements> list = getASTNodes(BlockStatements.class);
        if (list == null) return null;
        return list.get(list.size() - 1);
    }
}
