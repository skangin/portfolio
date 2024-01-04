package portfolio;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Controller
public class m_member_controller {
	private m_memberdto md = new m_memberdto();
	
	@Resource(name="m_member")
	private m_member_module mm;
	@Resource(name = "reservation")
	private reservation_module rm;
	
	private String service_id = "ncp:sms:kr:318002466922:raemian";
	private String access_id = "zE8mIfYWptsIGOy839SC";
	private String security_key = "HGFeVjxIjpP1QXfTpuYhwbby97YRB1gaetW5JOrn";
	private String service = "SMS"; 
	private String url = "https://sens.apigw.ntruss.com/sms/v2/services/"+service_id+"/messages";
	private String url2 = "/sms/v2/services/"+service_id+"/messages"; 
	private String timestamp = Long.toString(System.currentTimeMillis());
	
	@PostMapping("/main/main_idcheck.do")
	public String m_idcheck(Model m, @RequestParam String userid, HttpServletResponse res) {
		
    	
		try {
			this.md = this.mm.m_search_id(userid);
			
			if(this.md == null) {
				m.addAttribute("mid","no");
			}
			else {
				m.addAttribute("mid","yes");
			}
		}		
		catch(Exception e) {
			m.addAttribute("mid","error");
			System.out.println(e);
			System.out.println("모듈오류");
		}
		
		return "/WEB-INF/jsp/idcheck";
	}
	
	@PostMapping("/main/main_joinok.do")
	public String main_join(@ModelAttribute("main_member") m_memberdto dto,Model m) {
		
		if(dto.getMokmail() == null)
			dto.setMokmail("N");
		if(dto.getMokpost() == null)
			dto.setMokpost("N");
		if(dto.getMoktel() == null)
			dto.setMoktel("N");
		if(dto.getMoksms() == null)
			dto.setMoksms("N");
		
		int result = this.mm.m_insert_member(dto);
		if(result==0) {
			m.addAttribute("msg","회원가입중 오류가 발생하였습니다.");
		}else {
			m.addAttribute("msg","정상적으로 회원가입되었습니다.");
		}
		
		return "/WEB-INF/jsp/main_join";
	}
	
	@PostMapping("/main/main_loginok.do")
	public String main_login(HttpServletRequest req, Model m) {
		String mid = req.getParameter("mid");
		String mpw = req.getParameter("mpw");
		
		m_memberdto login = new m_memberdto();
		login.setMid(mid);
		login.setMpw(mpw);
		
		int result=0;
		
		this.md = this.mm.m_search_id(mid);
		if(this.md == null) {
			result = 1;
		}
		else {			
			result = this.mm.m_select_login(login);
			
		}
		String msg = "";
		HttpSession se = req.getSession();
		switch(result) {
		case 1 : 
			msg = "아이디와 비밀번호를 확인해주세요";
			break;			
		case 3 : 
			msg = "귀하의 계정은 관리자에 의해 이용이 제한되었습니다.";
			break;
		case 4 : 
			msg = "로그인 성공";
			se.setAttribute("dto", this.md);
			break;
		}

		m.addAttribute("result",result);
		m.addAttribute("msg",msg);
		
		return "/WEB-INF/jsp/main_login";
	}
	
	@RequestMapping("/member_main.do")
	public String member_main(@ModelAttribute("params") searchdto searchdto, Model m) {
		searchdto.setRecordSize(20);
		pageresponse<m_memberdto> data = this.mm.findAll(searchdto);
		m.addAttribute("data",data);
		return "member_main";
	}
	
	@RequestMapping("admin_main.do")
	public String member_today(Model m) {
		Date dt = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		String today = df.format(dt);
		List<m_memberdto> data = this.mm.select_today(today);
		List<reservationdto> data2 = this.rm.select_today();
		m.addAttribute("memberdata",data);
		m.addAttribute("rd",data2);
		
		return "admin_main";
	}
	
	@RequestMapping("/ban_member.do")
	public String ban_member(@RequestParam String midx, Model m) {
		int result = this.mm.ban_member(midx);
		String msg="";
		if(result == 1) {
			msg = "계정의 서비스 사용을 중지시켰습니다.";
		}else {
			msg = "작업 중 오류가 발생하였습니다.";
		}
		m.addAttribute("msg",msg);
		m.addAttribute("type","member");
		return "/WEB-INF/jsp/deleteok";
	}
	
	@PostMapping("/main/telcheck.do")
	public void telcheck(@RequestParam(required = false) String tel,
			  @RequestParam(required = false) String context, HttpServletResponse response)throws Exception {
		JSONObject code1 = new JSONObject();
		JSONObject code2 = new JSONObject();
		JSONArray code3 = new JSONArray();
		code1.put("type", "SMS");
		code1.put("countryCode", "82");
		code1.put("from", "01050555493");
		code1.put("contentType", "COMM");
		code1.put("content", "이벤트 내용 발송"); 
		code2.put("to", tel);
		code2.put("content", context);
		code3.add(code2);		
		code1.put("messages", code3);
		
		String data = code1.toString(); 		
		
		OkHttpClient client = new OkHttpClient(); 
		PrintWriter pw = response.getWriter();		
		
		Request req = new Request.Builder()
				.addHeader("x-ncp-apigw-timestamp", timestamp)
				.addHeader("x-ncp-iam-access-key", access_id)
				.addHeader("x-ncp-apigw-signature-v2", makes()) //SHA로 변환된 값을 적용
				.url(url)
				.post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"),data))
				.build();		
		
		Response res = client.newCall(req).execute(); //Naver API로 전송 실행 및 return 내용을 받음
		String result = res.body().string(); //문자열로 확인
		
		if(result.indexOf("202")>0) {
			pw.print("ok");
		}		
		else {
			pw.print("cancel");
		}
		
	}
	
	public String makes() {
		String sp = " ";
		String line = "\n";
		
		String msg = new StringBuilder()
				.append("POST")
				.append(sp)
				.append(url2)
				.append(line)
				.append(timestamp)
				.append(line)
				.append(access_id)
				.toString();
		SecretKeySpec key;
		String base64="";
		try {
			key = new SecretKeySpec(security_key.getBytes("UTF-8"), "HmacSHA256");
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(key);
			byte[] rawHmac = mac.doFinal(msg.getBytes("UTF-8"));
			base64 = Base64.getEncoder().encodeToString(rawHmac);	
		}
		catch(Exception e) {
			System.out.println("암호화 오류발생!!");
			base64=null;
		}		
		
		return base64;
	}
}
