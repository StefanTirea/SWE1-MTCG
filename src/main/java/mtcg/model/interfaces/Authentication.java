package mtcg.model.interfaces;

public interface Authentication {

    String getUsername();

    String getPasswordHash();

    String getToken();
}
