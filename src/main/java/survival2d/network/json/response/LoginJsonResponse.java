package survival2d.network.json.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LoginJsonResponse extends BaseJsonResponse {

  int userId;
  String userName;
}
