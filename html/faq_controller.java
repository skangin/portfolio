package portfolio;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class faq_controller {

	private faqdto fd = new faqdto();
	@Resource(name="faq")
	private faq_module fm;
	
	@RequestMapping("/faq_main.do")
	public String faq_main(@ModelAttribute("params") searchdto searchdto, Model m) {
		pageresponse<faqdto> data = this.fm.findAll(searchdto);
		m.addAttribute("data",data);
		
		return "faq_main";
	}
	
	@PostMapping("/faq_upload.do")
	public String notice_upload(@ModelAttribute("admin_faq") faqdto dto,Model m) {
		int result = this.fm.insert_faq(dto);
		String msg="";
		if(result ==1) {
			msg="FAQ 작성이 완료되었습니다.";
		}
		else {
			msg="FAQ 업로드 중 오류가 발생하였습니다.";
		}
		m.addAttribute("msg",msg);
		return "/WEB-INF/jsp/faq_upload";
	}
	
	@RequestMapping("/delete_faq.do")
	public String delete_faq(@RequestParam String fidx, Model m) {
		int result = this.fm.delete_faq(fidx);
		String msg="";
		if(result == 1) {
			msg = "해당 질답이 삭제되었습니다.";
		}else {
			msg = "삭제 중 오류가 발생하였습니다.";
		}
		m.addAttribute("msg",msg);
		m.addAttribute("type","faq");
		
		return "/WEB-INF/jsp/deleteok";
	}
	
	@RequestMapping("/main/main_faq.do")
	public String main_faq(@ModelAttribute("params") searchdto searchdto, Model m) {
		pageresponse<faqdto> data = this.fm.findAll(searchdto);
		m.addAttribute("data",data);
		
		return "/main/faq";
	}
}
