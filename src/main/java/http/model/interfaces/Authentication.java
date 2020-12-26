package http.model.interfaces;

import java.security.Principal;
import java.util.Collection;

public interface Authentication extends Principal {

    Collection<String> getRoles();
}
