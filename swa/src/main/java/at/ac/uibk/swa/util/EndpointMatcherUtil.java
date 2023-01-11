package at.ac.uibk.swa.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.web.util.matcher.*;

import java.util.Arrays;

/**
 * Helper Class for keeping track of the Endpoints and their respective required {@link at.ac.uibk.swa.models.Permission}s.
 *
 * @author David Rieser
 */
// All your Constructors are belong to us!
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EndpointMatcherUtil {

    //region Login-, register-, and logout-Endpoints
    public static final String LOGIN_ENDPOINT = "/api/login";
    public static final String LOGOUT_ENDPOINT = "/api/logout";
    public static final String REGISTER_ENDPOINT = "/api/register";
    //endregion

    //region Error Endpoints
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ErrorEndpoints {
        public static final String TOKEN_EXPIRED_ERROR_ENDPOINT = "/api/token-expired";
        public static final String AUTHENTICATION_ERROR_ENDPOINT = "/api/unauthorized";
        public static final String AUTHORIZATION_ERROR_ENDPOINT = "/api/forbidden";
        public static final String NOT_FOUND_ERROR_ENDPOINT = "/api/notFound";
        public static final String ERROR_ENDPOINT = "/api/error";
    }

    private static final String[] ERROR_ENDPOINTS =
            // Get all Error Routes defined in this Class using Runtime Reflection
            Arrays.stream(ErrorEndpoints.class.getDeclaredFields())
                // Only get static Fields of type <String> which contain the Error Endpoints.
                .filter(ReflectionUtil.isAssignableFromPredicate(String.class))
                .filter(ReflectionUtil::isStaticField)
                // Get the Endpoints
                .map(ReflectionUtil::getStaticFieldValueTyped)
                .toArray(String[]::new);

    private static final RequestMatcher ERROR_ROUTES = new OrRequestMatcher(
            Arrays.stream(ERROR_ENDPOINTS)
                    .map(AntPathRequestMatcher::new)
                    .toArray(AntPathRequestMatcher[]::new)
    );
    //endregion

    //region Route Matchers
    public static final RequestMatcher ANONYMOUS_ROUTES = AnyRequestMatcher.INSTANCE;
    public static final RequestMatcher API_ROUTES = new AntPathRequestMatcher("/api/**");
    public static final RequestMatcher ADMIN_ROUTES = new AntPathRequestMatcher("/src/admin/**");

    private static final RequestMatcher PUBLIC_API_ROUTES = new OrRequestMatcher(
            new AntPathRequestMatcher(LOGIN_ENDPOINT),
            // NOTE: DON'T ADD LOGOUT-ENDPOINT TO PUBLIC ROUTES, THE LOGOUT IS DONE USING THE TOKEN FROM THE REQUEST.
            // new AntPathRequestMatcher(LOGOUT_ENDPOINT),
            new AntPathRequestMatcher(REGISTER_ENDPOINT),
            ERROR_ROUTES
    );

    /**
     * Request Matcher matching all API-Routes that should be protected.
     */
    public static final RequestMatcher PROTECTED_API_ROUTES = new AndRequestMatcher(
            API_ROUTES, new NegatedRequestMatcher(PUBLIC_API_ROUTES)
    );

    /**
     * Request Matcher matching all Routes that should be accessable to everyone.
     */
    public static final RequestMatcher PUBLIC_ROUTES = new OrRequestMatcher(
            new NegatedRequestMatcher(new OrRequestMatcher(API_ROUTES, ADMIN_ROUTES)),
            ANONYMOUS_ROUTES
    );

    /**
     * Request Matcher matching all Routes that should be protected.
     */
    public static final RequestMatcher PROTECTED_ROUTES = new NegatedRequestMatcher(PUBLIC_ROUTES);
    //endregion

    //region Route Matchers
    public static boolean isPublicRoute(HttpServletRequest request) {
        return PUBLIC_ROUTES.matches(request);
    }

    public static boolean isApiRoute(HttpServletRequest request) {
        return API_ROUTES.matches(request);
    }

    public static boolean isAdminRoute(HttpServletRequest request) {
        return ADMIN_ROUTES.matches(request);
    }
    //endregion
}
