package com.performance.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

       // String header = request.getHeader("Authorization");
		/*
		 * if (header == null || !header.startsWith("Bearer ")) { //log.
		 * info("No JWT token found or invalid format — skipping JWT authentication");
		 * filterChain.doFilter(request, response); // skip filter for public endpoints
		 * return; }
		 */
    	
        String jwt = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
		} /*// for clearing cookie open this comment 
			 * try { String username = jwtUtil.extractUsername(jwt); UserDetails userDetails
			 * = userDetailsService.loadUserByUsername(username);
			 * 
			 * // validate and set authentication } catch (UsernameNotFoundException ex) {
			 * // 🔥 Clear cookie immediately Cookie expiredJwt = new Cookie("jwt", null);
			 * expiredJwt.setMaxAge(0); expiredJwt.setPath("/");
			 * response.addCookie(expiredJwt); // proceed silently or let Spring Security
			 * handle it }
			 */
         System.out.println(jwt+"JWT");
			/*
			 * if(jwt==null) { filterChain.doFilter(request, response); // skip filter for
			 * public endpoints return; }
			 */
        if (jwt != null) {
            String username = jwtUtil.extractUsername(jwt);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                if (jwtUtil.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
			/*
			 * if (header != null && header.startsWith("Bearer ")) { String token =
			 * header.substring(7); String username = jwtUtil.extractUsername(token);
			 * 
			 * if (username != null &&
			 * SecurityContextHolder.getContext().getAuthentication() == null) { UserDetails
			 * userDetails = userDetailsService.loadUserByUsername(username); if
			 * (jwtUtil.isTokenValid(token, userDetails)) {
			 * UsernamePasswordAuthenticationToken authToken = new
			 * UsernamePasswordAuthenticationToken( userDetails, null,
			 * userDetails.getAuthorities()); authToken.setDetails(new
			 * WebAuthenticationDetailsSource().buildDetails(request));
			 * SecurityContextHolder.getContext().setAuthentication(authToken); } }
			 */
        }

        filterChain.doFilter(request, response);
    }
}
