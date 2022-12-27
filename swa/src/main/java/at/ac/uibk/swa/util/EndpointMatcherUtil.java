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
    public static final String loginEndpoint = "/api/login";
    public static final String logoutEndpoint = "/api/logout";
    public static final String registerEndpoint = "/api/register";
    //endregion

    //region Error Endpoints
    public static class ErrorEndpoints {
        public static final String authenticationErrorEndpoint = "/api/unauthorized";
        public static final String authorizationErrorEndpoint = "/api/forbidden";
        public static final String notFoundErrorEndpoint = "/api/notFound";
        public static final String errorEndpoint = "/api/error";
    }

    private static final String[] errorEndpoints =
            // Get all Error Routes defined in this Class using Runtime Reflection
            Arrays.stream(ErrorEndpoints.class.getDeclaredFields())
                // Only get static Fields of type <String> which contain the Error Endpoints.
                .filter(ReflectionUtil.isAssignableFromPredicate(String.class))
                .filter(ReflectionUtil::isStaticField)
                // Get the Endpoints
                .map(ReflectionUtil::getStaticFieldValueTyped)
                .toArray(String[]::new);

    private static final RequestMatcher ERROR_ROUTES = new OrRequestMatcher(
            Arrays.stream(errorEndpoints)
                    .map(x ->new AntPathRequestMatcher(x))
                    .toArray(AntPathRequestMatcher[]::new)
    );
    //endregion

    //region Route Matchers
    public static final RequestMatcher ANONYMOUS_ROUTES = AnyRequestMatcher.INSTANCE;
    public static final RequestMatcher API_ROUTES = new AntPathRequestMatcher("/api/**");
    public static final RequestMatcher ADMIN_ROUTES = new AntPathRequestMatcher("/admin/**");

    private static final RequestMatcher PUBLIC_API_ROUTES = new OrRequestMatcher(
            new AntPathRequestMatcher(loginEndpoint),
            new AntPathRequestMatcher(logoutEndpoint),
            new AntPathRequestMatcher(registerEndpoint),
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
