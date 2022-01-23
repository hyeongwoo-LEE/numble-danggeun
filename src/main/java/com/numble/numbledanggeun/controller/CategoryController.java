package com.numble.numbledanggeun.controller;

import com.numble.numbledanggeun.dto.category.CategoryResDTO;
import com.numble.numbledanggeun.service.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categorys")
    public String categoryList(Model model){

        List<CategoryResDTO> categoryList = categoryService.getCategoryList();
        model.addAttribute("categoryList",categoryList);

        return "board/categorySearch";
    }

}
