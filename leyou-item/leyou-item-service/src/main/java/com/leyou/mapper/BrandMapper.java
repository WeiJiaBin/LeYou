package com.leyou.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<Brand> {

    /**
     * 新增商品分类和品牌中间表数据(测试git)
     * @param cid 商品分类id
     * @param id 品牌id
     * @return
     */
    @Insert("INSERT INTO tb_category_brand(category_id, brand_id) VALUES (#{cid},#{bid})")
    void insertCategoryAndBrand( @Param("cid") Long cid, @Param("bid")  Long id);

    @Select("select * from tb_brand a inner join tb_category_brand b on a.id=b.brand_id where b.category_id=#{cid}")
    List<Brand> selectBrandsByCid(@Param("cid") Long cid);


}
