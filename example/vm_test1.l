string printHeart(string s, int e) {
    for (float y = 1.5f; y > -1.5f; y -= 0.1f) {
        for (float x = -1.5f; x < 1.5f; x += 0.032f) {
            float a = x * x + y * y - 1
            boolean flag = a * a * a - x * x * y * y * y <= 0.0f
            if (flag) print("*"); else print(" ");
        }
        println()
    }
    println("我是参数: "  + s + e)
    s = s + '_new'
    return s
}

void _main() {
    int a = 1
    float b = 2.1
    println(a + b + "sss")
    float sum = 1.6
    for (int i = 1;i <= 100;i += 1) {
        sum += i
        println(i + ": " + sum)
    }
    println(sum)
    string s = printHeart("hello", 12)
    println(s)
}

_main()