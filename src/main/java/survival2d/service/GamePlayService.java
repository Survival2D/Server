package survival2d.service;

import survival2d.game.shared.PlayerHitData;
import survival2d.game.shared.PlayerInputData;
import survival2d.game.shared.PlayerSpawnData;
import com.tvd12.gamebox.math.Vec2;
import com.tvd12.gamebox.math.Vec3;
import java.util.List;

public interface GamePlayService {

  void handlePlayerInputData(String playerName, PlayerInputData inputData, float[] nextRotation);

  List<PlayerSpawnData> spawnPlayers(List<String> playerNames);

  boolean authorizeHit(String playerName, PlayerHitData playerHitData);

  void resetPlayersPositionHistory(List<String> playerNames);

  Vec3 playerMove(String name, Vec2 direction);
}
