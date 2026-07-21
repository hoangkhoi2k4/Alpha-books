package com.alphabook.service;


import com.alphabook.dto.CategoryDTO;
import com.alphabook.entity.Category;
import com.alphabook.repository.BookRepository;
import com.alphabook.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    public List<CategoryDTO> getAllCategories(){
        return categoryRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO getCategoryById(Long id){
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục có id: " + id));

        return convertToDTO(category);
    }

    public CategoryDTO createCategory(CategoryDTO categoryDTO){
        Category category = convertToEntity(categoryDTO);
        Category saved = categoryRepository.save(category);
        return convertToDTO(saved);
    }

    public CategoryDTO updateCategory(Long id,CategoryDTO categoryDTO){
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục có id: " + id + " để cập nhật"));

        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());

        return convertToDTO(categoryRepository.save(category));
    }

    public void deleteCategory(Long id){
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục có id: " + id + " để xóa"));

        categoryRepository.deleteById(id);
    }

    // Các hàm helper

    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }

    private Category convertToEntity(CategoryDTO categoryDTO){
        Category category = new Category();
        category.setId(categoryDTO.getId());
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        return category;
    }
}
