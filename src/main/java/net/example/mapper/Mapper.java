package net.example.mapper;

public interface Mapper<T, S> {

    T mapFrom(S source);
}
