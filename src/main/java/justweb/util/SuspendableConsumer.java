package justweb.util;

import co.paralleluniverse.fibers.Suspendable;

public interface SuspendableConsumer<T> {

    @Suspendable
    void accept(T callback);

}
