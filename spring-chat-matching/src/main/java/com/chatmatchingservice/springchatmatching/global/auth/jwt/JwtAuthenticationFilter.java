    package com.chatmatchingservice.springchatmatching.global.auth.jwt;

    import com.chatmatchingservice.springchatmatching.global.error.CustomException;
    import com.chatmatchingservice.springchatmatching.global.error.ErrorCode;
    import jakarta.servlet.FilterChain;
    import jakarta.servlet.ServletException;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.web.filter.OncePerRequestFilter;
    import java.io.IOException;
    public class JwtAuthenticationFilter extends OncePerRequestFilter {

        private final JwtTokenProvider jwtTokenProvider;

        public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
            this.jwtTokenProvider = jwtTokenProvider;
        }

        @Override
        protected void doFilterInternal(
                HttpServletRequest request,
                HttpServletResponse response,
                FilterChain filterChain
        ) throws ServletException, IOException {

            String header = request.getHeader("Authorization");

            try {
                if (header != null && header.startsWith("Bearer ")) {
                    String token = header.substring(7);

                    if (jwtTokenProvider.validateToken(token)) {
                        Authentication auth = jwtTokenProvider.getAuthentication(token);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }

                filterChain.doFilter(request, response);

            } catch (CustomException e) {
                SecurityContextHolder.clearContext();
                writeError(response, e.getErrorCode());

            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                writeError(response, ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }

        private void writeError(HttpServletResponse response, ErrorCode errorCode) throws IOException {
            if (response.isCommitted()) return;

            response.setStatus(errorCode.getStatus().value());
            response.setContentType("application/json;charset=UTF-8");

            response.getWriter().write(
                    "{\"code\":\"" + errorCode.getCode() + "\",\"message\":\"" + errorCode.getMessage() + "\"}"
            );
        }
        @Override
        protected boolean shouldNotFilter(HttpServletRequest request) {

            String path = request.getRequestURI();

            return path.startsWith("/swagger-ui")
                    || path.startsWith("/v3/api-docs")
                    || path.startsWith("/swagger-resources")
                    || path.startsWith("/webjars")
                    || path.equals("/"); // (선택)
        }

    }
