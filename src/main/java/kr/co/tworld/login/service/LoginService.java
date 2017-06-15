package kr.co.tworld.login.service;


import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
//@EnableHystrix
public class LoginService {

	/** test data 값 세팅 **/
//	final private String[] name = {"John","Michal","Noah","Daniel","David","Linda","Kevin","Alex","Lucas","Isaac"};
	final private String[] name = {"김길동","강길동","홍길동","미선이","김선이","지선이","지숙이","나고객","너고객","상고객"};
	final private String[] grade = {"SILVER","GOLD","VIP"};
	final private String[] prinPlanId = {"NA11111","NA22222","NA01001","NA55555","NA525455"};
	final private String[] equipModelCd = {"SM1200","SM700","SM0811","SM5000","SM2502","SM1000"};
	/** test data 값 세팅 끝 **/

	/** sessoin code **/
	@Value("${services.session.code}")
	private String SESSION_CODE;
	@Value("${services.session.attr}")
	private String SESSION_ATTR;
	
	/** error code **/
	@Value("${services.command.success}")
	private String SUCCESS;
	@Value("${services.command.error1}")
	private String ERROR_01;
	@Value("${services.command.error2}")
	private String ERROR_02;
	@Value("${services.command.error3}")
	private String ERROR_03;
	
	
	@Autowired@Resource(name="redisTemplate")
    private RedisTemplate<String, Object> template;
	@Autowired@Resource(name="sessionTemplate")//session용
    private RedisTemplate<String, Object> sessionTemplate;
	
	//session에 tokenId 생성
	public void setSession(String session, String token){
		sessionTemplate.opsForHash().put(SESSION_CODE+session, SESSION_ATTR, token);
	}
	//session에서 tokenId 검색
	public String getToken(String session){
		return (String) sessionTemplate.opsForHash().get(SESSION_CODE+session, SESSION_ATTR);
	}
	//session에서 tokenId 삭제
	public void delToken(String session){
		sessionTemplate.opsForHash().delete(SESSION_CODE+session, SESSION_ATTR);
	}
	//session 삭제
	public void delRedisSession(String session){
		sessionTemplate.delete(SESSION_CODE+session);
	}
	//session 존재 확인
	public String chkSession(String id,String session){
		String returnStr = "";
		long chk = sessionTemplate.getExpire(SESSION_CODE+session);
		if(chk<0){
			returnStr = ERROR_01;
		}else if(chk<300){
			returnStr = ERROR_02;
		}else{
			String token = getToken(session);
			
			if(token!=null&&!("".equals(token))){
				String tokenId = (String) template.opsForHash().get(token, "userID");
				if(id.equals(tokenId)){
					returnStr = ERROR_03;
				}else{
					delSession(token);
					returnStr = SUCCESS;
				}
			}else{
				returnStr = SUCCESS;
			}
			
		}
		return returnStr;
	}

	//redis data 셋팅
	public HashMap<String,String> putSession(String userId, String sessionId){
		HashMap<String,String> returnMap = new HashMap<String,String>();
		
		String token = UUID.randomUUID().toString().replaceAll("-", "");
		
		/** test data 셋팅 **/
		HashMap user = new HashMap();
		
		int svcCnt = (int) (Math.round(Math.random()*3)+1);
		user.clear();
		user.put("svcCnt", svcCnt);
		user.put("userId", userId);
		List<String> svcNum = new ArrayList<String>();
		List<String> svcNumMgm = new ArrayList<String>();
		for(int i=0;i<svcCnt;i++){
			HashMap svcMgmtNum = new HashMap();
			String num = "010"+(int)Math.round(Math.random()*100000000);
			NumberFormat nf = NumberFormat.getInstance();
			nf.setGroupingUsed(false);
			String mgmt = nf.format(Math.round(Math.random()*100000)+(Math.floor(Math.random()*100000)*100000));
			System.out.println(num);
			System.out.println(mgmt);
			svcNum.add(num);
			svcNumMgm.add(mgmt);
			svcMgmtNum.put("grade", grade[(int)Math.floor(Math.random()*grade.length)]);
			svcMgmtNum.put("svcNum",num);
			svcMgmtNum.put("svcCd", "N");
			svcMgmtNum.put("prinPlanId", prinPlanId[(int)Math.floor(Math.random()*prinPlanId.length)]);
			svcMgmtNum.put("equipModelCd", equipModelCd[(int)Math.floor(Math.random()*equipModelCd.length)]);
			
			user.put(mgmt, svcMgmtNum);
		}
		
		user.put("svcNum", svcNum);
		user.put("svcMgmtNum", svcNumMgm);
		
		user.put("custNm", name[(int)Math.floor(Math.random()*name.length)]);
		/** test data 셋팅 완료 **/
		
		//allData
		template.opsForHash().put(token,"all",user);

		//serviceData
		for(int i=0;i<svcCnt;i++){
			template.opsForHash().put(token,svcNumMgm.get(i),user.get(svcNumMgm.get(i)));
		}
		//selectData
		template.opsForHash().put(token,"selected",svcNumMgm.get(0));
		template.opsForHash().put(token,"userID",user.get("userId"));
		template.opsForHash().put(token,"custNm",user.get("custNm"));
		template.opsForHash().put(token,"svcMgmtNum",user.get("svcMgmtNum"));
		template.opsForHash().put(token,"svcNum",user.get("svcNum"));
		template.opsForHash().put(token,"SESSION", sessionId);
		
		returnMap.put("tokenId",token);
		returnMap.put("returnCode", "success");
//        template.expire(token, 20, TimeUnit.MINUTES);
		
        return returnMap;
        
	}
	
	//redis 삭제
	public int delSession(String token){
		int delOk = 0;
		
		template.delete(token);
		
		return delOk;
	}
	
	//redis data get
//	public String getSession(String token){
//		//key값 검색
//		Set<Object> keys = template.opsForHash().keys(token);
//		Iterator<Object> it = keys.iterator();
//		
//		Map<String,String> dataMap = new HashMap<String,String>();
//		
//		while (it.hasNext()) {
//			String dataKey = it.next().toString();
//		    	   dataMap.put(dataKey, getData(token,dataKey));
//		}
//		
//		return dataMap.toString();
//		
//	}
//	
//	//redis data get2
//	public String getSessionAll(String token){
//		
//		Map<String,String> dataMap = new HashMap<String,String>();
//		
//		dataMap = (Map<String, String>) template.opsForHash().get(token, "all");
//		
//		return dataMap.toString();
//		
//	}
//	
//	//data get sample
//	public String getData(String token,String key){
//		String re = null;
//		 
//		 switch (key) {
//		 	case "svcMgmtNum":
//		 	case "svcNum":
//		 		re = getList(token,key).toString();
//		 		break;
//			default:
//				if(key.length()>8) re = getHash(token,key).toString();
//				else re = getString(token,key);
//				break;
//		}
//		
//		
//		return re;
//	}
//	//data get sample
//	public List getList(String token,String key){
//		List list = (List) template.opsForHash().get(token,key);
//		return list;
//	}
//	//data get sample
//	public HashMap getHash(String token,String key){
//		HashMap dataMap = (HashMap) template.opsForHash().get(token,key);
//		return dataMap;
//	}
//	//data get sample
//	public String getString(String token,String key){
//		String re = template.opsForHash().get(token,key).toString();
//		return re;
//	}
}
