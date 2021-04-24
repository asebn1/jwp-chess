package chess.domain.piece;

import java.util.Map;

import chess.domain.board.Score;
import chess.domain.position.Direction;
import chess.domain.position.Position;

public class Knight extends Piece {

    private static final String NAME = "n";
    private static final Score SCORE = new Score(2.5);

    public Knight(Color color) {
        super(NAME, color, SCORE);
    }

    @Override
    public boolean canMove(Map<Position, Piece> board, Position source, Position target) {
        return Direction.knightDirection()
            .stream()
            .anyMatch(direction -> source.sum(direction).equals(target));
    }

}