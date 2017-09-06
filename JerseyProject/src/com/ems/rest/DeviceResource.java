package com.ems.rest;

import com.ems.entity.Device;
import com.ems.service.DeviceService;
import com.ems.util.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Path("/device")
public class DeviceResource {
	
	@Autowired
	private DeviceService deviceService;
	
	@Path("/{tagId}")
	@GET  //表示此服务路径基于get请求模式
	@Produces(MediaType.APPLICATION_JSON+";charset=utf-8")  //表示响应的结果以文本方式返回
	public Device get(@PathParam("tagId") String tagId){
		System.out.println(Test.getName("wangyingying"));
		return deviceService.get(tagId);
	}
	
	
}
