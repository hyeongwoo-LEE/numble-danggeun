package com.numble.numbledanggeun.service.category;

import com.numble.numbledanggeun.domain.category.Category;
import com.numble.numbledanggeun.domain.category.CategoryRepository;
import com.numble.numbledanggeun.dto.category.CategoryResDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;


    /**
     * 카테고리 리스트 조회
     */
    @Transactional(readOnly = true)
    @Override
    public List<CategoryResDTO> getCategoryList() {

        List<Category> result = categoryRepository.findAll();

        return result.stream()
                .map(category -> new CategoryResDTO(category)).collect(Collectors.toList());
    }
}
