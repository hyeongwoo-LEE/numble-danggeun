package com.numble.numbledanggeun.dto.category;

import com.numble.numbledanggeun.domain.category.Category;
import lombok.Data;

@Data
public class CategoryResDTO {

    private Long categoryId;

    private String name;

    public CategoryResDTO(Category category){
        categoryId = category.getCategoryId();
        name = category.getName();
    }
}
