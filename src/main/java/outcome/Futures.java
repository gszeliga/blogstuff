package outcome;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static outcome.Outcome.maybe;

/**
 * Created by guillermo on 21/10/15.
 */
public class Futures {

    public static <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> futures) {

        final CompletableFuture<List<T>> unit = completedFuture(new ArrayList<>(futures.size()));

        return futures
                .stream()
                .reduce(unit,
                        (acc, f) -> acc.thenCombine(f, (l, e) -> {
                            l.add(e);
                            return l;
                        }),
                        (f1, f2) -> f1.thenCombine(f2, (a, b) -> {

                            List<T> ll = new ArrayList<>(a.size() + b.size());

                            ll.addAll(a);
                            ll.addAll(b);

                            return ll;

                        }));
    }

    public static class CompositionSources<B>
    {
        public interface MergingStage<B,C>{
            Function<CompletableFuture<Outcome<B>>,CompletableFuture<Outcome<B>>> by
                    (BiFunction<B, Outcome<C>, Outcome<B>> f);
        }

        CompositionSources()
        {

        }

        public <C> MergingStage<B,C> value(CompletableFuture<Outcome<C>> concept){

            return f -> builder -> {

                BiFunction<Outcome<B>,Outcome<C>,Outcome<B>> g =
                        (ob, c) -> ob.dependingOn(c).flatMapR(b -> f.apply(b, c));

                return combine(builder, Optional.ofNullable(concept), g);
            };
        }

        //Ugly java type declaration, ughhh
        <B,C> CompletableFuture<Outcome<B>> combine(CompletableFuture<Outcome<B>>  builder,
                                                           Optional<CompletableFuture<Outcome<C>>> concept,
                                                           BiFunction<Outcome<B>,Outcome<C>,Outcome<B>> f)
        {
            if(concept.isPresent())
            {
                return builder.thenCombine(concept.get(),f);
            }
            else
            {
                return builder;
            }
        }

        public static <B> CompositionSources<B> stickedTo(Class<B> clazz)
        {
            return new CompositionSources<>();
        }
    }

    public interface Creator<C>
    {
        C create();
    }

    public static class FutureComposition<C,B extends Creator<C>>{

        private final Supplier<CompletableFuture<Outcome<B>>> _partial;

        private FutureComposition(Supplier<CompletableFuture<Outcome<B>>> state)
        {
            _partial=state;
        }

        public FutureComposition<C,B> using(Function<CompletableFuture<Outcome<B>>, CompletableFuture<Outcome<B>>> stage)
        {
            return new FutureComposition<>(() -> stage.apply(_partial.get()));
        }

        public CompletableFuture<Outcome<C>> perform()
        {
            return _partial.get().thenApply(o -> o.mapR(Creator::create));
        }

        public static <C,B extends Creator<C>> FutureComposition<C,B> compose(B state)
        {
            return new FutureComposition<>(() -> completedFuture(maybe(state)));
        }
    }

}
