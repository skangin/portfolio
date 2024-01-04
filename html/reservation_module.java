package portfolio;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

@Repository("reservation")
public class reservation_module {
	@Resource(name="template")
	private SqlSessionTemplate tm;
	
	public int insert_reservation(reservationdto dto) {
		int result = tm.insert("portfolioDB.insert_reservation",dto);
		return result;
	}
	
	public reservationdto find_reservation(m_memberdto md) {
		reservationdto rd = tm.selectOne("portfolioDB.find_reservation",md.getMid());
		return rd;
	}
	
	public int update_reservation(reservationdto dto) {
		int result = tm.update("portfolioDB.update_reservation",dto);
		return result;
	}
	
	public int delete_reservation(String ridx) {		
		int result = tm.delete("portfolioDB.delete_reservation",ridx);	
		return result;
	}
	
	public pageresponse<reservationdto> findAll(searchdto searchdto){
		
		int count = tm.selectOne("portfolioDB.reservation_count",searchdto);
		if(count<1) {
			return new pageresponse<>(Collections.emptyList(),null);
		}
		
		pagination pagination = new pagination(count, searchdto);
		searchdto.setPagination(pagination);
		
		List<reservationdto> list = tm.selectList("portfolioDB.select_reservation",searchdto);
		return new pageresponse<>(list, pagination);
	}
	
	public List<reservationdto> select_today(){
		List<reservationdto> result = tm.selectList("portfolioDB.select_recent");
		
		if(result == null)
			result = Collections.emptyList();
		return result;
	}
}
