package it.unipi.dsmt.dsmt_taboo.utility;

import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class FilterManagement extends GenericFilterBean implements Filter
{
    /**
     * This class represents a filter for managing cross-origin resource sharing (CORS) in the application.
     * It extends the GenericFilterBean class and implements the Filter interface.
     * The doFilter method intercepts HTTP requests and adds appropriate CORS headers to the response,
     * allowing cross-origin requests from any origin with any method and headers.
     * The filter is configured to disallow credentials and set a maximum age of 3600 seconds for preflight requests.
     */

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Allow-Methods", "*");
        //httpResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");

        httpResponse.setHeader("Access-Control-Allow-Headers", "*");
        //httpResponse.setHeader("Access-Control-Allow-Headers",
        //"Origin, X-Requested-With, Content-Type, Accept, X-Auth-Token, X-Csrf-Token, Authorization");

        httpResponse.setHeader("Access-Control-Allow-Credentials", "false");
        httpResponse.setHeader("Access-Control-Max-Age", "3600");

        //System.out.println("********** CORS Configuration Completed **********");

        chain.doFilter(request, response);
    }
}

