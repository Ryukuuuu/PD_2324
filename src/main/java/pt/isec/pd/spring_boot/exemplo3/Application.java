package pt.isec.pd.spring_boot.exemplo3;

import client.model.ModelManager;
import com.nimbusds.jose.crypto.impl.HMAC;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import database.DatabaseConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import pt.isec.pd.spring_boot.exemplo3.security.RsaKeysProperties;
import pt.isec.pd.spring_boot.exemplo3.security.UserAuthenticationProvider;
import server.MainServer;

import java.rmi.RemoteException;
import java.util.Arrays;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Application {

	private final RsaKeysProperties rsaKeys;

	public Application(RsaKeysProperties rsaKeys){this.rsaKeys = rsaKeys;}

	@Configuration
	@EnableWebSecurity
	public class SecurityConfig
	{
		@Autowired
		private UserAuthenticationProvider authProvider;

		@Autowired
		public void configAuthentication(AuthenticationManagerBuilder auth) {auth.authenticationProvider(authProvider);}

		@Bean
		public SecurityFilterChain loginFilterChain(HttpSecurity http) throws Exception
		{
			return http
				.csrf(AbstractHttpConfigurer::disable)
				.securityMatcher("/login")
				.authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
				.httpBasic(Customizer.withDefaults())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.build();
		}

		@Bean
		public SecurityFilterChain signingFilterChain(HttpSecurity http) throws Exception
		{
			return http
					.csrf(AbstractHttpConfigurer::disable)
					.securityMatcher("/signing")
					.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
					.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
					.build();
		}
		/*

		VERSAO FINAL COM AUTENTICACAO

		@Bean
		public SecurityFilterChain eventsFilterChain(HttpSecurity http) throws Exception{
			return http
					.csrf(AbstractHttpConfigurer::disable)
					.securityMatcher("events/","events/**")
					.authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
					.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
					.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
					.build();
		}*/
		//Versao de teste sem autenticacao
		@Bean
		public SecurityFilterChain eventsFilterChain(HttpSecurity http) throws Exception{
			return http
					.csrf(AbstractHttpConfigurer::disable)
					.securityMatcher("/events","/events/**")
					.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
					.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
					.build();
		}

		@Bean
		public SecurityFilterChain unauthenticatedFilterChain(HttpSecurity http) throws Exception
		{
			return http
				.csrf(AbstractHttpConfigurer::disable)
				.securityMatcher("/hello", "/hello/**")
				.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.build();
		}


		@Bean
		public SecurityFilterChain genericFilterChain(HttpSecurity http) throws Exception
		{
			return http
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
				.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.build();
		}
	}

	@Bean
	JwtEncoder jwtEncoder()
	{
		JWK jwk = new RSAKey.Builder(rsaKeys.publicKey()).privateKey(rsaKeys.privateKey()).build();
		JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
		return new NimbusJwtEncoder(jwkSource);
	}

	@Bean
	JwtDecoder jwtDecoder()
	{
		return NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey()).build();
	}

	public static void main(String[] args) throws RemoteException {
		MainServer mainServer = new MainServer(args);
		mainServer.run();
		SpringApplication.run(Application.class, args);
	}
}
