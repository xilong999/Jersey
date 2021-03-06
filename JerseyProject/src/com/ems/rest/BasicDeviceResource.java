package com.ems.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.ems.entity.BasicDevice;

import com.ems.service.BasicDeviceService;


@RestController
@Path("/basicDevice") // 此服务访问的路径
public class BasicDeviceResource {
	
	@Autowired
	BasicDeviceService basicDeviceService;

	@Path("/list/{deviceId}")
	@GET // 表示此服务路径基于get请求模式
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") // 表示响应的结果以文本方式返回
	public List<BasicDevice> getMutilTag(@PathParam("deviceId") String deviceId) {
		return basicDeviceService.getMutilTag(deviceId);
	}
}
