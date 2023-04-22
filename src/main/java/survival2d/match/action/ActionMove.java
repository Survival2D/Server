package survival2d.match.action;

import com.badlogic.gdx.math.Vector2;


public record ActionMove(Vector2 direction, float rotation) implements PlayerAction {
}
