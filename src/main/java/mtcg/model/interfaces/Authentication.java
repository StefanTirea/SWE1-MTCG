package mtcg.model.interfaces;

public interface Authentication {

    public String getUsername();

    public String getPasswordHash();

    public String getToken();
}
