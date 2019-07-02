package karbonator.test.memory;

import karbonator.memory.ByteBuffer;
import karbonator.memory.ByteOrder;

public class Foo {

    public static class Serializer implements HAbstractSerializer<Foo> {

        @Override
        public ByteBuffer serialize(Foo src, ByteOrder byteOrder) {
            ByteBuffer buffer = new ByteBuffer(byteOrder);
            
            buffer.writeInt32(src.bar);
            buffer.writeInt32(src.baz);
            buffer.writeInt32(src.qux);
            
            return buffer;
        }
        @Override
        public Foo unserialize(ByteBuffer buffer) {
            int bar = buffer.readInt32();
            int baz = buffer.readInt32();
            int qux = buffer.readInt32();

            return new Foo(bar, baz, qux);
        }
    
    }

    public int getBar() {
        return bar+qux;
    }
    public int getBaz() {
        return baz+qux;
    }
    
    public Foo(int bar, int baz, int qux) {
        this.bar = bar;
        this.baz = baz;
        this.qux = qux;
    }

    private int bar;
    private int baz;
    private int qux;

}
