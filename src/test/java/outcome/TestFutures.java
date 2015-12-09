package outcome;

import org.junit.Test;
import outcome.Futures.CompositionSources.Partial;
import outcome.Futures.FutureCompositions;
import outcome.TestFutures.Message.Builder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;
import static outcome.Failure.fail;
import static outcome.Futures.FutureCompositions.begin;
import static outcome.Outcome.failure;
import static outcome.Outcome.maybe;

/**
 * Created by guillermo on 21/10/15.
 */
public class TestFutures{

    public static class Message{

        private final String _text;
        private final Integer _number;

        private Message(String msg, Integer number){
            _text =msg;
            _number=number;
        }

        public String getContent(){
            return String.format(_text,_number);
        }

        public static Builder applicative(){
            return new Builder();
        }

        public static class Builder implements Futures.WannabeApplicative<Message> {

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
            public Message apply() {
                return new Message(_text,_number);
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
    public void shouldCombineTwoFuturesSuccessfully() throws ExecutionException, InterruptedException {

        CompletableFuture<Outcome<String>> textf = completedFuture(maybe("Hi dude %s!"));
        CompletableFuture<Outcome<Integer>> numberf = completedFuture(maybe(22));

        Futures.CompositionSources<Builder> sources = Futures.CompositionSources.stickedTo(Builder.class);

        Partial<Builder> textPartialApply = sources.value(textf).by((builder, text) -> builder.flatMapR(b -> text.mapR(b::text)));
        Partial<Builder> numberPartialApply = sources.value(numberf).by((builder, number) -> builder.flatMapR(b -> number.mapR(b::number)));

        Outcome<Message> message = FutureCompositions.begin(Message.applicative())
                .binding(textPartialApply)
                .binding(numberPartialApply)
                .perform().get();

        assertThat(message.isMaybe(), equalTo(true));
        assertThat(message.maybe().getContent(), equalTo("Hi dude 22!"));
    }

    @Test
    public void shouldCollectSingleFailure() throws ExecutionException, InterruptedException {

        CompletableFuture<Outcome<String>> textf = completedFuture(maybe("Hi dude %s!"));
        CompletableFuture<Outcome<Integer>> numberf = completedFuture(maybe(22));

        Futures.CompositionSources<Builder> sources = Futures.CompositionSources.stickedTo(Builder.class);

        Outcome<Message> message = begin(Message.applicative())
                .binding(sources.value(textf).by((b, text) -> failure(fail("I just failed"))))
                .binding(sources.value(numberf).by((builder, number) -> builder.flatMapR(b -> number.mapR(b::number))))
                .perform().get();

        assertThat(message.isMaybe(), equalTo(false));
        assertThat(message.failures(), hasSize(1));
        assertThat(message.failures().get(0).getDefaultMessage(), equalTo("I just failed"));
    }

    @Test
    public void shouldCollectSecondFailure() throws ExecutionException, InterruptedException {

        CompletableFuture<Outcome<String>> textf = completedFuture(maybe("Hi dude %s!"));
        CompletableFuture<Outcome<Integer>> numberf = completedFuture(maybe(22));

        Futures.CompositionSources<Builder> sources = Futures.CompositionSources.stickedTo(Builder.class);

        Outcome<Message> message = begin(Message.applicative())
                .binding(sources.value(textf).by((builder, text) -> builder.flatMapR(b -> text.mapR(b::text))))
                .binding(sources.value(numberf).by((b, number) -> failure(fail("Me too!!"))))
                .perform().get();

        assertThat(message.isMaybe(), equalTo(false));
        assertThat(message.failures(), hasSize(1));
        assertThat(message.failures().get(0).getDefaultMessage(), equalTo("Me too!!"));
    }

    @Test
    public void shouldCollectBothFailuresFailure() throws ExecutionException, InterruptedException {

        CompletableFuture<Outcome<String>> textf = completedFuture(maybe("Hi dude %s!"));
        CompletableFuture<Outcome<Integer>> numberf = completedFuture(maybe(22));

        Futures.CompositionSources<Builder> sources = Futures.CompositionSources.stickedTo(Builder.class);

        Outcome<Message> message = begin(Message.applicative())
                .binding(sources.value(textf).by((b, text) -> failure(fail("I just failed"))))
                .binding(sources.value(numberf).by((b, number) -> failure(fail("Me too!!"))))
                .perform().get();

        assertThat(message.isMaybe(), equalTo(false));
        assertThat(message.failures(), hasSize(2));
    }

    @Test
    public void shouldSingleFailureFromValue() throws ExecutionException, InterruptedException {

        CompletableFuture<Outcome<String>> textf = completedFuture(maybe("Hi dude %s!"));
        CompletableFuture<Outcome<Integer>> numberf = completedFuture(failure(fail("Not a number!")));

        Futures.CompositionSources<Builder> sources = Futures.CompositionSources.stickedTo(Builder.class);

        Outcome<Message> message = begin(Message.applicative())
                .binding(sources.value(textf).by((builder, text) -> builder.flatMapR(b -> text.mapR(b::text))))
                .binding(sources.value(numberf).by((builder, number) -> builder.flatMapR(b -> number.mapR(b::number))))
                .perform().get();

        assertThat(message.isMaybe(), equalTo(false));
        assertThat(message.failures(), hasSize(1));
        assertThat(message.failures().get(0).getDefaultMessage(), equalTo("Not a number!"));
    }

    @Test
    public void shouldBothFailuresFromValues() throws ExecutionException, InterruptedException {

        CompletableFuture<Outcome<String>> textf = completedFuture(failure(fail("Try again")));
        CompletableFuture<Outcome<Integer>> numberf = completedFuture(failure(fail("Not a number!")));

        Futures.CompositionSources<Builder> sources = Futures.CompositionSources.stickedTo(Builder.class);

        Outcome<Message> message = begin(Message.applicative())
                .binding(sources.value(textf).by((builder, text) -> builder.flatMapR(b -> text.mapR(b::text))))
                .binding(sources.value(numberf).by((builder, number) -> builder.flatMapR(b -> number.mapR(b::number))))
                .perform().get();

        assertThat(message.isMaybe(), equalTo(false));
        assertThat(message.failures(), hasSize(2));
    }
}