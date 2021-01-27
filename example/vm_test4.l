void printAge(int age) {
    println("age---" + age)
}

class Student {
    string name;
    string stuNumber;
    int sex;
    static int age;

    Student(string stuNumber, int age) {
        this.stuNumber = stuNumber
        Student.age = age
    }

    Student(string name, string stuNumber, int sex) {
        this.name = name
        this.stuNumber = stuNumber
        this.sex = sex
     }

    static Student takeClass() {
        println("上课------")
        printAge(Student.age)
        return Student('ass', 11)
    }

    Student test(string info) {
        println(Student.age)
        println(stuNumber)
        println(info + " ---test")
        return this
    }

    static StudentBuilder builder() {
        return StudentBuilder()
    }

    class StudentBuilder {
        string name;
        string stuNumber;
        int sex;

        Student build() {
            return Student(this.name, this.stuNumber, this.sex)
        }

        StudentBuilder name(string name) {
            this.name = name
            return this
        }

        StudentBuilder stuNumber(string stuNumber) {
            this.stuNumber = stuNumber
            return this
        }

        StudentBuilder sex(int sex) {
            this.sex = sex
            return this
        }
    }
}

Student test1() {
    return Student('123', 20)
}

void main() {
    Student s = Student('111', 18)
    Student.age = 11
    println(Student.age)
    Student.takeClass().test('测试1111').stuNumber = '10'
    Student[] students = Student[10]
    students[0] = s.test('测试1')
    Student s1 = students[0]
    s1.test('测试1').test('测试2').test('测试3').stuNumber = '1000'
    int[][] a = {{1, 2, 2}, {888, 0x11}}
    println(a[1].length)
    string[][][] a1 = string[1][2][3]
    println(a1[0][1].length)

    Student newS = test1().test('测试静态1').test("测试静态2")
    newS.stuNumber = '999'
    Student student = Student.builder()
            .name("小明").sex(1).stuNumber('10001').build()
    println(student.name)
    println(student.stuNumber)
}

main()