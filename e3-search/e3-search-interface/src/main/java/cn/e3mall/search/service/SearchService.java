package cn.e3mall.search.service;

import cn.e3mall.common.pojo.SearchResult;

import java.util.List;

public interface SearchService {
    SearchResult searchItemList(String keyword,int page, int rows) throws Exception;
}
