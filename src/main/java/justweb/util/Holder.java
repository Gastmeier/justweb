package justweb.util;

public class Holder<T> {

    private T held;

    public Holder() {
    }

    public Holder(T held) {
        this.held = held;
    }

    public T get() { return held; }
    public void set(T held) { this.held = held; }

}
