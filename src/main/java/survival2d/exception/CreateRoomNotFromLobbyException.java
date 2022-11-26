package survival2d.exception;

public class CreateRoomNotFromLobbyException extends RuntimeException {

  public CreateRoomNotFromLobbyException(String playerName) {
    super("player: " + playerName + " create room when not in lobby");
  }
}
