package com.cyber;

import com.cyber.domain.entity.PagingResponse;
import com.cyber.domain.entity.Response;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

@RestController
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class AddressRest {

	@GetMapping("/address/search")
	public Response searchAddress() {
		PagingResponse<Address> response = new PagingResponse<Address>();
		List<Address> list = new ArrayList<>();
		Address address1 = new Address();
		address1.setAddressId("0001");
		address1.setAddressName("深业上城");
		list.add(address1);

		Address address2 = new Address();
		address2.setAddressId("0002");
		address2.setAddressName("终极法院");
		list.add(address2);

		response.setRow(list.size());
		response.setData(list);
		return response;
	}
}
