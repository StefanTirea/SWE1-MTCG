package mtcg.model.user;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class UserData {

    private String username;
    private String password;

    public boolean valid() {
        return StringUtils.isNoneBlank(username, password);
    }
}
