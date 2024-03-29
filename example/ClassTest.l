/*
简单的面向对象特性。
*/

class Mammal{
  //类属性
  string name = "";

  //构造方法
  Mammal(string str){
    this.name = str;
  }

  //方法
  void speak(){
    println("mammal " + name +" speaking...");
  }
}

Mammal mammal = Mammal("dog");
mammal.speak();                          //访问对象方法
println("mammal.name = " + mammal.name); //访问对象的属性


//没有构造方法，创建的时候用缺省构造方法
class Bird{
  int speed = 50;    //在缺省构造方法里初始化

  Bird() {
    this.speed = 50
  }

  void fly(){
    println("bird flying...");
  }
}

Bird bird = Bird();              //采用缺省构造方法
println("bird.speed : " + bird.speed + "km/h");
bird.fly();
