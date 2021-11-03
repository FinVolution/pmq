package com.ppdai.infrastructure.mq.client.kafka;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.*;
import org.elasticsearch.client.indices.IndexTemplatesExistRequest;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class EsService {
    protected RestHighLevelClient client;
    public EsService(String url, String user, String password ) throws IOException {
        client=createClient(url,user,password);
        client.ping(RequestOptions.DEFAULT);
    }
    protected RestHighLevelClient createClient(
            String url, String user, String password) {
        List<HttpHost> pairsList=parseClusterNodes(url);
        RestClientBuilder builder;
        if (!isEmpty(user) && !isEmpty(password)) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, password));
            builder = RestClient.builder(pairsList.toArray(new HttpHost[0]))
                    .setHttpClientConfigCallback(
                            httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(
                                    credentialsProvider));

        } else {
            builder = RestClient.builder(pairsList.toArray(new HttpHost[0]));
        }

        return new RestHighLevelClient(builder);
    }

    public static List<HttpHost> parseClusterNodes(String nodes) {
        List<HttpHost> httpHosts = new LinkedList<>();
        if(isEmpty(nodes))
        {
            return httpHosts;
        }
        String[] nodesSplit = nodes.split(",");
        for (String node : nodesSplit) {
            String host = node.split(":")[0];
            String port = node.split(":")[1];
            httpHosts.add(new HttpHost(host, Integer.parseInt(port), "http"));
        }
        return httpHosts;
    }
    public static boolean isEmpty(String value) {
        if (value == null) {
            return true;
        } else {
            return value.trim().length() == 0;
        }
    }
    public boolean isExistsTemplate(String indexName) throws IOException {

        IndexTemplatesExistRequest indexTemplatesExistRequest = new IndexTemplatesExistRequest(indexName);

        return client.indices().existsTemplate(indexTemplatesExistRequest, RequestOptions.DEFAULT);
    }
    public static String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date());
    }
    public boolean createTemplate(String templateName, String templateJson) throws IOException {
        if(isExistsTemplate(templateName)){
            return true;
        }
        HttpEntity entity = new NStringEntity(templateJson, ContentType.APPLICATION_JSON);
        Request request = new Request(HttpPut.METHOD_NAME, "/_template/" + templateName);
        request.setEntity(entity);
        Response response = client.getLowLevelClient()
                .performRequest(request);
        return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
    }

    public void insert(EsData data) throws IOException {
        IndexRequest request = new IndexRequest(data.getIndexName());
        request.source(data.getJson(), XContentType.JSON);
        //request.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        client.index(request, RequestOptions.DEFAULT);
    }
    public void insert(List<EsData> datas) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        datas.forEach(data->{
            IndexRequest request = new IndexRequest(data.getIndexName());
            request.source(data.getJson(), XContentType.JSON);
            bulkRequest.add(request);
        });
        //bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        BulkResponse bulkItemResponses= client.bulk(bulkRequest,RequestOptions.DEFAULT);
        if(bulkItemResponses.hasFailures()){
            //log.error(bulkItemResponses.buildFailureMessage());
        }
    }

    public void close() {
        try {
            client.close();
        } catch (IOException e) {
        }
    }
}
