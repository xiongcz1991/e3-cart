package cn.e3mall.service;

import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.mapper.TbItemCatMapper;
import cn.e3mall.pojo.TbItemCat;
import cn.e3mall.pojo.TbItemCatExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class ItemCatServiceImpl implements ItemCatService {
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Override
    public List<EasyUITreeNode> getItemCatList(Long parentId) {
        //根据parentId查询节点目录
        TbItemCatExample itemCatExample = new TbItemCatExample();
        //设置查询条件
        TbItemCatExample.Criteria criteria = itemCatExample.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        List<TbItemCat> itemCats = this.itemCatMapper.selectByExample(itemCatExample);
        //转换成EasyUITreeNode列表
        ArrayList<EasyUITreeNode> resultList = new ArrayList<>();
        for (TbItemCat itemCat : itemCats) {
            EasyUITreeNode easyUITreeNode = new EasyUITreeNode();
            easyUITreeNode.setId(itemCat.getId());
            easyUITreeNode.setText(itemCat.getName());
            easyUITreeNode.setState(itemCat.getIsParent()?"closed":"open");
            //逆向生成会自动将只有0,1的列转换为true.false close在easyui tree中是含子节点
            //添加到列表
            resultList.add(easyUITreeNode);
        }
        return resultList;
    }
}
