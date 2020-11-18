package cn.zlmthy.chairvoyance;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Paths;

/**
 * @author liming zeng
 * @create 2020-11-18 19:29
 */
public class Demo {

    private static String[] titles = new String[]{"中", "中文", "中文分词", "学中文", "asdasdasdasdasdasd学中 文"};

    public static void main(String[] args) throws Exception {
        write();
        search();
    }

    private static void write() throws Exception {
        final SmartChineseAnalyzer smartChineseAnalyzer = new SmartChineseAnalyzer();
        final IndexWriterConfig indexWriterConfig = new IndexWriterConfig(smartChineseAnalyzer);
        final FSDirectory fsDirectory = FSDirectory.open(Paths.get("./index"));
        final IndexWriter indexWriter = new IndexWriter(fsDirectory, indexWriterConfig);
        for (int i = 0; i < titles.length; i++) {
            Document document = new Document();
            document.add(new StringField("id", String.valueOf(i), Field.Store.YES));
            document.add(new TextField("title", titles[i], Field.Store.YES));
            indexWriter.addDocument(document);
        }
        indexWriter.commit();
        indexWriter.close();
    }

    private static void search() throws Exception {
        // 搜索
        final SmartChineseAnalyzer smartChineseAnalyzer = new SmartChineseAnalyzer();
        final FSDirectory fsDirectory = FSDirectory.open(Paths.get("./index"));
        String q = "中文";
        IndexSearcher indexSearcher = new IndexSearcher(DirectoryReader.open(fsDirectory));
        final QueryParser queryParser = new QueryParser("title", smartChineseAnalyzer);
        final Query query = queryParser.parse(q);
        final TopDocs topDocs = indexSearcher.search(query, 10);
        System.out.println("topDocs = " + topDocs.scoreDocs.length);
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            final Document doc = indexSearcher.doc(scoreDoc.doc);
            String title = doc.get("title");
            System.out.println("id = " + doc.get("id"));
            System.out.println("title = " + title);
        }
    }
}
