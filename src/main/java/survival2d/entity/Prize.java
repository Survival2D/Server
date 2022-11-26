package survival2d.entity;

import com.tvd12.ezyfox.annotation.EzyId;
import com.tvd12.ezyfox.database.annotation.EzyCollection;
import java.util.Date;
import lombok.Data;

@Data
@EzyCollection
public class Prize {

  @EzyId
  Long id;

  private Date date = new Date();
  private String username;
  private int prize;
}
