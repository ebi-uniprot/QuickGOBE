package uk.ac.ebi.quickgo.web.servlet;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import uk.ac.ebi.quickgo.util.Interval;
import uk.ac.ebi.quickgo.web.util.WebUtils;

/**
 * class that represents a QuickGO cookie
 * 
 * QuickGO stores various parameters in a cookie, the format of which is an underscore-separated list of key-value pairs.
 * 
 * For example:
 * 		c$fill-true_t-64$01MB01i3020R021E_c$ids-true_c$fontName-Arial_c$font-11_c$showChildren-false_c$key-true_c$width-85_c$height-55
 * 
 * which breaks down to:
 * 		c$fill = true
 * 		t = 64$01MB01i3020R021E
 * 		c$ids = true
 * 		c$fontName = Arial
 * 		c$font = 11
 * 		c$showChildren = false
 * 		c$key = true
 * 		c$width = 85
 * 		c$height = 55
 * 
 * @author tonys
 *
 */
public class QuickGOCookie {
    private static final String QUICKGO_COOKIE_NAME = "quickgo";    

    private Map<String, String> parameters = new HashMap<>();
    
    /**
     * construct a QuickGOCookie object and build a map of (key, value) pairs for the parameters that it contains
     *  
     * @param r request in which (we hope to) find a QuickGO cookie
     */
    public QuickGOCookie(HttpServletRequest r) {
        String cookie = WebUtils.getCookieValue(r, QUICKGO_COOKIE_NAME);
        if (cookie != null) {
            for (String kvPair : cookie.split("_")) {
                String[] kv = kvPair.split("-");
                if (kv.length == 2) {
	                parameters.put(kv[0], kv[1]);
                }
            }
        }
    }

    /**
     * get the full set of parameters
     * 
     * @return the parameter map
     */
    public Map<String, String> getParameters() {
    	return parameters;
    }

    /**
     * get the value of the parameter with the given key
     * 
     * @param key
     * @return the associated value
     */
    public String getParameter(String key) {
        return parameters.get(key);
    }

    /**
     * set the parameter with the given key to the specified value
     * 
     * @param key
     * @param value
     */
    public void setParameter(String key, String value) {
    	parameters.put(key, value);
    }

    /**
     * build a string containing the current set of parameters suitable for storing in a cookie
     * 
     * @return underscore-separated string of key-value pairs
     */
    public String getValue() {
        StringBuilder sb = new StringBuilder();
        for (String key : parameters.keySet()) {
            if (sb.length() > 0) {
                sb.append("_");
            }
            sb.append(key).append("-").append(parameters.get(key));
        }

        return sb.toString();
    }

    /**
     * make a Cookie based on this QuickGOCookie
     * 
     * @return the freshly-baked Cookie
     */
    public Cookie makeCookie() {
        Cookie cookie = new Cookie(QUICKGO_COOKIE_NAME, getValue());
        cookie.setMaxAge((int)(365 * Interval.DAY_NS / Interval.SECOND_NS));
        return cookie;
    }
}
