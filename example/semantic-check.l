/**
做一些语义检查。
*/

return 1;      //return语句只能在函数里。

int foo(){     //定义了非void的返回值，就一定要有return语句
}

void foo2(){   //void类型的返回值，不需要有return语句
}

class C1{
  int C1(){
    return 0;  //构造方法里不能有返回值
  }
  
  int C1(int a){
    return;    //用一个空的return是可以的。
  }

  //return 0;  如果在这里出现return语句，那么语法检查阶段就通不过。
}


//////////////

break;  //break语句只能在循环语句和case语句中。

void foo2(){
  break;   //不允许
}

for (int i = 0; i < 2; i+=1){
  break;   //这里可以
}

/////////////

void foo3(){
  class C2{ //不允许在函数内部声明类
  }
}



