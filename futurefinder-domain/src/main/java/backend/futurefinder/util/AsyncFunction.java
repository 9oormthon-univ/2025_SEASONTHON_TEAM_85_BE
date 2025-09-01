package backend.futurefinder.util;

@FunctionalInterface
public interface AsyncFunction<T, R> {
    R apply(T item) throws Exception;
}
