package portfolio;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


@Controller
public class member_controller{
	private memberdto md = new memberdto();
	
	@Resource(name="members")
	private member_module mm;
	
	
	@PostMapping("/idcheck.do")
	public String idcheck(Model m, @RequestParam(required = false) String userid, HttpServletResponse res) {
		
    	
		try {
			this.md = this.mm.search_id(userid);
			
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
	
	@PostMapping("/admin_joinok.do")
	public String admin_join(@ModelAttribute("admin_member") memberdto dto,Model m) {
		
		
		
		int result = this.mm.insert_member(dto);
		if(result==0) {
			m.addAttribute("msg","회원가입중 오류가 발생하였습니다.");
			m.addAttribute("result",1);
		}else {
			m.addAttribute("msg","정상적으로 회원가입되었습니다.");
			m.addAttribute("result",2);
		}
		
		return "/WEB-INF/jsp/admin_join";
	}
	
	@PostMapping("/admin_loginok.do")
	public String admin_login(HttpServletRequest req, Model m) {
		String mid = req.getParameter("login_id");
		String mpass = req.getParameter("login_pass");
		
		memberdto login = new memberdto();
		login.setMid(mid);
		login.setMpass(mpass);
		int result, count=0;
		
		this.md = this.mm.search_id(mid);
		if(this.md == null) {
			result = 1;
		}
		else {
			count = Integer.valueOf(this.md.getMlcount());
			result = this.mm.select_login(login);
			
		}
		String msg = "";
		HttpSession se = req.getSession();
		switch(result) {
		case 1 : 
			msg = "아이디와 비밀번호를 확인해주세요";
			break;
		case 2 : 
			msg = "아이디와 비밀번호를 확인해주세요";
			this.mm.count_up(this.md);
			if(count==2)
				msg+="3회 로그인에 실패하셨습니다. 5회 실패시 로그인이 제한됩니다.";
			if(count==4)
				msg="5회 로그인에 실패하여 계정이 정지됩니다. 관리자에게 문의 바랍니다.";
			break;
		case 3 : 
			msg = "귀하의 계정은 관리자에 의해 이용이 제한되었습니다.";
			break;
		case 4 : 
			msg = "로그인 성공";
			this.mm.count_reset(this.md);
			se.setAttribute("mid", this.md.getMname());
			break;
		}

		m.addAttribute("result",result);
		m.addAttribute("msg",msg);
		
		return "/WEB-INF/jsp/admin_login";
	}
	
	@RequestMapping("/config_main.do")
	public String config_main(@ModelAttribute("params") searchdto searchdto, Model m) {
		
		pageresponse<memberdto> data = this.mm.findAll(searchdto);
		m.addAttribute("data",data);
		return "config_main";
	}
	
	@PostMapping("/modify_status.do")
	public String modify_status(@RequestParam(required = false) String midx, @RequestParam(required = false) String status,Model m) {
	
		this.md.setMidx(midx);
		this.md.setMuse(status);
		int result = this.mm.update_status(this.md);
		this.mm.count_reset(this.md);
		if(result==1) {
			m.addAttribute("msg","변경되었습니다.");
		}else {
			m.addAttribute("msg","오류발생");
		}
		return "ajax";
	}
	
	@RequestMapping("/logout.do")
	public String logout() {
		return "/WEB-INF/jsp/logout";
	}
	
	
	
}
