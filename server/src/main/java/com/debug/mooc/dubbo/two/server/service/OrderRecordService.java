package com.debug.mooc.dubbo.two.server.service;/**
 * Created by Administrator on 2019/1/20.
 */

import com.debug.mooc.dubbo.two.server.data.DubboRecordResponse;
import com.debug.mooc.dubbo.two.server.request.RecordPushRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author:debug (SteadyJack)
 * @Date: 2019/1/20 17:32
 **/
@Service
public class OrderRecordService {

    private static final Logger log= LoggerFactory.getLogger(OrderRecordService.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HttpService httpService;

    //dubboOne发布的下单功能接口的url
    private static final String url="http://127.0.0.1:9013/v1/record/push";

    private OkHttpClient httpClient=new OkHttpClient();//通过okhttp组件

    /**
     * 处理controller层过来的用户下单数据
     * @param pushRequest
     */
    public void pushOrder(RecordPushRequest pushRequest) throws Exception{
        try {
            //TODO：构造builder 请求头
            Request.Builder builder = new Request.Builder().url(url).header("Content-Type","application/json");

            //TODO：构造请求体  需要把传递进来的参数pushRequest转换成json
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),objectMapper.writeValueAsString(pushRequest));

            //TODO：构造请求
            Request request = builder.post(requestBody).build();

            //TODO：发起请求
            Response response = httpClient.newCall(request).execute();
            log.info("处理后返回的数据：{} ",response.body().toString());

        }catch (Exception e){
            throw e;
        }
    }


    /**
     * 处理controller层过来的用户下单数据-采用通用化http服务类实战
     * @param pushRequest
     */
    public void pushOrderV2(RecordPushRequest pushRequest) throws Exception{
        try{
            Map<String,String> headMap = new HashMap<>();
            headMap.put("Content-Type","application/json");

            String response = httpService.post(url,headMap,"application/json",
                    objectMapper.writeValueAsString(pushRequest));
            log.info("响应结果：{}",response);
            //TODO:Map解析响应结果 //{"code":0,"msg":"成功","data":5} 针对数据量比较小的解析
            Map<String,Object> resMap = objectMapper.readValue(response,Map.class);//直接使用ObejctMapper封装的方法解析json字符串至map
            log.info("得到的响应解析结果：{}",resMap);
            Integer code = (Integer) resMap.get("code");
            String msg = (String) resMap.get("msg");
            Integer data = (Integer) resMap.get("data");
            log.info("code={} msg={} data={}",code,msg,data);

            //TODO:对象解析-更加通用 需要新建一个类 DubboRecordResponse对应响应字段 code msg data 针对数据量比较复杂的解析
            DubboRecordResponse dubboRecordResponse = objectMapper.readValue(response,DubboRecordResponse.class);//反序列化的过程
            log.info("得到的响应解析结果：{}",dubboRecordResponse);


        }catch (Exception e){
            throw e;
        }
    }
}



















