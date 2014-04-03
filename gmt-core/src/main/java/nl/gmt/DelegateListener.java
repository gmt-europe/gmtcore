package nl.gmt;

public interface DelegateListener<T> {
    void call(Object sender, T arg);
}
