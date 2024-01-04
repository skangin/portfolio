package portfolio;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class pageresponse<T> {
	 	
		private List<T> list = new ArrayList();
	    private pagination pagination;

	    public pageresponse(List<T> list, pagination pagination) {
	        this.list.addAll(list);
	        this.pagination = pagination;
	    }
}
