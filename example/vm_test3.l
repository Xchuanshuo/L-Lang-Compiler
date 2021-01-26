class Student {
    static string clazz = "ass"
    string name
    int age
    int[] arr

    Student(string name, int age) {
        Student.clazz += "_软件1班"
        this.name = name
        this.age = age
    }

    string name(string new) {
        name += "_ssss" + new
        return name
    }

    int age() {
        return age
    }

    static void sayHello() {
        println("Hello!" + Student.clazz)
    }
}

void _main() {
    Student student = Student("小明", 18)
    student.age = 16
    student.name +=  "1"
    println(student.name("离开"))
    println("---------------------------------------------")
    Student s1 = Student("小红", 20)
    println(s1.name("sad"))
    println(s1  + "_" + s1.age() + Student.clazz)
    Student.sayHello()
}

_main()
