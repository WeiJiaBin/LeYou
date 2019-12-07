package com.leyou.goods.service;

import com.leyou.goods.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Writer;


@Service
public class GoodsHtmlService {

    @Autowired
    private TemplateEngine engine;

    @Autowired
    private GoodsService goodsService;

    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsHtmlService.class);

    public void createHtml(Long spuId) {
        //初始化静态页面模板
        //初始化运行上下文
        Context context = new Context();//org.thymeleaf.context.Context;

        //设置数据模型
        context.setVariables(this.goodsService.loadData(spuId));

        PrintWriter printWriter = null;
        try {
            //把静态文件生成到服务器本地
            File file = new File("D:\\leyou\\nginx-1.14.0\\html\\item\\" + spuId + ".html");
            printWriter = new PrintWriter(file);
            this.engine.process("item", context,  printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }

    }

    /**
     * 新建线程处理页面静态化
     * @param spuId
     */
    public void asyncExcute(Long spuId) {
        ThreadUtils.execute(()->createHtml(spuId));
        /*ThreadUtils.execute(new Runnable() {
            @Override
            public void run() {
                createHtml(spuId);
            }
        });*/
    }
}
