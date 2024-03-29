/**
测试递归函数。
递归函数对语义分析的要求：需要能够在函数声明完毕之前，就再次使用这个函数。
例子：斐波那契数列。前两个数字是0，1。后面的每个数值是前两个数字的和。
0，1，1，2，3，5，8，13...
*/

int fibonacci(int n){
    if (n <= 1){
        return n
    } else {
        return fibonacci(n-1) + fibonacci(n-2)
    }
}

println("fibonacci:")
for (int i = 0; i< 10; i+=1 ){
    println(i+" : "+fibonacci(i))
}

