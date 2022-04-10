package com.imango.resttemplate;

import com.imango.resttemplate.common.pojo.vo.EbookVO;
import com.imango.resttemplate.service.util.XMLUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RestTemplateTests {
    RestTemplate restTemplate = null;

    @Before
    public void setup() {
        // 不带超时
        // restTemplate = new RestTemplate();
        // 带有超时
        restTemplate = new RestTemplate(getClientHttpRequestFactory());
    }

    private SimpleClientHttpRequestFactory getClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory clientHttpRequestFactory
                = new SimpleClientHttpRequestFactory();
        // 连接超时设置 10s
        clientHttpRequestFactory.setConnectTimeout(1000);

        // 读取超时设置 10s
        clientHttpRequestFactory.setReadTimeout(1000);
        return clientHttpRequestFactory;
    }

    @Test
    public void testGetEbookPkg() {
        String url = "http://localhost:8088/api/ebook/get_ebook_example";
        //方式一：GET 方式获取 JSON 串数据
        System.out.println("================== 方式一 =================");
        String result = restTemplate.getForObject(url, String.class);
        System.out.println("get_ebook_example 返回结果：" + result);
        Assert.hasText(result, "get_ebook_example 返回结果为空");

        //方式二：GET 方式获取 JSON 数据映射后的 Product 实体对象
        System.out.println("================== 方式二 =================");
        EbookVO product = restTemplate.getForObject(url, EbookVO.class);
        System.out.println("get_ebook_example 返回结果：" + product);
        Assert.notNull(product, "get_ebook_example 返回结果为空");

        //方式三：GET 方式获取包含 Product 实体对象 的响应实体 ResponseEntity 对象,用 getBody() 获取
        System.out.println("================== 方式三 =================");
        ResponseEntity<EbookVO> responseEntity = restTemplate.getForEntity(url, EbookVO.class);
        System.out.println("get_ebook_example 返回结果：" + responseEntity);
        Assert.isTrue(responseEntity.getStatusCode().equals(HttpStatus.OK), "get_ebook_example 响应不成功");

    }

    @Test
    public void testGetEbookExec() {
        String url = "http://localhost:8088/api/ebook/get_ebook_example";

        // 方式一：构建请求实体 HttpEntity 对象，用于配置 Header 信息和请求参数
        System.out.println("================== 方式一 =================");
        MultiValueMap header = new LinkedMultiValueMap();
        header.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<Object> requestEntity = new HttpEntity<>(header);
        // 执行请求获取包含 Product 实体对象 的响应实体 ResponseEntity 对象,用 getBody() 获取
        ResponseEntity<EbookVO> exchangeResult = restTemplate.exchange(url, HttpMethod.GET, requestEntity, EbookVO.class);
        System.out.println("get_ebook_example 返回结果：" + exchangeResult);
        Assert.isTrue(exchangeResult.getStatusCode().equals(HttpStatus.OK), "get_ebook_example 响应不成功");

        // 方式二：根据 RequestCallback 接口实现类设置Header信息,用 ResponseExtractor 接口实现类读取响应数据
        // 备注：这里使用了 Java8 特性：Lambda 表达式语法，若未接触 Lambda 表达式后可以使用匿名内部类代替实现
        System.out.println("================== 方式二 =================");
        String executeResult = restTemplate.execute(url, HttpMethod.GET, request -> {
            request.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        }, (clientHttpResponse) -> {
            InputStream body = clientHttpResponse.getBody();
            byte[] bytes = new byte[body.available()];
            body.read(bytes);
            return new String(bytes);
        });
        System.out.println("get_ebook_example 返回结果：" + executeResult);
        Assert.hasText(executeResult, "get_ebook_example 返回结果为空");
    }

    @Test
    public void testGetEbook() {
        String url = "http://localhost:8088/api/ebook/get_ebook?id={id}";

        //方式一：将参数的值存在可变长度参数里，按照顺序进行参数匹配
        System.out.println("================== 方式一 =================");
        ResponseEntity<EbookVO> responseEntity = restTemplate.getForEntity(url, EbookVO.class, "01");
        System.out.println(responseEntity);
        Assert.isTrue(responseEntity.getStatusCode().equals(HttpStatus.OK), "get_ebook 请求不成功");
        Assert.notNull(responseEntity.getBody().getId(), "get_ebook  传递参数不成功");

        //方式二：将请求参数以键值对形式存储到 Map 集合中，用于请求时URL上的拼接
        System.out.println("================== 方式二 =================");
        Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("id", "01");
        EbookVO result = restTemplate.getForObject(url, EbookVO.class, uriVariables);
        System.out.println(result);
        Assert.notNull(result.getId(), "get_ebook  传递参数不成功");
    }

    @Test
    public void testCreateEbookForm() {
        String url = "http://localhost:8088/api/ebook/create_ebook_form";
        EbookVO eBookVO = new EbookVO("02", "Java", "Tom");
        // 设置请求的 Content-Type 为 application/x-www-form-urlencoded
        MultiValueMap<String, String> header = new LinkedMultiValueMap();
        header.add(HttpHeaders.CONTENT_TYPE, (MediaType.APPLICATION_FORM_URLENCODED_VALUE));

        // 方式一： 将请求参数值以 K=V 方式用 & 拼接，发送请求使用
        System.out.println("================== 方式一 =================");
        String ebookStr = "id=" + eBookVO.getId() + "&name=" + eBookVO.getName() + "&author=" + eBookVO.getAuthor();
        HttpEntity<String> request = new HttpEntity<>(ebookStr, header);
        ResponseEntity<String> exchangeResult = restTemplate.postForEntity(url, request, String.class);
        System.out.println("create_ebook: " + exchangeResult);
        Assert.isTrue(exchangeResult.getStatusCode().equals(HttpStatus.OK), "create_ebook 请求不成功");

        // 方式二： 将请求参数以键值对形式存储在 MultiValueMap 集合，发送请求时使用
        System.out.println("================== 方式二 =================");
        MultiValueMap<String, Object> map = new LinkedMultiValueMap();
        map.add("id", (eBookVO.getId()));
        map.add("name", (eBookVO.getName()));
        map.add("author", (eBookVO.getAuthor()));
        HttpEntity<MultiValueMap> request2 = new HttpEntity<>(map, header);
        ResponseEntity<String> exchangeResult2 = restTemplate.postForEntity(url, request2, String.class);
        System.out.println("create_ebook： " + exchangeResult2);
        Assert.isTrue(exchangeResult.getStatusCode().equals(HttpStatus.OK), "create_ebook 请求不成功");
    }

    @Test
    public void testCreateEbookXml() {
        String url = "http://localhost:8088/api/ebook/create_ebook_json";
        HttpHeaders headers = new HttpHeaders();
        // 发送XML
        headers.setContentType(MediaType.APPLICATION_XML);
        // 接收XML
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE);
        headers.add(HttpHeaders.ACCEPT_CHARSET, "utf-8");

        // 对象转换XML
        EbookVO eBookVO = new EbookVO("02", "Java", "Tom");
        String body = XMLUtil.convertToXml(eBookVO);
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(url, request, String.class);
        System.out.println("create_ebook： " + result);
        Assert.isTrue(result.getStatusCode().equals(HttpStatus.OK), "create_ebook 请求不成功");
    }

    @Test
    public void testCreateEbookJson() {
        String url = "http://localhost:8088/api/ebook/create_ebook_json";

        // 设置请求的 Content-Type 为 application/json
        MultiValueMap<String, String> header = new LinkedMultiValueMap();
        header.put(HttpHeaders.CONTENT_TYPE, Arrays.asList(MediaType.APPLICATION_JSON_VALUE));
        // 设置 Accept 向服务器表明客户端可处理的内容类型
        header.put(HttpHeaders.ACCEPT, Arrays.asList(MediaType.APPLICATION_JSON_VALUE));
        // 直接将实体 Product 作为请求参数传入，底层利用 Jackson 框架序列化成 JSON 串发送请求
        HttpEntity<EbookVO> request = new HttpEntity<>(new EbookVO("02", "公子驾到", "烽火"), header);
        ResponseEntity<String> exchangeResult = restTemplate.postForEntity(url, request, String.class);
        System.out.println("create_ebook_json: " + exchangeResult);
        Assert.isTrue(exchangeResult.getStatusCode().equals(HttpStatus.OK), "create_ebook_json 请求不成功");
    }

    // DELETE 方法请求一：不带返回参数，参数在 URL 中加占位符，参数值传到 uriVariables 里（服务端的路径也需要设置占位符）
    @Test
    public void testDeletePath() {
        String url = "http://localhost:8088/api/ebook/delete_ebook_path/{id}";
        restTemplate.delete(url, "01");
    }

    // DELETE 方法请求一：带有返回参数（URL 可以使用上述两种方式，但是客户端和服务端需要适配）
    @Test
    public void testDeletePathExec() {
        String url = "http://localhost:8088/api/ebook/delete_ebook_path/{id}";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", "01");
        ResponseEntity<String > response = restTemplate.exchange(url, HttpMethod.DELETE, null, String .class, paramMap);
        String result = response.getBody();
    }

    // DELETE 方法请求二：不带返回参数，参数值直接拼到 URL 中（服务端不需要设置参数占位符）
    @Test
    public void testDelete() {
        String url = "http://localhost:8088/api/ebook/delete_ebook?id=01";
        restTemplate.delete(url);
    }

    // DELETE 方法请求二：带有返回参数（URL 可以使用上述两种方式，但是客户端和服务端需要适配）
    @Test
    public void testDeleteExec() {
        String url = "http://localhost:8088/api/ebook/delete_ebook?id=01";
        ResponseEntity<String > response = restTemplate.exchange(url, HttpMethod.DELETE, null, String .class);
        String result = response.getBody();
    }

    // PUT 方法请求
    @Test
    public void testPut() {
        String url = "http://localhost:8088/api/ebook/update_ebook";
        Map<String, ?> variables = new HashMap<>();
        MultiValueMap<String, String> header = new LinkedMultiValueMap();
        header.put(HttpHeaders.CONTENT_TYPE, Arrays.asList(MediaType.APPLICATION_FORM_URLENCODED_VALUE));
        EbookVO ebook_latest = new EbookVO("02", "雪中悍刀行_02", "烽火");
        String productStr = "id=" + ebook_latest.getId() + "&name=" + ebook_latest.getName() + "&author=" + ebook_latest.getAuthor();
        HttpEntity<String> request = new HttpEntity<>(productStr, header);
        restTemplate.put(url, request);
    }
}