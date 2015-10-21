package outcome;

import org.junit.Test;
import outcome.Futures.MergingStage;
import outcome.TestFutures.Message.Builder;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static outcome.Futures.Composer.compose;
import static outcome.Futures.Composer2.compose2;
import static outcome.Futures.merge;
import static outcome.Futures.merge2;
import static outcome.Outcome.maybe;

/**
 * Created by guillermo on 21/10/15.
 */
public class TestFutures{

    public static class Message{
        private Message(){}

        public static Builder begin(){
            return new Builder();
        }

        public static class Builder implements Futures.Creator<Message> {
            public Builder text(String text){
                return this;
            }

            public Builder number(Integer number){
                return this;
            }

            @Override
            public Message create() {
                return null;
            }
        }
    }

    @Test
    public void shouldCombineTwoFutures(){

        CompletableFuture<Outcome<String>> textf = completedFuture(maybe("Hi dude %s!"));
        CompletableFuture<Outcome<Integer>> numberf = completedFuture(maybe(22));

        MergingStage<Builder, String> fillUpText =
                merge((b, text) -> text.mapR(b::text));

        MergingStage<Builder, Integer> fillUpNumber =
                merge((b, number) -> number.mapR(b::number));

         compose(Message.begin())
                .staging(fillUpText.merge(Optional.ofNullable(textf)))
                .staging(fillUpNumber.merge(Optional.ofNullable(numberf)))
                .apply();

    }

    @Test
    public void shouldCombineTwoFuturesUsingMerge2(){

        CompletableFuture<Outcome<String>> textf = completedFuture(maybe("Hi dude %s!"));
        CompletableFuture<Outcome<Integer>> numberf = completedFuture(maybe(22));

        compose2(Message.begin())
                .staging2(merge2(Builder.class,() -> textf).as((b, text) -> text.mapR(b::text)))
                .apply();

    }

}