package com.jason.baseframe.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/2/17.
 */
public class MData<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    public String id;
    public String type;
    public T dataList;//多种类型数据，一般是List集合，比如获取所有员工列表

}
