package pt.isec.pd.spring_boot.exemplo3.security;

import data.ClientData;
import database.DatabaseConnection;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserAuthenticationProvider implements AuthenticationProvider
{

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        DatabaseConnection dbConnection = DatabaseConnection.getInstance();
        ClientData client = dbConnection.getClient(username,password);

        if (client != null) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            if(client.isAdmin())
                authorities.add(new SimpleGrantedAuthority("ADMIN"));
            else
                authorities.add(new SimpleGrantedAuthority("CLIENT"));
            return new UsernamePasswordAuthenticationToken(username, password, authorities);
        }

        return null;
    }

    @Override
    public boolean supports(Class<?> authentication)
    {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
