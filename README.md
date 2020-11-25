# L-Lang-Compiler
L-Lang 一个类Java的面向对象语言, 去除了new, 每行结尾的分号等冗余的字符
实现了函数作为一等公民,可以赋值、作为函数参数，支持闭包等特性, 具体示例查看example目录
### 示例
#### 函数
```
// 定义一个函数　返回值为函数类型　函数类型返回值为int,参数为空
function int() fun(){
    int a = 0
    int b = 1
    int fibonacci(){
        int c = a
        a = b
        b = a+c
        return c
    }
    return fibonacci
}
// 定义一个函数变量　接收返回值为int,参数个数为０的函数
function int() fib = fun()
// 打印斐波那契数列，在闭包里记住了相邻两次计算的结果
for (int i = 0;i < 80;i += 1){
    println(fib())
}
```

#### 对象与数组实例化
```
A a = A() // 实例化一个对象Ａ
A a = A("ass",1) // 实例化一个对象Ａ，构造函数接受两个参数
int[][] a = int[10][10] // 实例化一个10*10的数组ａ
int[][] b = {{1,2,3}, {2+1<<9}, {1,2,2345}} // 带初始化值的数组b
```


下面给出一个打印心形以及排序算法的示例

```java
int[] a = {1, 2, 10, 8, 7, 6, 3, 20, 5}

void printHeart() {
    for (float y = 1.5f; y > -1.5f; y = y - 0.1f) {
        for (float x = -1.5f; x < 1.5f; x = x + 0.05f) {
            float a = x * x + y * y - 1;
            boolean flag = a * a * a - x * x * y * y * y <= 0.0f;
            if (flag) print("*"); else print(" ");
        }
        println();
    }
}
printHeart();

void printArr() {
    for (int i = 0;i < a.length;i+=1) {
        print(a[i] + " ");
    }
    println();
}

class Sort {
    void sort(int[] a);

    string name() {
        return "";
    }

    void swap(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }
}

class QuickSort extends Sort {
    int a;

    void sort(int[] a) {
      quickSort(a, 0, a.length - 1);
    }

    void quickSort(int[] a, int l, int r) {
        if (l >= r) return;
        int p = partition(a, l, r);
        quickSort(a, l, p - 1);
        quickSort(a, p + 1, r);
    }

    int partition(int[] a, int l, int r) {
        int t = a[l], i = l, j = r + 1;
        while (true) {
            while (a[i+=1] < t && i != r);
            while (a[j-=1] > t && j != l);
            if (i >= j) break
            swap(a, i, j);
        }
        swap(a, l, j);
        return j;
    }

    string name() {
        return "快速排序";
    }
}

class MergeSort extends Sort {
    int[] aux;
    void sort(int[] a) {
        aux = int[a.length];
        mergeSort(a, 0, a.length - 1);
    }

    void mergeSort(int[] a, int l, int r) {
        if (l >= r) return;
        int mid = l + (r - l) / 2;
        mergeSort(a, l, mid);
        mergeSort(a, mid + 1, r);
        merge(a, l, mid, r);
    }

    void merge(int[] a, int l, int m, int r) {
        int i = l, j = m + 1;
        for (int k = l;k <= r;k+=1) {
            aux[k] = a[k];
        }
        int k = l;
        while (i <= m && j <= r) {
            if (aux[i] < aux[j]) {
                a[k] = aux[i];
                k += 1;
                i += 1;
            } else {
                a[k] = aux[j];
                k += 1;
                j += 1;
            }
        }
        while (i <= m) {
            a[k] = aux[i];
            k += 1;
            i += 1;
        }
        while (j <= r) {
            a[k] = aux[j];
            k += 1;
            j += 1;
        }
    }

    string name() {
        return "归并排序";
    }
}

class SelectionSort extends Sort {
    void sort(int[] a) {
        for (int i = 0;i < a.length;i+=1) {
            int minIdx = i;
            for (int j = i + 1;j < a.length;j+=1) {
                if (a[j] < a[minIdx]) {
                    minIdx = j;
                }
            }
            swap(a, i, minIdx);
        }
    }

    string name() {
        return "选择排序";
    }
}

void sort(Sort sort, int[] array) {
    println("当前选用的排序算法: " + sort.name());
    sort.sort(array);
}

println("原数组: ");
printArr();
Sort quickSort = QuickSort();
Sort mergeSort = MergeSort();
Sort selectionSort = SelectionSort();

sort(mergeSort, a);

println("排序后:");
printArr();
```
