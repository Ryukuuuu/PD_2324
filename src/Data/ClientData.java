package Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class ClientData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private String name;
    private long id;
    private String email;
    private String password;
    private boolean logged;
    private boolean admin;

    public ClientData(String name, long id, String email, String password, boolean logged, boolean admin) {
        this.name = name;
        this.id = id;
        this.email = email;
        this.password = password;
        this.logged = logged;
        this.admin = admin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLogged() {
        return logged;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    //Note
    //Two User objects are equals if they have the same email
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientData clientData = (ClientData) o;
        return email.equals(clientData.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", logged=" + logged +
                ", admin=" + admin +
                '}';
    }
}
