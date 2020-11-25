/**
测试各种表达式功能。
*/

//纯字面量计算
println(2+3*5);
println(2 % 3)
//表达式中使用变量
int a = 10;
int b = 2;
println(a + b*b);

//关系运算
println("a == b : " + (a == b));
println("a > b : " + (a > b));

//逻辑运算
boolean c = a == b;
println("c || true || a>b : " + (c || true || a>b));
println("!c : " + !c );

//字符串类型
string str1 = "Hello ";
string str2 = "World!";
println(str1 + str2);

string s = null
println("s: " + s)

println("位运算----------------")
println(3 << 2 << 2)
println(16 >> 2)
println(3 | 0xC)
println(3 & 0xC)
println(0 ^ 1)