package justweb;

public class Holder<T> {

    private T held;

    public T get() { return held; }
    public void set(T held) { this.held = held; }

}
