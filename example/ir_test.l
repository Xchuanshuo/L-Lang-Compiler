int a = 1 + 2 * 3 + 4
int d = a + 5
int e = d * 9 << 2
float b = d * 9.9
int f = 3
f = a
f += 1
f *= 8
if (a == 1) {
    a = 10 + 1
} else if (a == 2){
    a = 10 - 2
} else if (a == 3) {
    a = 10 * 3
    if (a == 99) {
        a = 100
    }
} else {
    a = 10 / 4
}

for (int i = 0;i < 10;i+=1) {
    d = i * 88
    while (a < 1000) {
        a += 1
        if (a == 500) {
            break
        }
        for (int j = 0;j < 100;j+=1) {
            if (j == 20) break
        }
    }
}

int testFunc(int a, string b) {
    println(float(a) + strLen(" ass") + b)
    if (a < 10 && a >= 0) {
        return -1
    }
    return 0
}

int bar(int a){
    for (int i = 0; i< a; i+=1){
        //println("i="+i);
        if (i >= 5){
            return i;   //在任意点返回
        }
    }
    return a;
}
int[][] arr = {{1,2},{3}}
int[] a1 = arr[0]
a1[0] = 10
int a2 = a1[0] * 9
a2 *= 6
arr[1][0] -= arr[2][0]
int v = arr[0][1] + 8
int[] e1 = arr[0]
int[] f1 = e1
int vv = f1[1]
int[][][] newArr = int[1][2][3]
int[][] newArr1 = newArr[0]
int c = 99
a = c = newArr1[0][1] = 1 + 2*3