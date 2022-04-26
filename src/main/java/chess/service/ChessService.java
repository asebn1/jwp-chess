package chess.service;

import chess.domain.board.ChessBoardGenerator;
import chess.domain.game.dto.MoveDTO;
import chess.domain.piece.property.Team;
import chess.domain.position.Position;
import chess.domain.gameRoom.ChessGame;
import chess.domain.gameRoom.dao.ChessGameRoomDAO;
import chess.domain.game.Movement;
import chess.domain.game.dao.MovementDAO;
import chess.domain.gameRoom.dto.ChessGameRoomInfoDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chess.exception.InvalidMoveException;
import org.springframework.stereotype.Service;

@Service
public final class ChessService {

    private final ChessGameRoomDAO chessGameDAO;
    private final MovementDAO movementDAO;

    public ChessService(final ChessGameRoomDAO chessGameDAO, final MovementDAO movementDAO) {
        this.chessGameDAO = chessGameDAO;
        this.movementDAO = movementDAO;
    }

    public String addChessGame(final String gameName, final String password) {
        ChessGame chessGame = new ChessGame(new ChessBoardGenerator());
        chessGame.setName(gameName);
        chessGame.setPassword(password);

        return chessGameDAO.addGame(chessGame);
    }

    public ChessGame getChessGamePlayed(final String gameId) {
        List<Movement> movementByGameId = movementDAO.findMovementByGameId(gameId);
        ChessGame chessGame = ChessGame.initChessGame();
        for (Movement movement : movementByGameId) {
            chessGame.execute(movement);
        }
        chessGame.setId(gameId);
        return chessGame;
    }

    public ChessGame movePiece(final String gameId, final MoveDTO moveDTO){
        final ChessGame chessGame = getChessGamePlayed(gameId);
        final String source = moveDTO.getSource();
        final String target = moveDTO.getTarget();
        final Team team = moveDTO.getTeam();

        validateCurrentTurn(chessGame, team);
        move(chessGame, new Movement(Position.of(source), Position.of(target)), team);
        if (chessGame.isGameSet()) {
            chessGameDAO.updateGameEnd(gameId);
        }
        return chessGame;
    }

    private void validateCurrentTurn(final ChessGame chessGame, final Team team) {
        if (!chessGame.getChessBoard().getCurrentTurn().equals(team)) {
            throw new InvalidMoveException("플레이어의 턴이 아닙니다");
        }
    }

    private void move(final ChessGame chessGame, final Movement movement, final Team team) {
        chessGame.execute(movement);
        movement.setGameId(chessGame.getId());
        movement.setTeam(team);
        int insertedRowCount = movementDAO.addMoveCommand(movement);

        if (insertedRowCount == 0) {
            throw new InvalidMoveException("플레이어의 턴이 아닙니다");
        }
    }

    private Map<String, Object> result(final ChessGame chessGame) {
        Map<String, Object> model = new HashMap<>();
        model.put("winner", chessGame.getChessBoard().calculateWhoWinner().toString());

        return model;
    }

    public List<ChessGameRoomInfoDTO> getGames() {
        return chessGameDAO.findActiveGames();
    }

    public ChessGameRoomInfoDTO findGameById(String id) {
        return chessGameDAO.findGameById(id);
    }

    public Map<String, Object> getResult(ChessGame chessGame) {
        final Map<String, Object> model = new HashMap<>();
        chessGameDAO.updateGameEnd(chessGame.getId());
        model.put("gameResult", result(chessGame));
        model.put("isGameSet", Boolean.TRUE);

        return model;
    }
}
