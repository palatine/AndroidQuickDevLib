package com.yzh.androidquickdevlib.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yzh on 2017/2/27.
 */

public class ListUtils {
    public interface IListSearchComparator<T> {
        boolean isBingo(T object);
    }

    /**
     * 从list中查找指定的项
     *
     * @param originalList
     * @param comparator
     * @param <T>
     * @return
     */
    public static <T> List<T> searchItems(List<T> originalList, IListSearchComparator<? super T> comparator) {
        if (originalList == null || comparator == null) {
            return originalList;
        }
        List<T> list = new ArrayList<>();
        for (T item : originalList) {
            if (comparator.isBingo(item)) {
                list.add(item);
            }
        }

        return list;
    }

    /**
     * 从list中删除指定的项
     *
     * @param originalList
     * @param comparator
     * @param <T>
     * @return
     */
    public static <T> void removeItems(List<T> originalList, IListSearchComparator<? super T> comparator) {
        if (originalList == null || comparator == null) {
            return;
        }

        final List<T> list = searchItems(originalList, comparator);
        originalList.removeAll(list);
    }

    /**
     * 将list拆分成最大max size的多个List集合
     *
     * @param originalList
     * @param maxSize
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> splitList(List<T> originalList, int maxSize) {
        if (originalList == null) {
            return null;
        }
        final int COUNT = originalList.size();
        List<List<T>> result = new ArrayList<>();
        for (int i = 0; i < COUNT; ) {
            int index = i / maxSize;
            result.add(index, new ArrayList<T>());

            int end = i + maxSize;
            end = end > COUNT ? COUNT : end;
            result.get(index)
                    .addAll(originalList.subList(i, end));
            i = end;
        }
        return result;
    }
}
