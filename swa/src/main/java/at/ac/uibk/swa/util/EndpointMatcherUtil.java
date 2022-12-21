package at.ac.uibk.swa.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.web.util.matcher.*;

import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EndpointMatcherUtil {

    //region Error Endpoints
    public static final String authenticationErrorEndpoint = "/api/unauthorized";
    public static final String authorizationErrorEndpoint = "/api/forbidden";
    public static final String notFoundErrorEndpoint = "/api/notFound";
    public static final String errorEndpoint = "/api/error";

    private static final String[] errorEndpoints =
            // Get all Error Routes defined in this Class using Runtime Reflection
            // TODO: Make this more sophisticated because someone way want to define other Strings in here
            Arrays.stream(EndpointMatcherUtil.class.getDeclaredFields())
                // Only get static Fields of type <String> which contain the Error Endpoints.
                .filter(ReflectionUtil.isAssignableFrom(String.class))
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
    public static final RequestMatcher API_ROUTES = new AntPathRequestMatcher("/api/**");
    public static final RequestMatcher ADMIN_ROUTES = new AntPathRequestMatcher("/admin/**");

    private static final RequestMatcher PUBLIC_API_ROUTES = new OrRequestMatcher(
            new AntPathRequestMatcher("/api/login"),
            new AntPathRequestMatcher("/api/register"),
            ERROR_ROUTES
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
            new NegatedRequestMatcher(AnyRequestMatcher.INSTANCE),
            new OrRequestMatcher(API_ROUTES, ADMIN_ROUTES)
    );

    /**
     * Request Matcher matching all Routes that should be accessable to everyone.
     */
    public static final RequestMatcher PUBLIC_ROUTES = new OrRequestMatcher(
            new NegatedRequestMatcher(new OrRequestMatcher(API_ROUTES, ADMIN_ROUTES)),
            AnyRequestMatcher.INSTANCE
    );
    //endregion

    //region Helper Route
    public static boolean isApiRoute(HttpServletRequest request) {
        return API_ROUTES.matches(request);
    }
    //endregion
}
