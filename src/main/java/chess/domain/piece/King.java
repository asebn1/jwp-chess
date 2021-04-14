package chess.domain.piece;

public final class King extends GeneralPiece {

    private static final String INITIAL_NAME = "K";

    public King(final Color color) {
        super(color, INITIAL_NAME);
    }
}
