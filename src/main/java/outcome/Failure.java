package outcome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by guillermo on 21/10/15.
 */
public interface Failure {
    String getDefaultMessage();

    static boolean passed(Optional<Failure> result){
        return result.isPresent();
    }

    final class SimpleFailure implements Failure{

        private final String _message;

        SimpleFailure(String message) {
            _message = message;
        }

        @Override
        public String getDefaultMessage() {
            return _message;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SimpleFailure that = (SimpleFailure) o;

            return !(_message != null ? !_message.equals(that._message) : that._message != null);

        }

        @Override
        public int hashCode() {
            return _message != null ? _message.hashCode() : 0;
        }
    }

    static <T> Optional<Failure> fail(String defaultMessage, String field, T value) {
        return Optional.ofNullable(failRaw(defaultMessage, field, value));
    }

    static <T> Failure failRaw(String defaultMessage, String field, T value) {

        AcceptanceFailure<T> fail = new AcceptanceFailure<>();
        fail.path = field;
        fail.value = value;
        fail._defaultMessage = defaultMessage;

        return fail;
    }

    static <T> List<Failure> failList(String defaultMessage, String field, T value) {

        AcceptanceFailure<T> fail = new AcceptanceFailure<>();
        fail.path = field;
        fail.value = value;
        fail._defaultMessage = defaultMessage;


        return Arrays.asList(fail);
    }


    static Failure fail(String defaultMessage) {

        return new SimpleFailure(defaultMessage);
    }

    static List<Failure> successList(){
        // don't return collections.emptyList();
        return new ArrayList<>();
    }

    static Optional<Failure> success(){
        return Optional.empty();
    }


    class AcceptanceFailure<T> implements Failure {

        private String _defaultMessage;
        private String path;
        private T value;

        @Override
        public String getDefaultMessage() {
            return _defaultMessage;
        }

        public String getPath() {
            return path;
        }

        public T getValue() {
            return value;
        }
    }
}
