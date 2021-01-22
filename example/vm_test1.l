int a = 1
int b = 2
println(a + b + "sss")
float sum = 1.0
for (int i = 1;i <= 100;i += 1) {
    sum += i
    println(i + ": " + sum)
}
println(sum)
//for (float y = 1.5f; y > -1.5f; y = y - 0.1f) {
//    for (float x = -1.5f; x < 1.5f; x = x + 0.05f) {
//        float a = x * x + y * y - 1
//        boolean flag = a * a * a - x * x * y * y * y <= 0.0f
////        print(flag)
//        if (flag) print("*"); else print(" ");
//    }
//    println();
//}