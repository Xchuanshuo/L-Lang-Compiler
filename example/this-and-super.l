/**
this-and-super.l 面向对象的this和super特性。
*/
class Mammal{
    int weight1;

    Mammal(){
        println("Mammal() called");
        this.weight1 = 100;
    }

    Mammal(int weight){
        this();                   //调用另一个构造函数
        println("Mammal(int weight) called");
        this.weight1 = weight;
        this.speak();             //这里多态仍然会生效，实际调用的是Cow的speak方法
    }

    void speak(){
        println("Mammal speaking...");
    }
}

class Cow extends Mammal{
    int weight = 300;

    Cow(){
        super(200);   //调用父类的构造函数
        this.weight = 300
    }

    void speak(){
        println("in Cow.speak");
        println("super.weight1 : "  + super.weight1);
        println("this.weight : "  + this.weight);
        println("call super.speak() : " );
        super.speak()

        println("Cow speak is : moo~~ moo~~");
    }
}

Mammal cow = Cow();
println();
cow.speak();