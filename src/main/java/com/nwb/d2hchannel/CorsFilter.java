package com.nwb.d2hchannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
public class CorsFilter implements Filter {
    private static final String[] ALLOWED_METHODS = new String[]{"POST", "GET", "PUT", "PATCH", "OPTIONS", "DELETE", "TRACE"};

    private static final String[] ALLOWED_ORIGINS = new String[]{
            "http://localhost:4200",
            "http://localhost:7008",
            "http://madeforyou.com"
    };
    public static final String REFERER = "referer";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.debug("filter has been initialized");
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String referer = ((HttpServletRequest) servletRequest).getHeader(REFERER);
        if (referer != null) {
            final Optional<String> first = Arrays.stream(ALLOWED_ORIGINS)
                    .filter(referer::contains)
                    .findFirst();
            first.ifPresent(s -> response.setHeader("Access-Control-Allow-Origin", s));
        }
        response.setHeader("Access-Control-Allow-Methods", String.join(", ", ALLOWED_METHODS));
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers",
                "token, Content-Type, Accept, X-Requested-With, Access-Control-Request-Method");

        // need to be required for not having INVALID CORS REQUEST issue
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }
}