/**
普通函数功能。
包括普通函数功能和从中间返回的功能。
*/
int foo(int a){
    return a + a;
}

println("foo(10)=" + foo(10));

//测试return语句从中间返回
int bar(int a){
    for (int i = 0; i< a; i+=1){
        //println("i="+i);
        if (i >= 5){
            return i;   //在任意点返回
        }
    }
    return a;
}

println("bar(3)=" + bar(3));    //返回3，执行return a

println("bar(10)=" + bar(10));  //返回5，执行return i



