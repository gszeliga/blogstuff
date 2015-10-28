package outcome;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by guillermo on 28/10/15.
 */
public class TestOutcome {

    @Test
    public void kk(){
        Outcome<Object> first = Outcome.failure(Failure.fail("First"));
        Outcome<Object> second = Outcome.failure(Failure.fail("Second"));

        Outcome<String> result = second.mapR(o -> "").dependingOn(first);

        Assert.assertThat(result.failures(), Matchers.hasSize(2));
    }

}