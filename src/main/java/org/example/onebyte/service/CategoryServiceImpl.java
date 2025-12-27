package org.example.onebyte.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.onebyte.dto.category.CategoryRequestDto;
import org.example.onebyte.dto.category.CategoryResponse;
import org.example.onebyte.entity.Category;
import org.example.onebyte.repository.BoardRepository;
import org.example.onebyte.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;
    private final BoardRepository boardRepository;

    // 활성화된 카테고리 목록만 조회
    @Override
    public List<CategoryResponse> findAllActive() {
        return categoryRepository.findAllByIsActiveTrue().stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }

    // 관리자 전용 카테고리 생성
    @Override
    public CategoryResponse createCategory(CategoryRequestDto dto) {
        if (categoryRepository.findByCode(dto.getCode()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 카테고리 코드입니다.");
        }

        if (categoryRepository.findByName(dto.getName()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 카테고리 이름입니다.");
        }

        Category category = Category.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .build();

        Category saveCategory = categoryRepository.save(category);

        return CategoryResponse.from(saveCategory);
    }

    // 관리자 전용 카테고리 수정
    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequestDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 카테고리를 찾을 수 없습니다."));

        if (dto.getName() != null && !category.getName().equals(dto.getName())) {
            if (categoryRepository.existsByNameAndIdNot(dto.getName(), id)) {
                throw new IllegalArgumentException("해당 카테고리 이름은 사용 중 입니다.");
            }
            category.rename(dto.getName());
        }

        if (dto.getCode() != null && !category.getCode().equals(dto.getCode())) {
            if (categoryRepository.existsByCodeAndIdNot(dto.getCode(), id)) {
                throw new IllegalArgumentException("해당 카테고리 코드는 사용 중 입니다.");
            }

            category.changeCode(dto.getCode());
        }

        if (dto.getIsActive() != null) {
            if (dto.getIsActive()) {
                category.activate();
            } else {
                category.deactivate();
            }
        }
        return CategoryResponse.from(category);
    }

    // 관리자 전용 카테고리 삭제
    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다."));

        category.deactivate();

        Category etcCategory = categoryRepository.findByCode("ETC")
                .orElseThrow(() -> new IllegalStateException("기타 카테고리가 시스템에 존재하지 않습니다."));

        boardRepository.updateCategoryBatch(category.getId(), etcCategory.getId());

    }
}
