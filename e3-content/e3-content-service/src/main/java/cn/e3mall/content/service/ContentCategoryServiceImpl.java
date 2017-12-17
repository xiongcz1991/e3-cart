package cn.e3mall.content.service;

import cn.e3mall.common.pojo.E3Result;
import cn.e3mall.common.pojo.EasyUITreeNode;
import cn.e3mall.mapper.TbContentCategoryMapper;
import cn.e3mall.pojo.TbContentCategory;
import cn.e3mall.pojo.TbContentCategoryExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ContentCategoryServiceImpl implements ContentCategoryService{
    @Autowired
    private TbContentCategoryMapper contentCategoryMapper;
    @Override
    public List<EasyUITreeNode> getContentCatList(Long parentId) {
        TbContentCategoryExample contentCategoryExample = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = contentCategoryExample.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        List<TbContentCategory> list = this.contentCategoryMapper.selectByExample(contentCategoryExample);
        ArrayList<EasyUITreeNode> easyUITreeNodes = new ArrayList<>();
        for (TbContentCategory contentCategory : list) {
            EasyUITreeNode easyUITreeNode = new EasyUITreeNode();
            easyUITreeNode.setId(contentCategory.getId());
            easyUITreeNode.setText(contentCategory.getName());
            easyUITreeNode.setState(contentCategory.getIsParent()?"closed":"open");
            easyUITreeNodes.add(easyUITreeNode);
        }
        return easyUITreeNodes;
    }

    @Override
    public E3Result addContentCategory(Long parentId, String name) {
        TbContentCategory contentCategory = new TbContentCategory();
        contentCategory.setParentId(parentId);
        contentCategory.setName(name);
        contentCategory.setCreated(new Date());
        contentCategory.setUpdated(new Date());
        contentCategory.setIsParent(false);
        //默认排序就是1
        contentCategory.setSortOrder(1);
        //1(正常),2(删除)
        contentCategory.setStatus(1);
        this.contentCategoryMapper.insert(contentCategory);
        //判断父节点的isparent属性。如果不是true改为true
        //根据parentid查询父节点
        TbContentCategory parent = contentCategoryMapper.selectByPrimaryKey(parentId);
        if (!parent.getIsParent()) {
            parent.setIsParent(true);
            //更新到数数据库
            contentCategoryMapper.updateByPrimaryKey(parent);
        }
        //返回结果，返回E3Result，包含pojo
        return E3Result.ok(contentCategory);

    }

    @Override
    public E3Result deleteContentCategory(Long id) {
        TbContentCategory contentCategory = this.contentCategoryMapper.selectByPrimaryKey(id);
        if (contentCategory.getIsParent()){
            E3Result failResult = new E3Result();
            failResult.setStatus(404);
            failResult.setMsg("只能删除叶子节点!");
            return failResult;
        }else {
            if (contentCategory.getParentId()!=0) {
                this.contentCategoryMapper.deleteByPrimaryKey(id);
                //获取父
                TbContentCategory parentCat = this.contentCategoryMapper.selectByPrimaryKey(contentCategory.getParentId());
                TbContentCategoryExample example = new TbContentCategoryExample();
                TbContentCategoryExample.Criteria criteria = example.createCriteria();
                criteria.andParentIdEqualTo(parentCat.getId());
                List<TbContentCategory> list = this.contentCategoryMapper.selectByExample(example);
                if (list.size()>0){
                    return E3Result.ok();
                }else {
                    parentCat.setIsParent(false);
                    this.contentCategoryMapper.updateByPrimaryKeySelective(parentCat);
                }
            }
            return E3Result.ok();
        }
    }
    //修改分类名字
    @Override
    public E3Result updateContentCategory(Long id, String name) {
        TbContentCategory contentCategory = this.contentCategoryMapper.selectByPrimaryKey(id);
        contentCategory.setName(name);
        contentCategory.setUpdated(new Date());
        this.contentCategoryMapper.updateByPrimaryKeySelective(contentCategory);
        return E3Result.ok();
    }
}
