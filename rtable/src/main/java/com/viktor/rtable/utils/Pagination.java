package com.viktor.rtable.utils;

import java.util.List;

/**
 * Pagination Algorithm.
 *
 * @param <T>
 */
public class Pagination<T> {

    public int itemsPerPage;
    private int indexPage;
    private List<T> collection;

    public Pagination(List<T> items, int itemsPerPage, int indexPage) {
        this.indexPage = indexPage;
        this.itemsPerPage = itemsPerPage;
        this.collection = items;
    }

    public List<T> next(int pagina){

        if(((indexPage)*itemsPerPage) < collection.size())
            indexPage++;
        return pages();
    }

    public List<T> prev(){
        if(indexPage > 0)
            indexPage--;
        return pages();
    }

    public List<T> pages(){
        try {
            return collection.subList(indexPage*itemsPerPage, (indexPage*itemsPerPage + itemsPerPage));
        }catch (IndexOutOfBoundsException e) {
            try{
                return collection.subList(indexPage*itemsPerPage, (collection.size()));
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return collection;
        }
    }

    public int getPageIndex(){
        return indexPage;
    }

}