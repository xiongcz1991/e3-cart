package cn.e3mall.content.service;

import cn.e3mall.common.jedis.JedisClient;
import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.mapper.TbContentMapper;
import cn.e3mall.pojo.TbContent;
import cn.e3mall.pojo.TbContentExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {
    @Autowired
    private TbContentMapper contentMapper;
    @Autowired
    private JedisClient jedisClient;
    @Value("${CONTENT_LIST}")
    private String CONTENT_KEY;
    //添加内容
    @Override
    public E3Result addContent(TbContent tbContent) {
        tbContent.setCreated(new Date());
        tbContent.setUpdated(new Date());
        this.contentMapper.insert(tbContent);
        //修改内容后删除缓存
        jedisClient.hdel(CONTENT_KEY,tbContent.getCategoryId().toString());
        return E3Result.ok();
    }
    //首页广告动态展示
    @Override
    public List<TbContent> getContentListByCid(long cid) {
        try {
            String json = jedisClient.hget(CONTENT_KEY, cid + "");
            if (StringUtils.isNotBlank(json)){
                List<TbContent> list = JsonUtils.jsonToList(json,TbContent.class);
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //缓存中没有查询数据库
        TbContentExample example = new TbContentExample();
        TbContentExample.Criteria criteria = example.createCriteria();
        //设置查询条件
        criteria.andCategoryIdEqualTo(cid);
        //执行查询
        List<TbContent> list = contentMapper.selectByExampleWithBLOBs(example);
        //存入缓存
        try {
            String json = JsonUtils.objectToJson(list);
            jedisClient.hset(CONTENT_KEY,cid+"",json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
//内容列表分页显示
    @Override
    public EasyUIDataGridResult getContentList(Long categoryId, int page, int rows) {
        //设置分页信息    当前页和每页显示条数
        PageHelper.startPage(page,rows);
        //通过categoryId查询 contentCategory
        TbContentExample contentExample = new TbContentExample();
        TbContentExample.Criteria criteria = contentExample.createCriteria();
        criteria.andCategoryIdEqualTo(categoryId);
        //执行查询
        List<TbContent> list = this.contentMapper.selectByExampleWithBLOBs(contentExample);

        PageInfo<TbContent> pageInfo = new PageInfo(list);
        //设置返回值
        EasyUIDataGridResult result = new EasyUIDataGridResult();
        result.setTotal(pageInfo.getTotal());
        result.setRows(list);
        return result;
    }
//保存修改
    @Override
    public E3Result saveContent(TbContent content) {
        this.contentMapper.updateByPrimaryKey(content);
        try {
            //修改内容后删除缓存
            jedisClient.hdel(CONTENT_KEY,content.getCategoryId().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return E3Result.ok();
    }
//删除
    @Override
    public E3Result deleteContent(String ids,Long categoryId) {

        if (StringUtils.isNotBlank(ids)) {
            if (ids.contains(",")) {
                String[] strings = ids.split(",");
                for (String sid : strings) {
                    Long id = Long.parseLong(sid);
                    this.contentMapper.deleteByPrimaryKey(id);
                }
                try {
                    //修改内容后删除缓存
                    jedisClient.hdel(CONTENT_KEY,categoryId.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return E3Result.ok();
            }else {
                //一个时
                this.contentMapper.deleteByPrimaryKey(Long.parseLong(ids));
                try {
                    //修改内容后删除缓存
                    jedisClient.hdel(CONTENT_KEY,categoryId.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return E3Result.ok();
            }
        }
        E3Result failResult = new E3Result();
        failResult.setStatus(404);
        failResult.setMsg("删除失败!");
        return failResult;
    }
}
