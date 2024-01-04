package portfolio;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class info_controller {
	
	private infodto id = new infodto();
	@Resource(name="info")
	private info_module im;
	
	@PostMapping("/info_upload.do")
	public String info_upload(@ModelAttribute("admin_info") infodto dto,Model m) {
		int result = this.im.insert_info(dto);
		String msg="";
		if(result ==1) {
			msg="환경설정 등록이 완료되었습니다.";
		}
		else {
			msg="등록 중 오류가 발생하였습니다.";
		}
		m.addAttribute("msg",msg);
		return "/WEB-INF/jsp/info_upload";
	}
	
	@RequestMapping("/info_main.do")
	public String info_main(@ModelAttribute("params") searchdto searchdto, Model m) {
		pageresponse<infodto> data = this.im.findAll(searchdto);
		m.addAttribute("data",data);
		
		return "info_main";
	}
	
	@RequestMapping("/main/index.do")
	public String main_index(Model m) {
		List<infodto> list = this.im.main_info();
		m.addAttribute("data",list);
		return "/main/index";
	}
	
	@RequestMapping("/delete_info.do")
	public String delete_info(@RequestParam String idx, Model m) {
		int result = this.im.delete_info(idx);
		String msg="";
		if(result == 1) {
			msg = "해당 설정이 삭제되었습니다.";
		}else {
			msg = "삭제 중 오류가 발생하였습니다.";
		}
		m.addAttribute("msg",msg);
		m.addAttribute("type","info");
		
		return "/WEB-INF/jsp/deleteok";
	}
}
