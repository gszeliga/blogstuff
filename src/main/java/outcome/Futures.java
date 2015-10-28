package outcome;

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
        public interface MergingStage<B,C>{
            Function<CompletableFuture<Outcome<B>>,CompletableFuture<Outcome<B>>> by
                    (BiFunction<Outcome<B>, Outcome<C>, Outcome<B>> f);
        }

        CompositionSources(){ }

        public <C> MergingStage<B,C> value(CompletableFuture<Outcome<C>> value){

            return f -> builder -> builder.thenCombine(value, (b, v) -> f.apply(b, v).dependingOn(b).dependingOn(v));
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

    public static class FutureComposition<V , A extends WannabeApplicative<V>>{

        private final Supplier<CompletableFuture<Outcome<A>>> _partial;

        private FutureComposition(Supplier<CompletableFuture<Outcome<A>>> state)
        {
            _partial=state;
        }

        public FutureComposition<V, A> nourish(Function<CompletableFuture<Outcome<A>>, CompletableFuture<Outcome<A>>> stage)
        {
            return new FutureComposition<>(() -> stage.apply(_partial.get()));
        }

        public CompletableFuture<Outcome<V>> perform()
        {
            return _partial.get().thenApply(p -> p.mapR(WannabeApplicative::apply));
        }

        public static <V, A extends WannabeApplicative<V>> FutureComposition<V, A> compose(A applicative)
        {
            return new FutureComposition<>(() -> completedFuture(maybe(applicative)));
        }
    }

}
