package chess.domain.piece;

import java.util.Map;

import chess.domain.board.Score;
import chess.domain.position.Direction;
import chess.domain.position.Position;

public class Queen extends Piece {

    private static final String NAME = "q";
    private static final Score SCORE = new Score(9);

    public Queen(Color color) {
        super(NAME, color, SCORE);
    }

    private Direction targetDirection(Position source, Position target) {
        if (!source.isNotLinearPosition(target)) {
            return Direction.linearTargetDirection(source.difference(target));
        }
        return Direction.diagonalTargetDirection(source.difference(target));
    }

    @Override
    public boolean canMove(Map<Position, Piece> board, Position source, Position target) {
        if (source.isNotLinearPosition(target) && source.isNotDiagonalPosition(target)) {
            return false;
        }
        Direction direction = targetDirection(source, target);
        do {
            source = source.sum(direction);
        } while (!source.equals(target)
            && board.get(source).isEmpty() && source.isChessBoardPosition());
        return source.equals(target);
    }
}