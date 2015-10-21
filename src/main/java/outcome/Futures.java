package outcome;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

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

    public static <C> CompletableFuture<Outcome<C>> unit(C z){
        return  completedFuture(maybe(z));
    }

    //Ugly java type declaration, ughhh
    static <B,C> CompletableFuture<Outcome<B>> combine(CompletableFuture<Outcome<B>>  builder,
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

    public static <B,C> MergingStage<B,C> merge(BiFunction<B, Outcome<C>, Outcome<B>> f){

        return (concept) ->
                (builder) ->
                        combine(builder, concept,

                                (ob, c) ->
                                        ob.flatMapR(b -> f.apply(b, c)).dependingOn(c)
                        );
    }


    public interface MergingStage<B,C>{
        Function<CompletableFuture<Outcome<B>>,CompletableFuture<Outcome<B>>> merge
                (Optional<CompletableFuture<Outcome<C>>> concept);
    }

    public interface Creator<C>
    {
        C create();
    }

    public static class Composer<C,B extends Creator<C>>{

        private final CompletableFuture<Outcome<B>> _partial;

        private Composer(CompletableFuture<Outcome<B>> state)
        {
            _partial=state;
        }

        public Composer<C,B> staging(Function<CompletableFuture<Outcome<B>>, CompletableFuture<Outcome<B>>> stage)
        {
            return new Composer<>(stage.apply(_partial));
        }

        public CompletableFuture<Outcome<C>> apply()
        {
            return _partial.thenApply(o -> o.mapR(Creator::create));
        }

        public static <C,B extends Creator<C>> Composer<C,B> compose(B state)
        {
            return new Composer<>(Futures.unit(state));
        }
    }

}
