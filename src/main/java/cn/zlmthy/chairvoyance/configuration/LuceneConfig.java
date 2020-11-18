package cn.zlmthy.chairvoyance.configuration;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.store.MMapDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author liming zeng
 * @create 2020/11/18 11:45 下午
 */
@Configuration
public class LuceneConfig {

    @Value("${index.path:'./index'}")
    private String indexPath;

    @Bean
    public SmartChineseAnalyzer smartChineseAnalyzer() {
        return new SmartChineseAnalyzer();
    }

    @Bean
    public MMapDirectory mMapDirectory() throws IOException {
        return new MMapDirectory(Paths.get(indexPath));
    }
}
