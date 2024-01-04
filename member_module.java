package portfolio;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

@Repository("members")
public class member_module {
	@Resource(name="template")
	private SqlSessionTemplate tm;
	
	public memberdto search_id(String userid) {
		
		memberdto dto = tm.selectOne("portfolioDB.search_id",userid);
		
		return dto;
	}
	
	public int insert_member(memberdto dto) {
		int result = tm.insert("portfolioDB.insert_member",dto);
		return result;
	}
	
	public int select_login(memberdto login){
		List<memberdto> check= tm.selectList("portfolioDB.select_login", login);
		int result;
		if(check.size()==0) {
			
			result = 2;
		}else {
			if(check.get(0).getMuse().intern()=="Y")
				result = 4;
			else
				result = 3;
		}
			
		return result; 
	}
	
	public pageresponse<memberdto> findAll(searchdto searchdto){
		
		int count = tm.selectOne("portfolioDB.member_count",searchdto);
		if(count<1) {
			return new pageresponse<>(Collections.emptyList(),null);
		}
		
		pagination pagination = new pagination(count, searchdto);
		searchdto.setPagination(pagination);
		
		List<memberdto> list = tm.selectList("portfolioDB.select_config",searchdto);
		return new pageresponse<>(list, pagination);
	}
	
	public int update_status(memberdto dto) {
		
		
		int result = tm.update("portfolioDB.update_status",dto);
		return result;
	}
	
	public void count_up(memberdto dto) {
		
		tm.update("portfolioDB.update_count",dto);
		if(dto.getMlcount().intern()=="4") {
			dto.setMuse("N");
			update_status(dto);
		}
		
	}
	
public void count_reset(memberdto dto) {
		
		tm.update("portfolioDB.count_reset",dto);		
		
		
	}
	
}
