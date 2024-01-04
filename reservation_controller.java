package portfolio;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class reservation_controller {
	private reservationdto rd = new reservationdto();
	
	@Resource(name = "reservation")
	private reservation_module rm;
	
	@RequestMapping("/main/reservation_in.do")
	public String reservation_in(HttpServletRequest req, Model m) {
		HttpSession se = req.getSession();
		m_memberdto md = (m_memberdto)se.getAttribute("dto");
		String msg = "";
		int result = 0;
		
		if(md == null) {
			msg="해당 서비스는 로그인 후 사용하실 수 있습니다.";
			result = 1;
		}
		else {
			reservationdto rd = this.rm.find_reservation(md);
			if(rd == null) {
				
				result = 2;
			}				
			else {
				msg="이미 방문을 예약하셨습니다.";
				result = 3;
				se.setAttribute("rd", rd);
			}				
		}
		m.addAttribute("type","in");
		m.addAttribute("msg",msg);
		m.addAttribute("result",result);			
		
		return "/WEB-INF/jsp/reserve_login";
	}
	
	@RequestMapping("/main/reservation_ck.do")
	public String reservation_ck(HttpServletRequest req, Model m) {
		HttpSession se = req.getSession();
		m_memberdto md = (m_memberdto)se.getAttribute("dto");
		String msg = "";
		int result = 0;
		
		if(md == null) {
			msg="해당 서비스는 로그인 후 사용하실 수 있습니다.";
			result = 1;
		}
		else {
			reservationdto rd = this.rm.find_reservation(md);
			if(rd == null) {
				msg="예약된 내용이 없습니다.";
				result = 2;
			}				
			else {
				result = 3;
				se.setAttribute("rd", rd);
			}				
		}
		
		m.addAttribute("type","ck");
		m.addAttribute("msg",msg);
		m.addAttribute("result",result);		
		
		return "/WEB-INF/jsp/reserve_login";
	}
	
	@RequestMapping("/main/reservation_cancel.do")
	public String reservation_cancel(HttpServletRequest req, Model m) {
		HttpSession se = req.getSession();
		m_memberdto md = (m_memberdto)se.getAttribute("dto");
		String msg = "";
		int result = 0;
		
		if(md == null) {
			msg="해당 서비스는 로그인 후 사용하실 수 있습니다.";
			result = 1;
		}
		else {
			reservationdto rd = this.rm.find_reservation(md);
			if(rd == null) {
				msg="예약된 내용이 없습니다.";
				result = 2;
			}				
			else {
				result = 4;
				se.setAttribute("rd", rd);
			}				
		}
		
		m.addAttribute("type","cancel");
		m.addAttribute("msg",msg);
		m.addAttribute("result",result);		
		
		return "/WEB-INF/jsp/reserve_login";
	}
	
	@PostMapping("/main/reservation_upload.do")
	public String reservation_upload(@ModelAttribute("reservation") reservationdto dto,Model m) {
		
		
		int result = this.rm.insert_reservation(dto);
		String msg="";
		if(result ==1) {
			msg="예약 완료되었습니다.";
		}
		else {
			msg="예약 중 오류가 발생하였습니다.";
		}
		m.addAttribute("msg",msg);
		return "/WEB-INF/jsp/reservation_upload";
	}
	
	@PostMapping("/main/reservation_modify.do")
	public String reservation_modify(@ModelAttribute("reservation") reservationdto dto,Model m) {
		
		
		int result = this.rm.update_reservation(dto);
		String msg="";
		if(result ==1) {
			msg="예약이 변경되었습니다.";
		}
		else {
			msg="변경 중 오류가 발생하였습니다.";
		}
		m.addAttribute("msg",msg);
		return "/WEB-INF/jsp/reservation_modify";
	}
	
	@PostMapping("/main/delete_reservation.do")
	public String delete_reservation(@RequestParam String ridx, Model m) {
		int result = this.rm.delete_reservation(ridx);
		String msg="";
		if(result == 1) {
			msg = "사전방문 예약 취소가 정상적으로 처리 되었습니다";
		}else {
			msg = "예약 취소 중 오류가 발생하였습니다.";
		}
		m.addAttribute("msg",msg);
		m.addAttribute("type","reservation");
		
		return "/WEB-INF/jsp/deleteok";
		
	}
	@RequestMapping("delete_reservation.do")
	public String a_delete_reservation(@RequestParam String ridx, Model m) {
		int result = this.rm.delete_reservation(ridx);
		String msg="";
		if(result == 1) {
			msg = "사전방문 예약 취소가 정상적으로 처리 되었습니다";
		}else {
			msg = "예약 취소 중 오류가 발생하였습니다.";
		}
		m.addAttribute("msg",msg);
		m.addAttribute("type","a_reservation");
		
		return "/WEB-INF/jsp/deleteok";
		
	}
	
	@RequestMapping("/reservation_main.do")
	public String notice_main(@ModelAttribute("params") searchdto searchdto, Model m) {
		searchdto.setRecordSize(30);
		pageresponse<reservationdto> data = this.rm.findAll(searchdto);
		m.addAttribute("data",data);
		
		return "reserve_main";
	}
	
	
	
	
}
