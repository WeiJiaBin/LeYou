package com.leyou.search.client;
 
import com.leyou.LeyouSearchApplication;
import com.leyou.common.PageResult;
import com.leyou.item.bo.SpuBo;

import com.leyou.item.pojo.Spu;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;
 
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: 98050
 * Time: 2018-10-11 22:13
 * Feature:elasticsearch goods索引创建
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ElasticsearchTest {
    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private GoodsClient goodsClient;


    @Autowired
    private SearchService searchService;



    @Test
    public void createIndex(){
        // 创建索引
        this.elasticsearchTemplate.createIndex(Goods.class);
        // 配置映射
        this.elasticsearchTemplate.putMapping(Goods.class);
        Integer page = 1;
        Integer rows = 100;

        do {
            // 分批查询spuBo
            PageResult<SpuBo> result = this.goodsClient.querySpuByPage(null, null, page, rows);
            // 遍历spubo集合转化为List<Goods>
            List<SpuBo> items = result.getItems();
            List<Goods> goodsList = items.stream().map(spuBo -> {
                try {
                    return this.searchService.buildGoods(spuBo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
            this.goodsRepository.saveAll(goodsList);

            // 获取当前页的数据条数，如果是最后一页，没有100条
            rows = items.size();
            // 每次循环页码加1

            page++;
        } while (rows == 100);
    }


}

