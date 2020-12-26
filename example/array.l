int[] a = {1, 2, 10, 8, 7, 6, 3, 20, 5};

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
    int[] aux = int[10];
    void sort(int[] a) {
        aux = int[a.length]
        aux[0] = 1
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


//class A {
//    class B {
//        void test() {
//            println("B--Test")
//        }
//
//        class C {
//            void t() {
//                println("test--Cc")
//            }
//        }
//    }
//}
//
//A.B.C b = A.B.C()
//b.t()

//b.test()
//int[] b = {};
//int[][] a = {{1,2,3,4}, {5,6,7,8}}
//a[5>>6][1+2] = d
//int[10] a = {{1, 2, 3, 4}, 1,2,3,4, 5 * 6 +4, b}
//int[][][] a = a.j[10][10]
//
//af()
//5
//a
//!as
//!1
//!(a == 1)