package outcome;

import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by guillermo on 21/10/15.
 */
public abstract class Either<L,R> {

    public static <LL, RR> Either<LL, RR> left(final LL left) {
        return new Left<>(left);
    }

    public static <LL, RR> Either<LL, RR> right(final RR right) {
        return new Right<>(right);
    }

    public static <LL, RR,T> Either<LL, T> appendRight(final Either<LL, RR> v1,
                                                       final Either<LL, RR> v2,
                                                       final BiFunction<RR,RR,T> append)
    {
        return right(append.apply(v1.getRight(), v2.getRight()));

    }

    public abstract boolean isRight();
    public abstract boolean isLeft();

    public abstract L getLeft();
    public abstract R getRight();

    public final Left<L,R> left(){
        return (Left<L,R>)this;
    }

    public final Right<L,R> right(){
        return (Right<L,R>)this;
    }

    public final <X> Either<L,X> mapRight(Function<R,X> f)
    {
        if(isRight())
        {
            return new Right<>(f.apply(getRight()));
        }
        else
        {
            return (Either<L,X>)this;
        }
    }

    public final void ifRight(Consumer<R> f)
    {
        if(isRight())
        {
            f.accept(getRight());
        }
    }

    public final <X> Either<L,X> flatMapRight(Function<R,Either<L,X>> f)
    {
        if(isRight())
        {
            return f.apply(getRight());
        }
        else
        {
            return (Either<L,X>)this;
        }
    }

    public final <LL,RR> Either<LL,RR> fold(Function<L, LL> left, Function<R, RR> right)
    {
        if(isRight())
            return (Either<LL,RR>) new Right<L,RR>(right.apply(getRight()));
        else
        {
            return (Either<LL,RR>) new Left<LL,R>(left.apply(getLeft()));
        }

    }

    public final <LL,RR> Either<LL,RR> flatMap(Function<L, Either<LL, RR>> left, Function<R, Either<LL, RR>> right)
    {
        if(isRight())
            return right.apply(getRight());
        else
        {
            return left.apply(getLeft());
        }

    }

    public static class Left<L,R> extends Either<L,R>
    {
        private final L value;

        Left(L left)
        {
            value = left;
        }

        public <X> Left<X,R> map(Function<L, X> map)
        {
            return new Left(map.apply(value));
        }

        @Override
        public boolean isRight() {
            return false;
        }

        @Override
        public boolean isLeft() {
            return true;
        }

        @Override
        public L getLeft() {
            return value;
        }

        @Override
        public R getRight() {
            throw new NoSuchElementException("No value present");
        }

        public L orElse(Supplier<L> supplier)
        {
            return (value != null)? value : supplier.get();
        }

    }

    public static class Right<L,R> extends Either<L,R>
    {
        private final R value;

        Right(R right)
        {
            value = right;
        }

        public <X> Right<L,X> map(Function<R, X> map)
        {
            return new Right(map.apply(value));
        }

        @Override
        public boolean isRight() {
            return true;
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public L getLeft() {
            throw new NoSuchElementException("No value present");
        }

        @Override
        public R getRight() {
            return value;
        }

        public R orElse(Supplier<R> supplier)
        {
            return (value != null)? value : supplier.get();
        }

    }

}

