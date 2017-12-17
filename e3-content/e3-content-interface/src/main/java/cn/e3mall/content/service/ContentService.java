package cn.e3mall.content.service;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.pojo.TbContent;

import java.util.List;

public interface ContentService {
    E3Result addContent(TbContent tbContent);
    List<TbContent> getContentListByCid(long cid);
    EasyUIDataGridResult getContentList(Long categoryId, int page, int rows);
    E3Result saveContent(TbContent content);
    E3Result deleteContent(String ids,Long categoryId);
}
