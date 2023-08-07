package net.s3managerApi.mapper;

public interface Mapper<T, S> {

    T mapFrom(S source);
}
