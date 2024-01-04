package portfolio;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

@Repository("info")
public class info_module {
	@Resource(name = "template")
	private SqlSessionTemplate tm;
	
	public int insert_info(infodto dto) {
		int result = tm.insert("portfolioDB.insert_info",dto);
		return result;
	}
	
	public pageresponse<infodto> findAll(searchdto searchdto){
		
		int count = tm.selectOne("portfolioDB.info_count",searchdto);
		if(count<1) {
			return new pageresponse<>(Collections.emptyList(),null);
		}
		
		pagination pagination = new pagination(count, searchdto);
		searchdto.setPagination(pagination);
		
		List<infodto> list = tm.selectList("portfolioDB.select_info",searchdto);
		return new pageresponse<>(list, pagination);
	}
	
	public List<infodto> main_info(){
		List<infodto> list = tm.selectList("portfolioDB.main_info");
		return list;
	
	}
	
	public int delete_info(String idx) {		
		int result = tm.delete("portfolioDB.delete_info",idx);	
	
		return result;
	}
}
