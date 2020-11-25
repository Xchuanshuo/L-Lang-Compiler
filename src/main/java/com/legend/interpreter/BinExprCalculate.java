package com.legend.interpreter;

import com.legend.semantic.PrimitiveType;
import com.legend.semantic.Type;

/**
 * @author Legend
 * @data by on 20-11-20.
 * @description 二元表达式运算
 */
public class BinExprCalculate {

    protected static Object add(Object obj1, Object obj2, Type targetType) {
        Object rtn = null;
        if (targetType == PrimitiveType.String) {
            rtn = String.valueOf(obj1) + String.valueOf(obj2);
        } else if (targetType == PrimitiveType.Integer) {
            rtn = ((Number)obj1).longValue() + ((Number)obj2).longValue();
        } else if (targetType == PrimitiveType.Float) {
            rtn = ((Number)obj1).doubleValue() + ((Number)obj2).doubleValue();
        } else if (targetType == PrimitiveType.Long) {
            rtn = ((Number)obj1).longValue() + ((Number)obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            rtn = ((Number)obj1).longValue() + ((Number)obj2).longValue();
        } else if (targetType == PrimitiveType.Short) {
            rtn = ((Number)obj1).shortValue() + ((Number)obj2).shortValue();
        } else {
            System.out.println("unsupported add() operation");
        }
        return rtn;
    }

    protected static Object sub(Object obj1, Object obj2, Type targetType) {
        Object rtn = null;
        if (targetType == PrimitiveType.Integer) {
            rtn = ((Number)obj1).longValue() - ((Number)obj2).longValue();
        } else if (targetType == PrimitiveType.Float) {
            rtn = ((Number)obj1).doubleValue() - ((Number)obj2).doubleValue();
        } else if (targetType == PrimitiveType.Long) {
            rtn = ((Number)obj1).longValue() - ((Number)obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            rtn = ((Number)obj1).longValue() - ((Number)obj2).longValue();
        } else if (targetType == PrimitiveType.Short) {
            rtn = ((Number)obj1).shortValue() - ((Number)obj2).shortValue();
        } else {
            System.out.println("unsupported sub() operation");
        }
        return rtn;
    }

    protected static Object mul(Object obj1, Object obj2, Type targetType) {
        Object rtn = null;
        if (targetType == PrimitiveType.Integer) {
            rtn = ((Number)obj1).longValue() * ((Number)obj2).longValue();
        } else if (targetType == PrimitiveType.Float) {
            rtn = ((Number)obj1).doubleValue() * ((Number)obj2).doubleValue();
        } else if (targetType == PrimitiveType.Long) {
            rtn = ((Number)obj1).longValue() * ((Number)obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            rtn = ((Number)obj1).longValue() * ((Number)obj2).longValue();
        } else if (targetType == PrimitiveType.Short) {
            rtn = ((Number)obj1).shortValue() * ((Number)obj2).shortValue();
        } else {
            System.out.println("unsupported mul() operation");
        }
        return rtn;
    }

    protected static Object div(Object obj1, Object obj2, Type targetType) {
        Object rtn = null;
        if (targetType == PrimitiveType.Integer) {
            rtn = ((Number)obj1).longValue() / ((Number)obj2).longValue();
        } else if (targetType == PrimitiveType.Float) {
            rtn = ((Number)obj1).doubleValue() / ((Number)obj2).doubleValue();
        } else if (targetType == PrimitiveType.Long) {
            rtn = ((Number)obj1).longValue() / ((Number)obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            rtn = ((Number)obj1).longValue() / ((Number)obj2).longValue();
        } else if (targetType == PrimitiveType.Short) {
            rtn = ((Number)obj1).shortValue() / ((Number)obj2).shortValue();
        } else {
            System.out.println("unsupported div() operation");
        }
        return rtn;
    }

    protected static Object mod(Object obj1, Object obj2, Type targetType) {
        Object rtn = null;
        if (targetType == PrimitiveType.Integer) {
            rtn = ((Number)obj1).longValue() % ((Number)obj2).longValue();
        } else {
            System.out.println("unsupported div() operation");
        }
        return rtn;
    }

    protected static Object xor(Object obj1, Object obj2, Type targetType) {
        Object rtn = null;
        if (targetType == PrimitiveType.Integer) {
            rtn = ((Number)obj1).longValue() ^ ((Number)obj2).longValue();
        } else {
            System.out.println("unsupported div() operation");
        }
        return rtn;
    }

    protected static Object bitAnd(Object obj1, Object obj2, Type targetType) {
        Object rtn = null;
        if (targetType == PrimitiveType.Integer) {
            rtn = ((Number)obj1).longValue() & ((Number)obj2).longValue();
        } else {
            System.out.println("unsupported div() operation");
        }
        return rtn;
    }

    protected static Object bitOr(Object obj1, Object obj2, Type targetType) {
        Object rtn = null;
        if (targetType == PrimitiveType.Integer) {
            rtn = ((Number)obj1).longValue() | ((Number)obj2).longValue();
        } else {
            System.out.println("unsupported div() operation");
        }
        return rtn;
    }

    protected static Object leftShift(Object obj1, Object obj2, Type targetType) {
        Object rtn = null;
        if (targetType == PrimitiveType.Integer) {
            rtn = ((Number)obj1).intValue() << ((Number)obj2).intValue();
        } else {
            System.out.println("unsupported div() operation");
        }
        return rtn;
    }

    protected static Object rightShift(Object obj1, Object obj2, Type targetType) {
        Object rtn = null;
        if (targetType == PrimitiveType.Integer) {
            rtn = ((Number)obj1).intValue() >> ((Number)obj2).intValue();
        } else {
            System.out.println("unsupported div() operation");
        }
        return rtn;
    }

    protected static Boolean eq(Object obj1, Object obj2, Type targetType) {
        Boolean rtn = null;
        if (targetType == PrimitiveType.Integer) {
            rtn = ((Number)obj1).longValue() == ((Number)obj2).longValue();
        } else if (targetType == PrimitiveType.Float) {
            rtn = ((Number)obj1).doubleValue() == ((Number)obj2).doubleValue();
        } else if (targetType == PrimitiveType.Long) {
            rtn = ((Number)obj1).longValue() == ((Number)obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            rtn = ((Number)obj1).doubleValue() == ((Number)obj2).doubleValue();
        } else if (targetType == PrimitiveType.Short) {
            rtn = ((Number)obj1).shortValue() == ((Number)obj2).shortValue();
        } else if (targetType == PrimitiveType.String){
            rtn = String.valueOf(obj1).equals(String.valueOf(obj2));
        } else {
            // 对于对象实例、函数，直接比较引用对象
            rtn = obj1 == obj2;
        }
        return rtn;
    }

    protected static Boolean ge(Object obj1, Object obj2, Type targetType) {
        Boolean rtn = null;
        if (targetType == PrimitiveType.Integer) {
            rtn = ((Number)obj1).longValue() >= ((Number)obj2).longValue();
        } else if (targetType == PrimitiveType.Float) {
            rtn = ((Number)obj1).doubleValue() >= ((Number)obj2).doubleValue();
        } else if (targetType == PrimitiveType.Long) {
            rtn = ((Number)obj1).longValue() >= ((Number)obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            rtn = ((Number)obj1).doubleValue() >= ((Number)obj2).doubleValue();
        } else if (targetType == PrimitiveType.Short) {
            rtn = ((Number)obj1).shortValue() >= ((Number)obj2).shortValue();
        } else {
            System.out.println("unsupported ge() operation");
        }
        return rtn;
    }

    protected static Boolean gt(Object obj1, Object obj2, Type targetType) {
        Boolean rtn = null;
        if (targetType == PrimitiveType.Integer) {
            rtn = ((Number)obj1).longValue() > ((Number)obj2).longValue();
        } else if (targetType == PrimitiveType.Float) {
            rtn = ((Number)obj1).doubleValue() > ((Number)obj2).doubleValue();
        } else if (targetType == PrimitiveType.Long) {
            rtn = ((Number)obj1).longValue() > ((Number)obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            rtn = ((Number)obj1).doubleValue() > ((Number)obj2).doubleValue();
        } else if (targetType == PrimitiveType.Short) {
            rtn = ((Number)obj1).shortValue() > ((Number)obj2).shortValue();
        } else {
            System.out.println("unsupported gt() operation");
        }
        return rtn;
    }

    protected static Boolean le(Object obj1, Object obj2, Type targetType) {
        Boolean rtn = null;
        if (targetType == PrimitiveType.Integer) {
            rtn = ((Number)obj1).longValue() <= ((Number)obj2).longValue();
        } else if (targetType == PrimitiveType.Float) {
            rtn = ((Number)obj1).doubleValue() <= ((Number)obj2).doubleValue();
        } else if (targetType == PrimitiveType.Long) {
            rtn = ((Number)obj1).longValue() <= ((Number)obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            rtn = ((Number)obj1).doubleValue() <= ((Number)obj2).doubleValue();
        } else if (targetType == PrimitiveType.Short) {
            rtn = ((Number)obj1).shortValue() <= ((Number)obj2).shortValue();
        } else {
            System.out.println("unsupported le() operation");
        }
        return rtn;
    }

    protected static Boolean lt(Object obj1, Object obj2, Type targetType) {
        Boolean rtn = null;
        if (targetType == PrimitiveType.Integer) {
            rtn = ((Number)obj1).longValue() < ((Number)obj2).longValue();
        } else if (targetType == PrimitiveType.Float) {
            rtn = ((Number)obj1).doubleValue() < ((Number)obj2).doubleValue();
        } else if (targetType == PrimitiveType.Long) {
            rtn = ((Number)obj1).longValue() < ((Number)obj2).longValue();
        } else if (targetType == PrimitiveType.Double) {
            rtn = ((Number)obj1).doubleValue() < ((Number)obj2).doubleValue();
        } else if (targetType == PrimitiveType.Short) {
            rtn = ((Number)obj1).shortValue() < ((Number)obj2).shortValue();
        } else {
            System.out.println("unsupported lt() operation");
        }
        return rtn;
    }
}
