/**
 * closure.l
 * 测试闭包特性
 */

int a = 0;

function int() fun1(){
    int b = 0;                //函数内的局部变量
    int d = 16
    int e = 7
    int inner(){              //内部的一个函数
        //a = a+1;
        println(e)
        b = b+1;
        int c = 1
        int f = 1
        return b;             //返回内部的成员
    }

    return inner;             //返回一个函数
}

function int() fun2 = fun1();
for (int i = 0; i< 3; i+=1){
    println("b = " + fun2() + ", a = "+a);
}

function int() fun3 = fun1();  
for (int i = 0; i< 3; i+=1){
    println("b = " + fun3() + ", a = "+a);
}