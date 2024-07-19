package com.patientpal.backend.common.utils;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort.Order;

public class PageableUtil {

    public static String getSortAsString(Pageable pageable) {
        if (pageable == null || pageable.getSort() == null) {
            return "";
        }

        Sort sort = pageable.getSort();
        return sort.stream()
                .map(Order::getProperty)
                .collect(Collectors.joining(", "));
    }
}

