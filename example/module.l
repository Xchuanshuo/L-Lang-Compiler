import m1
import sys.math
import sys.strings
import LinkedList

int a = max(1, 2)
println("max:" + a)
float b = min(1, 1)
println("min:"  + b)
println(int(PI))
println(float(0x21))
string s = "jaj"
string s1 = "ja"
println(s == s1)
println("字符串s长度:" + strLen(s))
println("字符串s第2个字符:" + strAt(s, 1))
println("字符串切割:" + subString(s, 1,2))
println("字符串startsWith:" + startsWith(s, s1))
println("字符串endsWith:" + endsWith(s, s1))
println("-------------------链表-----------------")

LinkedList list = LinkedList();
list.add(2);
list.add(3);
list.add(5);

println("original list:");
list.dump();

println("add 1 to each element:");
LinkedList list2 = list.map(addOne);
list2.dump();

println("square of each element:");
LinkedList list3 = list.map(square);
list3.dump();

println("add 10 of each element:");
LinkedList list4 = list.map(addTen);
list4.dump();

int cubic(int cubic) {
    return cubic * cubic * cubic
}

println("cubic of each element:")
LinkedList list5 = list.map(cubic)
list5.dump()

string[][] strings = {{1,2,2,3}, {"Asas", "sdfsd", "sdgfsd", "是电风扇的 ",""}}
strings[0][1] = 1.0
for (int i = 0;i < strings.length;i+=1) {
    for (int j = 0;j < strings[i].length;j+=1) {
        println("当前元素: " + strings[i][j])
    }
}
println("---------------------------")
string[] s0 = strings[0]
for (int i = 0;i < s0.length;i+=1) {
    print(s0[i] + " ")
}