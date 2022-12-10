package at.ac.uibk.swa.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.util.matcher.*;

/**
 * <p>
 *     Class for configuring the Authentication Process of the Web-Server.
 * </p>
 * <p>
 *     All API-Paths (except for "/api/login") are secured using an Authentication Token.
 * </p>
 * <p>
 *     The Front-End can fetch a Token from "/api/login" using a User's username and password-Hash.
 * </p>
 * <p>
 *     This Token can then be used in the Authentication Header as a Bearer Token to authenticate
 *     the user for the API.
 * </p>
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

    private static final RequestMatcher PUBLIC_API_ROUTES = new OrRequestMatcher(
            new AntPathRequestMatcher("/api/login"),
            new AntPathRequestMatcher("/api/register"),
            new AntPathRequestMatcher("/token"),
            new AntPathRequestMatcher("/admin/**")
    );

    private static final RequestMatcher PROTECTED_ROUTES = new AndRequestMatcher(
            new OrRequestMatcher(
                    new AntPathRequestMatcher("/api/**")
//                    new AntPathRequestMatcher("/admin/**")
            ),
            new NegatedRequestMatcher(PUBLIC_API_ROUTES)
    );

    private AuthenticationProvider provider;

    public SecurityConfiguration(final AuthenticationProvider authenticationProvider) {
        super();
        this.provider = authenticationProvider;
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(provider);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Use the custom AuthenticationProvider and AuthenticationFilter
                .authenticationProvider(provider)
                .addFilterBefore(authenticationFilter(http), AnonymousAuthenticationFilter.class)
                // Specify which Routes/Endpoints should be protected and which ones should be accessible to everyone.
                .authorizeHttpRequests((auth) ->
                    auth
                            // Anyone should be able to log in (alias for getting a Token)
                            .requestMatchers(PUBLIC_API_ROUTES).permitAll()
                            // Only allow authenticated Users to use the API
                            .requestMatchers("/api/**").authenticated()
//                            .requestMatchers("/admin/**").hasAuthority(Permission.ADMIN.toString())
                            // Permit everyone to get the static resources
                            .requestMatchers("/admin/**").permitAll()
                            .requestMatchers("/**").permitAll()
                )

                // Disable CORS, CSRF as well as the default Web Security Login and Logout Pages.
                .csrf().disable()
                .cors().disable()
                .formLogin().disable()
                .logout().disable();

        return http.build();
    }

    @Bean
    AuthenticationFilter authenticationFilter(HttpSecurity http) throws Exception {
        final AuthenticationFilter filter = new AuthenticationFilter(PROTECTED_ROUTES);
        filter.setAuthenticationManager(authManager(http));
        return filter;
    }
}
