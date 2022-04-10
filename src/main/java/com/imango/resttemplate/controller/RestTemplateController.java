package com.imango.resttemplate.controller;

import com.imango.resttemplate.common.pojo.vo.EbookVO;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@RequestMapping("/ebook")
@RestController
public class RestTemplateController {
    @Resource
    RestTemplate restTemplate;

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        RestTemplate restTemplate = new RestTemplate(factory);
        return restTemplate;
    }

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5000);
        factory.setConnectTimeout(15000);
        // 设置代理
        //factory.setProxy(null);
        return factory;
    }

    @GetMapping("/get_ebook_example")
    public EbookVO getEbookExample() {
        return new EbookVO("ebook-1", "雪中悍刀行", "烽火戏诸侯");
    }

    @GetMapping("/get_ebook")
    public EbookVO getEbook(String id) {
        return new EbookVO(id, "雪中悍刀行" + "_" + id, "烽火戏诸侯");
    }

    @PostMapping("/create_ebook_form")
    public String createEbookForm(EbookVO ebookReq) {
        return ebookReq.getId();
    }

    @PostMapping("/create_ebook_json")
    public String createEbookJson(@RequestBody EbookVO ebookReq) {
        return ebookReq.getId();
    }

    @DeleteMapping("/delete_ebook_path/{id}")
    public String deleteEbookPath(@PathVariable String id) {
        return id;
    }

    @DeleteMapping("/delete_ebook")
    public String deleteEbook(String id) {
        /* 测试超时
        try {
            Thread.sleep(10000);
            System.out.println("sleet end...");
        } catch (InterruptedException e) {
            System.out.println("sleet err..." + e);
        }*/
        return id;
    }

    @PutMapping("/update_ebook")
    public String updateEbook(EbookVO ebookReq) {
        return ebookReq.getId();
    }


}
