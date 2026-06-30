package security;

// Spring Security's HTTP Basic filter handles authentication automatically.
// This class is a placeholder for a future JWT filter if needed in a later phase.
// To add JWT: extend OncePerRequestFilter, extract the token from
// Authorization header, validate it, and set the authentication in
// SecurityContextHolder before calling filterChain.doFilter().
public class AuthFilter {
}
