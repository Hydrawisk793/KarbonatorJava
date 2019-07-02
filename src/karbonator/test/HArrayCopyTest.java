package karbonator.test;

public class HArrayCopyTest {

    public static class Foo implements Cloneable {

        @Override
        protected Foo clone() throws CloneNotSupportedException {
            return new Foo(foo, bar);
        }
        
        public void set(int foo, int bar) {
            this.foo = foo;
            this.bar = bar;
        }
        
        public Foo() {
            this(0, 0);
        }
        public Foo(Foo o) {
            this(o.foo, o.bar);
        }        
        public Foo(int foo, int bar) {
            set(foo, bar);
        }
        
        @Override
        public String toString() {
            return String.format("[%d, %d]", foo, bar);
        }
        
        public int foo;
        public int bar;
        
    }

    public static void main(String [] args) {
        Foo [] foo = new Foo [] {
            new Foo(0x2E, 0x2E), new Foo(0x30, 0x30), new Foo(0x57, 0x57), new Foo(0x9F, 0x9F), 
            new Foo(0xF8, 0xF8), new Foo(0x5E, 0x5E), new Foo(0x77, 0x77), new Foo(0x89, 0x89)
        };
        Foo [] bar = new Foo [foo.length];
        
        System.arraycopy(foo, 0, bar, 0, bar.length);
        
        bar[0].set(0xFF, 0xFF);
        bar[4].set(0xFF, 0xFF);
        bar[7].set(0xFF, 0xFF);

        for(int r1=0;r1<foo.length;++r1) {
            System.out.printf("%s ", foo[r1]);
        }
        System.out.println();
        for(int r1=0;r1<bar.length;++r1) {
            System.out.printf("%s ", bar[r1]);
        }
        System.out.println();
    }

}
