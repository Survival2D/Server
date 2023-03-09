package survival2d.match.entity.base;

public interface Destroyable {

  boolean isDestroyed();

  void setDestroyed(boolean destroyed);

  default void markDestroyed() {
    setDestroyed(true);
  }
}
