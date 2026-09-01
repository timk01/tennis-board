package tennisboard.request;

public final class ValidationPatterns {

    private ValidationPatterns() {
    }

    public static final String PLAYER_NAME =
            "^ *[\\p{L}]+(?:[ '\\-][\\p{L}]+)* *$";
}
