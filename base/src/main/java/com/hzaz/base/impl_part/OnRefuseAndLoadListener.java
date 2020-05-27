package com.hzaz.base.impl_part;

public interface OnRefuseAndLoadListener {
    enum Status {
        FinishRefuseAndLoad, FinishLoad, FinishRefuse, loadAble_False, loadable_True, refreshable_False, refreshable_True
    }

    /**
     * @param page page == PAGE_SIZE_START =》 刷新数据     page > PAGE_SIZE_START =》 加载数据
     */
    void refuse(int page);
}
