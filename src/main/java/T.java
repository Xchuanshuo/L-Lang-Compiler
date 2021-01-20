import java.io.IOException;

/**
 * @author Legend
 * @data by on 20-12-12.
 * @description
 */
public class T {
    volatile Object obj = new Object();
    private int d;

    public T() {
        this.d = 99;
    }

    public void f(int a) throws InterruptedException {
//        c += 5;
//        d += 4;
//        int c = 1;
//        System.out.println(c);
        te(() -> {
            this.d = 10;
        });
        synchronized (obj) {
            while (true) {
                System.out.println(obj.hashCode() + ", "  + Thread.currentThread().getName());
            }
        }
    }

    public interface A {
        void t1();
    }

    public void te(A a) {
        a.t1();
    }

    public static void main(String[] args) throws Exception {
//        int[][] a = new int[3][4];
//        a[0][1] = 10;
//        a[1][2] = 10;
        int i = System.in.read();
        int d = i + 10;
        int[][][] a = new int[d + 1][d][d];
        a[1][1][1] = 9;
        System.out.println(a[0]);
        T t = new T();
        new Thread(() -> {
            try {
                t.f(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "1").start();
        Thread.sleep(1000);
        t.obj = new Object();
//        new Thread(() -> t.obj = new Object()).start();
        System.out.println("-----------------------");
        new Thread(() -> {
            try {
                t.f(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "2").start();
        new Thread(() -> {
            try {
                t.f(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "3").start();
//        int a = 1, b = 1;
//        a = b += 5;
//        System.out.println(a);
//        System.out.println(b);
    }
}
