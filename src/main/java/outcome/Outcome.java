package outcome;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Arrays.*;
import static java.util.Collections.emptyList;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static outcome.Either.left;
import static outcome.Failure.fail;

/**
 * Created by guillermo on 21/10/15.
 */
public class Outcome<T> {

    private Either<List<Failure>, Optional<T>> either;

    public static <TT> Outcome<TT> maybe(Optional<TT> maybeSuccess) {
        Outcome<TT> result = new Outcome<>();
        result.either = Either.right(maybeSuccess);
        return result;
    }

    public void ifMaybePresent(Consumer<T> consumer) {
        if(isMaybePresent()) {
            consumer.accept(this.maybe());
        }
    }

    public static <T> Outcome<T> unit()
    {
        return maybe(Optional.empty());
    }

    public static <T> Outcome<T> unit(T t)
    {
        return maybe(t);
    }

    public static <T> Outcome<List<T>> sequence(List<Outcome<T>> outcomes){

        final Outcome<List<T>> unit = Outcome.maybe(new ArrayList<>(outcomes.size()));

        return outcomes
                .stream()
                .reduce(unit,
                        //Extract T instance and add it to unit, otherwise combine errors
                        (o1, o2) ->
                                o1.flatMapR(
                                        l -> o2.mapR(ec -> {

                                            l.add(ec);
                                            return l;
                                        })

                                ).dependingOn(o2),
                        //Merge all results into one
                        (ol1, ol2) ->
                                ol1.flatMapR(l1 -> ol2.mapR(l2 -> {

                                    final List<T> ll = new ArrayList<>(l1.size() + l2.size());

                                    ll.addAll(l1);
                                    ll.addAll(l2);
                                    return ll;
                                })));
    }

    public static <T> Outcome<T> maybe(T maybeSuccess) {
        return maybe(Optional.ofNullable(maybeSuccess));
    }

    public static <T> Outcome<T> emptyMaybe() {
        return maybe(Optional.<T>empty());
    }

    public static <T> Outcome<T> failure(final List<Failure> failures) {
        Outcome<T> result = new Outcome<>();
        result.either = left(failures);
        return result;
    }

    public static <T> Outcome<T> failure(final Optional<Failure> failure) {

        if(failure.isPresent())
        {
            return failure(failure.get());
        }
        else
        {
            Outcome<T> result = new Outcome<>();
            result.either = left(emptyList());
            return result;
        }

    }


    public static <T> Outcome<T> maybeFailure(final List<Failure> failures) {

        if(failures.isEmpty()) {
            return Outcome.emptyMaybe();
        } else {
            Outcome<T> result = new Outcome<>();
            result.either = left(failures);
            return result;
        }
    }


    public static <T> Outcome<T> failure(final Failure failure) {
        Outcome<T> result = new Outcome<>();
        result.either = left(asList(failure));
        return result;
    }

    public static <T> Outcome<T> failure(Throwable e) {
        Outcome<T> result = new Outcome<>();
        result.either = left(asList(fail(e.getMessage())));
        return result;
    }

    public static <T> CompletableFuture<Outcome<T>> asyncFailure(String message)
    {
        return completedFuture(failure(fail(message)));
    }


    public boolean isMaybe() {
        return either.isRight();
    }

    public boolean isMaybePresent() {

        if (either.isRight()) {
            return either.getRight().isPresent();
        } else {
            return false;
        }
    }

    public boolean isFailure() {
        return either.isLeft();
    }

    public T maybe() {
        if (either.isRight() && either.getRight().isPresent()) {
            return either.getRight().get();
        } else {
            throw new NoSuchElementException("No value present");
        }
    }

    public List<Failure> failures() {
        if (either.isLeft()) {
            return either.getLeft();
        } else {
            throw new NoSuchElementException("No failures available");
        }
    }

    public final <TT> Outcome<TT> mapR(Function<T, TT> transform) {

        if (isMaybe()) {
            return maybe(this.either.getRight().map(transform));
        } else {
            return (Outcome<TT>) this;
        }
    }

    public final <TT> Outcome<TT> mapL(Function<List<Failure>, List<Failure>> add) {

        if (isFailure()) {
            return (Outcome<TT>) failure(add.apply(failures()));
        } else {
            return (Outcome<TT>) this;
        }
    }

    public final <TT> Outcome<TT> flatMapR(Function<T, Outcome<TT>> f) {
        if (isMaybe()) {
            return this.either.getRight().map(f).orElse((Outcome<TT>)this);
        } else {
            return (Outcome<TT>) this;
        }
    }

    public final <TT> Outcome<TT> inferred()
    {
        return (Outcome<TT>)this;
    }

    /**
     * Fails outcome if that fails and merge failures
     *
     * @param that
     * @param <U>
     * @return
     */
    public final <U> Outcome<T> dependingOn(Outcome<U> that) {

        final Outcome<U> o1 = that;

        if (this.isFailure() || o1.isFailure()) {

            if(!this.isFailure() && o1.isFailure())
            {
                return (Outcome<T>)o1;
            }
            else if(this.isFailure() && !o1.isFailure())
            {
                return this;
            }
            else if(this.isFailure() && o1.isFailure()){

                List<Failure> failures = new ArrayList<>(o1.failures());

                //Add only those failures that doesn't exist (avoid repeated values)
                this.failures()
                        .stream()
                        .filter(f -> !failures.contains(f))
                        .forEach(f -> failures.add(f));

                return Outcome.<T>failure(failures);
            }
            else{
                return this;
            }
        }
        else {
            return this;
        }
    }

    public final <TT> Outcome<TT> fold(Function<List<Failure>, TT> failure, Function<T, TT> success) {

        if (isMaybe()) {
            return maybe(this.either.getRight().map(success));
        } else {
            return maybe(Optional.ofNullable(failure.apply(this.either.getLeft())));
        }
    }


    public final <A,B> Outcome<B> combine( Outcome<A> combine, BiFunction<Outcome<T>,Outcome<A>,Outcome<B>>
            combineFunction) {
        return combineFunction.apply(this,combine);
    }

    public final T orElseGet(Supplier<T> otherValue) {
        if (isMaybe() && either.getRight().isPresent()) {
            return either.getRight().get();
        } else {
            return otherValue.get();
        }
    }

    public final Outcome<T> orIfEmpty(Supplier<Failure> failure) {
        if (isMaybePresent()) {
            return this;
        } else {
            final ArrayList<Failure> newfailures;
            if(isFailure()) {
                newfailures = new ArrayList<>(failures());
                newfailures.add(failure.get());
            }
            else
            {
                newfailures = new ArrayList<>(1);
                newfailures.add(failure.get());
            }
            return Outcome.failure(newfailures);
        }
    }
}

