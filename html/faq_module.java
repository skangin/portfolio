package portfolio;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

@Repository("faq")
public class faq_module {
	@Resource(name="template")
	private SqlSessionTemplate tm;
	
	public pageresponse<faqdto> findAll(searchdto searchdto){
			
			int count = tm.selectOne("portfolioDB.faq_count",searchdto);
			if(count<1) {
				return new pageresponse<>(Collections.emptyList(),null);
			}
			
			pagination pagination = new pagination(count, searchdto);
			searchdto.setPagination(pagination);
			
			List<faqdto> list = tm.selectList("portfolioDB.select_faq",searchdto);
			return new pageresponse<>(list, pagination);
		}
	
	public int insert_faq(faqdto dto) {
		int result = tm.insert("portfolioDB.insert_faq",dto);
		return result;
	}
	
	public int delete_faq(String fidx) {
		
		int result = tm.delete("portfolioDB.delete_faq",fidx);
		
	
		return result;
	}
}
