// 函数调用 使用a()表示调用这个函数 a表示直接返回这个函数对象
println("函数作为参数与返回值测试----------------------");

function string(boolean) a() {
    string getString(boolean isPrintMulti) {
        if (isPrintMulti) {
            for (int i = 1;i <= 9;i+=1) {
                for (int j = 1;j <= i;j+=1) {
                    print(j + "*" + i + "=" + i*j + " ");
                }
                println();
            }
            return "success";
        } else {
            println("not print-----");
        }
        return "failure";
    }
    return getString;
}

// 把a()当前一个函数变量 内部持有一个闭包函数
function string(boolean) printMultiplication = a();

string str1 = printMultiplication(true);
println(str1);

