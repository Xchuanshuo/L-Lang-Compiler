class Test {
    int a = 1
    static string s = "ss"

    Test(int a) {
        this.a = a
        Test.s = "ssssss1" + a
    }
}

Test t = Test(10)
println(t.a)
println(Test.s)