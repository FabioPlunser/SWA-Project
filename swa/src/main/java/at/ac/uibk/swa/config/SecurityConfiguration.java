package at.ac.uibk.swa.config;

import at.ac.uibk.swa.controllers.SwaErrorController;
import at.ac.uibk.swa.models.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.util.matcher.*;

import java.util.Arrays;

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
@EnableMethodSecurity
public class SecurityConfiguration {

    static final RequestMatcher API_ROUTES = new AntPathRequestMatcher("/api/**");
    static final RequestMatcher ADMIN_ROUTES = new AntPathRequestMatcher("/admin/**");

    private static final RequestMatcher ERROR_ROUTES = new OrRequestMatcher(
            Arrays.stream(SwaErrorController.errorEndpoints)
                    .map(x ->new AntPathRequestMatcher(x))
                    .toArray(AntPathRequestMatcher[]::new)
    );

    /**
     * Request Matcher matching all API-Routes that should be accessible to everyone.
     */
    private static final RequestMatcher PUBLIC_API_ROUTES = new OrRequestMatcher(
            new AntPathRequestMatcher("/api/login"),
            new AntPathRequestMatcher("/api/register"),
            ERROR_ROUTES,
            new AntPathRequestMatcher("/token")
    );

    /**
     * Request Matcher matching all API-Routes that should be protected.
     */
    public static final RequestMatcher PROTECTED_API_ROUTES = new AndRequestMatcher(
            API_ROUTES, new NegatedRequestMatcher(PUBLIC_API_ROUTES)
    );

    /**
     * Request Matcher matching all Routes that should be protected.
     */
    public static final RequestMatcher PROTECTED_ROUTES = new AndRequestMatcher(
            new NegatedRequestMatcher(PUBLIC_API_ROUTES),
            new OrRequestMatcher(API_ROUTES, ADMIN_ROUTES)
    );

    public static final RequestMatcher PUBLIC_ROUTES = new OrRequestMatcher(
            new NegatedRequestMatcher(new OrRequestMatcher(API_ROUTES, ADMIN_ROUTES)),
            AnyRequestMatcher.INSTANCE
    );

    public SecurityConfiguration(final PersonAuthenticationProvider authenticationProvider) {
        super();
        this.provider = authenticationProvider;
    }
    @Autowired
    @Qualifier("personAuthenticationProvider")
    private final AuthenticationProvider provider;

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
                .addFilterBefore(bearerAuthenticationFilter(http), AnonymousAuthenticationFilter.class)
                .addFilterBefore(cookieAuthenticationFilter(http), AnonymousAuthenticationFilter.class)
                // Specify which Routes/Endpoints should be protected and which ones should be accessible to everyone.
                .authorizeHttpRequests(auth ->
                    auth
                            // Only allow authenticated Users to use the API
                            .requestMatchers(PROTECTED_API_ROUTES).authenticated()
                            .requestMatchers(ADMIN_ROUTES).hasAuthority(Permission.ADMIN.toString())
                            // Permit everyone to get the static resources
                            .requestMatchers(AnyRequestMatcher.INSTANCE).permitAll()
                )

                // Disable CORS, CSRF as well as the default Web Security Login and Logout Pages.
                .csrf().disable()
                .cors().disable()
                .formLogin().disable()
                .logout().disable();

        return http.build();
    }

    @Bean
    AbstractAuthenticationProcessingFilter bearerAuthenticationFilter(HttpSecurity http) throws Exception {
        final AbstractAuthenticationProcessingFilter filter = new BearerTokenAuthenticationFilter(PROTECTED_API_ROUTES);

        filter.setAuthenticationManager(authManager(http));
        filter.setAuthenticationFailureHandler(restAuthenticationFailureHandler());

        return filter;
    }

    @Bean
    AbstractAuthenticationProcessingFilter cookieAuthenticationFilter(HttpSecurity http) throws Exception {
        final AbstractAuthenticationProcessingFilter filter = new CookieTokenAuthenticationFilter(ADMIN_ROUTES);

        filter.setAuthenticationManager(authManager(http));
        filter.setAuthenticationFailureHandler(restAuthenticationFailureHandler());

        return filter;
    }

    @Bean
    private static RestAuthenticationFailureHandler restAuthenticationFailureHandler() {
        return new RestAuthenticationFailureHandler();
    }
    @Bean
    private static AuthenticationEntryPoint restAuthenticationEntryPoint() {
        return new RestAuthenticationEntryPoint();
    }
}
