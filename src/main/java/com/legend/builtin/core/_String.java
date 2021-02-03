package com.legend.builtin.core;

import com.legend.builtin.BuiltInFunction;
import com.legend.builtin.Registry;
import com.legend.gen.operand.Register;
import com.legend.semantic.Class;
import com.legend.vm.Object;
import com.legend.vm.Slots;
import com.legend.vm.StringPool;

/**
 * @author Legend
 * @data by on 21-2-2.
 * @description
 */
public class _String extends Class {

    private static _String string = new _String();

    public static void bind() {
        Registry.register(Registry.DEFAULT, "len(ST,)I",
                new BuiltInFunction(string, "length"));
        Registry.register(Registry.DEFAULT, "strAt(ST,I,)ST",
                new BuiltInFunction(string, "strAt"));
        Registry.register(Registry.DEFAULT, "equals(ST,ST,)Z",
                new BuiltInFunction(string, "equals"));
    }

    public _String() {
        super("String", null);
        setParentClass(rootClass);
    }

    public void length(Slots args, Slots regs) {
        Object ref = args.getRef(0);
        int len = StringPool.getString(ref).length();
        regs.setInt(Register.RV, len);
    }

    public void strAt(Slots args, Slots regs) {
        Object ref = args.getRef(0);
        String str = StringPool.getString(ref);
        int idx = args.getInt(1);
        regs.setRef(Register.RV, StringPool.getStrObj(String.valueOf(str.charAt(idx))));
    }

    public void equals(Slots args, Slots regs) {
        Object ref1 = args.getRef(0);
        String str1 = StringPool.getString(ref1);
        Object ref2 = args.getRef( 1);
        String str2 = StringPool.getString(ref2);
        regs.setBoolean(Register.RV, str1.equals(str2));
    }
}
