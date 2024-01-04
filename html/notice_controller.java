package portfolio;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.net.ftp.FTP;
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
public class notice_controller {

	private noticedto nd = new noticedto();
	@Resource(name="notice")
	private notice_module nm;
	
	@PostMapping("/notice_upload.do")
	public String notice_upload(@ModelAttribute("admin_notice") noticedto dto,MultipartFile nfile ,Model m) {
		String msg ="";
		if(nfile.isEmpty()) {
			dto.setNisfile("X");
		}
		else {
			if(nfile.getSize()>2097152) {
				System.out.println(nfile.getSize());
				msg="첨부 가능한 파일 크기는 2MB까지입니다.";
			}else {			
			FTPClient ftp = new FTPClient();
			ftp.setControlEncoding("utf-8");
			FTPClientConfig cf = new FTPClientConfig();
			try {
				String filenm = nfile.getOriginalFilename();		
				
				String host ="iup.cdn1.cafe24.com";
				String user = "fluc5493";
				String pw = "sk85712564";
				int port = 21;
				ftp.configure(cf);	
				ftp.connect(host,port);	
				UUID uuid = UUID.randomUUID();
				if(ftp.login(user, pw)) {	
					ftp.setFileType(FTP.BINARY_FILE_TYPE);	
					
					int rp = ftp.getReplyCode();	
					
					boolean result = ftp.storeFile("/www/img/"+uuid+"."+filenm, nfile.getInputStream());
					if(result == true) {
						
						String url = "http://fluc5493.cdn1.cafe24.com/img/"+uuid+"."+ filenm;
						dto.setNfileroute(url);
						dto.setNisfile("O");
						dto.setNfilename(filenm);
						System.out.println("정상적으로 업로드 되었습니다.");	
						
					}
					else {
						System.out.println("해당 디렉토리 및 파일에 문제가 발생했습니다.");
					}
				}
				else{				
					System.out.println("FTP 정보가 올바르지 않습니다.");
				}
				
				ftp.disconnect();	
			}
			catch(Exception e) {
				System.out.println(e);
				System.out.println("FTP 접속 정보 오류 및 접속 사용자 ");
				
			}
			
			}
			
		}
		int result = this.nm.insert_notice(dto);
		if(result ==1) {
			msg="공지 작성이 완료되었습니다.";
		}
		else {
			msg="공지 업로드 중 오류가 발생하였습니다.";
		}
		
		m.addAttribute("msg",msg);
		return "/WEB-INF/jsp/notice_upload";
	}
	
	@RequestMapping("/notice_main.do")
	public String notice_main(@ModelAttribute("params") searchdto searchdto, Model m) {
		pageresponse<noticedto> data = this.nm.findAll(searchdto);
		m.addAttribute("data",data);
		
		return "notice_main";
	}
	
	@RequestMapping("/delete_notice.do")
	public String delete_notice(@RequestParam String nidx, Model m) {
		int result = this.nm.delete_notice(nidx);
		String msg="";
		if(result == 1) {
			msg = "해당 공지가 삭제되었습니다.";
		}else {
			msg = "삭제 중 오류가 발생하였습니다.";
		}
		m.addAttribute("msg",msg);
		m.addAttribute("type","notice");
		return "/WEB-INF/jsp/deleteok";
	}
	
	@RequestMapping("/notice_view.do")
	public String view_notice(@RequestParam String nidx, Model m) {
		this.nd = this.nm.select_view(nidx);
		m.addAttribute("dto",this.nd);
		return "notice_view";
	}
	
	@RequestMapping("/download_nfile.do")
	public void download_file(@RequestParam String nidx, HttpServletResponse res) {
		this.nd = this.nm.select_view(nidx);
		String path = this.nd.getNfileroute();
		try {
			String FileName = new String(this.nd.getNfilename().getBytes("UTF-8"), "ISO-8859-1");
			URL url = new URL(path);
			res.setHeader("Content-Disposition", "attachment;filename=" + FileName);
			InputStream is = url.openStream();
			OutputStream os = res.getOutputStream();
			byte[] buffer = new byte[2050];
			int bytesRead;
			while ((bytesRead = is.read(buffer)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@RequestMapping("/main/main_notice.do")
	public String main_notice(@ModelAttribute("params") searchdto searchdto, Model m) {
		pageresponse<noticedto> data = this.nm.findAll(searchdto);
		m.addAttribute("data",data);
		
		return "/main/notice";
	}
}
