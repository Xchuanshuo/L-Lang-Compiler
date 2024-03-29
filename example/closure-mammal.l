/**
用闭包来模拟面向对象的多态特性。
用Cow()函数和Sheep()函数返回的Mammal对象，其表现会不同
这种多态不是用类的继承实现的，但效果是一样的
*/

class Mammal{
    function void() speak;
    function int() getWeight;
    function void(int) setWeight;
    void foo(){
        //在内部的方法中也可以调用这些函数型变量，与普通方法没有任何区别。
        println("in foo")
        this.setWeight(23)
        speak()
    }
}

//返回一个Mammal对象，其特征是
Mammal Cow(){
    void speak(){
        println("moo~~ moo~~");
    }
    int weight = 100
    int getWeight(){
        return weight;
    }
    void setWeight(int w){
        weight = w;
    }

    Mammal m = Mammal();
    m.speak = speak;
    m.getWeight = getWeight;
    m.setWeight = setWeight;

    return m;
}

Mammal Sheep(){
    void speak(){
        println("mee~~ mee~~");
    }
    int weight = 20;
    int getWeight(){
        return weight;
    }
    void setWeight(int w){
        weight = w;
    }

    Mammal m = Mammal();
    m.speak = speak;
    m.getWeight = getWeight;
    m.setWeight = setWeight;
    return m;
}

Mammal m1 = Cow();
m1.speak();
println("weight of mammal1 : " + m1.getWeight());
m1.setWeight(101);
println("new weight of mammal1 : " + m1.getWeight());

println();

Mammal m2 = Sheep();
m2.speak();
println("weight of mammal2 : " + m2.getWeight());
m2.setWeight(21);
println("new weight of mammal2 : " + m2.getWeight());

println();
m2.foo();
println("new weight of mammal2 : " + m2.getWeight());
