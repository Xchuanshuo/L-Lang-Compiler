class Test {
    int a = 1
    static string s = "ss"

    Test(int a) {
        this.a = a
        Test.s = "ssssss1" + a
    }

    void t() {
        int b = 9
        b = a
        println(b)
    }
}

Test t = Test(10)
println(t.a)
t.t()
println(Test.s)