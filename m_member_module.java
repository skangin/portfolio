package portfolio;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

@Repository("m_member")
public class m_member_module {
	@Resource(name="template")
	private SqlSessionTemplate tm;
	
	public m_memberdto m_search_id(String userid) {
			
			m_memberdto dto = tm.selectOne("portfolioDB.m_search_id",userid);
			
			return dto;
	}
	
	public int m_insert_member(m_memberdto dto) {
		int result = tm.insert("portfolioDB.m_insert_member",dto);
		return result;
	}
	
	public int m_select_login(m_memberdto login){
		List<m_memberdto> check= tm.selectList("portfolioDB.m_select_login", login);
		int result;
		if(check.size()==0) {
			
			result = 1;
		}else {
			if(check.get(0).getMuse().intern()=="Y")
				result = 4;
			else
				result = 3;
		}
			
		return result; 
	}
	
	public pageresponse<m_memberdto> findAll(searchdto searchdto){
		
		int count = tm.selectOne("portfolioDB.m_member_count",searchdto);
		if(count<1) {
			return new pageresponse<>(Collections.emptyList(),null);
		}
		
		pagination pagination = new pagination(count, searchdto);
		searchdto.setPagination(pagination);
		
		List<m_memberdto> list = tm.selectList("portfolioDB.select_member",searchdto);
		return new pageresponse<>(list, pagination);
	}
	
	public int ban_member(String midx) {
		
		int result = tm.update("portfolioDB.update_member",midx);
		
	
		return result;
	}
	
	public List<m_memberdto> select_today(String today){
		List<m_memberdto> result = tm.selectList("portfolioDB.select_today",today);
		
		if(result == null)
			result = Collections.emptyList();
		return result;
	}
}
