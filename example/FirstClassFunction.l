/*
函数作为一等公民 也就是函数可以数值，赋给别的变量。
支持函数类型，即FunctionType。
*/

int foo(int a){
    println("in foo, a = " + a);
    return a;
}

int bar(function int(int) fun){
    int b = fun(6);
    println("in bar, b = " + b);
    return b;
}

function int(int) a = foo;  //函数作为变量初始化值
a(4);

function int(int) b;        
b = foo;                    //函数用于赋值语句
b(5);

bar(foo);                   //函数做为参数

