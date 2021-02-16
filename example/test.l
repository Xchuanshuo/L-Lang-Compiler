
class Test {
    int a
    static string s = "ss"

    Test(int a) {
        this.a = a
        Test.s = "ssssss1" + a
    }

    void t() {
        int b = 2
        a += 1
        a += b
        println(a)
    }
}

int[] arr = {1, 4, 10, 46, 6, 5}
void printArr() {
    for (int i = 0;i < arr.length;i+=1) {
        println(arr[i])
    }
}
Test t = Test(10)
println(t.a)
t.t()
//println(Test.s)
printArr()
