package cn.zlmthy.chairvoyance.service;

import cn.zlmthy.chairvoyance.dto.AddIndexDataDTO;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.util.List;

/**
 * @author zengliming
 * @Description TODO
 * @Date 2020/11/18 11:21 下午
 */
public interface IndexService {

    /**
     * 写入索引
     *
     * @param dto
     * @throws IOException
     */
    void write(AddIndexDataDTO dto) throws IOException;

    /**
     * 查询
     *
     * @param collect
     * @param query
     * @param key
     * @return
     * @throws IOException
     * @throws ParseException
     */
    List<String> search(String collect, String query, String key) throws ParseException, IOException;

    /**
     * 删除集合下所有的索引
     *
     * @param collect
     */
    void delAll(String collect) throws IOException;
}
