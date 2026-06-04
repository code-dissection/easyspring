package definition.exception;

public class BeanDefinitionCreationException extends RuntimeException {

    public BeanDefinitionCreationException(String message) {
        super(message);
    }

    public BeanDefinitionCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
