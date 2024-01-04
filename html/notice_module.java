package portfolio;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

@Repository("notice")
public class notice_module {
	@Resource(name="template")
	private SqlSessionTemplate tm;
	
	public int insert_notice(noticedto dto) {
		int result = tm.insert("portfolioDB.insert_notice",dto);
		return result;
	}
	
public pageresponse<noticedto> findAll(searchdto searchdto){
		
		int count = tm.selectOne("portfolioDB.notice_count",searchdto);
		if(count<1) {
			return new pageresponse<>(Collections.emptyList(),null);
		}
		
		pagination pagination = new pagination(count, searchdto);
		searchdto.setPagination(pagination);
		
		List<noticedto> list = tm.selectList("portfolioDB.select_notice",searchdto);
		return new pageresponse<>(list, pagination);
	}

public int delete_notice(String nidx) {
	
		int result = tm.delete("portfolioDB.delete_notice",nidx);
		
	
		return result;
	}

public noticedto select_view(String nidx) {
	tm.update("portfolioDB.update_viewcount",nidx);
	noticedto dto = tm.selectOne("portfolioDB.select_view",nidx);
	
	return dto;
}

}


