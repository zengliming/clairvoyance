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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author liming zeng
 * @create 2020/11/18 11:23 下午
 */
@Service
public class IndexServiceImpl implements IndexService {

    @Resource
    private SmartChineseAnalyzer smartChineseAnalyzer;

    @Value("${index.path:'./index'}")
    private String indexPath;

    private final ConcurrentMap<String, MMapDirectory> directoryMap = new ConcurrentHashMap<>();

    @Override
    public void write(AddIndexDataDTO dto) throws IOException {

        IndexWriter indexWriter = null;
        try {
            indexWriter = new IndexWriter(getDirectory(dto.getCollect()), new IndexWriterConfig(this.smartChineseAnalyzer));
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

        } finally {
            if (Objects.nonNull(indexWriter)) {
                indexWriter.commit();
                indexWriter.close();
            }
        }
    }

    @Override
    public List<String> search(String collect, String query, String key) throws ParseException, IOException {
        IndexSearcher indexSearcher = new IndexSearcher(DirectoryReader.open(getDirectory(collect)));
        final QueryParser queryParser = new QueryParser(key, smartChineseAnalyzer);
        final TopDocs topDocs = indexSearcher.search(queryParser.parse(query), 10);
        List<String> result = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            final Document doc = indexSearcher.doc(scoreDoc.doc);
            result.add(doc.get(key));
        }
        return result;
    }

    private synchronized MMapDirectory getDirectory(String collect) throws IOException {
        MMapDirectory mMapDirectory = directoryMap.get(collect);
        if (Objects.isNull(mMapDirectory)) {
            synchronized (this) {
                mMapDirectory = new MMapDirectory(Paths.get(indexPath + collect));
                directoryMap.put(collect, mMapDirectory);
            }
        }
        return mMapDirectory;
    }

    @Override
    public void delAll(String collect) throws IOException {

        IndexWriter indexWriter = null;
        try {
            indexWriter = new IndexWriter(getDirectory(collect), new IndexWriterConfig(this.smartChineseAnalyzer));
            indexWriter.deleteAll();
        } finally {
            if (Objects.nonNull(indexWriter)) {
                indexWriter.commit();
                indexWriter.close();
            }
        }

    }
}
