package karbonator.util;

/**
 * 
 * @author Hydarwisk793
 * @since 2017-10-09
 */
public interface EventListener<E> {
    void invoke(E e);
}
