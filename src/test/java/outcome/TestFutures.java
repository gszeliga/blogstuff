package outcome;

import org.junit.Test;
import outcome.TestFutures.Message.Builder;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static outcome.Futures.FutureComposition.compose;
import static outcome.Outcome.maybe;

/**
 * Created by guillermo on 21/10/15.
 */
public class TestFutures{

    public static class Message{

        private String _msg;

        private Message(String msg){
            _msg=msg;
        }

        @Override
        public String toString() {
            return _msg;
        }

        public static Builder begin(){
            return new Builder();
        }

        public static class Builder implements Futures.Creator<Message> {

            private String _text;
            private Integer _number;

            public Builder text(String text){
                _text=text;
                return this;
            }

            public Builder number(Integer number){
                _number=number;
                return this;
            }

            @Override
            public Message create() {
                return new Message(String.format(_text,_number));
            }
        }
    }

/*    @Test
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

    }*/

    @Test
    public void shouldCombineTwoFuturesUsingMerge2(){

        CompletableFuture<Outcome<String>> textf = completedFuture(maybe("Hi dude %s!"));
        CompletableFuture<Outcome<Integer>> numberf = completedFuture(maybe(22));

        Futures.CompositionSources<Builder> sources = Futures.CompositionSources.stickedTo(Builder.class);

        CompletableFuture<Outcome<Message>> message = compose(Message.begin())
                .using(sources.value(textf).by((b, text) -> text.mapR(b::text)))
                .using(sources.value(numberf).by((b, number) -> number.mapR(b::number)))
                .perform();

        System.out.println(message);

        message.thenAccept(System.out::println);

    }

}