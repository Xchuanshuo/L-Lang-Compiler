/**
测试几种循环语句。 包括对break的支持
*/
//测试while循环
println("while loop:");
int i = 0;
while(i < 10){
    i = i+1;
    println("i="+i);
}


//测试for循环
println();
println("for loop:");
int a = 0;
for(int i = 0; i<10; i+=1){
    a = a + i
    println("i="+i + ", a="+a)
}

//测试带break的while循环
println()
println("while loop, breaks when i > 5:")
i = 0;
while(i < 10){
    i = i+1;
    println("i="+i);
    if (i > 5)
        break;
}

//测试带break的for循环
println();
println("for loop, breaks when i > 5:");
a = 0;
for(int i = 0; i<10;i += 1){
    a = a + i
    println("i="+i + ", a="+a)
    if (i > 5) break
}

//测试嵌套循环，和break
println();
println("for loop within while loop:");
i = 0;
while (i<5){
    i = i+1;
    for (int j = 0; j< 5; j += 1){
        println("i="+i + ", j="+j)
        if (j>=2) break
    }
    if (i >= 3){
        break;
    }
}

