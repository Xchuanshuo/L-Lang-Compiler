class Student {
    static string clazz = "ass"
    string name
    int age
    int[] arr

    Student(string name, int age) {
//        Student.clazz = "_软件1班" + Student.clazz
        this.name = name
        this.age = age
    }

    string name() {
        name += "_ssss"
        return name
    }

    int age() {
        return age
    }
}

void _main() {
    Student student = Student("小明", 18)
    student.age = 16
    student.name = student.name + "1"
    println(student.name())
    println("---------------------------------------------")
    Student s1 = Student("小红", 20)
    println(s1.name())
}

_main()
