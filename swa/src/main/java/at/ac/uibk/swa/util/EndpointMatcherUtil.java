package at.ac.uibk.swa.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.util.matcher.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Helper Class for keeping track of the Endpoints and their respective required {@link at.ac.uibk.swa.models.Permission}s.
 *
 * @author David Rieser
 */
// All your Constructors are belong to us!
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class EndpointMatcherUtil {

    //region Base Routes
    @Value("${swa.api.base}")
    private String API_BASE_ROUTE;
    @Value("${swa.admin.base}")
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

    private final String API_LOGIN_ENDPOINT = ApiRoute(LOGIN_ENDPOINT);
    private final String API_LOGOUT_ENDPOINT = ApiRoute(LOGOUT_ENDPOINT);
    private final String API_REGISTER_ENDPOINT = ApiRoute(REGISTER_ENDPOINT);
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
                // Map them to their respective API-Endpoints
                .map(r -> this.ApiRoute(r))
                .toArray(String[]::new);

    private final RequestMatcher ERROR_ROUTES = new OrRequestMatcher(
            Arrays.stream(ERROR_ENDPOINTS)
                    .map(AntPathRequestMatcher::new)
                    .toArray(AntPathRequestMatcher[]::new)
    );
    //endregion

    //region Route Matchers
    public final RequestMatcher ANONYMOUS_ROUTES = AnyRequestMatcher.INSTANCE;
    public final RequestMatcher API_ROUTES = new AntPathRequestMatcher(ApiRoute("/**"));
    public final RequestMatcher ADMIN_ROUTES = new AntPathRequestMatcher(AdminRoute("/**"));

    private final RequestMatcher PUBLIC_API_ROUTES = new OrRequestMatcher(
            new AntPathRequestMatcher(API_LOGIN_ENDPOINT),
            // NOTE: DON'T ADD THE LOGOUT-ENDPOINT TO PUBLIC ROUTES, THE LOGOUT IS DONE USING THE TOKEN FROM THE REQUEST.
            // new AntPathRequestMatcher(API_LOGOUT_ENDPOINT),
            new AntPathRequestMatcher(API_REGISTER_ENDPOINT),
            ERROR_ROUTES
    );

    /**
     * Request Matcher matching all API-Routes that should be protected.
     */
    public final RequestMatcher PROTECTED_API_ROUTES = new AndRequestMatcher(
            API_ROUTES, new NegatedRequestMatcher(PUBLIC_API_ROUTES)
    );

    /**
     * Request Matcher matching all Routes that should be accessable to everyone.
     */
    public final RequestMatcher PUBLIC_ROUTES = new OrRequestMatcher(
            new NegatedRequestMatcher(new OrRequestMatcher(API_ROUTES, ADMIN_ROUTES)),
            ANONYMOUS_ROUTES
    );

    /**
     * Request Matcher matching all Routes that should be protected.
     */
    public final RequestMatcher PROTECTED_ROUTES = new NegatedRequestMatcher(PUBLIC_ROUTES);
    //endregion

    //region Route Matchers
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
