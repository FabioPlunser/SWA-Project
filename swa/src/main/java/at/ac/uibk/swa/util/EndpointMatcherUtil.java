package at.ac.uibk.swa.util;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.util.matcher.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Helper Class for keeping track of the Endpoints and their respective required {@link at.ac.uibk.swa.models.Permission}s.
 *
 * @author David Rieser
 */
@Component
public class EndpointMatcherUtil {

    //region Base Routes
    @Value("${swa.api.base:/api}")
    private String API_BASE_ROUTE;
    @Value("${swa.admin.base:/admin}")
    private String ADMIN_BASE_ROUTE;

    public String ApiRoute(String route) {
        return API_BASE_ROUTE + route;
    }
    public String AdminRoute(String route) {
        return ADMIN_BASE_ROUTE + route;
    }
    //endregion

    //region Login-, register-, and logout-Endpoints
    public static final String LOGIN_ENDPOINT = "/login";
    public static final String LOGOUT_ENDPOINT = "/logout";
    public static final String REGISTER_ENDPOINT = "/register";

    public String API_LOGIN_ENDPOINT;
    public String API_LOGOUT_ENDPOINT;
    public String API_REGISTER_ENDPOINT;
    //endregion

    //region Error Endpoints
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ErrorEndpoints {
        public static final String TOKEN_EXPIRED_ERROR_ENDPOINT = "/token-expired";
        public static final String AUTHENTICATION_ERROR_ENDPOINT = "/unauthorized";
        public static final String AUTHORIZATION_ERROR_ENDPOINT = "/forbidden";
        public static final String NOT_FOUND_ERROR_ENDPOINT = "/notFound";
        public static final String ERROR_ENDPOINT = "/error";
    }

    private final String[] ERROR_ENDPOINTS =
            // Get all Error Routes defined in this Class using Runtime Reflection
            Arrays.stream(ErrorEndpoints.class.getDeclaredFields())
                    // Only get static Fields of type <String> which contain the Error Endpoints.
                    .filter(ReflectionUtil.isAssignableFromPredicate(String.class))
                    .filter(ReflectionUtil::isStaticField)
                    // Get the Endpoints
                    .map(ReflectionUtil::<String>getStaticFieldValueTyped)
                    .toArray(String[]::new);

    private RequestMatcher ERROR_ROUTES;
    //endregion

    //region Route Matchers
    public RequestMatcher ANONYMOUS_ROUTES;
    public RequestMatcher API_ROUTES;
    public RequestMatcher ADMIN_ROUTES;

    private RequestMatcher PUBLIC_API_ROUTES;

    /**
     * Request Matcher matching all API-Routes that should be protected.
     */
    public RequestMatcher PROTECTED_API_ROUTES;

    /**
     * Request Matcher matching all Routes that should be accessable to everyone.
     */
    public RequestMatcher PUBLIC_ROUTES;

    /**
     * Request Matcher matching all Routes that should be protected.
     */
    public RequestMatcher PROTECTED_ROUTES;
    //endregion

    //region Initialization
    @PostConstruct
    private void init() {
        this.API_LOGIN_ENDPOINT = this.ApiRoute(LOGIN_ENDPOINT);
        this.API_LOGOUT_ENDPOINT = this.ApiRoute(LOGOUT_ENDPOINT);
        this.API_REGISTER_ENDPOINT = this.ApiRoute(REGISTER_ENDPOINT);

        this.ERROR_ROUTES = new OrRequestMatcher(
                Arrays.stream(this.ERROR_ENDPOINTS)
                        .map(this::ApiRoute)
                        .map(AntPathRequestMatcher::new)
                        .toArray(AntPathRequestMatcher[]::new)
        );

        this.ANONYMOUS_ROUTES = AnyRequestMatcher.INSTANCE;
        this.API_ROUTES = new AntPathRequestMatcher(ApiRoute("/**"));
        this.ADMIN_ROUTES = new AntPathRequestMatcher(AdminRoute("/**"));

        this.PUBLIC_API_ROUTES = new OrRequestMatcher(
                new AntPathRequestMatcher(this.API_LOGIN_ENDPOINT),
                // NOTE: DON'T ADD THE LOGOUT-ENDPOINT TO PUBLIC ROUTES, THE LOGOUT IS DONE USING THE TOKEN FROM THE REQUEST.
                // new AntPathRequestMatcher(API_LOGOUT_ENDPOINT),
                new AntPathRequestMatcher(this.API_REGISTER_ENDPOINT),
                this.ERROR_ROUTES
        );
        this.PROTECTED_API_ROUTES = new AndRequestMatcher(
                this.API_ROUTES, new NegatedRequestMatcher(this.PUBLIC_API_ROUTES)
        );

        this.PUBLIC_ROUTES = new OrRequestMatcher(
                new NegatedRequestMatcher(new OrRequestMatcher(this.API_ROUTES, this.ADMIN_ROUTES)),
                this.ANONYMOUS_ROUTES
        );
        this.PROTECTED_ROUTES = new NegatedRequestMatcher(this.PUBLIC_ROUTES);
    }
    //endregion

    //region Route Matching Helper Methods
    public boolean isPublicRoute(HttpServletRequest request) {
        return PUBLIC_ROUTES.matches(request);
    }

    public boolean isApiRoute(HttpServletRequest request) {
        return API_ROUTES.matches(request);
    }

    public boolean isAdminRoute(HttpServletRequest request) {
        return ADMIN_ROUTES.matches(request);
    }
    //endregion
}
