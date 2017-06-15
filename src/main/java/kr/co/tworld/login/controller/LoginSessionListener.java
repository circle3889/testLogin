package kr.co.tworld.login.controller;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.beans.factory.annotation.Autowired;

import kr.co.tworld.login.service.LoginService;
 
public class LoginSessionListener implements HttpSessionListener{

	@Autowired  
	private LoginService loginService;
	
	@Override
	public void sessionCreated(HttpSessionEvent se) {
		// TODO Auto-generated method stub
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		// TODO Auto-generated method stub
		Object token = se.getSession().getAttribute("tokenId");
		String session = se.getSession().getId();
		if(token!=null){
			loginService.delSession(token.toString());
			loginService.delToken(session);
		}
		loginService.delRedisSession(session);
		
	}
}
