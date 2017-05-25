package com.tyc129.vectormap.resolver;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 默认解析器
 * 解析操作对外提供的接口
 * Created by Code on 2017/5/16 0016.
 *
 * @author 谈永成
 * @version 1.0
 */
public interface Resolver<T> {

    void initialize();

    /**
     * 导入资源（流）
     *
     * @param stream 输入流
     * @return 是否导入成功
     */
    boolean importSource(InputStream stream);

    /**
     * 判断解析器是否准备好解析
     *
     * @return 准备好返回真，未准备好返回假
     */
    boolean isReady();

    /**
     * 启动数据解析
     */
    void doParse() throws Exception;

    /**
     * 清理解析器，关闭所有使用到的域和变量并置为null
     */
    void destroy();

    /**
     * 获得解析结果
     *
     * @return 数据类型为T的列表 <b>当列表为空时，返回null</b>
     */
    List<T> getResult();
}
