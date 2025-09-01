package backend.futurefinder.util;

@FunctionalInterface
public interface AsyncAction<T> {
    void execute(T item) throws Exception;
}