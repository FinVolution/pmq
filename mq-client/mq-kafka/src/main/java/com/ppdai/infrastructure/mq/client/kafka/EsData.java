package com.ppdai.infrastructure.mq.client.kafka;

public class EsData {
    private String indexName;
    private String json;

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }


}