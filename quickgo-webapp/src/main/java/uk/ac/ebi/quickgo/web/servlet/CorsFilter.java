package uk.ac.ebi.quickgo.web.servlet;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author Tony Wardell
 * Date: 22/12/2014
 * Time: 14:45
 * Created with IntelliJ IDEA.
 */
public class CorsFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		fixHeaders(servletResponse);
		filterChain.doFilter(servletRequest, servletResponse);
	}

	@Override
	public void destroy() {

	}

	public void fixHeaders(ServletResponse servletResponse) throws IOException, ServletException {
		HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
		try {

			httpServletResponse.addHeader("Access-Control-Allow-Origin", "*");
			httpServletResponse.addHeader("Access-Control-Allow-Credentials", "true");
			httpServletResponse.addHeader("Access-Control-Allow-Methods", "GET, HEAD, OPTIONS, POST, PUT");
			httpServletResponse.addHeader("Access-Control-Allow-Headers", "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers,  x-http-method-override");

		} catch (Exception e) {
			httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			httpServletResponse.setContentType("text/plain");
			httpServletResponse.getWriter().println(buildErrorMessage(e));
		} catch (Throwable e) {
			httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			httpServletResponse.setContentType("text/plain");
			httpServletResponse.getWriter().println(buildErrorMessage(e));
		}
	}

	private static String buildErrorMessage(Exception e) {
		String msg = e.toString() + "\r\n";

		for (StackTraceElement stackTraceElement : e.getStackTrace()) {
			msg += "\t" + stackTraceElement.toString() + "\r\n";
		}

		return msg;
	}

	private static String buildErrorMessage(Throwable e) {
		String msg = e.toString() + "\r\n";

		for (StackTraceElement stackTraceElement : e.getStackTrace()) {
			msg += "\t" + stackTraceElement.toString() + "\r\n";
		}

		return msg;
	}


	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		fixHeaders(response);
	}




}
