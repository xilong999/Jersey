package com.ems.rest;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.ems.entity.EnergyType;
import com.ems.entity.HisQuery;
import com.ems.entity.HisRising;
import com.ems.entity.HistoricalAmount;
import com.ems.entity.MapHistorical;
import com.ems.entity.RisingLevels;
import com.ems.entity.SpaceMessage;
import com.ems.service.HistoricalConsumptionService;

@RestController
@Path("/historical") // 此服务访问的路径
public class HistoricalConsumptionResource {

	@Autowired
	HistoricalConsumptionService historicalConsumptionService;
	
	
	@Path("/spaceMessage")
	@GET // 表示此服务路径基于get请求模式
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") // 表示响应的结果以文本方式返回
	public List<SpaceMessage> getArea(){
		return historicalConsumptionService.getArea();
	}
	
	@Path("/spaceBuild/{areaId}")
	@GET // 表示此服务路径基于get请求模式
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") // 表示响应的结果以文本方式返回
	public List<SpaceMessage> getBuild(@PathParam("areaId") String areaId){
		return historicalConsumptionService.getBuild(areaId);
	}
	
	@Path("/allEnergyType/{spaceId}")
	@GET // 表示此服务路径基于get请求模式
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") // 表示响应的结果以文本方式返回
	public List<EnergyType> getAllEnergyType(@PathParam("spaceId") String spaceId){
		return historicalConsumptionService.getAllEnergyType(spaceId);
	}
	
	@Path("/device/{spaceId}/{energyType}")
	@GET // 表示此服务路径基于get请求模式
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") // 表示响应的结果以文本方式返回
	public List<EnergyType> getDevice(@PathParam("spaceId") String spaceId,@PathParam("energyType") String energyType){
		return historicalConsumptionService.getDevice(spaceId,energyType);
	}

	@Path("/historicalConsumption/{area}/{build}/{energyType}/{tagId}/{deviceName}/{startTime}/{endTime}/{page}/{pageSize}")
	@GET // 表示此服务路径基于get请求模式
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") // 表示响应的结果以文本方式返回
	public List<HistoricalAmount> getHistoricalConsumption(@PathParam("area") String area,@PathParam("build") String build,@PathParam("energyType") String energyType,@PathParam("tagId") String tagId,@PathParam("deviceName") String deviceName,@PathParam("startTime") String startTime,@PathParam("endTime") String endTime,@PathParam("page") int page, @PathParam("pageSize") int pageSize ){
		return historicalConsumptionService.getHistoricalConsumption(area, build, energyType, tagId, deviceName, startTime, endTime, page, pageSize);
	}
	
	@Path("/historicalConsumptions/")
    @POST
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") // 表示响应的结果以文本方式返回
	public String getHistoricalConsumptions(HisQuery hisQuery){
		return historicalConsumptionService.getHistoricalConsumptions(hisQuery);
	}
	
	@Path("/historicalRisingLevels/")
    @POST
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8") // 表示响应的结果以文本方式返回
	public RisingLevels getRisingLevels(HisRising hisRising){
		return historicalConsumptionService.getRisingLevels(hisRising);
		
	}
}
