package outcome;

import outcome.Futures.CompositionSources.Partial;

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

    public static class CompositionSources<B>
    {
        private CompositionSources(){ }

        public interface Partial<B>
        {
            CompletableFuture<Outcome<B>> apply(CompletableFuture<Outcome<B>> b);
        }

        public interface MergingStage<B, V>{
            Partial<B> by(BiFunction<Outcome<B>, Outcome<V>, Outcome<B>> f);
        }

        public <V> MergingStage<B, V> value(CompletableFuture<Outcome<V>> value){

            return f -> builder
                     -> builder.thenCombine(value, (b, v) -> f.apply(b, v)
                                                              .dependingOn(b)
                                                              .dependingOn(v));
        }

        public static <B> CompositionSources<B> stickedTo(Class<B> clazz)
        {
            return new CompositionSources<>();
        }
    }

    public interface WannabeApplicative<V>
    {
        V apply();
    }

    public static class FutureCompositions<V , A extends WannabeApplicative<V>>{

        private final Supplier<CompletableFuture<Outcome<A>>> _partial;

        private FutureCompositions(Supplier<CompletableFuture<Outcome<A>>> state)
        {
            _partial=state;
        }

        public FutureCompositions<V, A> binding(Partial<A> stage)
        {
            return new FutureCompositions<>(() -> stage.apply(_partial.get()));
        }

        public CompletableFuture<Outcome<V>> perform()
        {
            return _partial.get().thenApply(p -> p.mapR(WannabeApplicative::apply));
        }

        public static <V, A extends WannabeApplicative<V>> FutureCompositions<V, A> begin(A applicative)
        {
            return new FutureCompositions<>(() -> completedFuture(maybe(applicative)));
        }
    }

}
