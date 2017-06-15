package kr.co.tworld.login.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.tworld.login.service.LoginService;

@RestController
public class LoginController {

	/** error code **/
	@Value("${services.command.success}")
	private String SUCCESS;
	@Value("${services.command.error4}")
	private String ERROR_04;
	@Value("${services.command.error5}")
	private String ERROR_05;
	@Value("${services.command.error6}")
	private String ERROR_06;
  
	@Autowired  
	private LoginService loginService;
	
	@RequestMapping("/loginProc")
	private HashMap putRedis(@RequestParam("id") String custId, @RequestParam("password") String custPw, HttpServletRequest request){
		
		String sessionId = request.getParameter("SESSION");
		
        HashMap resultMap = new HashMap();

		/******TID 확인 및 토큰 복호화****
		*발급기관
		*유효시간
		*발급시간
		*nonce
		*issuingType
		*수신동의여부
		  **/
        try {
			Thread.sleep(500);
		/**ICAS 확인
		 * 
		 *
		 **/
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		String token = null;
		if(custId!=null&&custPw!=null){
			String str = loginService.chkSession(custId,sessionId);
			
			if(SUCCESS.equals(str)){
				resultMap = loginService.putSession(custId,sessionId);
				loginService.setSession(sessionId, (String)resultMap.get("tokenId"));
				resultMap.remove("tokenId");
			}else{
				resultMap.put("returnCode", "fail");
				resultMap.put("errorCode", str);
			}
//			request.getSession().setAttribute("tokenId",resultMap.get("tokenId"));
//			request.getSession().setMaxInactiveInterval(60);
		}else{
			resultMap = new HashMap();
			resultMap.put("returnCode", "fail");
			resultMap.put("errorCode", ERROR_04);
		}
		
        return resultMap;
	}
	
//	@RequestMapping("/login")//test tokenId 발생
//	private HashMap putRedis1(HttpServletRequest request){
//		
//		String sessionId = request.getSession().getId();
//		
//        HashMap resultMap = new HashMap();
//        
//		resultMap = loginService.putSession("sss",sessionId);
//		
//		loginService.setSession(sessionId, (String)resultMap.get("tokenId"));
//		
//        return resultMap;
//	}
	
	@RequestMapping("/logoutProc")
	private HashMap delRedis(@RequestParam("SESSION") String sessionId, HttpServletRequest request){
       
		HashMap resultMap = new HashMap();
        
		String result = null;
        int delOk = 0;
        
        String token = loginService.getToken(sessionId);
        		
        if(token!=null){
        	delOk = loginService.delSession(token);
        	loginService.delToken(sessionId);
        }else{
        	delOk = 1;
        }
		
		if(delOk==0){
			result = "del ok";
			resultMap.put("returnCode", "success");
		}else if(delOk==1){
			result = "insert token";
			resultMap.put("returnCode", "fail");
			resultMap.put("errorCode", ERROR_06);
		}else{
			result = "error";
			resultMap.put("returnCode", "fail");
			resultMap.put("errorCode", ERROR_06);
		}
		

        return resultMap;
	}
	
//	@RequestMapping("/logout")//test logout
//	private HashMap delRedis2(@RequestParam("tokenId") String token, HttpServletRequest request){
//       
//		HashMap resultMap = new HashMap();
//        
//		String result = null;
//        int delOk = 0;
//        		
//        if(token!=null){
//        	delOk = loginService.delSession(token);
//        }else{
//        	delOk = 1;
//        }
//		
//		if(delOk==0){
//			result = "del ok";
//			resultMap.put("returnCode", "success");
//		}else if(delOk==1){
//			result = "insert token";
//			resultMap.put("returnCode", "fail");
//			resultMap.put("errorCode", result);
//		}else{
//			result = "error";
//			resultMap.put("returnCode", "fail");
//			resultMap.put("errorCode", result);
//		}
//		
//		request.getSession().removeAttribute("tokenId");
//
//        return resultMap;
//	}
//	
//	@RequestMapping("/session")//test data 확인 및 data get sample
//	private String getRedis(@RequestParam("tokenId") String token){
//		String user = null; 
//
//		if(token!=null){
//        	user = loginService.getSession(token).toString();
//			
//        }else{
//        	user = "insert token";
//        }
//
//        return user;
//	}
//	
//
//	@RequestMapping("/sessionAll")//test data 확인 및 data get sample
//	private String getRedisAll(@RequestParam("tokenId") String token, HttpServletRequest req){
//		String user = null; 
//
//		if(token!=null){
//        	user = loginService.getSessionAll(token).toString();
//			
//        }else{
//        	user = "insert token";
//        }
//		
//        return user;
//	}
//	
//	@RequestMapping("/get")//test session 확인
//	private HashMap getRedisAll(HttpServletRequest req){
//		HashMap user = new HashMap(); 
//		
//		user.put("tokenId", req.getSession().getAttribute("tokenId"));
//		user.put("sessionId", req.getSession().getId());
//
//
//        return user;
//	}
//	
//	@RequestMapping("/del")//새션 파기 test
//	private HashMap getRedis(HttpServletRequest req){
//		HashMap user = new HashMap(); 
//		
//		req.getSession().invalidate();
//
//        return user;
//	}
	
}