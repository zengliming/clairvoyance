package cn.zlmthy.chairvoyance.controller;

import cn.zlmthy.chairvoyance.dto.AddIndexDataDTO;
import cn.zlmthy.chairvoyance.service.IndexService;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author liming zeng
 * @create 2020/11/18 11:18 下午
 */
@RestController
@RequestMapping("/index")
public class IndexController {

    @Resource
    private IndexService indexService;

    @PostMapping("/add")
    public Object add(@RequestBody AddIndexDataDTO dto) {
        try {
            indexService.write(dto);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/search")
    public List<String> search(@RequestParam String query, @RequestParam String targetKey) {
        try {
            return indexService.search("", query, targetKey);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
