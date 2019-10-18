package com.ppdai.infrastructure.ui.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import com.ppdai.infrastructure.mq.biz.service.Message01Service;
import com.ppdai.infrastructure.mq.biz.service.UserInfoHolder;
import com.ppdai.infrastructure.ui.util.CookieUtil;
import com.ppdai.infrastructure.ui.util.DesUtil;

@Order(1)
@WebFilter(filterName = "WebAuthFilter", urlPatterns = "/*")
public class AuthFilter implements Filter {
	Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	private Message01Service message01Service;
	
	@Autowired
	UserInfoHolder userInfoHolder;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String uri = request.getRequestURI();
		if (skipUri(uri)) {
			chain.doFilter(request, response);
		} else {
			try {
				Cookie cookie = CookieUtil.getCookie(request, "userSessionId");
				if (cookie == null) {
					response.sendRedirect("/login");
				} else {
					String userId = DesUtil.decrypt(cookie.getValue());
					userInfoHolder.setUserId(userId);
					chain.doFilter(request, response);
				}

			} catch (Exception e) {
				log.error("login fail", e);
				response.sendRedirect("/login");
			} finally {
				message01Service.clearDbId();
				userInfoHolder.clear();
			}
		}
	}

	private List<String> skipUrlLst = new ArrayList<>();

	public AuthFilter() {		
		skipUrlLst=Arrays.asList("/login", ".js", ".css", ".jpg", ".woff", ".png", "/auth" ,"/cat","/hs","/message/getByTopic");
	}

	private boolean skipUri(String uri) {
		for(String t : skipUrlLst){
			if(uri.indexOf(t)!=-1){
				return true;
			}
		}
		return false;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
