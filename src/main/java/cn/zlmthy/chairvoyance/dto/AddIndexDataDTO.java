package cn.zlmthy.chairvoyance.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author liming zeng
 * @create 2020/11/18 11:52 下午
 */
@Data
public class AddIndexDataDTO {

    private String collect;

    private List<Map<String, Object>> data;

}
