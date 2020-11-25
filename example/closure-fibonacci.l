/**
 * 用闭包实现斐波那契数列
 */
function int() fun(){
    int a = 0
    int b = 1
    int fibonacci(){
        int c = a
        a = b
        b = a+c
        return c
    }
    return fibonacci
}

function int() fib = fun()
// 打印斐波那契数列，在闭包里记住了相邻两次计算的结果
for (int i = 0;i < 80;i += 1){
    println(fib())
}

