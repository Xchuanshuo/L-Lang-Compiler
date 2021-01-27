int[] a = {1, 4, 2, 3}
int v = a[0]
int l = 0, i = 4
while (a[i-=1] >= v && i != l) {
    println(i)
}