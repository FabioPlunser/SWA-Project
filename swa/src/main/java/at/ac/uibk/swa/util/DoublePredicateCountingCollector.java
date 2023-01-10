package at.ac.uibk.swa.util;

import java.util.Set;
import java.util.function.*;
import java.util.stream.Collector;

public class DoublePredicateCountingCollector<T>
        implements Collector<T, DoubleCounter, DoubleCounter>
{
    private Predicate<T> _firstPredicate;
    private Predicate<T> _secondPredicate;

    public DoublePredicateCountingCollector(
            Predicate<T> firstPredicate, Predicate<T> secondPredicate)
    {
        this._firstPredicate = firstPredicate;
        this._secondPredicate = secondPredicate;
    }

    @Override
    public Supplier<DoubleCounter> supplier() {
        return () -> new DoubleCounter();
    }

    @Override
    public BiConsumer<DoubleCounter, T> accumulator() {
        return (acc, elem) -> {
            if (this._firstPredicate.test(elem)) {
                acc.matchesFirst ++;
            }
            if (this._secondPredicate.test(elem)) {
                acc.matchesSecond ++;
            }
        };
    }

    @Override
    public BinaryOperator<DoubleCounter> combiner() {
        return (acc1, acc2) -> new DoubleCounter(
                acc1.matchesFirst + acc2.matchesFirst,
                acc1.matchesSecond + acc2.matchesSecond
        );
    }

    @Override
    public Function<DoubleCounter, DoubleCounter> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(Characteristics.IDENTITY_FINISH, Characteristics.UNORDERED);
    }
}