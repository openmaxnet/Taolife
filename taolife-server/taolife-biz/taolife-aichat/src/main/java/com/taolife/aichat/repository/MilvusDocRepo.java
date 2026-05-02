package com.taolife.aichat.repository;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.taolife.aichat.enums.MilvusField;
import com.taolife.common.properties.MilvusProperties;
import com.taolife.aichat.entity.HealthDocument;
import com.taolife.common.exception.BusinessException;
import com.taolife.common.exception.ExceptionCode;
import com.taolife.common.utils.PageResult;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.common.DataType;
import io.milvus.v2.common.IndexParam;
import io.milvus.v2.service.collection.request.*;
import io.milvus.v2.service.collection.response.GetCollectionStatsResp;
import io.milvus.v2.service.vector.request.*;
import io.milvus.v2.service.vector.request.data.FloatVec;
import io.milvus.v2.service.vector.response.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Milvus文档仓库
 * 替代MongoDB，同时承担文档存储和向量检索
 *
 * @author 文二
 * @date 2026-04-15
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class MilvusDocRepo {

    /** 检索时返回的字段（不含embedding和id） */
    private static final List<String> SEARCH_FIELDS = Arrays.stream(MilvusField.values())
            .filter(f -> f != MilvusField.ID && f != MilvusField.EMBEDDING)
            .map(MilvusField::getColumnName)
            .collect(Collectors.toUnmodifiableList());

    /** 全部字段（不含embedding） */
    private static final List<String> ALL_FIELDS = Arrays.stream(MilvusField.values())
            .filter(f -> f != MilvusField.EMBEDDING)
            .map(MilvusField::getColumnName)
            .collect(Collectors.toUnmodifiableList());

    private final MilvusClientV2 milvusClient;
    private final MilvusProperties milvusProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String collectionName() {
        return milvusProperties.getCollectionName();
    }

    /**
     * 应用启动时自动建表+建索引
     */
    @PostConstruct
    public void ensureCollection() {
        try {
            Boolean exists = milvusClient.hasCollection(HasCollectionReq.builder()
                    .collectionName(collectionName()).build());
            if (Boolean.TRUE.equals(exists)) {
                log.info("Milvus集合已存在: {}", collectionName());
                return;
            }

            CreateCollectionReq.CollectionSchema schema = CreateCollectionReq.CollectionSchema.builder().build();
            schema.addField(AddFieldReq.builder().fieldName(MilvusField.ID.getColumnName()).dataType(DataType.Int64).isPrimaryKey(true).autoID(true).build());
            schema.addField(AddFieldReq.builder().fieldName(MilvusField.DOC_ID.getColumnName()).dataType(DataType.VarChar).maxLength(64).build());
            schema.addField(AddFieldReq.builder().fieldName(MilvusField.TITLE.getColumnName()).dataType(DataType.VarChar).maxLength(512).build());
            schema.addField(AddFieldReq.builder().fieldName(MilvusField.CONTENT.getColumnName()).dataType(DataType.VarChar).maxLength(8192).build());
            schema.addField(AddFieldReq.builder().fieldName(MilvusField.CATEGORY.getColumnName()).dataType(DataType.VarChar).maxLength(64).build());
            schema.addField(AddFieldReq.builder().fieldName(MilvusField.TAGS.getColumnName()).dataType(DataType.VarChar).maxLength(2048).build());
            schema.addField(AddFieldReq.builder().fieldName(MilvusField.SOURCE.getColumnName()).dataType(DataType.VarChar).maxLength(256).build());
            schema.addField(AddFieldReq.builder().fieldName(MilvusField.CONSTITUTION_TYPE.getColumnName()).dataType(DataType.VarChar).maxLength(64).build());
            schema.addField(AddFieldReq.builder().fieldName(MilvusField.SEASON.getColumnName()).dataType(DataType.VarChar).maxLength(32).build());
            schema.addField(AddFieldReq.builder().fieldName(MilvusField.EMBEDDING.getColumnName()).dataType(DataType.FloatVector).dimension(milvusProperties.getEmbeddingDimension()).build());
            schema.addField(AddFieldReq.builder().fieldName(MilvusField.CREATED_AT.getColumnName()).dataType(DataType.Int64).build());
            schema.addField(AddFieldReq.builder().fieldName(MilvusField.UPDATED_AT.getColumnName()).dataType(DataType.Int64).build());

            List<IndexParam> indexes = List.of(IndexParam.builder()
                    .fieldName(MilvusField.EMBEDDING.getColumnName())
                    .indexType(IndexParam.IndexType.HNSW)
                    .metricType(IndexParam.MetricType.COSINE)
                    .extraParams(Map.of("M", 16, "efConstruction", 256))
                    .build());

            milvusClient.createCollection(CreateCollectionReq.builder()
                    .collectionName(collectionName())
                    .collectionSchema(schema)
                    .indexParams(indexes)
                    .build());
            log.info("Milvus集合创建成功: {}", collectionName());

        } catch (Exception e) {
            log.error("Milvus集合初始化失败: {}", collectionName(), e);
        }
    }

    /**
     * 插入单条文档
     */
    public void insert(HealthDocument doc) {
        try {
            milvusClient.insert(InsertReq.builder()
                    .collectionName(collectionName())
                    .data(List.of(documentToRow(doc)))
                    .build());
            log.info("插入文档成功，docId: {}", doc.getDocId());
        } catch (Exception e) {
            log.error("插入文档失败，docId: {}", doc.getDocId(), e);
            throw new BusinessException(ExceptionCode.AI_GENERATION_FAILED, "插入文档失败: " + e.getMessage());
        }
    }

    /**
     * 批量插入文档
     */
    public void insertBatch(List<HealthDocument> docs) {
        try {
            List<JsonObject> rows = docs.stream().map(this::documentToRow).toList();
            milvusClient.insert(InsertReq.builder()
                    .collectionName(collectionName())
                    .data(rows)
                    .build());
            log.info("批量插入文档成功，数量: {}", docs.size());
        } catch (Exception e) {
            log.error("批量插入文档失败", e);
            throw new BusinessException(ExceptionCode.AI_GENERATION_FAILED, "批量插入文档失败: " + e.getMessage());
        }
    }

    /**
     * 按docId删除文档
     */
    public void deleteByDocId(String docId) {
        try {
            milvusClient.delete(DeleteReq.builder()
                    .collectionName(collectionName())
                    .filter(MilvusField.DOC_ID.getColumnName() + " == \"" + docId + "\"")
                    .build());
            log.info("删除文档成功，docId: {}", docId);
        } catch (Exception e) {
            log.error("删除文档失败，docId: {}", docId, e);
            throw new BusinessException(ExceptionCode.AI_GENERATION_FAILED, "删除文档失败: " + e.getMessage());
        }
    }

    /**
     * 按主键ID删除文档
     */
    public void deleteById(Long id) {
        try {
            milvusClient.delete(DeleteReq.builder()
                    .collectionName(collectionName())
                    .filter(MilvusField.ID.getColumnName() + " == " + id)
                    .build());
            log.info("删除文档成功，id: {}", id);
        } catch (Exception e) {
            log.error("删除文档失败，id: {}", id, e);
            throw new BusinessException(ExceptionCode.AI_GENERATION_FAILED, "删除文档失败: " + e.getMessage());
        }
    }

    /**
     * 更新文档（先删后插）
     */
    public void update(HealthDocument doc) {
        deleteByDocId(doc.getDocId());
        insert(doc);
        log.info("更新文档成功，docId: {}", doc.getDocId());
    }

    /**
     * 向量检索
     */
    public List<HealthDocument> vectorSearch(float[] queryVector, int topK, String categoryFilter) {
        try {
            String filter = "";
            if (categoryFilter != null && !categoryFilter.isEmpty()) {
                filter = MilvusField.CATEGORY.getColumnName() + " == \"" + categoryFilter + "\"";
            }

            SearchResp searchResp = milvusClient.search(SearchReq.builder()
                    .collectionName(collectionName())
                    .data(List.of(new FloatVec(queryVector)))
                    .limit(topK)
                    .filter(filter)
                    .outputFields(SEARCH_FIELDS)
                    .build());

            List<HealthDocument> results = new ArrayList<>();
            List<List<SearchResp.SearchResult>> searchResults = searchResp.getSearchResults();
            if (searchResults != null && !searchResults.isEmpty()) {
                for (SearchResp.SearchResult hit : searchResults.get(0)) {
                    HealthDocument doc = rowToDocument(hit.getEntity());
                    doc.setSimilarity((double) hit.getScore());
                    results.add(doc);
                }
            }
            log.info("向量检索完成，结果数: {}", results.size());
            return results;

        } catch (Exception e) {
            log.error("向量检索失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 按docId查询
     */
    public HealthDocument queryByDocId(String docId) {
        try {
            QueryResp resp = milvusClient.query(QueryReq.builder()
                    .collectionName(collectionName())
                    .filter(MilvusField.DOC_ID.getColumnName() + " == \"" + docId + "\"")
                    .limit(1L)
                    .outputFields(ALL_FIELDS)
                    .build());
            List<QueryResp.QueryResult> results = resp.getQueryResults();
            if (results != null && !results.isEmpty()) {
                return rowToDocument(results.get(0).getEntity());
            }
            return null;
        } catch (Exception e) {
            log.error("按docId查询失败，docId: {}", docId, e);
            return null;
        }
    }

    /**
     * 按主键查询
     */
    public HealthDocument queryById(Long id) {
        try {
            QueryResp resp = milvusClient.query(QueryReq.builder()
                    .collectionName(collectionName())
                    .filter(MilvusField.ID.getColumnName() + " == " + id)
                    .limit(1L)
                    .outputFields(ALL_FIELDS)
                    .build());
            List<QueryResp.QueryResult> results = resp.getQueryResults();
            if (results != null && !results.isEmpty()) {
                return rowToDocument(results.get(0).getEntity());
            }
            return null;
        } catch (Exception e) {
            log.error("按ID查询失败，id: {}", id, e);
            return null;
        }
    }

    /**
     * 按分类查询
     */
    public List<HealthDocument> queryByCategory(String category, int limit) {
        try {
            QueryResp resp = milvusClient.query(QueryReq.builder()
                    .collectionName(collectionName())
                    .filter(MilvusField.CATEGORY.getColumnName() + " == \"" + category + "\"")
                    .limit((long) limit)
                    .outputFields(ALL_FIELDS)
                    .build());
            return resp.getQueryResults().stream()
                    .map(r -> rowToDocument(r.getEntity()))
                    .toList();
        } catch (Exception e) {
            log.error("分类查询失败，category: {}", category, e);
            return Collections.emptyList();
        }
    }

    /**
     * 分页查询（管理后台用）
     */
    public PageResult<HealthDocument> pageQuery(int pageNo, int pageSize, String category, String keyword) {
        try {
            StringBuilder filterBuilder = new StringBuilder();
            if (category != null && !category.isEmpty()) {
                filterBuilder.append(MilvusField.CATEGORY.getColumnName()).append(" == \"").append(category).append("\"");
            }
            if (keyword != null && !keyword.isEmpty()) {
                if (!filterBuilder.isEmpty()) {
                    filterBuilder.append(" and ");
                }
                filterBuilder.append(MilvusField.TITLE.getColumnName()).append(" like \"%").append(keyword).append("%\"");
            }
            String filter = filterBuilder.isEmpty() ? "id > 0" : filterBuilder.toString();

            // 获取总数
            QueryResp countResp = milvusClient.query(QueryReq.builder()
                    .collectionName(collectionName())
                    .filter(filter)
                    .outputFields(List.of(MilvusField.ID.getColumnName()))
                    .build());
            long total = countResp.getQueryResults().size();

            // 分页查询
            long offset = (long) (pageNo - 1) * pageSize;
            QueryResp pageResp = milvusClient.query(QueryReq.builder()
                    .collectionName(collectionName())
                    .filter(filter)
                    .offset(offset)
                    .limit((long) pageSize)
                    .outputFields(ALL_FIELDS)
                    .build());
            List<HealthDocument> docs = pageResp.getQueryResults().stream()
                    .map(r -> rowToDocument(r.getEntity()))
                    .toList();

            PageResult<HealthDocument> result = new PageResult<>();
            result.setList(docs);
            result.setPageNo(pageNo);
            result.setPageSize(pageSize);
            result.setTotalRow(total);
            result.setTotalPage((int) ((total + pageSize - 1) / pageSize));
            return result;
        } catch (Exception e) {
            log.error("分页查询失败", e);
            return new PageResult<>();
        }
    }

    /**
     * 获取文档总数
     */
    public long count() {
        try {
            GetCollectionStatsResp stats = milvusClient.getCollectionStats(
                    GetCollectionStatsReq.builder().collectionName(collectionName()).build());
            return stats.getNumOfEntities();
        } catch (Exception e) {
            log.error("获取文档总数失败", e);
            return 0;
        }
    }

    /**
     * 检查文档是否存在
     */
    public boolean existsByDocId(String docId) {
        return queryByDocId(docId) != null;
    }

    // ========== 转换方法 ==========

    private JsonObject documentToRow(HealthDocument doc) {
        JsonObject row = new JsonObject();
        row.addProperty(MilvusField.DOC_ID.getColumnName(), doc.getDocId());
        row.addProperty(MilvusField.TITLE.getColumnName(), doc.getTitle() != null ? doc.getTitle() : "");
        row.addProperty(MilvusField.CONTENT.getColumnName(), doc.getContent() != null ? doc.getContent() : "");
        row.addProperty(MilvusField.CATEGORY.getColumnName(), doc.getCategory() != null ? doc.getCategory() : "");
        row.addProperty(MilvusField.TAGS.getColumnName(), doc.getTags() != null ? toJson(doc.getTags()) : "[]");
        row.addProperty(MilvusField.SOURCE.getColumnName(), doc.getSource() != null ? doc.getSource() : "");
        row.addProperty(MilvusField.CONSTITUTION_TYPE.getColumnName(), doc.getConstitutionType() != null ? doc.getConstitutionType() : "");
        row.addProperty(MilvusField.SEASON.getColumnName(), doc.getSeason() != null ? doc.getSeason() : "");
        if (doc.getEmbedding() != null) {
            JsonArray arr = new JsonArray();
            for (float v : doc.getEmbedding()) {
                arr.add(v);
            }
            row.add(MilvusField.EMBEDDING.getColumnName(), arr);
        }
        row.addProperty(MilvusField.CREATED_AT.getColumnName(), doc.getCreatedAt() != null ? doc.getCreatedAt().toEpochSecond(ZoneOffset.UTC) : 0L);
        row.addProperty(MilvusField.UPDATED_AT.getColumnName(), doc.getUpdatedAt() != null ? doc.getUpdatedAt().toEpochSecond(ZoneOffset.UTC) : 0L);
        return row;
    }

    private HealthDocument rowToDocument(Map<String, Object> row) {
        if (row == null) return null;
        HealthDocument doc = new HealthDocument();
        Object idVal = row.get(MilvusField.ID.getColumnName());
        if (idVal != null) {
            doc.setId(((Number) idVal).longValue());
        }
        doc.setDocId(getStr(row, MilvusField.DOC_ID));
        doc.setTitle(getStr(row, MilvusField.TITLE));
        doc.setContent(getStr(row, MilvusField.CONTENT));
        doc.setCategory(getStr(row, MilvusField.CATEGORY));
        doc.setSource(getStr(row, MilvusField.SOURCE));
        doc.setConstitutionType(getStr(row, MilvusField.CONSTITUTION_TYPE));
        doc.setSeason(getStr(row, MilvusField.SEASON));

        String tagsJson = getStr(row, MilvusField.TAGS);
        if (tagsJson != null && !tagsJson.isEmpty()) {
            try {
                doc.setTags(objectMapper.readValue(tagsJson, new TypeReference<List<String>>() {}));
            } catch (JacksonException e) {
                doc.setTags(Collections.emptyList());
            }
        }

        Object createdAt = row.get(MilvusField.CREATED_AT.getColumnName());
        if (createdAt != null) {
            doc.setCreatedAt(LocalDateTime.ofEpochSecond(((Number) createdAt).longValue(), 0, ZoneOffset.UTC));
        }
        Object updatedAt = row.get(MilvusField.UPDATED_AT.getColumnName());
        if (updatedAt != null) {
            doc.setUpdatedAt(LocalDateTime.ofEpochSecond(((Number) updatedAt).longValue(), 0, ZoneOffset.UTC));
        }
        return doc;
    }

    private String getStr(Map<String, Object> row, MilvusField field) {
        Object val = row.get(field.getColumnName());
        return val != null ? val.toString() : null;
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JacksonException e) {
            return "[]";
        }
    }
}
