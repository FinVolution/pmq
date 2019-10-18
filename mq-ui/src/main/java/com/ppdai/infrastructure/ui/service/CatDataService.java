package com.ppdai.infrastructure.ui.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ppdai.infrastructure.mq.biz.common.SoaConfig;
import com.ppdai.infrastructure.mq.biz.common.trace.Tracer;
import com.ppdai.infrastructure.mq.biz.common.trace.spi.Transaction;
import com.ppdai.infrastructure.mq.biz.ui.dto.request.CatGetDataRequest;
import com.ppdai.infrastructure.mq.biz.ui.dto.response.CatGetDataResponse;

@Service
public class CatDataService {
    @Autowired
    private SoaConfig soaConfig;
    private RestTemplate restTemplate = new RestTemplate();
    private String excludeType="";
    private String date = "";
    private String domain="";

    public CatGetDataResponse getCatData(CatGetDataRequest catGetDataRequest) {
        date=new SimpleDateFormat("yyyyMMddHH").format(new Date());
        if(StringUtils.isNotEmpty(catGetDataRequest.getDomain())){
            domain= catGetDataRequest.getDomain();
        }else{
           // domain=soaConfig.getDefaultDomain();
        }
        String url="";
        //String url=soaConfig.getCatUrl()+"&domain="+domain+"&date="+date;
        if(StringUtils.isNotEmpty(catGetDataRequest.getType())){
            url=url+"&type="+ catGetDataRequest.getType();
        }
        if (StringUtils.isNotEmpty(catGetDataRequest.getIp())){
            url=url+"&ip="+ catGetDataRequest.getIp();
        }
        String xml = call(url, String.class);
        return readStringXmlOut(xml, catGetDataRequest);
    }

    private <T> T call(String url, Class<T> t) {
        Transaction transaction = Tracer.newTransaction("cat", url);
        try {
            ResponseEntity<T> responseEntity = restTemplate.getForEntity(url, t);
            transaction.setStatus(Transaction.SUCCESS);
            return responseEntity.getBody();
        } catch (Exception e) {
            transaction.setStatus(e);
        } finally {
            transaction.complete();
        }
        return null;
    }


    /**
     * @description 将xml字符串转换成map
     * @param xml
     * @return CatGetDataResponse
     */
    public CatGetDataResponse readStringXmlOut(String xml,CatGetDataRequest catGetDataRequest) {
        List<Map<String,Object>> catDataList=new ArrayList<>();
        Document doc = null;
        try {
            // 将字符串转为XML
            doc = DocumentHelper.parseText(xml);
            // 获取根节点
            Element rootElt = doc.getRootElement();

            // 获取根节点下的子节点machine
            Iterator machine = rootElt.elementIterator("machine");
            Element machineEle = (Element)machine.next();
            Iterator typeIterator = machineEle.elementIterator("type");

            //type不为空时，查询该type下的所有name
            if(StringUtils.isNotEmpty(catGetDataRequest.getType())){
                while (typeIterator.hasNext()){
                    Element typeEle = (Element) typeIterator.next();
                    if(typeEle.attributeValue("id").equals(catGetDataRequest.getType())){
                        Iterator nameIterator=typeEle.elementIterator("name");
                        addDataToList(nameIterator,catDataList, catGetDataRequest);
                    }
                }
            }else {//type为空是，只查询所有type数据
                addDataToList(typeIterator,catDataList, catGetDataRequest);
            }


        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new CatGetDataResponse(new Long(catDataList.size()),catDataList);
    }

    private void addDataToList(Iterator iterator,List<Map<String,Object>> list,CatGetDataRequest catGetDataRequest){
        // 遍历节点
        while (iterator.hasNext()) {
            Element recordEle = (Element) iterator.next();
            if(!canShow(recordEle)){
                continue;
            }
            // 拿到节点的属性值
            list.add(setAttribute(recordEle, catGetDataRequest));
        }
    }

    private Map setAttribute(Element recordEle,CatGetDataRequest catGetDataRequest){
        Map<String,Object> map = new HashMap();
        map.put("pageLevel", catGetDataRequest.getPageLevel());
        map.put("id", recordEle.attributeValue("id"));
        map.put("totalCount",recordEle.attributeValue("totalCount"));
        map.put("failCount",recordEle.attributeValue("failCount"));
        map.put("failPercent",recordEle.attributeValue("failPercent"));
        map.put("min",recordEle.attributeValue("min"));
        map.put("max",recordEle.attributeValue("max"));
        map.put("avg",recordEle.attributeValue("avg"));
        map.put("std",recordEle.attributeValue("std"));
        map.put("tps",recordEle.attributeValue("tps"));
        map.put("line95Value",recordEle.attributeValue("line95Value"));
        map.put("line99Value",recordEle.attributeValue("line99Value"));
        return map;
    }

    public List getIps(String domain){

        //String url=soaConfig.getCatUrl()+"&domain="+domain;
    	String url="";
        String xml = call(url, String.class);
        Document doc = null;
        List ipList =new ArrayList();
        try {
            // 将字符串转为XML
            doc = DocumentHelper.parseText(xml);
            // 获取根节点
            Element rootElt = doc.getRootElement();

            // 获取根节点下的子节点machine
            Iterator ipIterator = rootElt.elementIterator("ip");

            // 遍历节点
            while (ipIterator.hasNext()) {
                Element recordEle = (Element) ipIterator.next();
                ipList.add(recordEle.getText());
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ipList;
    }

    private boolean canShow(Element recordEle){
        excludeType=recordEle.attributeValue("id");
        if(excludeType.equals("_CatMergeTree")){
            return false;
        }
        if(excludeType.equals("System")){
            return false;
        }
        if(excludeType.startsWith("Apollo")){
            return false;
        }
        if(excludeType.equals("cat")){
            return false;
        }
        return true;
    }


}
