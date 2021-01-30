
class Test {
    int a = 1
    static string s = "ss"

    Test(int a) {
        this.a = a
        Test.s = "ssssss1" + a
    }

    void t() {
        int b = 2
        a += 1
        a += b
        println(b)
    }
}

int[] arr = {1, 4, 10, 46, 6, 5}
void printArr() {
    for (int i = 0;i < arr.length;i+=1) {
        println(arr[i])
    }
}
Test t = Test(10)
t.t()
println(t.a)
println(Test.s)
printArr()
