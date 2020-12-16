import java.io.IOException;

/**
 * @author Legend
 * @data by on 20-12-12.
 * @description
 */
public class T {
    private int c;

    public T(int a) {
        this.c = a;
    }


    public void f(int a) {
        a += 5;
    }

    public static void main(String[] args) throws IOException {
//        int[][] a = new int[3][4];
//        a[0][1] = 10;
//        a[1][2] = 10;
//        int i = System.in.read();
//        int d = i + 10;
//        int[][][] a = new int[d + 1][d][d];
//        System.out.println(a[0]);
        T t = new T(99);
        t.f(1);
        int a = 1, b = 1;
        a = b += 5;
        System.out.println(a);
        System.out.println(b);
    }
}
