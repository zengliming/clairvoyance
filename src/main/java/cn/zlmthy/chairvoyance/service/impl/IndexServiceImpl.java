package cn.zlmthy.chairvoyance.service.impl;

import cn.zlmthy.chairvoyance.dto.AddIndexDataDTO;
import cn.zlmthy.chairvoyance.service.IndexService;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.MMapDirectory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author liming zeng
 * @create 2020/11/18 11:23 下午
 */
@Service
public class IndexServiceImpl implements IndexService {

    @Resource
    private SmartChineseAnalyzer smartChineseAnalyzer;

    @Resource
    private MMapDirectory mMapDirectory;

    @Override
    public void write(AddIndexDataDTO dto) throws IOException {

        IndexWriter indexWriter = null;
        try {
            indexWriter = new IndexWriter(mMapDirectory, new IndexWriterConfig(this.smartChineseAnalyzer));
            List<Map<String, Object>> data = dto.getData();
            for (Map<String, Object> map : data) {
                Document document = new Document();
                for (String key : map.keySet()) {
                    Object value = map.get(key);
                    if (Objects.nonNull(value)) {
                        document.add(new TextField(key, value.toString(), Field.Store.YES));
                    }
                }
                indexWriter.addDocument(document);
            }

        }finally {
            if (Objects.nonNull(indexWriter)) {
                indexWriter.commit();
                indexWriter.close();
            }
        }
    }

    @Override
    public List<String> search(String query, String key) throws ParseException, IOException {
        IndexSearcher indexSearcher = new IndexSearcher(DirectoryReader.open(mMapDirectory));
        final QueryParser queryParser = new QueryParser(key, smartChineseAnalyzer);
        final TopDocs topDocs = indexSearcher.search(queryParser.parse(query), 10);
        List<String> result = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            final Document doc = indexSearcher.doc(scoreDoc.doc);
            result.add(doc.get(key));
        }
        return result;
    }
}
