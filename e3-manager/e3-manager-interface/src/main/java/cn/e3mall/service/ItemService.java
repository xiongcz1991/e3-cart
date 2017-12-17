package cn.e3mall.service;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.pojo.EasyUIDataGridResult;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;

import java.util.ArrayList;

public interface ItemService {
    TbItem getItemById(Long id);
    EasyUIDataGridResult getItemList(int page,int rows);
    E3Result addItem(TbItem item ,String description);
    E3Result deleteItem(String ids);
    E3Result instockItem(String ids);
    E3Result reshelfItem(String ids);
    TbItemDesc getItemDescById(String id);
    E3Result updateItem(TbItem item,String desc);
}
