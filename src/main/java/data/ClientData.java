package data;

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
    private boolean admin;

    public ClientData() {}
    public ClientData(String email,String password){
        this.email = email;
        this.password = password;
    }


    public ClientData(String name,long id,String email,String password){
        this.name = name;
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public ClientData(String name, long id, String email, String password, boolean admin) {
        this.name = name;
        this.id = id;
        this.email = email;
        this.password = password;
        this.admin = admin;
    }
    public ClientData(String name, long id, String email){
        this.name = name;
        this.id = id;
        this.email = email;
    }

    public ClientData(String email) {
        this.email = email;
    }


    public boolean hasInformationToDisplay(){
        if(name != null && email != null && id != 0){
            return true;
        }
        return false;
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
    public String getIdString(){return Long.toString(id);}

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
        return "Client{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", admin=" + admin +
                '}';
    }
}
