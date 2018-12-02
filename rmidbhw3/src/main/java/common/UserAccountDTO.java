package common;

import java.io.Serializable;

public interface UserAccountDTO extends Serializable {

    public String getUsername();

    public String getPassword();
}
